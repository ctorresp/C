import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('Frontend');
  
  isMenuOpen = false;
  mostrarMenu = false; // Empezamos en falso por seguridad
  isLoggedIn = false;  // Nueva variable para saber el estado real

  constructor(private router: Router) {
    // Escuchamos los cambios de ruta
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      
      // 1. Verificamos si realmente hay sesión activa
      this.verificarEstadoLogin();
      
      // 2. Definimos las rutas donde NO queremos que se vea el menú
      const rutasPublicas = ['/login', '/recuperar', '/register', '/'];
      const esRutaPublica = rutasPublicas.includes(event.urlAfterRedirects);

      // 3. El menú SOLO se muestra si está logueado Y no está en la pantalla de login/registro
      this.mostrarMenu = this.isLoggedIn && !esRutaPublica;
      
      // Extra de seguridad: Si intenta ir a una ruta privada sin estar logueado, lo pateamos al login
      if (!this.isLoggedIn && !esRutaPublica) {
        this.router.navigate(['/login']);
      }
    });
  }

  ngOnInit() {
    this.verificarEstadoLogin();
  }

  // Verifica si el token existe en el almacenamiento local
  verificarEstadoLogin() {
    const token = localStorage.getItem('token');
    this.isLoggedIn = !!token; // Convierte el token a booleano (true si existe, false si es null)
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  logout() {
    this.closeMenu();
    localStorage.removeItem('token'); // Destruimos la llave de acceso
    this.isLoggedIn = false;
    this.mostrarMenu = false;
    
    // Lo redirigimos a la pantalla de inicio de sesión de forma segura
    this.router.navigate(['/login']);
  }
}