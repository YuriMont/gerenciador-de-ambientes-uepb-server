import { Brand } from "@/components/app-shell";
import { Button } from "@/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Spinner } from "@/components/ui/spinner";
import { apiErrorMessage } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { ArrowRight, Eye, EyeOff } from "lucide-react";
import { useState, type FormEvent } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";

/** Painel ilustrativo: um dia de agenda com quatro horários já confirmados. */
export function AgendaTeaser() {
  const busyHours = [10, 13, 14, 18];

  return (
    <div className="flex flex-col gap-3 rounded-xl border border-border bg-card p-5">
      <div className="flex h-16 items-end gap-1.5" aria-hidden="true">
        {Array.from({ length: 14 }, (_, index) => 8 + index).map((hour) => (
          <span
            key={hour}
            className={
              busyHours.includes(hour)
                ? "h-16 flex-1 rounded-sm bg-primary"
                : "h-4 flex-1 rounded-sm bg-muted"
            }
          />
        ))}
      </div>
      <div className="flex items-baseline justify-between gap-3">
        <span className="text-xs text-muted-foreground">
          Um dia de agenda · 14 slots de 1 hora
        </span>
        <span className="text-xs font-medium text-subtle">08:00 → 22:00</span>
      </div>
    </div>
  );
}

/**
 * Moldura das telas de autenticação: painel de apresentação à esquerda e formulário
 * à direita. No celular, só o formulário aparece.
 */
export function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-svh flex-col lg:flex-row">
      <div className="hidden w-[40%] max-w-[588px] flex-col justify-between border-r border-border bg-canvas p-10 lg:flex">
        <Brand />

        <div className="flex flex-col gap-8">
          <div className="flex flex-col gap-4">
            <h2 className="text-4xl leading-[1.1] font-semibold tracking-tight text-balance text-foreground">
              Peça a sala.
              <br />A coordenação confirma.
            </h2>
            <p className="max-w-md text-sm leading-relaxed text-muted-foreground">
              Solicite laboratórios, salas e auditórios da UEPB em blocos de uma
              hora. Cada pedido chega à coordenação do ambiente e volta aprovado
              ou recusado.
            </p>
          </div>

          <AgendaTeaser />
        </div>

        <p className="text-xs text-subtle">
          Universidade Estadual da Paraíba · Coordenação de Espaços Acadêmicos
        </p>
      </div>

      <div className="flex flex-1 items-center justify-center bg-background px-5 py-10 lg:px-16">
        <div className="flex w-full max-w-sm flex-col gap-8">
          <Brand className="lg:hidden" />
          {children}
        </div>
      </div>
    </div>
  );
}

export default function LoginPage() {
  const { signIn, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await signIn(email, password);
      navigate("/", { replace: true });
    } catch (submitError) {
      setError(
        apiErrorMessage(
          submitError,
          "E-mail ou senha não conferem. Tente de novo.",
        ),
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout>
      <div className="flex flex-col gap-1.5">
        <h1 className="text-2xl font-semibold tracking-tight text-foreground">
          Entrar
        </h1>
        <p className="text-sm text-muted-foreground">
          Use o e-mail institucional cadastrado no portal.
        </p>
      </div>

      <form onSubmit={handleSubmit} noValidate>
        <FieldGroup>
          <Field data-invalid={error ? true : undefined}>
            <FieldLabel htmlFor="email">E-mail institucional</FieldLabel>
            <Input
              id="email"
              type="email"
              autoComplete="email"
              required
              placeholder="usuario@uepb.edu.br"
              value={email}
              aria-invalid={Boolean(error)}
              onChange={(event) => setEmail(event.target.value)}
            />
          </Field>

          <Field data-invalid={error ? true : undefined}>
            <div className="flex items-baseline justify-between gap-3">
              <FieldLabel htmlFor="password">Senha</FieldLabel>
              <span className="text-xs text-subtle">
                Fale com a coordenação para redefinir
              </span>
            </div>
            <InputGroup>
              <InputGroupInput
                id="password"
                type={showPassword ? "text" : "password"}
                autoComplete="current-password"
                required
                placeholder="••••••••••"
                value={password}
                aria-invalid={Boolean(error)}
                onChange={(event) => setPassword(event.target.value)}
              />
              <InputGroupAddon align="inline-end">
                <InputGroupButton
                  type="button"
                  aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                  onClick={() => setShowPassword((visible) => !visible)}
                >
                  {showPassword ? <EyeOff /> : <Eye />}
                </InputGroupButton>
              </InputGroupAddon>
            </InputGroup>
            {error && <FieldError>{error}</FieldError>}
          </Field>

          <Field>
            <Button type="submit" size="lg" disabled={isSubmitting}>
              {isSubmitting ? <Spinner data-icon="inline-start" /> : null}
              Entrar
              {!isSubmitting && <ArrowRight data-icon="inline-end" />}
            </Button>
          </Field>
        </FieldGroup>
      </form>

      <div className="flex items-center gap-3">
        <span className="h-px flex-1 bg-border" />
        <span className="text-xs text-subtle">ou</span>
        <span className="h-px flex-1 bg-border" />
      </div>

      <Button variant="outline" size="lg" asChild>
        <Link to="/criar-conta">Criar conta com e-mail UEPB</Link>
      </Button>

      <p className="text-center text-xs text-subtle">
        Ao entrar você concorda com as normas de uso dos espaços acadêmicos.
      </p>
    </AuthLayout>
  );
}
