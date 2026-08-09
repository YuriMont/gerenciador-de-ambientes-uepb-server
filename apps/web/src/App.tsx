import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "@/components/app-shell";
import { Spinner } from "@/components/ui/spinner";
import { useAuth } from "@/lib/auth-context";
import ApprovalsPage from "@/pages/approvals";
import EnvironmentsPage from "@/pages/environments";
import HomePage from "@/pages/home";
import LoginPage from "@/pages/login";
import MyReservesPage from "@/pages/my-reserves";
import NotFoundPage from "@/pages/not-found";
import RegisterPage from "@/pages/register";
import ReservePage from "@/pages/reserve";
import UsersPage from "@/pages/users";

/** Tela de espera enquanto a sessão é carregada. */
function SessionLoading() {
  return (
    <div className="flex min-h-svh items-center justify-center">
      <Spinner className="size-6 text-muted-foreground" />
    </div>
  );
}

/** Só deixa passar quem está autenticado. */
function RequireAuth({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <SessionLoading />;
  if (!isAuthenticated) return <Navigate to="/entrar" replace />;

  return children;
}

/** Só deixa passar ADMIN e OWNER; os demais voltam para o início. */
function RequireAdmin({ children }: { children: React.ReactNode }) {
  const { isAdmin, isLoading } = useAuth();

  if (isLoading) return <SessionLoading />;
  if (!isAdmin) return <Navigate to="/" replace />;

  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/entrar" element={<LoginPage />} />
      <Route path="/criar-conta" element={<RegisterPage />} />

      <Route
        element={
          <RequireAuth>
            <AppShell />
          </RequireAuth>
        }
      >
        <Route path="/" element={<HomePage />} />
        <Route path="/ambientes" element={<EnvironmentsPage />} />
        <Route path="/ambientes/:environmentId" element={<ReservePage />} />
        <Route path="/minhas-reservas" element={<MyReservesPage />} />
        <Route
          path="/aprovacoes"
          element={
            <RequireAdmin>
              <ApprovalsPage />
            </RequireAdmin>
          }
        />
        <Route
          path="/usuarios"
          element={
            <RequireAdmin>
              <UsersPage />
            </RequireAdmin>
          }
        />
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
