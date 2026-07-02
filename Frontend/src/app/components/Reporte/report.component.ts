import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import * as L from 'leaflet'; 
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

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
            </div>
            
            <form *ngIf="!isLoading" [formGroup]="reportForm" (ngSubmit)="onSubmit()" class="mt-4">
              
              <div class="mb-4 p-3 rounded-3" style="background-color: #f8f9fa; border: 1px solid #e9ecef;">
                <label class="form-label fw-bold text-dark d-block mb-3">¿Qué deseas reportar? *</label>
                <div class="d-flex gap-4">
                  <div class="form-check">
                    <input class="form-check-input" type="radio" formControlName="tipoReporte" value="PERDIDA" id="radioPerdida" (change)="onTipoReporteChange()">
                    <label class="form-check-label text-danger fw-bold" for="radioPerdida">
                      <i class="bi bi-search"></i> Perdí a mi mascota
                    </label>
                  </div>
                  <div class="form-check">
                    <input class="form-check-input" type="radio" formControlName="tipoReporte" value="ENCONTRADA" id="radioEncontrada" (change)="onTipoReporteChange()">
                    <label class="form-check-label text-success fw-bold" for="radioEncontrada">
                      <i class="bi bi-house-heart"></i> Encontré una mascota
                    </label>
                  </div>
                </div>
              </div>

              <div class="mb-4" *ngIf="isPerdida()">
                <label class="form-label fw-bold text-dark">¿Qué mascota se perdió? *</label>
                <select *ngIf="misMascotas.length > 0" class="form-select form-select-lg" formControlName="mascotaId">
                  <option value="" disabled selected>Selecciona una mascota de tu lista</option>
                  <option *ngFor="let mascota of misMascotas" [value]="mascota.id">
                    {{ mascota.nombre }} ({{ mascota.especie }})
                  </option>
                </select>
                <div *ngIf="misMascotas.length === 0" class="alert alert-warning mt-2">
                  No tienes mascotas registradas. Ve a tu perfil primero.
                </div>
              </div>

              <div class="row mb-4" *ngIf="!isPerdida()">
                <div class="col-md-6">
                  <label class="form-label fw-bold text-dark">Especie *</label>
                  <input type="text" class="form-control" formControlName="especieEncontrada" placeholder="Ej: Perro, Gato">
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold text-dark">Raza aproximada *</label>
                  <input type="text" class="form-control" formControlName="razaEncontrada" placeholder="Ej: Poodle, Mestizo">
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Fecha y hora aproximada *</label>
                <input type="datetime-local" class="form-control" formControlName="fechaSuceso">
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Información adicional *</label>
                <textarea class="form-control" rows="4" formControlName="descripcion"></textarea>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Ubicación *</label>
                <div id="select-map" style="height: 350px; width: 100%; border: 2px solid #ddd;" class="rounded-3"></div>
              </div>

              <div class="d-grid mt-5">
                <button type="submit" class="btn btn-lg fw-bold rounded-pill text-white"
                        [ngClass]="isPerdida() ? 'btn-danger' : 'btn-success'"
                        [disabled]="reportForm.invalid || isSubmitting">
                  {{ isSubmitting ? 'Procesando...' : (isPerdida() ? 'Publicar Extravío' : 'Registrar Encontrada') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `
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
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.reportForm = this.fb.group({
      tipoReporte: ['PERDIDA', Validators.required],
      mascotaId: [''], // Ya no es requerido por defecto
      especieEncontrada: [''], // Campos nuevos
      razaEncontrada: [''],
      fechaSuceso: ['', Validators.required],
      descripcion: ['', Validators.required],
      latitud: [null, Validators.required],
      longitud: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarMascotas();
    
    this.route.queryParams.subscribe(params => {
      if (params['action'] === 'found') {
        this.reportForm.patchValue({ tipoReporte: 'ENCONTRADA' });
      }
      
      if (params['mascotaId']) {
        this.reportForm.patchValue({ 
          tipoReporte: 'PERDIDA',
          mascotaId: Number(params['mascotaId'])
        });
      }

      this.onTipoReporteChange();
    });
  }

  isPerdida(): boolean {
    return this.reportForm.get('tipoReporte')?.value === 'PERDIDA';
  }

  // Ajusta las validaciones dependiendo de lo que seleccione el usuario
  onTipoReporteChange(): void {
    const isPerdida = this.isPerdida();
    const mascotaIdCtrl = this.reportForm.get('mascotaId');
    const especieCtrl = this.reportForm.get('especieEncontrada');
    const razaCtrl = this.reportForm.get('razaEncontrada');

    if (isPerdida) {
      mascotaIdCtrl?.setValidators([Validators.required]);
      especieCtrl?.clearValidators();
      razaCtrl?.clearValidators();
    } else {
      mascotaIdCtrl?.clearValidators();
      especieCtrl?.setValidators([Validators.required]);
      razaCtrl?.setValidators([Validators.required]);
    }

    mascotaIdCtrl?.updateValueAndValidity();
    especieCtrl?.updateValueAndValidity();
    razaCtrl?.updateValueAndValidity();
  }

  cargarMascotas(): void {
    this.mascotaService.obtenerMascotas().subscribe({
      next: (data) => {
        this.misMascotas = data;
        this.isLoading = false;
        this.cdr.detectChanges(); 
        setTimeout(() => this.inicializarMapaSeleccion(), 100);
      }
    });
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formVals = this.reportForm.value;

    // LÓGICA MÁGICA AJUSTADA A TU SERVICIO ACTUAL:
    const mascotaObservable = this.isPerdida() 
      ? of(formVals.mascotaId) 
      : this.mascotaService.registrarMascota({ // <-- Usamos registrarMascota
          nombre: 'Peludito Encontrado', // Nombre genérico
          especie: formVals.especieEncontrada,
          raza: formVals.razaEncontrada,
          edad: 0, // Valor por defecto
          sexo: 'Desconocido', // Valor por defecto
          color: 'No especificado', // Valor por defecto
          descripcion: 'Registro generado automáticamente desde reporte de hallazgo.',
          estado: 'ENCONTRADA'
        }).pipe(switchMap((nuevaMascota: any) => of(nuevaMascota.id))); // Extraemos el ID generado

    // Ejecutamos la cadena de peticiones
    mascotaObservable.subscribe({
      next: (finalMascotaId: number) => {
        
        const reporteData = {
          mascotaId: finalMascotaId,
          tipoReporte: formVals.tipoReporte,
          fechaSuceso: formVals.fechaSuceso,
          descripcion: formVals.descripcion,
          estado: formVals.tipoReporte
        };

        this.reporteService.crearReporte(reporteData).subscribe({
          next: (reporteRes) => {
            const marcadorData: MarcadorRequest = {
              reporteId: reporteRes.id, 
              tipoMarcador: formVals.tipoReporte,
              latitud: formVals.latitud,
              longitud: formVals.longitud
            };

            this.geoService.crearMarcador(marcadorData).subscribe({
              next: () => {
                this.isSubmitting = false;
                alert('¡Reporte procesado correctamente!');
                this.router.navigate(['/reportHistory']);
              }
            });
          }
        });
      },
      error: (err) => {
        console.error('Error en el flujo:', err);
        this.isSubmitting = false;
        alert('Hubo un error al procesar el reporte.');
      }
    });
  }

  inicializarMapaSeleccion(): void {
    // ... Tu misma lógica de mapa ...
    if (this.map) return;
    this.map = L.map('select-map').setView([-33.4489, -70.6693], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);
    let marcadorActual: L.Marker | null = null;
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.reportForm.patchValue({ latitud: e.latlng.lat, longitud: e.latlng.lng });
      if (marcadorActual) this.map!.removeLayer(marcadorActual);
      marcadorActual = L.marker([e.latlng.lat, e.latlng.lng]).addTo(this.map!);
    });
  }
}