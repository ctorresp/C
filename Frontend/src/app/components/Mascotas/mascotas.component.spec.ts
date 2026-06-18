import '@angular/compiler'; // <-- ¡Imprescindible para que lea el decorador @Component!
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { MascotasComponent } from './mascotas.component';
import { of } from 'rxjs';

describe('MascotasComponent', () => {
  let component: MascotasComponent;
  let mascotaServiceMock: any;
  let routerMock: any;
  let cdrMock: any;

  beforeEach(() => {
    mascotaServiceMock = {
      obtenerMascotas: vi.fn(),
      eliminarMascota: vi.fn()
    };
    
    routerMock = { navigate: vi.fn() };
    cdrMock = { detectChanges: vi.fn() };

    component = new MascotasComponent(mascotaServiceMock, cdrMock, routerMock);
  });

  afterEach(() => {
    vi.restoreAllMocks(); // Limpiamos los espías después de cada test
  });

  it('debería cargar y filtrar SOLO las mascotas del usuario logueado', () => {
    // 1. Simulamos que el usuario logueado tiene el UUID 'user-1'
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue('user-1');

    // 2. Simulamos la BD devolviendo mascotas de varios usuarios
    const mockMascotas = [
      { id: 1, nombre: 'Toby', usuarioUuid: 'user-1' },
      { id: 2, nombre: 'Rex', usuarioUuid: 'user-2' }, // No es mía
      { id: 3, nombre: 'Luna', usuarioUuid: 'user-1' }
    ];
    mascotaServiceMock.obtenerMascotas.mockReturnValue(of(mockMascotas));

    // 3. Ejecutamos el método que se llama en el ngOnInit
    component.cargarMascotas();

    // 4. Verificaciones
    expect(component.listaMascotas.length).toBe(2); // Solo debe tener a Toby y Luna
    expect(component.listaMascotas[0].nombre).toBe('Toby');
    expect(component.isLoading).toBe(false);
    expect(cdrMock.detectChanges).toHaveBeenCalled();
  });

  it('debería navegar a la página de edición al hacer clic en Editar', () => {
    component.onEditar(99);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/editar', 99]);
  });

  it('NO debería llamar al servicio de eliminar si el usuario cancela la alerta (confirm)', () => {
    // Simulamos que el usuario le dio a "Cancelar"
    vi.spyOn(window, 'confirm').mockReturnValue(false);

    component.onEliminar(1);

    expect(mascotaServiceMock.eliminarMascota).not.toHaveBeenCalled();
  });

  it('debería eliminar la mascota de la lista si el usuario acepta', () => {
    // Simulamos que el usuario le dio a "Aceptar"
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    vi.spyOn(window, 'alert').mockImplementation(() => {}); // Ocultamos el alert de éxito
    mascotaServiceMock.eliminarMascota.mockReturnValue(of({})); // Éxito en BD
    
    // Le ponemos una mascota inicial para ver si la borra
    component.listaMascotas = [{ id: 1, nombre: 'Toby', especie: 'Perro', raza: 'Pug', edad: 2, sexo: 'M', color: 'B', descripcion: '', estado: 'ENCONTRADA' }];

    component.onEliminar(1);

    expect(mascotaServiceMock.eliminarMascota).toHaveBeenCalledWith(1);
    expect(component.listaMascotas.length).toBe(0); // La lista debe quedar vacía
    expect(cdrMock.detectChanges).toHaveBeenCalled();
  });
});