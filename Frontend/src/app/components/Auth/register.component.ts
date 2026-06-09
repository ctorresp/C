import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="main-wrapper animated-bg-gradient">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-md-8 col-lg-6 glass-card shadow-lg bg-white p-4 fade-in-scale">
            <div class="text-center mb-4">
              <h2 class="fw-bold" style="color: var(--pet-brown);">Registrarme</h2>
              <p class="text-muted">Crea tu cuenta para reportar mascotas y recibir alertas.</p>
            </div>
            <form #registerForm="ngForm" (ngSubmit)="onSubmit(registerForm)">
              <div class="mb-3">
                <label class="form-label">Nombre completo</label>
                <input type="text" class="form-control rounded-pill" [(ngModel)]="user.name" name="name" #name="ngModel" pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+" placeholder="Tu nombre completo" required />
                <div *ngIf="name.invalid && name.touched" class="text-danger small mt-1">El nombre solo puede contener letras y espacios.</div>
              </div>
              <div class="mb-3">
                <label class="form-label">Correo electrónico</label>
                <input type="email" class="form-control rounded-pill" [(ngModel)]="user.email" name="email" #email="ngModel" placeholder="correo@ejemplo.com" required email />
                <div *ngIf="email.invalid && email.touched" class="text-danger small mt-1">Ingresa un correo electrónico válido.</div>
              </div>
              <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control rounded-pill" [(ngModel)]="user.password" name="password" #password="ngModel" placeholder="Crea una contraseña" required minlength="6" />
                <div *ngIf="password.invalid && password.touched" class="text-danger small mt-1">La contraseña debe tener al menos 6 caracteres.</div>
              </div>
              <button type="submit" class="btn btn-pet w-100" [disabled]="registerForm.invalid">Crear cuenta</button>
            </form>
            <div class="text-center mt-3">
              <a routerLink="/" style="color: var(--pet-brown);">Volver al inicio</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .animated-bg-gradient {
      background: linear-gradient(90deg, #faf0e6, #f5f5dc, #faf0e6);
      background-size: 200% 200%;
      animation: gradientMove 5s ease infinite;
    }
    @keyframes gradientMove {
      0% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
      100% { background-position: 0% 50%; }
    }
    .fade-in-scale {
      animation: scaleIn 1s ease-out;
    }
    @keyframes scaleIn {
      from { transform: scale(0.9); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }
  `]
})
export class RegisterComponent {
  user = { name: '', email: '', password: '' };
  errorMessage = '';
  isLoading = false;

  nombrePattern = /^[a-zA-ZÀ-ÿ\s]+$/;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(form: any) {
    if (form.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.authService.register(this.user).subscribe({
        next: (response) => {
          this.isLoading = false;
          alert('¡Cuenta creada con éxito! Ahora puedes ingresar.');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Error en registro', error);
          this.errorMessage = 'El correo ya existe o hubo un error en el servidor.';
          this.isLoading = false;
        }
      });
    }
  }
}
