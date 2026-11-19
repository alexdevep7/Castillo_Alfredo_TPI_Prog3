import { getSession } from "./auth";
import { Rol } from "../types/Rol";

export const protectRoute = (): void => {
  const path = window.location.pathname;
  const session = getSession();

  const loginUrl = "/src/pages/auth/login/login.html";
  const adminUrl = "/src/pages/admin/home/home.html";
  const clientUrl = "/src/pages/client/home/home.html";
  const isRoot = path === "/" || path.endsWith("/index.html");

  // 1. Si NO hay sesión y entra a raíz, admin o client -> ir al login
  if (
    !session &&
    (isRoot || path.includes("/admin") || path.includes("/client"))
  ) {
    window.location.href = loginUrl;
    return;
  }

  // 2. Si HAY sesión
  if (session) {
    // Si entra a raíz, login o registro -> redirigir según su rol
    if (
      isRoot ||
      path.includes("/auth/login") ||
      path.includes("/auth/registro")
    ) {
      window.location.href = session.rol === Rol.ADMIN ? adminUrl : clientUrl;
      return;
    }

    // Evitar que un cliente entre a la zona de administración
    if (path.includes("/admin") && session.rol !== Rol.ADMIN) {
      window.location.href = clientUrl;
      return;
    }
  }
};
