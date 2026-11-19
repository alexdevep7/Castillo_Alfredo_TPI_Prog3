import { login } from "../../../utils/auth";
import { Rol } from "../../../types/Rol";
import { initData } from "../../../utils/localStorage";

initData();

const formLogin = document.querySelector<HTMLFormElement>("#form-login");

formLogin?.addEventListener("submit", (e: Event) => {
  e.preventDefault();

  const email = document.querySelector<HTMLInputElement>("#email")!.value;
  const password = document.querySelector<HTMLInputElement>("#password")!.value;

  // Llamamos a la función que valida contra el localStorage
  const user = login(email, password);

  if (user) {
    // Redirección con ruta absoluta
    window.location.href =
      user.rol === Rol.ADMIN
        ? "/src/pages/admin/home/home.html"
        : "/src/pages/client/home/home.html";
  } else {
    alert("Credenciales incorrectas. Inténtalo nuevamente.");
  }
});
