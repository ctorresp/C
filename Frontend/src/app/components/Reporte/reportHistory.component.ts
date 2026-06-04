import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-report-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="main-wrapper">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-5">
            
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h1 class="fw-bold mb-0" style="color: var(--pet-brown);">Mis Reportes</h1>
              <button class="btn btn-pet" routerLink="/report">Generar nuevo reporte</button>
            </div>
            
            <p class="text-muted mb-4">Aquí puedes revisar y actualizar tus reportes de mascotas extraviadas.</p>
            
            <div class="mt-4">
              <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                <h5 class="fw-bold">Reporte 1</h5>
                <p class="small text-muted">Descripción breve del reporte de mascota extraviada.</p>
                <button class="btn btn-outline-secondary w-75 rounded-pill">Editar</button>
                <button class="btn btn-danger w-75 rounded-pill mt-2">Eliminar</button>
                </div>
                
                <div class="p-4 rounded-4 mb-3 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                <h5 class="fw-bold">Reporte 2</h5>
                <p class="small text-muted">Descripción breve del reporte de mascota extraviada.</p>
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

export class ReportHistoryComponent {
  // pagina para ver el historial de reportes realizados por el usuario, con opciones para editar o eliminar cada reporte
}