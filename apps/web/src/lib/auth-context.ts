import { createContext, useContext } from "react";
import type { UserResponse } from "@/generated/models";

export interface AuthContextValue {
  /** Usuário autenticado, ou `undefined` enquanto carrega ou se não houver sessão. */
  user: UserResponse | undefined;
  /** Verdadeiro enquanto os dados da sessão ainda estão sendo carregados. */
  isLoading: boolean;
  /** Verdadeiro quando há um token guardado. */
  isAuthenticated: boolean;
  /** Verdadeiro para os perfis ADMIN e OWNER. */
  isAdmin: boolean;
  /** Verdadeiro apenas para o perfil OWNER. */
  isOwner: boolean;
  /** Autentica com e-mail e senha e guarda o token. */
  signIn: (email: string, password: string) => Promise<void>;
  /** Descarta o token e o cache das consultas. */
  signOut: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

/** Acessa a sessão atual. Só pode ser usado dentro do `AuthProvider`. */
export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth precisa estar dentro de um AuthProvider");
  }
  return context;
}
