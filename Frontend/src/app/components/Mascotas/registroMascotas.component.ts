import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- IMPORTANTE PARA LOS FORMULARIOS
import { MascotaService } from '../../services/mascota.service'; // Ajusta la ruta según tu proyecto

@Component({
  selector: 'app-registro-mascotas',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="main-wrapper">
      <div class="container py-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-8 glass-card shadow-lg bg-white p-5 rounded-4">
            <h2 class="fw-bold text-center mb-2" style="color: var(--pet-brown);">Registrar Mascota</h2>
            <p class="text-muted text-center mb-4">Ingresa los datos del peludito para añadirlo al sistema.</p>
            
            <form (ngSubmit)="onSubmit()">
              
              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Nombre</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.nombre" name="nombre" required placeholder="Ej. Firulais" />
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Especie</label>
                  <select class="form-select rounded-pill" [(ngModel)]="mascota.especie" name="especie" required>
                    <option value="" disabled>Selecciona...</option>
                    <option value="Perro">Perro</option>
                    <option value="Gato">Gato</option>
                    <option value="Otro">Otro</option>
                  </select>
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Raza</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.raza" name="raza" required placeholder="Ej. Mestizo" />
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Edad (Años)</label>
                  <input type="number" class="form-control rounded-pill" [(ngModel)]="mascota.edad" name="edad" required min="0" />
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Sexo</label>
                  <select class="form-select rounded-pill" [(ngModel)]="mascota.sexo" name="sexo" required>
                    <option value="" disabled>Selecciona...</option>
                    <option value="Macho">Macho</option>
                    <option value="Hembra">Hembra</option>
                  </select>
                </div>
                <div class="col-md-6 mt-3 mt-md-0">
                  <label class="form-label fw-semibold">Color principal</label>
                  <input type="text" class="form-control rounded-pill" [(ngModel)]="mascota.color" name="color" required placeholder="Ej. Negro con blanco" />
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label fw-semibold">Descripción / Detalles particulares</label>
                <textarea class="form-control rounded-4" rows="3" [(ngModel)]="mascota.descripcion" name="descripcion" required placeholder="Tiene una mancha en el ojo derecho..."></textarea>
              </div>

              <div class="mb-4">
                <label class="form-label fw-semibold">Foto de la mascota</label>
                <input type="file" class="form-control rounded-pill" (change)="onFileSelected($event)" accept="image/*" required />
              </div>

              <button type="submit" class="btn btn-pet w-100 rounded-pill py-2 fw-bold" [disabled]="isLoading">
                {{ isLoading ? 'Guardando...' : 'Registrar Mascota 🐾' }}
              </button>

            </form>
          </div>
        </div>
      </div>
    </div>
  `
})
export class RegistroMascotasComponent {
  
  // Objeto que coincide con lo que pide tu Java / DTO
  mascota = {
    nombre: '',
    especie: '',
    raza: '',
    edad: 0,
    sexo: '',
    color: '',
    descripcion: '',
    estado: 'ENCONTRADA' // Basado en tu Enum Java
  };

  selectedFile: File | null = null;
  isLoading = false;

  constructor(private mascotaService: MascotaService, private router: Router) {}

  // Captura el archivo cuando el usuario selecciona una foto
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (!this.selectedFile) {
      alert('Por favor, selecciona una foto para la mascota.');
      return;
    }

    this.isLoading = true;

    // 1. Primero creamos la mascota en la BD
    this.mascotaService.registrarMascota(this.mascota).subscribe({
      next: (response) => {
        const mascotaIdGenerada = response.id; // Asumiendo que tu DTO de respuesta devuelve el ID
        
        // 2. Si se creó bien, subimos la foto asociándola a ese ID
        this.mascotaService.subirImagen(this.selectedFile!, mascotaIdGenerada).subscribe({
          next: () => {
            this.isLoading = false;
            alert('¡Mascota registrada con éxito!');
            this.router.navigate(['/mascotasHistory']); // Redirigimos a la lista
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error al subir la imagen', err);
            alert('Mascota creada, pero hubo un error al subir la foto.');
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error al registrar mascota', err);
        alert('Hubo un error al registrar los datos.');
      }
    });
  }
}