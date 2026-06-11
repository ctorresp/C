// coincidencia.model.ts

export interface Ubicacion {
  latitudOfuscada: number;
  longitudOfuscada: number;
  // Puedes agregar más campos aquí si tu backend los devuelve (ej. direccion, ciudad)
}

export interface Mascota {
  id: number;
  nombre?: string;
  especie: string;
  raza: string;
  // Añade otros campos que tu mascota tenga (ej. color, tamaño, urlFoto)
}

export interface CoincidenciaDetalle {
  id: number;
  porcentajeSimilitud: number;
  estado: string; // 'PENDIENTE', 'REVISADA', 'DESCARTADA', 'EXITOSA'
  fechaCreacion: string;
  
  // Datos del Reporte Perdido
  reportePerdidoId: number;
  mascotaPerdida: Mascota;
  ubicacionPerdido: Ubicacion;

  // Datos del Reporte Encontrado
  reporteEncontradoId: number;
  mascotaEncontrada: Mascota;
  ubicacionEncontrado: Ubicacion;
}