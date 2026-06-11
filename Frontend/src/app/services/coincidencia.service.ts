import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { CoincidenciaDetalle } from '../models/coincidencia.model'; // Ajusta la ruta según tu estructura

@Injectable({
  providedIn: 'root'
})
export class CoincidenciaService {
  // Apunta al microservicio de coincidencias (puerto 8083)
  private baseUrl = `${environment.apiCoincidenciasUrl}/coincidencias`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene la lista de coincidencias enriquecidas (BFF) para un reporte específico.
   */
  obtenerCoincidenciasPorReporte(reporteId: number): Observable<CoincidenciaDetalle[]> {
    return this.http.get<CoincidenciaDetalle[]>(`${this.baseUrl}/reporte/${reporteId}`);
  }

  /**
   * Actualiza el estado de la coincidencia (Ej. de PENDIENTE a EXITOSA o DESCARTADA).
   */
  actualizarEstadoCoincidencia(id: number, nuevoEstado: string): Observable<void> {
    const params = new HttpParams().set('nuevoEstado', nuevoEstado);
    return this.http.patch<void>(`${this.baseUrl}/${id}/estado`, null, { params });
  }
}