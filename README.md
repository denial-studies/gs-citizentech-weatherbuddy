# Weather Buddy — Sistema de Monitoramento e Alerta Preventivo de Enchentes

Solução de **CitizenTech** desenvolvida pelo time **Hidro-Alerta** em arquitetura **Multi-Módulo Maven** seguindo **Domain-Driven Design (DDD)**, **Java 25** e **Spring Boot 4.1.2**.

---

## 1. Arquitetura Multi-Módulo (DDD)

```text
weatherbuddy/
├── pom.xml                  # Root POM (groupId: br.com.fiap.hidroalerta, artifactId: weatherbuddy)
├── run.bat / run.sh         # Scripts de inicialização rápida
├── dados-mockados.txt       # Guia de consulta rápida dos dados de teste (db-env: true)
├── domain/                  # Núcleo de domínio puro (Entidades, Enums, Value Objects, Portas de Repositório)
├── application/             # Casos de uso (Application Services, DTOs Request/Response, Portas de Saída, Exceções)
├── infrastructure/          # Adaptadores técnicos (Oracle JPA Entities, Mappers, RestClient Open-Meteo, Spring Security)
└── presentation/            # Adaptadores de entrada (WeatherBuddyApplication, Thymeleaf Web UI, REST Controllers OpenAPI, Scheduler)
```

---

## 2. Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 4.1.2** (`Web`, `Data JPA`, `Security`, `Thymeleaf`, `Validation`)
- **Oracle Database** (`ojdbc11`) + Modo Local Mockado (`db-env: true`)
- **Lombok 1.18.30**
- **SpringDoc OpenAPI 2.8.6 (Swagger UI)**
- **Open-Meteo API** (`RestClient` nativo do Spring)
- **Cobalt (`cobalt-lib`)** — integração WhatsApp com sessão temporária em memória (pareamento por código ou QR Code a cada inicialização)

---

## 3. Configuração do Banco de Dados Oracle e Dados Mockados

Edite o arquivo `presentation/src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: weatherbuddy

  datasource:
    url: jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
    username: rm000000
    password: senha
    driver-class-name: oracle.jdbc.OracleDriver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  thymeleaf:
    cache: false

server:
  port: 8080

# Habilita dados mockados (true, on ou 1)
db-env: true
```

- **`db-env: true`**: Inicia com banco em memória e carrega os dados mockados automaticamente.
- **`db-env: false`**: Conecta diretamente ao banco Oracle configurado em `spring.datasource`.

---

## 4. Como Compilar e Executar

### Compilar todos os módulos:
```bash
./mvnw clean package
```

### Executar a aplicação:
Caso o JDK 25 não esteja no `PATH` do sistema, configure a variável `JDK_HOME` no topo de `run.bat` ou `run.sh`.

- **Windows:**
  ```cmd
  run.bat
  ```
- **Linux / macOS:**
  ```bash
  ./run.sh
  ```

### Limpar pastas `target`:
- **Windows:** `clean_builds.bat`
- **Linux / macOS:** `./clean_builds.sh`

---

## 5. Acessos da Aplicação

| Interface | URL | Acesso |
| :--- | :--- | :--- |
| **Cadastro Público do Cidadão** | `http://localhost:8080/` | Público |
| **Login de Responsáveis** | `http://localhost:8080/login` | Público |
| **Painel Operacional (Dashboard)** | `http://localhost:8080/dashboard` | Autenticado (`ROLE_RESPONSAVEL`) |
| **Swagger UI (OpenAPI)** | `http://localhost:8080/swagger-ui.html` | Autenticado (`ROLE_RESPONSAVEL`) |
