import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = environment.apiMascotasUrl;

  constructor(private http: HttpClient) {}

  // Crear un nuevo reporte (ReporteController -> @PostMapping)
  crearReporte(reporteData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/reportes`, reporteData);
  }

  // Obtener el historial de reportes (ReporteController -> @GetMapping)
  obtenerReportes(): Observable<any> {
    return this.http.get(`${this.baseUrl}/reportes`);
  }

  // <-- NUEVO: Eliminar un reporte por ID (ReporteController -> @DeleteMapping)
  eliminarReporte(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/reportes/${id}`);
  }
}

export interface ReporteResponse {
  id: number;
  mascotaId: number;
  usuarioUuid: string;
  tipoReporte: string;
  fechaSuceso: string;
  estado: string;
}