import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- IMPORTANTE: Importamos FormsModule para ngModel
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], // <-- Lo agregamos a los imports
  template: `
    <div class="main-wrapper animated-bg">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-md-8 col-lg-6 glass-card shadow-lg bg-white p-5 fade-in">
            <div class="text-center mb-4">
              <h2 class="fw-bold" style="color: var(--pet-brown);">Mi Perfil</h2>
              <p class="text-muted">Consulta tus datos o actualiza tu seguridad.</p>
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
              <ul class="list-group list-group-flush rounded border mb-4">
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

              <div class="mb-4">
                <button class="btn btn-outline-brown w-100 rounded-pill mb-3 fw-bold" (click)="togglePasswordForm()">
                  <i class="bi bi-key-fill me-2"></i> 
                  {{ mostrarFormPassword ? 'Cancelar cambio de contraseña' : 'Cambiar Contraseña' }}
                </button>

                <div *ngIf="mostrarFormPassword" class="p-3 border rounded bg-light fade-in">
                  
                  <div *ngIf="passwordSuccessMessage" class="alert alert-success small">
                    {{ passwordSuccessMessage }}
                  </div>
                  <div *ngIf="passwordErrorMessage" class="alert alert-danger small">
                    {{ passwordErrorMessage }}
                  </div>

                  <form #passForm="ngForm" (ngSubmit)="onCambiarContrasena()">
                    <div class="mb-3">
                      <label class="form-label small fw-bold">Contraseña Actual</label>
                      <input type="password" class="form-control rounded-pill" 
                             [(ngModel)]="passData.currentPassword" name="currentPassword" required>
                    </div>
                    <div class="mb-3">
                      <label class="form-label small fw-bold">Nueva Contraseña</label>
                      <input type="password" class="form-control rounded-pill" 
                             [(ngModel)]="passData.newPassword" name="newPassword" required minlength="6">
                    </div>
                    <div class="mb-3">
                      <label class="form-label small fw-bold">Confirmar Nueva Contraseña</label>
                      <input type="password" class="form-control rounded-pill" 
                             [(ngModel)]="passData.confirmPassword" name="confirmPassword" required>
                    </div>
                    <button type="submit" class="btn btn-pet w-100 rounded-pill" 
                            [disabled]="passForm.invalid || isUpdatingPassword">
                      {{ isUpdatingPassword ? 'Actualizando...' : 'Guardar Nueva Contraseña' }}
                    </button>
                  </form>
                </div>
              </div>
              </div>

            <div *ngIf="!isLoading && usuario" class="d-grid gap-3 border-top pt-4">
              <button class="btn btn-outline-secondary rounded-pill py-2" routerLink="/principal">
                Volver al inicio
              </button>
              <button class="btn btn-danger rounded-pill py-2" (click)="onEliminarCuenta()">
                <i class="bi bi-trash-fill me-2"></i> Eliminar mi cuenta
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
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
    }
    .glass-card {
      border-radius: 20px;
      backdrop-filter: blur(10px);
      background: rgba(255, 255, 255, 0.85) !important;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    .btn-pet {
      background-color: #8b5a2b; 
      color: white;
      border: none;
    }
    .btn-pet:hover {
      background-color: #6b4423;
      color: white;
    }
    .btn-outline-brown {
      color: #8b5a2b;
      border: 1px solid #8b5a2b;
      background-color: transparent;
    }
    .btn-outline-brown:hover {
      background-color: #8b5a2b;
      color: white;
    }
  `]
})
export class PerfilComponent implements OnInit {
  usuario: any = null;
  isLoading = true;
  errorMessage = '';

  // Variables para el cambio de contraseña
  mostrarFormPassword = false;
  isUpdatingPassword = false;
  passwordSuccessMessage = '';
  passwordErrorMessage = '';
  passData = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(
    private authService: AuthService, 
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    const uuid = localStorage.getItem('uuid');
    
    if (uuid) {
      this.cargarDatosUsuario(uuid);
    } else {
      this.errorMessage = 'No se encontró la sesión del usuario. Por favor, vuelve a iniciar sesión.';
      this.isLoading = false;
      this.cdr.detectChanges(); 
    }
  }

  cargarDatosUsuario(uuid: string): void {
    this.authService.obtenerUsuario(uuid).subscribe({
      next: (data) => {
        this.usuario = data;
        this.isLoading = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error al obtener usuario:', err);
        this.errorMessage = 'Hubo un error al cargar los datos del perfil.';
        this.isLoading = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  // Activa o desactiva el formulario de contraseña
  togglePasswordForm(): void {
    this.mostrarFormPassword = !this.mostrarFormPassword;
    // Limpiamos los campos y mensajes al abrir/cerrar
    this.passData = { currentPassword: '', newPassword: '', confirmPassword: '' };
    this.passwordSuccessMessage = '';
    this.passwordErrorMessage = '';
  }

  // Ejecuta la petición al backend para cambiar contraseña
  onCambiarContrasena(): void {
    this.passwordSuccessMessage = '';
    this.passwordErrorMessage = '';

    // Validar que las contraseñas coincidan
    if (this.passData.newPassword !== this.passData.confirmPassword) {
      this.passwordErrorMessage = 'La nueva contraseña y la confirmación no coinciden.';
      return;
    }

    const uuid = localStorage.getItem('uuid');
    if (!uuid) return;

    this.isUpdatingPassword = true;
    
    const payload = {
      currentPassword: this.passData.currentPassword,
      newPassword: this.passData.newPassword
    };

    this.authService.actualizarContrasena(uuid, payload).subscribe({
      next: () => {
        this.isUpdatingPassword = false;
        this.passwordSuccessMessage = '¡Contraseña actualizada con éxito!';
        // Reseteamos el formulario
        this.passData = { currentPassword: '', newPassword: '', confirmPassword: '' };
        this.cdr.detectChanges();
        
        // Opcional: Cerrar el formulario después de un par de segundos
        setTimeout(() => {
          this.mostrarFormPassword = false;
          this.cdr.detectChanges();
        }, 3000);
      },
      error: (err) => {
        console.error('Error al cambiar contraseña:', err);
        this.isUpdatingPassword = false;
        this.passwordErrorMessage = 'Error al actualizar. Verifica que tu contraseña actual sea correcta.';
        this.cdr.detectChanges();
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
      this.cdr.detectChanges(); 

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
          this.cdr.detectChanges(); 
        }
      });
    }
  }
}