import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-principal',
  standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5">
            <h1 class="fw-bold" style="color: var(--pet-brown);">Bienvenido a tu panel principal</h1>
            <p class="text-muted">Aquí podrás gestionar tus reportes y mascotas encontradas.</p>
            <div class="mt-4">
              <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                <h5 class="fw-bold">Mis Reportes</h5>
                <p class="small text-muted">Revisa y actualiza tus reportes de mascotas extraviadas.</p>
                <button class="btn btn-pet w-75" routerLink="/reportHistory">Ver mis reportes</button>
              </div>
              <div class="p-4 rounded-4 text-center" style="border: 1px solid #eee;">
                <h5 class="fw-bold">Mis Mascotas Encontradas</h5>
                <p class="small text-muted">Gestiona las mascotas que has encontrado y reportado.</p>
                <button class="btn btn-outline-secondary w-75 rounded-pill" routerLink="/mascotas">Ver mis mascotas</button>
              </div>
            </div>
          </div>
        </div>
        </div>
    </div>
  `
})

export class PrincipalComponent {

}