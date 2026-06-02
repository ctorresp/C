import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white">
            <div class="row">
              <div class="col-lg-5 d-none d-lg-block p-0">
                <div style="background: linear-gradient(rgba(111, 78, 55, 0.4), rgba(61, 43, 31, 0.6)), url('https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=2069&auto=format&fit=crop'); height: 100%; background-size: cover; background-position: center;">
                </div>
              </div>
              <div class="col-12 col-lg-7 p-5">
                <div class="text-center mb-4">
                  <h1 class="fw-bold" style="color: var(--pet-brown);">Sanos y Salvos 🐾</h1>
                  <p class="text-muted">Reconectando mascotas con sus familias</p>
                </div>

                <div class="mt-4">
                  <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                    <h5 class="fw-bold">¿Ya eres parte?</h5>
                    <p class="small text-muted">Inicia sesión para gestionar tus reportes.</p>
                    <button class="btn btn-pet w-75" routerLink="/login">Ingresar</button>
                  </div>

                  <div class="p-4 rounded-4 text-center" style="border: 1px solid #eee;">
                    <h5 class="fw-bold">¿Nuevo aquí?</h5>
                    <p class="small text-muted">Únete para reportar una mascota extraviada.</p>
                    <button class="btn btn-outline-secondary w-75 rounded-pill" routerLink="/register">Registrarme</button>
                  </div>
                </div>

                <p class="text-center mt-4 small text-muted">
                  Al continuar, aceptas nuestra <a href="#" style="color: var(--pet-brown);">política de protección animal</a>.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class HomeComponent {}
