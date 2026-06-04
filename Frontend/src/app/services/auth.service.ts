import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/usuarios/login`, credentials);
  }

  register(userData: any): Observable<any> {
    const payload = {
      nombre: userData.name,
      email: userData.email,
      password: userData.password,
      rol: 'CIUDADANO'
    };
    return this.http.post(`${this.baseUrl}/usuarios/register`, payload);
  }

  recuperarContrasena(email: string): Observable<any> {
    // Se envía un objeto JSON con el email al endpoint del backend
    return this.http.post(`${this.baseUrl}/usuarios/recuperar-password`, { email: email });
  }
}