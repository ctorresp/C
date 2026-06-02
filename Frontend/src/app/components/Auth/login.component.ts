import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="main-wrapper animated-bg">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-md-8 col-lg-6 glass-card shadow-lg bg-white p-4 fade-in">
            <div class="text-center mb-4">
              <h2 class="fw-bold" style="color: var(--pet-brown);">Ingresar</h2>
              <p class="text-muted">Accede a tu cuenta para gestionar reportes y mascotas encontradas.</p>
            </div>
            <form #loginForm="ngForm" (ngSubmit)="onSubmit(loginForm)">
              <div class="mb-3">
                <label class="form-label">Correo electrónico</label>
                <input type="email" class="form-control rounded-pill" [(ngModel)]="credentials.email" name="email" #email="ngModel" placeholder="correo@ejemplo.com" required email />
                <div *ngIf="email.invalid && email.touched" class="text-danger small mt-1">Ingresa un correo electrónico válido.</div>
              </div>
              <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control rounded-pill" [(ngModel)]="credentials.password" name="password" #password="ngModel" placeholder="Tu contraseña" required />
              </div>
              <button type="submit" class="btn btn-pet w-100" [disabled]="loginForm.invalid">Ingresar</button>
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
    .animated-bg {
      animation: bgPulse 4s ease-in-out infinite;
    }
    @keyframes bgPulse {
      0%, 100% { background-color: #faf0e6; }
      50% { background-color: #f5f5dc; }
    }
    .fade-in {
      animation: fadeIn 1s ease-in;
    }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class LoginComponent {
  credentials = { email: '', password: '' };
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(form: any) {
    if (form.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.authService.login(this.credentials).subscribe({
        next: (response: any) => {
          if(response && response.token) {
            localStorage.setItem('token', response.token);
          }
          this.isLoading = false;
          alert('Ingreso exitoso');
          this.router.navigate(['/principal']); 
        },
        error: (error) => {
          console.error('Error de login', error);
          this.errorMessage = 'Credenciales incorrectas o error de conexión.';
          this.isLoading = false;
        }
      });
    }
  }
}
