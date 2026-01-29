# Gerenciador de Ambientes UEPB - Server

Este é o repositório do servidor (backend) do **Gerenciador de Ambientes da UEPB**. A aplicação foi desenvolvida para facilitar a gestão e reserva de espaços e ambientes dentro da Universidade Estadual da Paraíba.

## 🚀 Tecnologias

O projeto foi construído utilizando as seguintes tecnologias:

*   **Java 21**
*   **Spring Boot 3**
*   **Spring Data JPA** (Persistência de dados)
*   **Spring Security** (Segurança e Autenticação)
*   **Flyway** (Migrações de banco de dados)
*   **PostgreSQL** (Banco de dados de produção)
*   **H2 Database** (Banco de dados de testes/desenvolvimento)
*   **Maven** (Gerenciador de dependências)

## 📚 Bibliotecas Principais

*   **SpringDoc OpenAPI (Swagger)**: Documentação interativa da API.
*   **Auth0 Java JWT**: Implementação de tokens JWT para autenticação segura.
*   **Lombok**: Redução de código boilerplate (getters, setters, etc.).
*   **Hibernate Validator**: Validação de dados de entrada.

## 📖 Documentação (Swagger)

Após executar a aplicação, você pode acessar a documentação interativa da API através do Swagger UI no seguinte endereço:

> [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🛠️ Como Executar

### Pré-requisitos

*   Java 21 instalado.
*   Maven instalado (opcional, pois o projeto inclui o `mvnw`).
*   PostgreSQL (opcional, caso queira usar o banco em disco).

### Passos para execução

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/YuriMont/gerenciador-de-ambientes-uepb-server.git
    cd gerenciador-de-ambientes-uepb-server
    ```

2.  **Configuração do Banco de Dados:**
    Por padrão, o projeto pode estar configurado com o banco h2, para usar PostgreSQL. Verifique o arquivo `src/main/resources/application.properties` e ajuste as credenciais se necessário.

3.  **Execute a aplicação:**
    Utilize o Maven Wrapper incluso no projeto:
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Acesse a API:**
    A aplicação estará disponível em `http://localhost:8080`.

