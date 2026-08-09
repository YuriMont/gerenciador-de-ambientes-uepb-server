import { Button } from "@/components/ui/button";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import { register } from "@/generated/api/auth/auth";
import { apiErrorMessage } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { ArrowRight } from "lucide-react";
import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthLayout } from "./login";

export default function RegisterPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);

    if (password.length < 8) {
      setError("A senha precisa ter no mínimo 8 caracteres.");
      return;
    }

    setIsSubmitting(true);

    try {
      await register({ name, email, password });
      await signIn(email, password);
      navigate("/", { replace: true });
    } catch (submitError) {
      setError(
        apiErrorMessage(
          submitError,
          "Não foi possível criar a conta. Confira os dados e tente de novo.",
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
          Criar conta
        </h1>
        <p className="text-sm text-muted-foreground">
          A conta abre com perfil USER e já permite solicitar reservas.
        </p>
      </div>

      <form onSubmit={handleSubmit} noValidate>
        <FieldGroup>
          <Field>
            <FieldLabel htmlFor="name">Nome completo</FieldLabel>
            <Input
              id="name"
              autoComplete="name"
              required
              placeholder="Maria Rodrigues"
              value={name}
              onChange={(event) => setName(event.target.value)}
            />
          </Field>

          <Field>
            <FieldLabel htmlFor="email">E-mail institucional</FieldLabel>
            <Input
              id="email"
              type="email"
              autoComplete="email"
              required
              placeholder="usuario@uepb.edu.br"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </Field>

          <Field data-invalid={error ? true : undefined}>
            <FieldLabel htmlFor="password">Senha</FieldLabel>
            <Input
              id="password"
              type="password"
              autoComplete="new-password"
              required
              value={password}
              aria-invalid={Boolean(error)}
              onChange={(event) => setPassword(event.target.value)}
            />
            <FieldDescription>Mínimo de 8 caracteres.</FieldDescription>
            {error && <FieldError>{error}</FieldError>}
          </Field>

          <Field>
            <Button type="submit" size="lg" disabled={isSubmitting}>
              {isSubmitting ? <Spinner data-icon="inline-start" /> : null}
              Criar conta
              {!isSubmitting && <ArrowRight data-icon="inline-end" />}
            </Button>
          </Field>
        </FieldGroup>
      </form>

      <p className="text-center text-xs text-subtle">
        Já tem acesso?{" "}
        <Link
          to="/entrar"
          className="font-medium text-primary underline-offset-4 hover:underline"
        >
          Entrar
        </Link>
      </p>
    </AuthLayout>
  );
}
