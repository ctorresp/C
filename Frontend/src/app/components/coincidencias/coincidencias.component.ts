import { Component, Input, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoincidenciaService } from '../../services/coincidencia.service';
import { MascotaService } from '../../services/mascota.service'; 
import { CoincidenciaDetalle } from '../../models/coincidencia.model'; 

@Component({
  selector: 'app-coincidencias',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="mt-4" *ngIf="!cargando && matches.length > 0">
      <div class="glass-card shadow-lg bg-white p-4 rounded-4 border-top border-warning border-5">
        
        <div class="text-center mb-4">
          <h4 class="fw-bold text-warning mb-1">
            <i class="bi bi-stars"></i> ¡Posibles Coincidencias Encontradas!
          </h4>
          <p class="text-muted small mb-0">
            Nuestro motor detectó similitudes. Por favor revisa la información.
          </p>
        </div>

        <div class="card mb-3 border-0 shadow-sm rounded-4" *ngFor="let match of matches" style="background-color: #f8f9fa;">
          <div class="card-header bg-transparent border-bottom-0 pt-3 pb-0 text-center">
            <span class="badge bg-danger rounded-pill px-3 py-2">
              Similitud: {{ match.porcentajeSimilitud | number:'1.0-0' }}%
            </span>
          </div>

          <div class="card-body">
            <div class="row align-items-center text-center">
              <div class="col-12 col-md-5 p-3 bg-white rounded-4 shadow-sm border">
                <h6 class="fw-bold text-danger"><i class="bi bi-search"></i> Reportada</h6>
                <p class="mb-1 small"><strong>Especie:</strong> {{ match.mascotaPerdida?.especie }}</p>
                <p class="mb-0 small"><strong>Raza:</strong> {{ match.mascotaPerdida?.raza }}</p>
              </div>

              <div class="col-12 col-md-2 py-2 fw-bold text-muted fs-5">VS</div>

              <div class="col-12 col-md-5 p-3 bg-white rounded-4 shadow-sm border">
                <h6 class="fw-bold text-success"><i class="bi bi-house-heart"></i> Encontrada</h6>
                <p class="mb-1 small"><strong>Especie:</strong> {{ match.mascotaEncontrada?.especie }}</p>
                <p class="mb-0 small"><strong>Raza:</strong> {{ match.mascotaEncontrada?.raza }}</p>
              </div>
            </div>
          </div>

          <div class="card-footer bg-transparent border-top-0 pb-4 text-center">
            <div class="d-flex flex-column flex-sm-row justify-content-center gap-3 mt-2">
              <button class="btn btn-outline-danger px-4 py-2 rounded-pill fw-bold w-100" (click)="tomarDecision(match.id, 'DESCARTADA')">
                <i class="bi bi-x-circle"></i> Descartar
              </button>
              
              <button *ngIf="tipoReporte === 'PERDIDA'" class="btn btn-success px-4 py-2 rounded-pill fw-bold shadow-sm w-100" 
                      (click)="tomarDecision(match.id, 'EXITOSA', match.mascotaEncontrada?.id)">
                <i class="bi bi-check-circle"></i> ¡Es mi mascota!
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  `,
  styles: [`
    .glass-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
    }
  `]
})
export class CoincidenciasComponent implements OnInit, OnChanges {
  @Input() reporteId!: number; 
  @Input() tipoReporte!: string; 
  
  matches: CoincidenciaDetalle[] = [];
  cargando = true;

  constructor(
    private coincidenciaService: CoincidenciaService,
    private mascotaService: MascotaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.reporteId) {
      this.cargarMatches();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['reporteId'] && changes['reporteId'].currentValue) {
      this.cargarMatches();
    }
  }

  cargarMatches(): void {
    this.cargando = true;
    this.coincidenciaService.obtenerCoincidenciasPorReporte(this.reporteId).subscribe({
      next: (data) => {
        this.matches = data.filter(m => m.estado === 'PENDIENTE' || m.estado === 'REVISADA');
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar coincidencias', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  tomarDecision(matchId: number, decision: 'EXITOSA' | 'DESCARTADA', mascotaTempId?: number): void {
    this.coincidenciaService.actualizarEstadoCoincidencia(matchId, decision).subscribe({
      next: () => {
        this.matches = this.matches.filter(m => m.id !== matchId);
        this.cdr.detectChanges();
        
        if (decision === 'EXITOSA' && mascotaTempId) {
          this.mascotaService.eliminarMascota(mascotaTempId).subscribe();
        }

        alert(decision === 'EXITOSA' ? '¡Qué alegría! Hemos notificado a la otra parte.' : 'Coincidencia descartada.');
      },
      error: (err) => {
        console.error('Error al actualizar el estado', err);
        alert('Hubo un error al procesar tu decisión.');
      }
    });
  }
}