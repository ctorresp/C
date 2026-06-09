import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // <-- CAMBIO: Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReporteService } from '../../services/reporte.service';
import { MascotaService } from '../../services/mascota.service';

interface Mascota { id: number; nombre: string; especie: string; }
interface Reporte { id: number; mascotaId: number; tipoReporte: string; fechaSuceso: string; estado: string; nombreMascota?: string; }

@Component({
  selector: 'app-report-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="main-wrapper">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5 rounded-4">
            
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-3">
              <div>
                <h1 class="fw-bold mb-1" style="color: var(--pet-brown);">Mis Reportes</h1>
                <p class="text-muted m-0">Aquí puedes revisar el estado y gestionar las alertas de mascotas que has publicado.</p>
              </div>
              <button class="btn btn-pet align-self-start align-self-md-center px-4 rounded-pill fw-bold" routerLink="/report">
                Generar nuevo reporte 🚨
              </button>
            </div>
            
            <hr class="my-4" style="border-color: var(--pet-beige); opacity: 0.3;">
            
            <div *ngIf="isLoading" class="text-center py-5">
              <div class="spinner-border text-danger" role="status">
                <span class="visually-hidden">Cargando reportes...</span>
              </div>
              <p class="text-muted mt-2">Cargando tu historial de reportes...</p>
            </div>

            <div *ngIf="!isLoading && listaReportes.length === 0" class="text-center py-5">
              <h5 class="fw-bold text-secondary">No tienes reportes activos</h5>
              <p class="small text-muted">Afortunadamente no has tenido que reportar ningún incidente reciente.</p>
            </div>
            
            <div *ngIf="!isLoading && listaReportes.length > 0" class="mt-4">
              <div class="p-4 rounded-4 mb-3 position-relative shadow-sm transition-hover" 
                   *ngFor="let reporte of listaReportes"
                   style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                
                <span class="badge rounded-pill position-absolute top-0 end-0 m-3 px-3 py-2 small shadow-sm"
                      [ngClass]="{'bg-danger': reporte.estado === 'PERDIDA', 'bg-success': reporte.estado === 'ENCONTRADA'}">
                  {{ reporte.estado === 'PERDIDA' ? 'EN BÚSQUEDA' : 'RESUELTO' }}
                </span>

                <div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between">
                  <div class="mb-3 mb-md-0 pe-md-5">
                    <h5 class="fw-bold mb-1" style="color: var(--pet-brown);">
                      Reporte de {{ reporte.tipoReporte | titlecase }}
                    </h5>
                    <p class="mb-1 fw-semibold text-dark">
                      Mascota: <span class="text-primary">{{ reporte.nombreMascota || 'Cargando datos...' }}</span>
                    </p>
                    <p class="small text-muted mb-0">
                      📅 Fecha del suceso: <span class="fw-medium">{{ reporte.fechaSuceso | date:'dd/MM/yyyy, h:mm a' }}</span>
                    </p>
                  </div>
                  <div class="d-flex flex-column flex-sm-row gap-2">
                    <button class="btn btn-outline-secondary px-4 rounded-pill fw-semibold" (click)="onEditar(reporte.id)">Editar</button>
                    <button class="btn btn-danger px-4 rounded-pill fw-semibold" (click)="onEliminar(reporte.id)">Eliminar</button>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .transition-hover { transition: transform 0.2s ease, box-shadow 0.2s ease; }
    .transition-hover:hover { transform: translateY(-2px); box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
  `]
})
export class ReportHistoryComponent implements OnInit {
  listaReportes: Reporte[] = [];
  misMascotas: Mascota[] = [];
  isLoading = true;

  // <-- CAMBIO: Inyectamos ChangeDetectorRef en el constructor
  constructor(
    private reporteService: ReporteService,
    private mascotaService: MascotaService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void { this.cargarDatos(); }

  cargarDatos(): void {
    this.isLoading = true;

    this.mascotaService.obtenerMascotas().subscribe({
      next: (mascotasData) => {
        this.misMascotas = mascotasData;
        
        this.reporteService.obtenerReportes().subscribe({
          next: (reportesData: Reporte[]) => {
            this.listaReportes = reportesData.map(reporte => {
              const mascotaEncontrada = this.misMascotas.find(m => m.id === reporte.mascotaId);
              return {
                ...reporte,
                nombreMascota: mascotaEncontrada ? mascotaEncontrada.nombre : 'Mascota Desconocida'
              };
            });
            this.isLoading = false;
            this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado al cruzar datos exitosamente
          },
          error: (err) => {
            console.error('Error al cargar reportes:', err);
            this.isLoading = false;
            this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado en error interno
            alert('No se pudieron cargar tus reportes.');
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar mascotas para el cruce de datos:', err);
        this.isLoading = false;
        this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado en error externo
      }
    });
  }

  onEditar(id: number): void { console.log('Editar reporte ID:', id); }
  onEliminar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este reporte?')) {
      console.log('Eliminar reporte ID:', id);
    }
  }
}