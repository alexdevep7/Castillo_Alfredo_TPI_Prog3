import { saveUser } from "../../../utils/auth";
import { Rol } from "../../../types/Rol";
import { initData } from "../../../utils/localStorage";

initData();

const formRegistro = document.querySelector<HTMLFormElement>("#form-registro");

formRegistro?.addEventListener("submit", (e: Event) => {
  e.preventDefault();

  const nombre = document
    .querySelector<HTMLInputElement>("#nombre")!
    .value.trim();
  const apellido = document
    .querySelector<HTMLInputElement>("#apellido")!
    .value.trim();
  const mail = document.querySelector<HTMLInputElement>("#email")!.value.trim();
  const celular =
    document.querySelector<HTMLInputElement>("#celular")?.value.trim() || "";
  const password = document.querySelector<HTMLInputElement>("#password")!.value;

  try {
    saveUser({
      id: 0, // auth.ts calcula el ID correlativo automáticamente
      nombre,
      apellido,
      mail,
      celular,
      password,
      rol: Rol.USUARIO,
      eliminado: false,
    });
    alert("Registro exitoso. Ya puedes iniciar sesión.");
    window.location.href = "/src/pages/auth/login/login.html";
  } catch (error: any) {
    alert(error.message);
  }
});
