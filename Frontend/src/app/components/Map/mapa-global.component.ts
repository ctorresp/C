import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { GeolocalizacionService } from '../../services/geolocalizacion.service';

@Component({
  selector: 'app-mapa-global',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="map-modal-overlay" *ngIf="isOpen" (click)="cerrarMapa()">
      
      <div class="map-modal-content glass-card rounded-4 shadow-lg" (click)="$event.stopPropagation()">
        
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h4 class="fw-bold m-0" style="color: var(--pet-brown);">📍 Radar de Mascotas</h4>
          <button class="btn-close" (click)="cerrarMapa()"></button>
        </div>
        
        <div id="global-map" class="rounded-3 shadow-sm"></div>
      </div>

    </div>
  `,
  styles: [`
    .map-modal-overlay {
      position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
      background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(4px);
      z-index: 9999; display: flex; justify-content: center; align-items: center;
    }
    .map-modal-content {
      background: white; padding: 20px; width: 90%; max-width: 1000px; height: 80vh;
      display: flex; flex-direction: column;
    }
    #global-map { flex-grow: 1; width: 100%; border: 2px solid var(--pet-beige); }
    .glass-card {
      backdrop-filter: blur(10px);
      background: rgba(255, 255, 255, 0.95) !important;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
  `]
})
export class MapaGlobalComponent implements OnChanges {
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();

  private map: L.Map | undefined;

  constructor(private geoService: GeolocalizacionService) {
    this.configurarIconosLeaflet();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      // Damos tiempo a Angular para crear el div y luego inicializamos/refrescamos
      setTimeout(() => {
        this.inicializarMapa();
      }, 200);
    } else if (!this.isOpen && this.map) {
      this.map.remove(); 
      this.map = undefined;
    }
  }

  cerrarMapa(): void {
    this.close.emit();
  }

  private inicializarMapa(): void {
    if (this.map) {
        this.map.invalidateSize(); // Soluciona errores visuales en modales
        return;
    }

    this.map = L.map('global-map').setView([-33.4489, -70.6693], 12); 

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.cargarMarcadores();
    
    // Forzamos el recálculo de tamaño una vez más después de cargar tiles
    setTimeout(() => {
        this.map?.invalidateSize();
    }, 300);
  }

  private cargarMarcadores(): void {
    this.geoService.obtenerTodosLosMarcadores().subscribe({
      next: (marcadores) => {
        marcadores.forEach(m => {
          const colorIcon = m.tipoMarcador === 'PERDIDA' ? 'red' : 'green';
          L.marker([m.latitudOfuscada, m.longitudOfuscada])
            .bindPopup(`<b>Reporte #${m.reporteId}</b><br>Estado: ${m.tipoMarcador}`)
            .addTo(this.map!);
        });
      },
      error: (err) => console.error('Error al cargar marcadores', err)
    });
  }

  private configurarIconosLeaflet(): void {
    const iconDefault = L.icon({
          iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
          iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
          shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
          iconSize: [25, 41], 
          iconAnchor: [12, 41], 
          popupAnchor: [1, -34], 
          shadowSize: [41, 41]
        });
    L.Marker.prototype.options.icon = iconDefault;
  }
}