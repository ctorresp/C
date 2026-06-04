import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-mascotas',
  standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5">

            <div class="d-flex justify-content-between align-items-center mb-3">
              <h1 class="fw-bold" style="color: var(--pet-brown);">Mis Mascotas Encontradas</h1>
              <button class="btn btn-pet" routerLink="/mascotas">Registrar nueva mascota</button>
            </div>

            <p class="text-muted">Aquí puedes gestionar las mascotas que has encontrado y reportado.</p>


            <div class="mt-4">
              <!-- Aquí puedes agregar una lista de mascotas encontradas con opciones para editar o eliminar -->
                <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                <h5 class="fw-bold">Mascota 1</h5>
                <p class="small text-muted">Descripción breve de la mascota encontrada.</p>
                <button class="btn btn-outline-secondary w-75 rounded-pill">Editar</button>
                <button class="btn btn-danger w-75 rounded-pill mt-2">Eliminar</button>
                </div>
                <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                <h5 class="fw-bold">Mascota 2</h5>
                <p class="small text-muted">Descripción breve de la mascota encontrada.</p>
                <button class="btn btn-outline-secondary w-75 rounded-pill">Editar</button>
                <button class="btn btn-danger w-75 rounded-pill mt-2">Eliminar</button>
                </div>
            </div>
          </div>
        </div>
        </div>
    </div>
  `
})

export class MascotasComponent {
  // pagina para ver el historial de mascotas encontradas por el usuario, con opciones para editar o eliminar cada mascota
}