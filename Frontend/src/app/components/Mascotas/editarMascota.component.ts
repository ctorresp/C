import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MascotaService } from '../../services/mascota.service';

@Component({
  selector: 'app-editar-mascota',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="main-wrapper">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-8 glass-card shadow-lg bg-white p-5 rounded-4">
            <h2 class="fw-bold text-center mb-2" style="color: var(--pet-brown);">Editar Mascota</h2>
            <p class="text-muted text-center mb-4">Actualiza los datos o la foto de tu peludito.</p>

            <div *ngIf="isLoadingData" class="text-center py-4">
              <div class="spinner-border text-warning" role="status"></div>
              <p class="mt-2 text-muted">Cargando datos...</p>
            </div>

            <form *ngIf="!isLoadingData" (ngSubmit)="onSubmit()">
              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Nombre</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.nombre" name="nombre" required />
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Especie</label>
                  <select class="form-select rounded-pill" [(ngModel)]="mascota.especie" name="especie" required>
                    <option value="Perro">Perro</option>
                    <option value="Gato">Gato</option>
                    <option value="Otro">Otro</option>
                  </select>
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Raza</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.raza" name="raza" required />
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Edad (Años)</label>
                  <input type="number" class="form-control rounded-pill" [(ngModel)]="mascota.edad" name="edad" required min="0" />
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Sexo</label>
                  <select class="form-select rounded-pill" [(ngModel)]="mascota.sexo" name="sexo" required>
                    <option value="Macho">Macho</option>
                    <option value="Hembra">Hembra</option>
                  </select>
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Color principal</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.color" name="color" required />
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label fw-semibold">Descripción / Detalles particulares</label>
                <textarea class="form-control rounded-4" rows="3" [(ngModel)]="mascota.descripcion" name="descripcion" required></textarea>
              </div>

              <div class="mb-4">
                <label class="form-label fw-semibold">Actualizar foto (Opcional)</label>
                <input type="file" class="form-control rounded-pill" (change)="onFileSelected($event)" accept="image/*" />
                <small class="text-muted">Si no seleccionas nada, se conservará la foto actual.</small>
              </div>

              <div class="d-flex gap-2">
                <button type="button" class="btn btn-outline-secondary w-50 rounded-pill py-2 fw-bold" routerLink="/mascotasHistory">Cancelar</button>
                <button type="submit" class="btn btn-pet w-50 rounded-pill py-2 fw-bold" [disabled]="isSaving">
                  {{ isSaving ? 'Guardando...' : 'Guardar Cambios 💾' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `
})
export class EditarMascotaComponent implements OnInit {
  mascotaId!: number;
  mascota: any = {};
  selectedFile: File | null = null;
  isLoadingData = true;
  isSaving = false;

  constructor(
    private mascotaService: MascotaService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.mascotaId = Number(this.route.snapshot.paramMap.get('id'));

    this.mascotaService.obtenerMascotaPorId(this.mascotaId).subscribe({
      next: (data) => {
        this.mascota = data;
        this.isLoadingData = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener la mascota', err);
        this.isLoadingData = false;
        this.cdr.detectChanges();
        alert('No se pudo cargar la información de la mascota.');
        this.router.navigate(['/mascotasHistory']);
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  onSubmit() {
    this.isSaving = true;

    this.mascotaService.actualizarMascota(this.mascotaId, this.mascota).subscribe({
      next: () => {
        if (this.selectedFile) {
          this.mascotaService.subirImagen(this.selectedFile, this.mascotaId).subscribe({
            next: () => {
              this.finalizarGuardado();
            },
            error: (err) => {
              console.error('Error al actualizar la imagen', err);
              alert('Datos actualizados, pero hubo un error al subir la nueva foto.');
              this.isSaving = false;
            }
          });
        } else {
          this.finalizarGuardado();
        }
      },
      error: (err) => {
        this.isSaving = false;
        console.error('Error al actualizar mascota', err);
        alert('Hubo un error al actualizar los datos.');
      }
    });
  }

  finalizarGuardado() {
    this.isSaving = false;
    alert('¡Mascota actualizada con éxito!');
    this.router.navigate(['/mascotas']);
  }
}
