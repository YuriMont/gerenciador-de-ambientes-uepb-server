import { useQueryClient } from "@tanstack/react-query";
import { atom, useAtom } from "jotai";
import { useCallback, useMemo, type ReactNode } from "react";
import { useCurrentUser } from "@/api/persons";
import { api, TOKEN_KEY } from "@/lib/api";
import { AuthContext, type AuthContextValue } from "@/lib/auth-context";

/** Token JWT da sessão, espelhado no `localStorage` para sobreviver a um recarregamento. */
const tokenAtom = atom<string | null>(localStorage.getItem(TOKEN_KEY));

/** Disponibiliza a sessão para toda a árvore de componentes. */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useAtom(tokenAtom);
  const queryClient = useQueryClient();
  const { data: user, isLoading } = useCurrentUser(Boolean(token));

  const signIn = useCallback(
    async (email: string, password: string) => {
      const { data } = await api.post<{ token: string }>("/auth/login", {
        email,
        password,
      });
      localStorage.setItem(TOKEN_KEY, data.token);
      setToken(data.token);
      await queryClient.invalidateQueries();
    },
    [queryClient, setToken],
  );

  const signOut = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    queryClient.clear();
  }, [queryClient, setToken]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isLoading: Boolean(token) && isLoading,
      isAuthenticated: Boolean(token),
      isAdmin: user?.role === "ADMIN" || user?.role === "OWNER",
      isOwner: user?.role === "OWNER",
      signIn,
      signOut,
    }),
    [isLoading, signIn, signOut, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
