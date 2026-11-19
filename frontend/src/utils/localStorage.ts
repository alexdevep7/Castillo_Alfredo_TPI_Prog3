import type { IUser } from "../types/IUser";

// Carga los 4 JSON en localStorage si aún no existen
export const initData = async (): Promise<void> => {
  const resources = [
    { key: "categorias", url: "/data/categorias.json" },
    { key: "productos", url: "/data/productos.json" },
    { key: "usuarios", url: "/data/usuarios.json" },
    { key: "pedidos", url: "/data/pedidos.json" },
  ];

  for (const item of resources) {
    if (!localStorage.getItem(item.key)) {
      const response = await fetch(item.url);
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem(item.key, JSON.stringify(data));
      }
    }
  }
};

export const saveUser = (user: IUser): void => {
  // Excluimos password antes de guardar la sesión
  const { password, ...safeUser } = user;
  localStorage.setItem("userData", JSON.stringify(safeUser));
};

export const getUser = (): IUser | null => {
  const data = localStorage.getItem("userData");
  return data ? JSON.parse(data) : null;
};

export const removeUser = (): void => {
  localStorage.removeItem("userData");
};
