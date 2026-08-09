import Axios, { type AxiosRequestConfig, AxiosError } from "axios";

/** Chave do token JWT no `localStorage`. */
export const TOKEN_KEY = "token";

export const api = Axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

// Request interceptor for auth
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const isAuthRequest = error.config?.url?.startsWith("/auth") ?? false;

    // Uma sessão expirada volta para a tela de entrada. Credenciais recusadas no
    // próprio login são tratadas pelo formulário, sem recarregar a página.
    if (error.response?.status === 401 && !isAuthRequest) {
      localStorage.removeItem(TOKEN_KEY);
      if (window.location.pathname !== "/entrar") {
        window.location.href = "/entrar";
      }
    }
    return Promise.reject(error);
  },
);

export const customInstance = <T>(
  config: AxiosRequestConfig,
  options?: AxiosRequestConfig,
): Promise<T> => {
  return api({
    ...config,
    ...options,
  }).then(({ data }) => data);
};

export type ErrorType<Error> = AxiosError<Error>;
export type BodyType<BodyData> = BodyData;

/**
 * Mensagem legível de um erro da API, com um texto de reserva quando o servidor
 * não devolve nada aproveitável.
 */
export function apiErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof AxiosError) {
    const data = error.response?.data as
      { message?: string; detail?: string } | undefined;
    return data?.message ?? data?.detail ?? fallback;
  }
  return fallback;
}
