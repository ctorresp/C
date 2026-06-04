import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-registro-mascotas',
  standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5">
            <h1 class="fw-bold" style="color: var(--pet-brown);">Registrar Nueva Mascota Encontrada</h1>
            <p class="text-muted">Aquí puedes registrar una nueva mascota que has encontrado.</p>
            <div class="mt-4">
              <!-- Aquí puedes agregar un formulario para registrar la mascota encontrada -->
                <form>
                  <div class="mb-3">
                    <label class="form-label">Nombre de la mascota</label>
                    <input type="text" class="form-control rounded-pill" placeholder="Nombre de la mascota encontrada" required />
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Descripción</label>
                    <textarea class="form-control rounded-pill" rows="3" placeholder="Descripción de la mascota encontrada" required></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Fecha de encuentro</label>
                    <input type="date" class="form-control rounded-pill" required />
                  </div>
                  <button type="submit" class="btn btn-pet w-100">Registrar Mascota</button>
                </form>
            </div>
          </div>
        </div>
        </div>
    </div>
  `
})

export class RegistroMascotasComponent {
  // Aquí puedes agregar la lógica para registrar nuevas mascotas
}