import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
// Importamos withInterceptors
import { provideHttpClient, withInterceptors } from '@angular/common/http'; 

import { routes } from './app.routes';
// Importamos nuestro nuevo interceptor (ajusta la ruta según dónde lo guardaste)
import { authInterceptor } from './services/auth.interceptor'; 

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Aquí le decimos a Angular que use HttpClient y que pase por nuestro interceptor
    provideHttpClient(withInterceptors([authInterceptor])) 
  ]
};