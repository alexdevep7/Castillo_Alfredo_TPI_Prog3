import { Rol } from "./Rol";

export interface IUser {
  id: number;
  nombre: string;
  apellido: string;
  celular?: string;
  eliminado: boolean;
  mail: string;
  password?: string;
  rol: Rol;
}
