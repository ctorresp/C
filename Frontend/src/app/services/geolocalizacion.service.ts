import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';

export interface MarcadorRequest {
  reporteId: number;
  tipoMarcador: string;
  latitud: number;
  longitud: number;
}

export interface MarcadorResponse {
  id: number;
  reporteId: number;
  tipoMarcador: string;
  latitudOfuscada: number;  // Coincide con tu DTO ofuscado
  longitudOfuscada: number;
  fechaHoraRegistro: string;
}

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacionService {
  // Asegúrate de que el puerto coincida con tu API Gateway o Microservicio
  private baseUrl = `${environment.apiGeolocalizacionUrl}/api/geolocalizacion/marcadores`;
  
  constructor(private http: HttpClient) {}

  obtenerTodosLosMarcadores(): Observable<MarcadorResponse[]> {
    return this.http.get<MarcadorResponse[]>(this.baseUrl);
  }

  crearMarcador(marcador: MarcadorRequest): Observable<any> {
    return this.http.post(this.baseUrl, marcador); 
  }
}