import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms'; // IMPORTANTE para usar ngModel
import { AuthService } from '../../services/auth.service'; // Asegúrate de que la ruta sea correcta

@Component({
  selector: 'app-recuperar',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="main-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-6 glass-card shadow-lg bg-white p-5">
                    <h1 class="fw-bold" style="color: var(--pet-brown);">Recuperar Contraseña</h1>
                    <p class="text-muted">Ingresa tu correo electrónico para recibir instrucciones de recuperación de contraseña.</p>
                    
                    <div *ngIf="successMessage" class="alert alert-success text-center small">
                      {{ successMessage }}
                    </div>
                    <div *ngIf="errorMessage" class="alert alert-danger text-center small">
                      {{ errorMessage }}
                    </div>

                    <form #recuperarForm="ngForm" (ngSubmit)="onSubmit(recuperarForm)">
                        <div class="mb-3">
                            <label class="form-label">Correo electrónico</label>
                            <input type="email" class="form-control rounded-pill" 
                                   [(ngModel)]="email" name="email" #emailInput="ngModel" 
                                   placeholder="correo@ejemplo.com" required email />
                            <div *ngIf="emailInput.invalid && emailInput.touched" class="text-danger small mt-1">
                                Ingresa un correo electrónico válido.
                            </div>
                        </div>
                        <button type="submit" class="btn btn-pet w-100" [disabled]="recuperarForm.invalid || isLoading">
                            {{ isLoading ? 'Enviando...' : 'Recuperar Contraseña' }}
                        </button>
                    </form>
                    
                    <div class="text-center mt-3">
                      <a routerLink="/login" style="color: var(--pet-brown);">Volver al ingreso</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
  `
})

export class RecuperarComponent {
  email: string = '';
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  onSubmit(form: any) {
    if (form.valid) {
      this.isLoading = true;
      this.successMessage = '';
      this.errorMessage = '';

      this.authService.recuperarContrasena(this.email).subscribe({
        next: (response) => {
          this.isLoading = false;
          // Por seguridad, siempre decimos que se envió el correo, exista o no en la BD.
          this.successMessage = 'Si el correo está registrado, te hemos enviado un enlace para recuperar tu contraseña.';
          form.reset();
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error al solicitar recuperación', error);
          this.errorMessage = 'Hubo un error al procesar tu solicitud. Intenta más tarde.';
        }
      });
    }
  }
}