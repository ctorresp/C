import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { environment } from '../../environments/environment.development';

describe('AuthService', () => {
  let service: AuthService;
  let httpClientMock: any;

  beforeEach(() => {
    // Simulamos el HttpClient de Angular
    httpClientMock = {
      post: vi.fn(),
      get: vi.fn(),
      delete: vi.fn()
    };

    // Instanciamos el servicio inyectando nuestro mock
    service = new AuthService(httpClientMock);
  });

  it('debería enviar las credenciales al endpoint de login', () => {
    const credenciales = { email: 'test@test.com', password: '123' };
    httpClientMock.post.mockReturnValue(of({ token: 'fake-token' }));

    service.login(credenciales).subscribe();

    expect(httpClientMock.post).toHaveBeenCalledWith(
      `${environment.apiUsuariosUrl}/usuarios/login`, 
      credenciales
    );
  });

  it('debería transformar el payload de registro y añadir el rol CIUDADANO', () => {
    const userData = { name: 'Ale', email: 'ale@test.com', password: '123' };
    httpClientMock.post.mockReturnValue(of({ success: true }));

    service.register(userData).subscribe();

    // Verificamos que el servicio armó correctamente el objeto antes de enviarlo
    const expectedPayload = {
      nombre: 'Ale',
      email: 'ale@test.com',
      password: '123',
      rol: 'CIUDADANO' // <-- Lógica clave
    };

    expect(httpClientMock.post).toHaveBeenCalledWith(
      `${environment.apiUsuariosUrl}/usuarios/register`, 
      expectedPayload
    );
  });

  it('debería enviar la petición DELETE para eliminar cuenta', () => {
    const fakeUuid = 'abc-123';
    httpClientMock.delete.mockReturnValue(of({ success: true }));

    service.eliminarCuenta(fakeUuid).subscribe();

    expect(httpClientMock.delete).toHaveBeenCalledWith(
      `${environment.apiUsuariosUrl}/usuarios/${fakeUuid}`
    );
  });
});