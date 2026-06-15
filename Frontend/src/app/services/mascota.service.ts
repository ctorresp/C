import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MascotaService {
  private baseUrl = environment.apiMascotasUrl;

  constructor(private http: HttpClient) {}

  registrarMascota(mascotaData: any): Observable<any> {
    // ¡Mira qué limpio! El interceptor se encarga del token automáticamente.
    return this.http.post(`${this.baseUrl}/mascotas`, mascotaData);
  }

  subirImagen(archivo: File, mascotaId: number): Observable<any> {
    const formData = new FormData();
    formData.append('archivo', archivo);
    formData.append('mascotaId', mascotaId.toString());
    formData.append('contexto', 'PERFIL');
    formData.append('esPrincipal', 'true');

    // Cero configuración de headers aquí también.
    return this.http.post(`${this.baseUrl}/imagenes/subir`, formData);
  }

  obtenerMascotas(): Observable<any> {
    return this.http.get(`${this.baseUrl}/mascotas`);
  }

  obtenerMascotaPorId(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/mascotas/${id}`);
  }

  actualizarMascota(id: number, mascota: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/mascotas/${id}`, mascota);
  }

  eliminarMascota(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/mascotas/${id}`);
  }
}