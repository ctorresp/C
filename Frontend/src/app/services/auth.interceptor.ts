import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Obtenemos el token guardado (asegúrate de que en tu login lo estés guardando así)
  const token = localStorage.getItem('token');

  // 2. Si el token existe, clonamos la petición original para inyectarle el Header
  if (token) {
    const peticionClonada = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Mandamos la petición clonada (con el token) hacia el backend
    return next(peticionClonada);
  }

  // 3. Si no hay token (ej. el usuario no ha iniciado sesión), la petición sigue su curso normal
  return next(req);
};