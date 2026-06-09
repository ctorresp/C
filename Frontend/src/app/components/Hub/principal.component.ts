import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core'; // <-- CAMBIO: Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReporteService } from '../../services/reporte.service';
import { MascotaService } from '../../services/mascota.service';

interface ReporteMascota {
  id: number;
  nombre: string;
  tipo: string;
  estado: string;
  zona: string;
  fecha: string;
  foto: string;
}

@Component({
  selector: 'app-principal',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="main-wrapper">
      <div class="container py-4">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 glass-card shadow-lg bg-white p-4 p-md-5 rounded-4">
            
            <h1 class="fw-bold text-center" style="color: var(--pet-brown);">Bienvenido a tu panel principal</h1>
            <p class="text-muted text-center">Aquí podrás gestionar tus reportes y mascotas encontradas.</p>
            
            <div class="mt-4 row row-cols-1 row-cols-md-2 g-3">
              <div class="col">
                <div class="p-4 rounded-4 h-100 text-center" style="background-color: #fffaf0; border: 1px solid var(--pet-beige);">
                  <h5 class="fw-bold">Mis Reportes</h5>
                  <p class="small text-muted mb-3">Revisa y actualiza tus reportes de mascotas extraviadas.</p>
                  <button class="btn btn-pet w-100 rounded-pill fw-semibold" routerLink="/reportHistory">Ver mis reportes</button>
                </div>
              </div>
              <div class="col">
                <div class="p-4 rounded-4 h-100 text-center" style="border: 1px solid #eee;">
                  <h5 class="fw-bold">Mis Mascotas</h5>
                  <p class="small text-muted mb-3">Gestiona las mascotas que has registrado en el sistema.</p>
                  <button class="btn btn-outline-secondary w-100 rounded-pill fw-semibold" routerLink="/mascotasHistory">Ver mis mascotas</button>
                </div>
              </div>
            </div>

            <div class="dashboard-section mt-5 pt-3">
              <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
                <div>
                  <h3 class="fw-bold mb-1" style="color: var(--pet-brown);">🚨 Últimos reportes de la comunidad</h3>
                  <p class="text-muted m-0 small">Mascotas perdidas o encontradas recientemente.</p>
                </div>
                <div class="carousel-controls d-none d-md-flex gap-2" *ngIf="reportes.length > 0">
                  <button class="btn btn-outline-brown rounded-circle" (click)="prevSlide(); stopAutoPlay()">‹</button>
                  <button class="btn btn-outline-brown rounded-circle" (click)="nextSlide(); stopAutoPlay()">›</button>
                </div>
              </div>

              <div *ngIf="isLoading" class="text-center py-5">
                <div class="spinner-border text-warning" role="status"></div>
                <p class="text-muted mt-2 small">Cargando reportes de la comunidad...</p>
              </div>

              <div *ngIf="!isLoading && reportes.length === 0" class="text-center py-5 bg-light rounded-4 border">
                <h5 class="fw-bold text-secondary mb-1">No hay reportes recientes</h5>
                <p class="small text-muted m-0">Actualmente no hay mascotas extraviadas en la plataforma.</p>
              </div>

              <div class="carousel-window" (mouseenter)="stopAutoPlay()" (mouseleave)="startAutoPlay()" *ngIf="!isLoading && reportes.length > 0">
                <div class="carousel-track" [style.transform]="getTransform()">
                  <div class="carousel-card-wrapper" *ngFor="let reporte of reportes">
                    <div class="card h-100 pet-card shadow-sm">
                      <div class="position-relative overflow-hidden card-img-container">
                        <img [src]="reporte.foto" class="card-img-top pet-image" [alt]="reporte.nombre">
                        <span class="badge position-absolute top-0 end-0 m-3 px-3 py-2 rounded-pill shadow-sm"
                              [ngClass]="reporte.estado === 'PERDIDA' ? 'bg-danger' : 'bg-success'">
                          {{ reporte.estado === 'PERDIDA' ? 'PERDIDO' : 'ENCONTRADO' }}
                        </span>
                      </div>
                      <div class="card-body d-flex flex-column justify-content-between">
                        <div>
                          <div class="d-flex justify-content-between align-items-center mb-2">
                            <h6 class="card-title fw-bold m-0 text-truncate" style="color: #555; max-width: 70%;">{{ reporte.nombre }}</h6>
                            <span class="badge bg-light text-dark border small">{{ reporte.tipo }}</span>
                          </div>
                          <p class="card-text text-secondary mb-1 small">📍 Comunidad Sanos y Salvos</p>
                          <p class="card-text text-muted" style="font-size: 11px;">🕒 {{ reporte.fecha | date:'dd/MM/yyyy, h:mm a' }}</p>
                        </div>
                        <button class="btn btn-pet-sm w-100 mt-3 rounded-pill" [routerLink]="['/reportes', reporte.id]">
                          Ver detalles 🐾
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="d-flex justify-content-center gap-4 d-md-none mt-4" *ngIf="!isLoading && reportes.length > 0">
                <button class="btn btn-brown px-4 py-2 rounded-pill shadow-sm" (click)="prevSlide()">Anterior</button>
                <button class="btn btn-brown px-4 py-2 rounded-pill shadow-sm" (click)="nextSlide()">Siguiente</button>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .carousel-window { overflow: hidden; width: 100%; padding: 10px 0; }
    .carousel-track { display: flex; transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1); width: 100%; }
    .carousel-card-wrapper { flex: 0 0 100%; padding: 0 12px; box-sizing: border-box; }
    @media (min-width: 768px) { .carousel-card-wrapper { flex: 0 0 50%; } }
    @media (min-width: 992px) { .carousel-card-wrapper { flex: 0 0 33.333%; } }
    .pet-card { border-radius: 20px !important; background-color: #ffffff; border: 1px solid #f8f9fa; transition: transform 0.3s ease, box-shadow 0.3s ease; }
    .pet-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(139, 90, 43, 0.12) !important; }
    .card-img-container { border-top-left-radius: 20px; border-top-right-radius: 20px; height: 170px; }
    .pet-image { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
    .pet-card:hover .pet-image { transform: scale(1.05); }
    .btn-outline-brown { color: #8B5A2B; border-color: #8B5A2B; width: 40px; height: 40px; font-size: 20px; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
    .btn-outline-brown:hover { background-color: #8B5A2B; color: white; }
    .btn-brown { background-color: #8B5A2B; color: white; border: none; }
    .btn-pet-sm { background-color: #fffaf0; color: #8B5A2B; border: 1px solid var(--pet-beige, #8B5A2B); font-weight: 600; font-size: 13px; transition: all 0.2s; }
    .btn-pet-sm:hover { background-color: #8B5A2B; color: white; }
  `]
})
export class PrincipalComponent implements OnInit, OnDestroy {
  reportes: ReporteMascota[] = [];
  isLoading = true;
  currentIndex = 0;
  autoPlayInterval: any;

  // <-- CAMBIO: Inyectamos ChangeDetectorRef en el constructor
  constructor(
    private reporteService: ReporteService,
    private mascotaService: MascotaService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() { this.cargarDatosReales(); }
  ngOnDestroy() { this.stopAutoPlay(); }

  cargarDatosReales() {
    this.isLoading = true;
    
    this.mascotaService.obtenerMascotas().subscribe({
      next: (mascotas) => {
        this.reporteService.obtenerReportes().subscribe({
          next: (reportesDB) => {
            this.reportes = reportesDB.map((rep: any) => {
              const mascota = mascotas.find((m: any) => m.id === rep.mascotaId);
              return {
                id: rep.id,
                nombre: mascota ? mascota.nombre : 'Mascota Desconocida',
                tipo: mascota ? mascota.especie : 'No especificado',
                estado: rep.estado, 
                zona: 'Zona no registrada',
                fecha: rep.fechaSuceso,
                foto: mascota?.fotoUrl || 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=500'
              };
            });

            this.isLoading = false;
            this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado tras cruzar reportes y mascotas exitosamente

            if (this.reportes.length > 1) {
              this.startAutoPlay();
            }
          },
          error: (err) => {
            console.error('Error al cargar reportes:', err);
            this.isLoading = false;
            this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado en error interno
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar mascotas:', err);
        this.isLoading = false;
        this.cdr.detectChanges(); // <-- CAMBIO: Forzar renderizado en error externo
      }
    });
  }

  startAutoPlay() { this.autoPlayInterval = setInterval(() => { this.nextSlide(); }, 5000); }
  stopAutoPlay() { if (this.autoPlayInterval) clearInterval(this.autoPlayInterval); }
  nextSlide() {
    if (this.reportes.length === 0) return;
    const maxIndex = this.getMaxIndex();
    this.currentIndex = this.currentIndex < maxIndex ? this.currentIndex + 1 : 0;
    this.cdr.detectChanges(); // <-- Opcional: Asegura el movimiento suave en eventos manuales
  }
  prevSlide() {
    if (this.reportes.length === 0) return;
    this.currentIndex = this.currentIndex > 0 ? this.currentIndex - 1 : this.getMaxIndex();
    this.cdr.detectChanges();
  }
  getMaxIndex(): number {
    const width = window.innerWidth;
    if (width >= 992) return Math.max(0, this.reportes.length - 3); 
    if (width >= 768) return Math.max(0, this.reportes.length - 2); 
    return Math.max(0, this.reportes.length - 1); 
  }
  getTransform() {
    const width = window.innerWidth;
    let percentage = 100;
    if (width >= 992) percentage = 33.333;
    else if (width >= 768) percentage = 50;
    return `translateX(-${this.currentIndex * percentage}%)`;
  }
}