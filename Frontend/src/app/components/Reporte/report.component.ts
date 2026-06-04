import { Component } from '@angular/core';

@Component({
  selector: 'app-report',
  standalone: true,
  template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5">
            <h1 class="fw-bold" style="color: var(--pet-brown);">Generar Reporte</h1>
            <p class="text-muted">Aquí puedes generar un reporte de tu mascota extraviada.</p>
            <div class="mt-4">
              <!-- Aquí puedes agregar un formulario para generar el reporte -->
                <form>
                  <div class="mb-3">
                    <label class="form-label">Nombre de la mascota</label>
                    <input type="text" class="form-control rounded-pill" placeholder="Nombre de tu mascota" required />
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Descripción</label>
                    <textarea class="form-control rounded-pill" rows="3" placeholder="Descripción de tu mascota extraviada" required></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Fecha de extravío</label>
                    <input type="date" class="form-control rounded-pill" required />
                  </div>
                  <button type="submit" class="btn btn-pet w-100">Generar Reporte</button>
                </form>
            </div>
          </div>
        </div>
        </div>
    </div>
  `
})

export class ReportComponent {
  // Aquí puedes agregar la lógica para generar el reporte
}