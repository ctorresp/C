import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router'; // <-- Añadimos Router
import { MascotaService } from '../../services/mascota.service';

interface Mascota {
  id: number;
  usuarioUuid?: string;
  nombre: string;
  especie: string;
  raza: string;
  edad: number;
  sexo: string;
  color: string;
  descripcion: string;
  estado: 'ENCONTRADA' | 'PERDIDA';
  fotoUrl?: string; 
}

@Component({
  selector: 'app-mascotas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="main-wrapper">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5 rounded-4">

            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-3">
              <div>
                <h1 class="fw-bold mb-1" style="color: var(--pet-brown);">Mis Mascotas</h1>
                <p class="text-muted m-0">Aquí puedes gestionar tus mascotas registradas en el sistema.</p>
              </div>
              <button class="btn btn-pet align-self-start align-self-md-center px-4 rounded-pill fw-bold" routerLink="/mascotas">
                Registrar nueva mascota 🐾
              </button>
            </div>

            <hr class="my-4" style="border-color: var(--pet-beige); opacity: 0.3;">

            <div *ngIf="isLoading" class="text-center py-5">
              <div class="spinner-border text-warning" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
              <p class="text-muted mt-2">Buscando tus peluditos registrados...</p>
            </div>

            <div *ngIf="!isLoading && listaMascotas.length === 0" class="text-center py-5">
              <h5 class="fw-bold text-secondary">Aún no tienes mascotas registradas</h5>
              <p class="small text-muted">Añade a tus peludos para mantenerlos seguros en nuestro sistema.</p>
            </div>

            <div *ngIf="!isLoading && listaMascotas.length > 0" class="row row-cols-1 row-cols-md-2 g-4 mt-2">
              <div class="col" *ngFor="let mascota of listaMascotas">
                <div class="rounded-4 h-100 shadow-sm d-flex flex-column position-relative overflow-hidden" 
                     style="background-color: #fffaf0; border: 1px solid var(--pet-beige, #e9ecef);">
                  
                  <img [src]="mascota.fotoUrl || 'https://images.unsplash.com/photo-1543466835-00a7907e9de1'" 
                       alt="Foto de {{ mascota.nombre }}" 
                       class="w-100" style="aspect-ratio: 4/3; object-fit: cover; object-position: center; display: block;">

                  <span *ngIf="mascota.estado === 'PERDIDA'" 
                        class="badge rounded-pill bg-danger position-absolute top-0 end-0 m-3 px-3 py-2 small shadow-sm">
                    ⚠️ {{ mascota.estado }}
                  </span>

                  <div class="p-4 d-flex flex-column grow justify-content-between">
                    <div>
                      <h4 class="fw-bold mb-2" style="color: var(--pet-brown);">{{ mascota.nombre }}</h4>
                      <div class="d-flex flex-wrap gap-2 mb-3">
                        <span class="badge bg-white text-dark border rounded-pill px-3">{{ mascota.especie }}</span>
                        <span class="badge bg-white text-dark border rounded-pill px-3">{{ mascota.raza }}</span>
                        <span class="badge bg-white text-dark border rounded-pill px-3">{{ mascota.edad }} {{ mascota.edad === 1 ? 'año' : 'años' }}</span>
                      </div>
                      <p class="text-secondary small mb-3 text-truncate-3">{{ mascota.descripcion }}</p>
                    </div>
                    
                    <div class="d-flex gap-2 mt-3">
                      <button class="btn btn-outline-secondary w-50 rounded-pill fw-semibold" (click)="onEditar(mascota.id)">Editar ✏️</button>
                      <button class="btn btn-danger w-50 rounded-pill fw-semibold" (click)="onEliminar(mascota.id)">Eliminar 🗑️</button>
                    </div>
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
    .text-truncate-3 {
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;  
      overflow: hidden;
    }
  `]
})
export class MascotasComponent implements OnInit {
  listaMascotas: Mascota[] = [];
  isLoading = true;

  constructor(
    private mascotaService: MascotaService,
    private cdr: ChangeDetectorRef,
    private router: Router // <-- Inyectamos el Router
  ) {}

  ngOnInit(): void {
    this.cargarMascotas();
  }

  cargarMascotas(): void {
    this.isLoading = true;
    const currentUserUuid = localStorage.getItem('uuid');

    this.mascotaService.obtenerMascotas().subscribe({
      next: (data: Mascota[]) => {
        this.listaMascotas = data.filter(m => m.usuarioUuid === currentUserUuid);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al traer el listado de mascotas:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
        alert('No se pudo cargar el historial de mascotas.');
      }
    });
  }

  onEditar(id: number): void { 
    this.router.navigate(['/editar', id]); 
  }

  onEliminar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta mascota del sistema?')) {
      this.mascotaService.eliminarMascota(id).subscribe({
        next: () => {
          // Si se elimina con éxito, la sacamos de la lista sin recargar la página
          this.listaMascotas = this.listaMascotas.filter(m => m.id !== id);
          this.cdr.detectChanges();
          alert('Mascota eliminada con éxito.');
        },
        error: (err) => {
          console.error('Error al eliminar mascota:', err);
          alert('Hubo un error al intentar eliminar la mascota.');
        }
      });
    }
  }
}