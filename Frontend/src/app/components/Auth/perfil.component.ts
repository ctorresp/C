import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // <-- Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="main-wrapper animated-bg">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-md-8 col-lg-6 glass-card shadow-lg bg-white p-5 fade-in">
            <div class="text-center mb-4">
              <h2 class="fw-bold" style="color: var(--pet-brown);">Mi Perfil</h2>
              <p class="text-muted">Consulta tus datos o elimina tu cuenta.</p>
            </div>

            <div *ngIf="isLoading" class="text-center my-4">
              <div class="spinner-border" style="color: var(--pet-brown);" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
            </div>

            <div *ngIf="errorMessage" class="alert alert-danger text-center">
              {{ errorMessage }}
            </div>

            <div *ngIf="!isLoading && usuario" class="profile-data mb-4">
              <ul class="list-group list-group-flush rounded border">
                <li class="list-group-item d-flex justify-content-between align-items-center p-3">
                  <strong>Nombre:</strong>
                  <span>{{ usuario.nombre }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center p-3">
                  <strong>Correo:</strong>
                  <span>{{ usuario.email }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center p-3">
                  <strong>Teléfono:</strong>
                  <span>{{ usuario.phone || 'No registrado' }}</span>
                </li>
              </ul>
            </div>

            <div *ngIf="!isLoading && usuario" class="d-grid gap-3">
              <button class="btn btn-danger rounded-pill py-2" (click)="onEliminarCuenta()">
                <i class="bi bi-trash-fill me-2"></i> Eliminar mi cuenta
              </button>
              <button class="btn btn-outline-secondary rounded-pill py-2" routerLink="/principal">
                Volver
              </button>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .animated-bg {
      min-height: 100vh;
      display: flex;
      align-items: center;
      animation: bgPulse 4s ease-in-out infinite;
    }
    @keyframes bgPulse {
      0%, 100% { background-color: #faf0e6; }
      50% { background-color: #f5f5dc; }
    }
    .fade-in {
      animation: fadeIn 0.8s ease-out;
    }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
    .glass-card {
      border-radius: 20px;
      backdrop-filter: blur(10px);
      background: rgba(255, 255, 255, 0.85) !important;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    .btn-pet {
      background-color: #8b5a2b; /* Var --pet-brown de ejemplo */
      color: white;
      border: none;
    }
    .btn-pet:hover {
      background-color: #6b4423;
      color: white;
    }
  `]
})
export class PerfilComponent implements OnInit {
  usuario: any = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private authService: AuthService, 
    private router: Router,
    private cdr: ChangeDetectorRef // <-- Inyectamos ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const uuid = localStorage.getItem('uuid');
    
    if (uuid) {
      this.cargarDatosUsuario(uuid);
    } else {
      this.errorMessage = 'No se encontró la sesión del usuario. Por favor, vuelve a iniciar sesión.';
      this.isLoading = false;
      this.cdr.detectChanges(); // <-- Forzamos detección de cambios
    }
  }

  cargarDatosUsuario(uuid: string): void {
    this.authService.obtenerUsuario(uuid).subscribe({
      next: (data) => {
        this.usuario = data;
        this.isLoading = false;
        this.cdr.detectChanges(); // <-- Obligamos a Angular a actualizar la vista
      },
      error: (err) => {
        console.error('Error al obtener usuario:', err);
        this.errorMessage = 'Hubo un error al cargar los datos del perfil.';
        this.isLoading = false;
        this.cdr.detectChanges(); // <-- Obligamos a Angular a actualizar la vista en caso de error
      }
    });
  }

  onEliminarCuenta(): void {
    const confirmar = confirm(
      '¿Estás completamente seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer y perderás el acceso a la plataforma.'
    );

    if (confirmar) {
      const uuid = localStorage.getItem('uuid');
      if (!uuid) return;

      this.isLoading = true;
      this.cdr.detectChanges(); // <-- Mostramos el loader de inmediato

      this.authService.eliminarCuenta(uuid).subscribe({
        next: () => {
          alert('Tu cuenta ha sido eliminada correctamente.');
          localStorage.removeItem('token');
          localStorage.removeItem('uuid');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Error al eliminar cuenta:', err);
          this.errorMessage = 'No se pudo eliminar la cuenta. Inténtalo de nuevo más tarde.';
          this.isLoading = false;
          this.cdr.detectChanges(); // <-- Actualizamos vista si falla
        }
      });
    }
  }
}