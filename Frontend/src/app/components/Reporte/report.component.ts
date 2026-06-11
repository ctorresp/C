import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import * as L from 'leaflet'; 

import { ReporteService, ReporteResponse } from '../../services/reporte.service';
import { MascotaService } from '../../services/mascota.service';
import { GeolocalizacionService, MarcadorRequest } from '../../services/geolocalizacion.service'; 

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="main-wrapper">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-md-10 col-lg-8 glass-card shadow-lg bg-white p-4 p-md-5 rounded-4">
            
            <div class="text-center mb-4">
              <h2 class="fw-bold" [ngStyle]="{'color': isPerdida() ? 'var(--pet-brown)' : '#198754'}">
                {{ isPerdida() ? 'Generar Reporte de Extravío 🚨' : 'Reportar Mascota Encontrada 💚' }}
              </h2>
              <p class="text-muted">
                {{ isPerdida() ? 'Si desafortunadamente tu peludito se perdió, llena este formulario.' : '¡Gracias por ayudar! Registra los datos de la mascota que encontraste para hallar a su familia.' }}
              </p>
            </div>
            
            <div *ngIf="isLoading" class="text-center py-4">
              <div class="spinner-border text-danger" role="status"></div>
              <p class="mt-2 text-muted small">Cargando tus mascotas registradas...</p>
            </div>

            <form *ngIf="!isLoading" [formGroup]="reportForm" (ngSubmit)="onSubmit()" class="mt-4">
              
              <div class="mb-4 p-3 rounded-3" style="background-color: #f8f9fa; border: 1px solid #e9ecef;">
                <label class="form-label fw-bold text-dark d-block mb-3">¿Qué deseas reportar? *</label>
                <div class="d-flex gap-4">
                  <div class="form-check">
                    <input class="form-check-input" type="radio" formControlName="tipoReporte" value="PERDIDA" id="radioPerdida">
                    <label class="form-check-label text-danger fw-bold" for="radioPerdida">
                      <i class="bi bi-search"></i> Perdí a mi mascota
                    </label>
                  </div>
                  <div class="form-check">
                    <input class="form-check-input" type="radio" formControlName="tipoReporte" value="ENCONTRADA" id="radioEncontrada">
                    <label class="form-check-label text-success fw-bold" for="radioEncontrada">
                      <i class="bi bi-house-heart"></i> Encontré una mascota
                    </label>
                  </div>
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">
                  {{ isPerdida() ? '¿Qué mascota se perdió? *' : 'Selecciona la mascota encontrada (debes registrarla primero) *' }}
                </label>
                <select *ngIf="misMascotas.length > 0" class="form-select form-select-lg" formControlName="mascotaId"
                        [ngClass]="{'is-invalid': reportForm.get('mascotaId')?.invalid && reportForm.get('mascotaId')?.touched}">
                  <option value="" disabled selected>Selecciona una mascota de tu lista</option>
                  <option *ngFor="let mascota of misMascotas" [value]="mascota.id">
                    {{ mascota.nombre }} ({{ mascota.especie }})
                  </option>
                </select>
                
                <div *ngIf="misMascotas.length === 0" class="alert alert-warning mt-2">
                  <i class="bi bi-exclamation-triangle"></i> No tienes mascotas registradas en tu perfil. 
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Fecha y hora aproximada del suceso *</label>
                <input type="datetime-local" class="form-control form-control-lg" formControlName="fechaSuceso"
                       [ngClass]="{'is-invalid': reportForm.get('fechaSuceso')?.invalid && reportForm.get('fechaSuceso')?.touched}">
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Información adicional *</label>
                <textarea class="form-control" rows="4" formControlName="descripcion" 
                          [placeholder]="isPerdida() ? 'Ej: Llevaba un collar rojo, se asusta fácil...' : 'Ej: Tenía un collar azul, es muy dócil, la encontré cerca de la plaza...'"
                          [ngClass]="{'is-invalid': reportForm.get('descripcion')?.invalid && reportForm.get('descripcion')?.touched}"></textarea>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">
                  {{ isPerdida() ? 'Ubicación de extravío *' : 'Lugar donde la encontraste *' }}
                </label>
                <p class="text-muted small mb-2">Haz clic en el mapa para marcar el lugar exacto. El motor de búsqueda necesita esto para encontrar coincidencias.</p>
                <div id="select-map" style="height: 350px; width: 100%; border: 2px solid #ddd;" class="rounded-3 shadow-sm"></div>
                <div *ngIf="reportForm.get('latitud')?.invalid && reportForm.get('latitud')?.touched" class="text-danger small mt-1">
                  Por favor, marca un punto en el mapa.
                </div>
              </div>

              <div class="d-grid mt-5">
                <button type="submit" class="btn btn-lg fw-bold rounded-pill shadow-sm"
                        [ngClass]="isPerdida() ? 'btn-pet text-white' : 'btn-success text-white'"
                        [disabled]="reportForm.invalid || isSubmitting || misMascotas.length === 0">
                  <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  {{ isSubmitting ? 'Procesando...' : (isPerdida() ? 'Publicar Alerta Inmediata' : 'Registrar Mascota Encontrada') }}
                </button>
              </div>
            </form>

          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .btn-pet { background-color: var(--pet-orange); border: none; }
    .btn-pet:hover { background-color: #e67e22; }
  `]
})
export class ReportComponent implements OnInit {
  reportForm: FormGroup;
  misMascotas: any[] = [];
  isLoading = true;
  isSubmitting = false;
  private map: L.Map | undefined; 

  constructor(
    private fb: FormBuilder,
    private mascotaService: MascotaService,
    private reporteService: ReporteService,
    private geoService: GeolocalizacionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.reportForm = this.fb.group({
      tipoReporte: ['PERDIDA', Validators.required], // <-- Iniciamos con PERDIDA por defecto
      mascotaId: ['', Validators.required],
      fechaSuceso: ['', Validators.required],
      descripcion: ['', Validators.required],
      latitud: [null, Validators.required],
      longitud: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarMascotas();
  }

  // Helper para hacer el HTML más limpio
  isPerdida(): boolean {
    return this.reportForm.get('tipoReporte')?.value === 'PERDIDA';
  }

  cargarMascotas(): void {
    this.mascotaService.obtenerMascotas().subscribe({
      next: (data) => {
        this.misMascotas = data;
        this.isLoading = false;
        this.cdr.detectChanges(); 
        setTimeout(() => this.inicializarMapaSeleccion(), 100);
      },
      error: (err) => {
        console.error('Error al cargar mascotas', err);
        this.isLoading = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  onSubmit(): void {
    if (this.reportForm.valid) {
      this.isSubmitting = true;
      
      const tipoSeleccionado = this.reportForm.value.tipoReporte; // Extraemos si es PERDIDA o ENCONTRADA

      const reporteData = {
        mascotaId: this.reportForm.value.mascotaId,
        tipoReporte: tipoSeleccionado,
        fechaSuceso: this.reportForm.value.fechaSuceso,
        descripcion: this.reportForm.value.descripcion,
        estado: tipoSeleccionado // Pasamos dinámicamente el estado
      };

      this.reporteService.crearReporte(reporteData).subscribe({
        next: (reporteRespuesta: ReporteResponse) => {
          
          const marcadorData: MarcadorRequest = {
            reporteId: reporteRespuesta.id, 
            tipoMarcador: tipoSeleccionado, // Pasamos dinámicamente el estado al mapa
            latitud: this.reportForm.value.latitud,
            longitud: this.reportForm.value.longitud
          };

          this.geoService.crearMarcador(marcadorData).subscribe({
            next: () => {
              this.isSubmitting = false;
              this.cdr.detectChanges(); 
              const mensaje = tipoSeleccionado === 'PERDIDA' 
                ? '¡Reporte publicado! Esperamos que aparezca pronto.' 
                : '¡Gracias por reportarla! El motor de búsqueda ya está analizando coincidencias.';
              alert(mensaje);
              this.router.navigate(['/reportHistory']);
            },
            error: (errGeo) => {
              console.error('Falló la geolocalización:', errGeo);
              this.isSubmitting = false;
              this.cdr.detectChanges();
              alert('Reporte creado, pero hubo un problema al guardar el mapa.');
            }
          });
        },
        error: (err) => {
          console.error('Error al crear reporte:', err);
          this.isSubmitting = false;
          this.cdr.detectChanges(); 
          alert('Ocurrió un error al publicar el reporte.');
        }
      });
    } else {
      this.reportForm.markAllAsTouched();
    }
  }

  inicializarMapaSeleccion(): void {
    if (this.map) return;

    this.map = L.map('select-map').setView([-33.4489, -70.6693], 13);
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    let marcadorActual: L.Marker | null = null;

    const iconDefault = L.icon({
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;

      this.reportForm.patchValue({ latitud: lat, longitud: lng });

      if (marcadorActual) {
        this.map!.removeLayer(marcadorActual);
      }
      
      marcadorActual = L.marker([lat, lng], { icon: iconDefault }).addTo(this.map!);
    });
  }
}