import {
  Bell,
  Building2,
  CalendarDays,
  ChevronDown,
  LayoutDashboard,
  LogOut,
  ShieldCheck,
  User as UserIcon,
  Users,
  type LucideIcon,
} from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { useDashboard } from "@/api/reserves";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/lib/auth-context";
import { initials } from "@/lib/format";
import { cn } from "@/lib/utils";

interface NavItem {
  to: string;
  label: string;
  icon: LucideIcon;
  /** Rótulo curto usado na barra inferior do celular. */
  shortLabel?: string;
}

const MAIN_NAV: NavItem[] = [
  { to: "/", label: "Início", icon: LayoutDashboard },
  { to: "/ambientes", label: "Ambientes", icon: Building2 },
  {
    to: "/minhas-reservas",
    label: "Minhas reservas",
    icon: CalendarDays,
    shortLabel: "Reservas",
  },
];

const ADMIN_NAV: NavItem[] = [
  {
    to: "/aprovacoes",
    label: "Aprovar reservas",
    icon: ShieldCheck,
    shortLabel: "Aprovar",
  },
  { to: "/usuarios", label: "Usuários", icon: Users },
];

/** Marca do portal, repetida na barra lateral e na tela de entrada. */
export function Brand({ className }: { className?: string }) {
  return (
    <div className={cn("flex items-center gap-2.5", className)}>
      <span className="flex size-9 items-center justify-center rounded-lg bg-primary text-primary-foreground">
        <Building2 className="size-4.5" />
      </span>
      <span className="flex flex-col leading-tight">
        <span className="text-sm font-semibold text-foreground">
          Ambientes UEPB
        </span>
        <span className="text-xs text-subtle">Portal de reservas</span>
      </span>
    </div>
  );
}

function NavItemLink({
  item,
  onNavigate,
}: {
  item: NavItem;
  onNavigate?: () => void;
}) {
  return (
    <NavLink
      to={item.to}
      end={item.to === "/"}
      onClick={onNavigate}
      className={({ isActive }) =>
        cn(
          "flex items-center gap-2.5 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
          isActive
            ? "bg-sidebar-accent text-sidebar-accent-foreground"
            : "text-muted-foreground hover:bg-muted hover:text-foreground",
        )
      }
    >
      <item.icon className="size-4.5 shrink-0" />
      {item.label}
    </NavLink>
  );
}

function Sidebar({ isAdmin }: { isAdmin: boolean }) {
  return (
    <aside className="hidden w-56 shrink-0 flex-col gap-1 border-r border-border bg-sidebar px-4 py-5 lg:flex">
      <Brand className="px-1 pb-5" />

      <nav className="flex flex-col gap-1">
        {MAIN_NAV.map((item) => (
          <NavItemLink key={item.to} item={item} />
        ))}
      </nav>

      {isAdmin && (
        <nav className="mt-6 flex flex-col gap-1">
          <span className="px-3 pb-1 text-[11px] font-semibold tracking-wider text-subtle uppercase">
            Administração
          </span>
          {ADMIN_NAV.map((item) => (
            <NavItemLink key={item.to} item={item} />
          ))}
        </nav>
      )}
    </aside>
  );
}

function TabBar({ isAdmin }: { isAdmin: boolean }) {
  const items = [...MAIN_NAV, ...(isAdmin ? ADMIN_NAV.slice(0, 1) : [])];

  return (
    <nav className="fixed inset-x-0 bottom-0 z-40 flex border-t border-border bg-background pb-[env(safe-area-inset-bottom)] lg:hidden">
      {items.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          end={item.to === "/"}
          className={({ isActive }) =>
            cn(
              "flex flex-1 flex-col items-center gap-1 py-2.5 text-[11px] font-medium transition-colors",
              isActive ? "text-primary" : "text-subtle",
            )
          }
        >
          <item.icon className="size-5" />
          {item.shortLabel ?? item.label}
        </NavLink>
      ))}
    </nav>
  );
}

/**
 * Cabeçalho da aplicação: título da página à esquerda; ação primária, notificações e
 * conta à direita. A busca de cada tela fica junto da lista que ela filtra, nunca aqui.
 */
export function PageHeader({
  title,
  description,
  leading,
  action,
}: {
  title: string;
  description?: string;
  /** Conteúdo antes do título, como o botão de voltar da tela de reserva. */
  leading?: React.ReactNode;
  /** Ação primária da página. */
  action?: React.ReactNode;
}) {
  const { user, isAdmin, signOut } = useAuth();
  const { data: dashboard } = useDashboard(isAdmin);
  const pendingCount = dashboard?.pendingCount ?? 0;

  return (
    <header className="sticky top-0 z-30 flex min-h-18 flex-wrap items-center gap-3 border-b border-border bg-background px-5 py-3 lg:px-8">
      {leading}

      <div className="flex min-w-0 flex-1 flex-col">
        <h1 className="truncate text-lg font-semibold text-foreground">
          {title}
        </h1>
        {description && (
          <p className="truncate text-xs text-muted-foreground">
            {description}
          </p>
        )}
      </div>

      <div className="flex items-center gap-2">
        {action}

        {isAdmin && (
          <Button
            variant="ghost"
            size="icon"
            className="relative"
            aria-label="Notificações"
            asChild
          >
            <NavLink to="/aprovacoes">
              <Bell />
              {pendingCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 flex size-4 items-center justify-center rounded-full bg-primary text-[10px] font-semibold text-primary-foreground">
                  {pendingCount}
                </span>
              )}
            </NavLink>
          </Button>
        )}

        <Separator orientation="vertical" className="hidden h-6 sm:block" />

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              className="h-auto gap-1.5 px-1.5 py-1"
              aria-label="Conta"
            >
              <Avatar className="size-8">
                <AvatarFallback className="bg-accent-soft text-xs font-semibold text-accent-strong">
                  {initials(user?.name)}
                </AvatarFallback>
              </Avatar>
              <ChevronDown className="text-subtle" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel className="flex flex-col gap-0.5">
              <span className="truncate font-medium">{user?.name ?? "—"}</span>
              <span className="truncate text-xs font-normal text-muted-foreground">
                {user?.email ?? ""}
              </span>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
              <DropdownMenuItem disabled>
                <UserIcon />
                Perfil {user?.role ?? ""}
              </DropdownMenuItem>
              <DropdownMenuItem onSelect={signOut}>
                <LogOut />
                Sair
              </DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}

/** Moldura das telas autenticadas: barra lateral no desktop, barra de abas no celular. */
export function AppShell() {
  const { isAdmin } = useAuth();

  return (
    <div className="flex min-h-svh">
      <Sidebar isAdmin={isAdmin} />

      <div className="flex min-w-0 flex-1 flex-col pb-16 lg:pb-0">
        <Outlet />
      </div>

      <TabBar isAdmin={isAdmin} />
    </div>
  );
}
