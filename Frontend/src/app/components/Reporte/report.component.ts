import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // <-- Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ReporteService } from '../../services/reporte.service';
import { MascotaService } from '../../services/mascota.service';

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
              <h2 class="fw-bold" style="color: var(--pet-brown);">Generar Reporte de Extravío 🚨</h2>
              <p class="text-muted">Si desafortunadamente tu peludito se perdió, llena este formulario para activar la búsqueda comunitaria.</p>
            </div>
            
            <div *ngIf="isLoading" class="text-center py-4">
              <div class="spinner-border text-danger" role="status"></div>
              <p class="mt-2 text-muted small">Cargando tus mascotas registradas...</p>
            </div>

            <form *ngIf="!isLoading" [formGroup]="reportForm" (ngSubmit)="onSubmit()" class="mt-4">
              
              <div class="mb-4">
                <label class="form-label fw-bold text-dark">¿Qué mascota se perdió? *</label>
                <select *ngIf="misMascotas.length > 0" class="form-select form-select-lg" formControlName="mascotaId"
                        [ngClass]="{'is-invalid': reportForm.get('mascotaId')?.invalid && reportForm.get('mascotaId')?.touched}">
                  <option value="" disabled selected>Selecciona una mascota de tu lista</option>
                  <option *ngFor="let mascota of misMascotas" [value]="mascota.id">
                    {{ mascota.nombre }} ({{ mascota.especie }})
                  </option>
                </select>
                
                <div *ngIf="misMascotas.length === 0" class="alert alert-warning mt-2">
                  <i class="bi bi-exclamation-triangle"></i> No tienes mascotas registradas. Debes registrar una mascota primero para poder reportarla.
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Fecha y hora aproximada del extravío *</label>
                <input type="datetime-local" class="form-control form-control-lg" formControlName="fechaSuceso"
                       [ngClass]="{'is-invalid': reportForm.get('fechaSuceso')?.invalid && reportForm.get('fechaSuceso')?.touched}">
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold text-dark">Información adicional o lugar de extravío *</label>
                <textarea class="form-control" rows="4" formControlName="descripcion" placeholder="Ej: Se perdió cerca del parque central, llevaba un collar rojo..."
                          [ngClass]="{'is-invalid': reportForm.get('descripcion')?.invalid && reportForm.get('descripcion')?.touched}"></textarea>
              </div>

              <div class="d-grid mt-5">
                <button type="submit" class="btn btn-pet btn-lg fw-bold rounded-pill shadow-sm"
                        [disabled]="reportForm.invalid || isSubmitting || misMascotas.length === 0">
                  <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  {{ isSubmitting ? 'Publicando alerta...' : 'Publicar Alerta Inmediata' }}
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

  constructor(
    private fb: FormBuilder,
    private mascotaService: MascotaService,
    private reporteService: ReporteService,
    private router: Router,
    private cdr: ChangeDetectorRef // <-- 1. Inyectamos la herramienta
  ) {
    this.reportForm = this.fb.group({
      mascotaId: ['', Validators.required],
      tipoReporte: ['PERDIDA', Validators.required],
      fechaSuceso: ['', Validators.required],
      descripcion: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarMascotas();
  }

  cargarMascotas(): void {
    this.mascotaService.obtenerMascotas().subscribe({
      next: (data) => {
        this.misMascotas = data;
        this.isLoading = false;
        
        // <-- 2. FORZAMOS A ANGULAR A ACTUALIZAR EL HTML -->
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error al cargar mascotas', err);
        this.isLoading = false;
        this.cdr.detectChanges(); // También lo forzamos en caso de error
      }
    });
  }

  onSubmit(): void {
    if (this.reportForm.valid) {
      this.isSubmitting = true;
      
      const reporteData = {
        ...this.reportForm.value,
        estado: 'PERDIDA' 
      };

      this.reporteService.crearReporte(reporteData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.cdr.detectChanges(); // Forzamos actualización al terminar de guardar
          alert('¡Reporte publicado con éxito! Esperamos que tu mascota aparezca pronto.');
          this.router.navigate(['/report-history']);
        },
        error: (err) => {
          console.error('Error al crear reporte:', err);
          this.isSubmitting = false;
          this.cdr.detectChanges(); // Forzamos actualización al dar error
          alert('Ocurrió un error al publicar el reporte.');
        }
      });
    } else {
      this.reportForm.markAllAsTouched();
    }
  }
}