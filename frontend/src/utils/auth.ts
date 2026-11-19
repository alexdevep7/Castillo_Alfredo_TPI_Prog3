import type { IUser } from "../types/IUser";
import { saveUser as saveSession, getUser, removeUser } from "./localStorage";

export const getUsers = (): IUser[] => {
  const users = localStorage.getItem("usuarios");
  return users ? JSON.parse(users) : [];
};

export const saveUser = (user: IUser): void => {
  const users = getUsers();
  const existe = users.find((u) => u.mail === user.mail && !u.eliminado);
  if (existe) throw new Error("El correo ya está registrado.");

  const nextId = users.length > 0 ? Math.max(...users.map((u) => u.id)) + 1 : 1;
  const newUser: IUser = {
    ...user,
    id: nextId,
    eliminado: false,
  };

  users.push(newUser);
  localStorage.setItem("usuarios", JSON.stringify(users));
};

export const login = (mail: string, password: string): IUser | null => {
  const users = getUsers();
  const user = users.find(
    (u) => u.mail === mail && u.password === password && !u.eliminado,
  );
  if (user) {
    saveSession(user);
    const { password: _, ...safeUser } = user;
    return safeUser;
  }
  return null;
};

export const getSession = (): IUser | null => {
  return getUser();
};

export const logout = (): void => {
  removeUser();
};
