import { useQueryClient } from "@tanstack/react-query";
import { atom, useAtom } from "jotai";
import { useCallback, useMemo, type ReactNode } from "react";
import { useGetCurrentUser } from "@/generated/api/users/users";
import { login } from "@/generated/api/auth/auth";
import { TOKEN_KEY } from "@/lib/api";
import { AuthContext, type AuthContextValue } from "@/lib/auth-context";

/** Token JWT da sessão, espelhado no `localStorage` para sobreviver a um recarregamento. */
const tokenAtom = atom<string | null>(localStorage.getItem(TOKEN_KEY));

/** Disponibiliza a sessão para toda a árvore de componentes. */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useAtom(tokenAtom);
  const queryClient = useQueryClient();
  const { data, isLoading } = useGetCurrentUser({
    query: { enabled: Boolean(token), retry: false, staleTime: 5 * 60 * 1000 },
  });
  const user = data?.data;

  const signIn = useCallback(
    async (email: string, password: string) => {
      const { data: response } = await login({ email, password });
      if (!response.token) {
        throw new Error("Resposta de login sem token.");
      }
      localStorage.setItem(TOKEN_KEY, response.token);
      setToken(response.token);
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
