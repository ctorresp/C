import '@angular/compiler';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { LoginComponent } from './login.component';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(() => {
    // 1. Mockeamos las dependencias (Servicios y Router)
    authServiceMock = {
      login: vi.fn()
    };
    routerMock = {
      navigate: vi.fn()
    };

    // 2. Instanciamos el componente inyectando los mocks manualmente
    component = new LoginComponent(authServiceMock, routerMock);
  });

  it('debería inicializarse con credenciales vacías y sin errores', () => {
    expect(component.credentials).toEqual({ email: '', password: '' });
    expect(component.errorMessage).toBe('');
    expect(component.isLoading).toBe(false);
  });

  it('NO debería llamar al authService si el formulario es inválido', () => {
    const formMock = { valid: false }; // Simulamos un formulario inválido
    
    component.onSubmit(formMock);
    
    expect(authServiceMock.login).not.toHaveBeenCalled();
    expect(component.isLoading).toBe(false);
  });

  it('debería guardar el token y navegar a /principal en un login exitoso', () => {
    const formMock = { valid: true };
    const mockResponse = { token: 'fake-jwt-token', usuario: { uuid: '123-abc' } };
    
    // Configuramos el mock para que devuelva una respuesta exitosa
    authServiceMock.login.mockReturnValue(of(mockResponse));
    
    // Espiamos el localStorage
    const setItemSpy = vi.spyOn(Storage.prototype, 'setItem');
    // Espiamos el window.alert para que no detenga el test
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    component.onSubmit(formMock);

    expect(authServiceMock.login).toHaveBeenCalledWith(component.credentials);
    expect(setItemSpy).toHaveBeenCalledWith('token', 'fake-jwt-token');
    expect(setItemSpy).toHaveBeenCalledWith('uuid', '123-abc');
    expect(component.isLoading).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/principal']);
  });

  it('debería mostrar un mensaje de error si el login falla', () => {
    const formMock = { valid: true };
    
    // Configuramos el mock para que devuelva un error
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Error de red')));
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    component.onSubmit(formMock);

    expect(component.errorMessage).toBe('Credenciales incorrectas o error de conexión.');
    expect(component.isLoading).toBe(false);
    
    consoleSpy.mockRestore(); // Limpiamos el espía
  });
});