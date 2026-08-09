import { Search, UserPlus, Users } from "lucide-react";
import { useMemo, useState, type FormEvent } from "react";
import { toast } from "sonner";
import { useAllUsers, useCreateAdministrator } from "@/generated/api/users/users";
import type { RoleName } from "@/generated/models/roleName";
import { PageHeader } from "@/components/app-shell";
import { Panel } from "@/components/panel";
import { RoleBadge, ToneBadge } from "@/components/status-badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/ui/spinner";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { apiErrorMessage } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { formatDayMonthYear, initials } from "@/lib/format";
import { useInvalidatePersons } from "@/lib/queries";

type Filter = "todos" | RoleName;

const FILTERS: { value: Filter; label: string }[] = [
  { value: "todos", label: "Todos" },
  { value: "USER", label: "USER" },
  { value: "ADMIN", label: "ADMIN" },
  { value: "OWNER", label: "OWNER" },
];

const PERMISSIONS: { role: RoleName; description: string }[] = [
  { role: "USER", description: "Solicita e acompanha as próprias reservas." },
  {
    role: "ADMIN",
    description:
      "Aprova e recusa reservas, gerencia ambientes e vê todas as pessoas.",
  },
  {
    role: "OWNER",
    description: "Tudo que o ADMIN faz e mais: cria novos administradores.",
  },
];

/** Formulário lateral de criação de administrador. Exclusivo do perfil OWNER. */
function CreateAdminPanel() {
  const invalidatePersons = useInvalidatePersons();
  const createAdministrator = useCreateAdministrator();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);

    if (password.length < 8) {
      setError("A senha provisória precisa ter no mínimo 8 caracteres.");
      return;
    }

    createAdministrator.mutate(
      {
        data: { name: name.trim(), email: email.trim(), password },
      },
      {
        onSuccess: () => {
          invalidatePersons();
          toast.success("Administrador criado.");
          setName("");
          setEmail("");
          setPassword("");
        },
        onError: (mutationError) =>
          setError(
            apiErrorMessage(
              mutationError,
              "Não foi possível criar o administrador.",
            ),
          ),
      },
    );
  }

  return (
    <Panel
      title="Criar administrador"
      description="Cria a conta já com perfil ADMIN, sem passar pelo cadastro público."
      action={<ToneBadge tone="success">Só owner</ToneBadge>}
    >
      <form onSubmit={handleSubmit} noValidate>
        <FieldGroup>
          <Field>
            <FieldLabel htmlFor="admin-name">Nome completo</FieldLabel>
            <Input
              id="admin-name"
              required
              placeholder="Ana Lúcia Farias"
              value={name}
              onChange={(event) => setName(event.target.value)}
            />
          </Field>

          <Field>
            <FieldLabel htmlFor="admin-email">E-mail institucional</FieldLabel>
            <Input
              id="admin-email"
              type="email"
              required
              placeholder="ana.farias@uepb.edu.br"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </Field>

          <Field data-invalid={error ? true : undefined}>
            <FieldLabel htmlFor="admin-password">Senha provisória</FieldLabel>
            <Input
              id="admin-password"
              type="password"
              required
              value={password}
              aria-invalid={Boolean(error)}
              onChange={(event) => setPassword(event.target.value)}
            />
            <FieldDescription>Mínimo de 8 caracteres.</FieldDescription>
            {error && <FieldError>{error}</FieldError>}
          </Field>

          <Field>
            <Button type="submit" disabled={createAdministrator.isPending}>
              {createAdministrator.isPending ? (
                <Spinner data-icon="inline-start" />
              ) : (
                <UserPlus data-icon="inline-start" />
              )}
              Criar administrador
            </Button>
          </Field>
        </FieldGroup>
      </form>
    </Panel>
  );
}

export default function UsersPage() {
  const { isOwner } = useAuth();
  const { data, isLoading } = useAllUsers();

  const [filter, setFilter] = useState<Filter>("todos");
  const [search, setSearch] = useState("");

  const users = useMemo(() => data?.data ?? [], [data]);

  const filtered = useMemo(() => {
    const term = search.trim().toLocaleLowerCase("pt-BR");

    return users.filter((user) => {
      const matchesRole = filter === "todos" || user.role === filter;
      const matchesSearch =
        term.length === 0 ||
        (user.name ?? "").toLocaleLowerCase("pt-BR").includes(term) ||
        (user.email ?? "").toLocaleLowerCase("pt-BR").includes(term);
      return matchesRole && matchesSearch;
    });
  }, [filter, search, users]);

  const admins = users.filter((user) => user.role === "ADMIN").length;
  const owners = users.filter((user) => user.role === "OWNER").length;

  return (
    <>
      <PageHeader
        title="Usuários"
        description="Quem tem acesso ao portal e com qual perfil"
      />

      <main className="flex flex-1 flex-col gap-4 bg-canvas p-5 lg:p-8">
        <div className="grid gap-4 xl:grid-cols-[minmax(0,1.6fr)_minmax(0,1fr)]">
          <Panel
            title={`${users.length} ${users.length === 1 ? "pessoa cadastrada" : "pessoas cadastradas"}`}
            description={`${admins} ${admins === 1 ? "administrador" : "administradores"} · ${owners} ${
              owners === 1 ? "proprietário" : "proprietários"
            }`}
            contentClassName="gap-4"
            action={
              <div className="flex flex-wrap items-center gap-2">
                <ToggleGroup
                  type="single"
                  value={filter}
                  onValueChange={(value) => value && setFilter(value as Filter)}
                  variant="outline"
                >
                  {FILTERS.map((item) => (
                    <ToggleGroupItem key={item.value} value={item.value}>
                      {item.label}
                    </ToggleGroupItem>
                  ))}
                </ToggleGroup>

                <InputGroup className="w-full sm:w-56">
                  <InputGroupAddon>
                    <Search />
                  </InputGroupAddon>
                  <InputGroupInput
                    placeholder="Buscar por nome ou e-mail"
                    aria-label="Buscar pessoa"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                  />
                </InputGroup>
              </div>
            }
          >
            {isLoading ? (
              <Skeleton className="h-72" />
            ) : filtered.length > 0 ? (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Pessoa</TableHead>
                      <TableHead>Perfil</TableHead>
                      <TableHead>Reservas</TableHead>
                      <TableHead>Cadastro</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filtered.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>
                          <span className="flex items-center gap-2.5">
                            <Avatar className="size-8">
                              <AvatarFallback className="bg-accent-soft text-xs font-semibold text-accent-strong">
                                {initials(user.name)}
                              </AvatarFallback>
                            </Avatar>
                            <span className="flex min-w-0 flex-col">
                              <span className="truncate font-medium text-foreground">
                                {user.name}
                              </span>
                              <span className="truncate text-xs text-muted-foreground">
                                {user.email}
                              </span>
                            </span>
                          </span>
                        </TableCell>
                        <TableCell>
                          <RoleBadge role={user.role} />
                        </TableCell>
                        <TableCell>{user.reserveCount}</TableCell>
                        <TableCell className="text-muted-foreground">
                          {formatDayMonthYear(user.createdAt)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            ) : (
              <Empty className="py-10">
                <EmptyHeader>
                  <EmptyMedia variant="icon">
                    <Users />
                  </EmptyMedia>
                  <EmptyTitle>Ninguém por aqui</EmptyTitle>
                  <EmptyDescription>
                    Ajuste a busca ou escolha outro perfil.
                  </EmptyDescription>
                </EmptyHeader>
              </Empty>
            )}
          </Panel>

          <div className="flex min-w-0 flex-col gap-4">
            {isOwner && <CreateAdminPanel />}

            <Panel
              title="O que cada perfil pode fazer"
              contentClassName="gap-3"
            >
              {PERMISSIONS.map((permission) => (
                <div key={permission.role} className="flex flex-col gap-1.5">
                  <RoleBadge role={permission.role} />
                  <p className="text-xs leading-relaxed text-muted-foreground">
                    {permission.description}
                  </p>
                </div>
              ))}
            </Panel>
          </div>
        </div>
      </main>
    </>
  );
}
