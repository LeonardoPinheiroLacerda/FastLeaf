# FastLeaf

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=coverage)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=bugs)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.leonardopinheirolacerda/fastleaf)
![Java](https://img.shields.io/badge/Java-21+-blue)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)


O FastLeaf é um framework Java acadêmico, construído com base na especificação [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616), para simplificar o desenvolvimento de aplicações web, desde o fornecimento de arquivos estáticos até a criação de endpoints RESTful.

Este guia detalha como utilizar o framework em seus projetos.

# Sumário
1. [Arquitetura do Framework](#arquitetura-do-framework)
2. [Começando](#começando)
3. [Configuração](#configuração)
    - [Propriedades da Aplicação](#propriedades-da-aplicação)
    - [Logs](#logs)
4. [Criando Endpoints](#criando-endpoints)
    - [Estrutura Básica](#estrutura-básica)
    - [Acessando Dados da Requisição](#acessando-dados-da-requisição)
        - [Path Variables](#path-variables)
        - [Query Parameters](#query-parameters)
        - [Corpo da Requisição (Body)](#corpo-da-requisição-body)
        - [Headers](#headers)
        - [Propriedades do Middleware](#propriedades-do-middleware)
    - [Construindo a Resposta (HttpResponse)](#construindo-a-resposta-httpresponse)
5. [Middlewares](#middlewares)
6. [Servindo Arquivos Estáticos: Do Básico ao Avançado](#servindo-arquivos-estáticos-do-básico-ao-avançado)

## Arquitetura do Framework

O design do framework é baseado em uma clara separação de responsabilidades, com componentes coesos e desacoplados. O núcleo da lógica de roteamento reside no pacote `router`.

### Visão Geral do Fluxo de Requisição

Uma requisição HTTP passa pelas seguintes camadas principais até gerar uma resposta:

```
[Requisição TCP]
       |
       v
+----------------+   1. Aceita a conexão e a entrega para um handler.
|     server     |
+----------------+
       |
       v
+----------------+   2. Lê a requisição como texto bruto. O `ApiHttpResponseWriter` orquestra
|       io       |      a busca e execução do endpoint, e escreve a resposta final.
+----------------+
       |
       v
+----------------+   3. Converte o texto bruto em um objeto de dados estruturado (`HttpRequestData`).
|     parser     |
+----------------+
       |
       v
+----------------+   4. O "cérebro": `HttpEndpointResolver` encontra o endpoint. `HttpEndpointWrapperFactory`
|     router     |      cria um "wrapper" com os dados extraídos, pronto para execução.
+----------------+
       |
       v
+----------------+   5. Sua classe de negócio, que contém a lógica da aplicação.
|  HttpEndpoint  |
+----------------+
       |
       v
[Resposta HTTP]
```

### Descrição dos Pacotes

A seguir, a função de cada pacote principal e suas classes mais importantes.

*   #### `br.com.leonardo.server`
    *   **Função:** Ponto de entrada e gerenciamento do ciclo de vida do servidor.
    *   **`ServerRunner`**: Classe principal que o usuário invoca. É responsável por instanciar o `HttpEndpointResolver` e iniciar o `Server`.
    *   **`Server`**: Gerencia o `ServerSocket` e o pool de threads. Aceita as conexões TCP e despacha cada uma para um `ConnectionIOHandler`, injetando as dependências necessárias.

*   #### `br.com.leonardo.io`
    *   **Função:** Camada de Entrada/Saída (I/O), responsável pela comunicação de baixo nível com o cliente.
    *   **`ConnectionIOHandler`**: Gerencia o ciclo de vida de uma única conexão. Orquestra a leitura da requisição e aciona o `HttpWriter` apropriado para gerar e escrever a resposta.
    *   **`ApiHttpResponseWriter`**: Implementação de `HttpWriter` para endpoints dinâmicos. Ele orquestra a busca e a preparação do endpoint:
        1.  Usa o `HttpEndpointResolver` (injetado) para encontrar o `HttpEndpoint` correto.
        2.  Invoca o método estático `HttpEndpointWrapperFactory.create()` para obter um `HttpEndpointWrapper`.
        3.  Executa o `wrapper` para gerar a `HttpResponse`.

*   #### `br.com.leonardo.parser`
    *   **Função:** Análise (parsing) da requisição HTTP bruta.
    *   **`HttpRequestFactory`**: Atua como uma fachada (Façade) que usa parsers específicos (`RequestLineParser`, `RequestHeaderParser`) para converter a string da requisição em um objeto `HttpRequestData`.

*   #### `br.com.leonardo.router`
    *   **Função:** O cérebro do framework, responsável pelo roteamento e preparação para a execução.
    *   **`router.core`**: Contém as entidades centrais do roteamento.
        *   **`HttpEndpointResolver`**: Um serviço, injetado onde necessário, cuja única função é encontrar o `HttpEndpoint` que corresponde a uma requisição.
        *   **`HttpEndpoint`**: A classe abstrata que os usuários do framework estendem para criar suas rotas.
        *   **`HttpEndpointWrapper`**: Um objeto que encapsula um `HttpEndpoint` e os dados específicos da requisição (path variables, body, etc.), representando uma "rota resolvida, pronta para ser executada".
        *   **`HttpEndpointWrapperFactory`**: Uma classe utilitária com um método de fábrica estático. Sua função é receber um `HttpEndpoint` e os dados da requisição para criar e retornar um `HttpEndpointWrapper` configurado, usando os `Extractors` no processo.
    *   **`router.matcher`**:
        *   **`UriMatcher` (Interface)**: Define um contrato para classes que comparam uma URI de requisição com um padrão de rota.
        *   **`EndpointUriMatcher` (Composite)**: Agrega múltiplos `UriMatcher`s para testar uma URI contra várias estratégias de correspondência.
    *   **`router.extractor`**:
        *   **`PathVariablesExtractor`, `QueryParametersExtractor`, etc.**: Classes utilitárias focadas em extrair informações específicas de uma requisição.

*   #### `br.com.leonardo.http`
    *   **Função:** Contém os modelos de dados imutáveis (records, enums) que representam os conceitos do protocolo HTTP.
    *   **`HttpRequest`, `HttpResponse`, `HttpMethod`, `HttpStatusCode`**: Representam as estruturas fundamentais do HTTP, servindo como a "linguagem" comum usada em todo o framework.

*   #### `br.com.leonardo.annotation`
    *   **Função:** Definição de anotações e lógica de escaneamento.
    *   **`@Endpoint`**: Anotação usada para marcar uma classe como um endpoint HTTP.
    *   **`EndpointScanner`**: Classe que, na inicialização, varre o classpath em busca de classes anotadas com `@Endpoint` e as registra no `HttpEndpointResolver`.

---

# Começando

Para iniciar, adicione a dependência do framework ao seu `pom.xml`:

```xml
<dependency>
    <groupId>io.github.leonardopinheirolacerda</groupId>
    <artifactId>fastleaf</artifactId>
    <version>1.0.2</version>
</dependency>
```

Em seguida, no método `main` da sua aplicação, chame o método estático `serve` da classe `Server`, passando como argumento a classe que servirá como ponto de partida para a configuração da sua aplicação.

```java
public class App {
    public static void main(String[] args) {
        // Inicia o servidor, escaneia os endpoints e bloqueia a thread principal
        ServerRunner.serve(App.class);
    }
}
```

A classe passada como argumento (`App.class`) é usada para determinar o pacote base a partir do qual o escaneamento de `@Endpoint`s será realizado.


# Configuração

Você pode customizar o comportamento do servidor criando um arquivo `http-server.properties` na pasta `src/main/resources`.

### Propriedades da Aplicação

| Nome da propriedade                  | Descrição                                                                  | Valor Padrão |
|--------------------------------------|----------------------------------------------------------------------------|--------------|
| `http.server.port`                   | Porta HTTP onde a aplicação irá rodar.                                     | `9000`       |
| `http.server.static.content.enabled` | Habilita ou desabilita o recurso de servir arquivos estáticos.             | `true`       |
| `http.server.static.content.path`    | Define o caminho (dentro de `resources`) que contém os arquivos estáticos. | `static`     |

### Logs

O framework utiliza SLF4J com Logback para um sistema de logs flexível.

#### Propriedades de Log

As seguintes propriedades podem ser configuradas no arquivo `http-server.properties`:

| Nome da propriedade                 | Descrição                                                                            | Valor Padrão        |
|-------------------------------------|--------------------------------------------------------------------------------------|---------------------|
| `log.level`                         | Altera o nível de log raiz da aplicação (`ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`). | `INFO`              |
| `log.pattern`                       | Define o padrão de formatação das mensagens de log no `logback.xml`.                 | (Padrão do Logback) |
| `http.server.log.detailed-request`  | Se `true`, o conteúdo detalhado das requisições HTTP será logado.                    | `false`             |
| `http.server.log.detailed-response` | Se `true`, o conteúdo detalhado das respostas HTTP será logado.                      | `false`             |

#### Customização Avançada (logback.xml)
Para customizações avançadas (ex: formatters, appenders), crie um arquivo `logback.xml` em `src/main/resources`. O framework fornece um `PatternLayout` customizado que adiciona cores ao output do console, melhorando a legibilidade.

**Exemplo de `logback.xml`:**
```xml
<configuration>
    <conversionRule conversionWord="clr" converterClass="br.com.leonardo.config.HighlightingCompositeConverter"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%clr(%d{HH:mm:ss.SSS}){faint} %clr(%-5level) %clr([%15.15t]){faint} %clr(%-36.36logger{36}){cyan} - %m%n</pattern>
        </encoder>
    </appender>

    <root level="${log.level:-INFO}">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

# Criando Endpoints

Endpoints são o coração da sua API. Eles são classes que manipulam as requisições recebidas.

## Recomendações para organização dos endpoints

Crie pacotes em árvore para cada dominio da sua aplicação, como `com.mycompany.endpoints.users`, `com.mycompany.endpoints.products` e assim por diante.

Cada endpoint é representado por uma classe separada, que deve ser criado no pacote correspondente. Assim promovendo uma melhor organização dos seus recursos.

### Estrutura Básica

1.  Crie uma classe que estenda `HttpEndpoint<I, O>`, onde `I` é o tipo do corpo da requisição (Request Body) e `O` é o tipo do corpo da resposta (Response Body). Se não houver corpo, utilize o tipo `Void`.
2.  Anote a classe com `@Endpoint`, especificando a `uri` e o `method`.

O corpo da requisição e da resposta será automaticamente (de)serializado de/para JSON.

**Exemplo: Endpoint GET que retorna uma lista de usuários.**
```java
// DTO para a resposta
public class UserDTO {
    private String name;

    public UserDTO() {
    }

    public UserDTO(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}

// Endpoint
@Endpoint(url = "/users", method = HttpMethod.GET)
public class GetUsersEndpoint extends HttpEndpoint<Void, List<UserDTO>> {

    @Override
    public HttpResponse<List<UserDTO>> handle(HttpRequest<Void> request) {
        // Lógica para buscar usuários
        List<UserDTO> users = List.of(new UserDTO("John Doe"));
        return HttpResponse
                .<List<UserDTO>> builder()
                .statusCode(HttpStatusCode.OK)
                .body(users)
                .build();
    }
}
```

> As classes DTO (classes que serão serializadas para JSON na resposta HTTP) devem conter getters, setters e um construtor sem argumentos, para que dessa forma o framework possa serializar e deserializar os dados.

### Acessando Dados da Requisição

O método `handle` recebe um objeto `HttpRequest` que contém todas as informações da requisição.

#### Path Variables
Para definir path variables, use chaves `{}` na URI do endpoint (ex: `/users/{id}`). Os valores são acessíveis através do objeto `PathVariableMap` retornado por `request.pathVariables()`.

O `PathVariableMap` oferece métodos convenientes para acessar e converter os valores das variáveis de caminho para diferentes tipos:

-   `getString(String name)`: Retorna o valor da variável como `String`. Lança `HttpException` se a variável não for encontrada.
-   `getInteger(String name)`: Retorna o valor da variável como `Integer`. Lança `HttpException` se a variável não for encontrada ou não for um número inteiro válido.
-   `getLong(String name)`: Retorna o valor da variável como `Long`. Lança `HttpException` se a variável não for encontrada ou não for um número longo válido.
-   `getBoolean(String name)`: Retorna o valor da variável como `Boolean`. Lança `HttpException` se a variável não for encontrada ou não for "true" ou "false" (case-insensitive).
-   `exists(String name)`: Verifica se uma variável de caminho com o nome especificado existe.

**Exemplo:**
```java
@Endpoint(url = "/users/{id}", method = HttpMethod.GET)
public class GetUserByIdEndpoint extends HttpEndpoint<Void, UserDTO> {
    @Override
    public HttpResponse<UserDTO> handle(HttpRequest<Void> request) {
        final String userId = request.pathVariables().getString("id");
        // Lógica para buscar o usuário
        UserDTO user = new UserDTO(userId);
        return HttpResponse
                .<UserDTO> builder()
                .statusCode(HttpStatusCode.OK)
                .body(user)
                .build();
    }
}
```

#### Query Parameters
Query parameters (ex: `/search?q=my-query`) são acessados através do método `request.queryParameters()`, que retorna um objeto `QueryParameterMap`.

Como query parameters são opcionais por natureza em uma requisição HTTP, os métodos de `QueryParameterMap` retornam um `Optional<T>`. Isso permite um tratamento elegante para casos onde o parâmetro pode ou não estar presente, evitando `NullPointerException`s e tornando o código mais robusto e legível.

O `QueryParameterMap` oferece os seguintes métodos:

-   `getString(String name)`: Retorna um `Optional<String>` contendo o valor do parâmetro.
-   `getInteger(String name)`: Tenta converter o parâmetro para `Integer` e retorna um `Optional<Integer>`. Lança `HttpException` se o valor não for um número inteiro válido.
-   `getLong(String name)`: Tenta converter o parâmetro para `Long` e retorna um `Optional<Long>`. Lança `HttpException` se o valor não for um número longo válido.
-   `getBoolean(String name)`: Tenta converter o parâmetro para `Boolean` e retorna um `Optional<Boolean>`. Lança `HttpException` se o valor não for "true" ou "false".
-   `exists(String name)`: Verifica se um query parameter com o nome especificado existe na requisição.

**Exemplo:**
```java
@Endpoint(url = "/search", method = HttpMethod.GET)
public class SearchEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String query = request
                .queryParameters()
                .getString("q")
                .orElse("default");

        return HttpResponse
                .<String> builder()
                .statusCode(HttpStatusCode.OK)
                .body("Você buscou por: " + query)
                .build();
    }
}

```

#### Corpo da Requisição (Body)
Para endpoints que recebem um corpo (ex: POST, PUT), defina o tipo genérico `I` na sua classe. O framework desserializará o JSON para um objeto desse tipo, que pode ser acessado via `request.body()`.

**Exemplo:**
```java
// DTO para o corpo da requisição
public class CreateUserDTO {
    private String name;

    public CreateUserDTO() {
    }

    public CreateUserDTO(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}

@Endpoint(url = "/users", method = HttpMethod.POST)
public class CreateUserEndpoint extends HttpEndpoint<CreateUserDTO, Void> {
    @Override
    public HttpResponse<Void> handle(HttpRequest<CreateUserDTO> request) {
        CreateUserDTO userToCreate = request.body();
        System.out.println("Criando usuário: " + userToCreate.getName());
        return HttpResponse
            .<Void> builder()
            .statusCode(HttpStatusCode.CREATED)
            .build();
    }
}
```

#### Headers
Os cabeçalhos da requisição podem ser acessados através do método `request.headers()`, que retorna um objeto `HeaderMap`.

Como os cabeçalhos são opcionais em uma requisição HTTP, os métodos de `HeaderMap` retornam um `Optional<T>`. Isso permite um tratamento elegante para casos onde o cabeçalho pode ou não estar presente, evitando `NullPointerException`s e tornando o código mais robusto e legível.

O `HeaderMap` oferece os seguintes métodos:

-   `getString(String name)`: Retorna um `Optional<String>` contendo o valor do cabeçalho.
-   `getInteger(String name)`: Tenta converter o cabeçalho para `Integer` e retorna um `Optional<Integer>`. Lança `HttpException` se o valor não for um número inteiro válido.
-   `getLong(String name)`: Tenta converter o cabeçalho para `Long` e retorna um `Optional<Long>`. Lança `HttpException` se o valor não for um número longo válido.
-   `getBoolean(String name)`: Tenta converter o cabeçalho para `Boolean` e retorna um `Optional<Boolean>`. Lança `HttpException` se o valor não for "true" ou "false".
-   `exists(String name)`: Verifica se um cabeçalho com o nome especificado existe na requisição.

**Exemplo:**
```java
@Endpoint(url = "/echo-user-agent", method = HttpMethod.GET)
public class EchoUserAgentEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String userAgent = request
                .headers()
                .getString("User-Agent")
                .orElse("Unknown");
        return HttpResponse
                .<String> builder()
                .statusCode(HttpStatusCode.OK)
                .body("User-Agent: " + userAgent)
                .build();
    }
}
```

#### Propriedades do Middleware
Além dos dados padrão de uma requisição HTTP, o objeto `HttpRequest` pode carregar dados customizados adicionados por `Middlewares`. Isso permite uma comunicação segura e desacoplada entre a lógica de infraestrutura (como autenticação) e a lógica de negócio do endpoint.

Os dados são acessados através de métodos específicos no objeto `request`:

-   `getMiddlewareProperty(String key, Class<T> clazz)`: Recupera uma propriedade adicionada por um middleware, convertendo-a para o tipo `clazz`.
-   `hasMiddlewareProperty(String key)`: Verifica se uma propriedade existe.

Para um exemplo detalhado de como adicionar e consumir essas propriedades, consulte a seção [Middlewares](#middlewares).

### Construindo a Resposta (HttpResponse)

O objeto `HttpResponse` é o que seu endpoint deve retornar. Ele contém o status, cabeçalhos e o corpo da resposta. A construção de um `HttpResponse` é feita exclusivamente através do `HttpResponse.builder()`.

O builder permite configurar cada parte da resposta de forma fluente:
- `HttpResponse.builder()`: Inicia a construção de uma resposta.
- `.statusCode(HttpStatusCode)`: Define o código de status HTTP da resposta.
- `.header(String, Object)`: Adiciona um cabeçalho à resposta. Pode ser chamado múltiplas vezes para adicionar vários cabeçalhos.
- `.body(Object)`: Define o corpo da resposta. O objeto será serializado para JSON automaticamente.
- `.build()`: Finaliza a construção e retorna o objeto `HttpResponse`. Este método é genérico e infere o tipo do corpo da resposta (`O`) a partir do tipo definido na assinatura do método `handle` do seu `HttpEndpoint`.

**Exemplo Completo: Retornando um arquivo para download com cabeçalhos customizados.**
```java
@Endpoint(url = "/download-report", method = HttpMethod.GET)
public class DownloadReportEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        String reportContent = "id,name\n1,John Doe";

        return HttpResponse
            .<String> builder()
            .statusCode(HttpStatusCode.OK)
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=\"report.csv\"")
            .body(reportContent)
            .build();
    }
}
```

# Middlewares

Middlewares são componentes poderosos que interceptam requisições HTTP antes que elas cheguem ao `HttpEndpoint`. Eles permitem executar lógicas transversais, como autenticação, logging, compressão e manipulação de headers, de forma modular e reutilizável.

Para criar um middleware, estenda a classe abstrata `Middleware` e implemente o método `run`. A responsabilidade de invocar o próximo middleware na cadeia recai sobre o desenvolvedor, que deve chamar `super.next(request)`. Se `super.next(request)` não for chamado, a cadeia de execução será interrompida e o endpoint final será executado. Caso não haja um próximo middleware, a chamada a `super.next(request)` não terá efeito.

Middlewares são associados a um endpoint através do parâmetro `middlewares` da anotação `@Endpoint` e são executados na ordem em que são declarados.

### Comunicação entre Middlewares e Endpoints

Uma das funcionalidades mais importantes dos middlewares é a capacidade de compartilhar dados com os próximos middlewares na cadeia e com o `HttpEndpoint` final. Isso é feito através de um mapa de propriedades que é transportado dentro do objeto `HttpRequest`.

Um middleware pode, por exemplo, validar um token de autenticação e, em seguida, adicionar o ID do usuário autenticado a essas propriedades. O endpoint pode então acessar esse ID de forma segura, sem precisar conhecer os detalhes da lógica de autenticação.

O objeto `HttpRequest` fornece métodos específicos para essa comunicação:
-   `addMiddlewareProperty(String key, Object value)`: Adiciona um novo dado ao contexto da requisição.
-   `getMiddlewareProperty(String key, Class<T> clazz)`: Recupera um dado do contexto, fazendo o cast para o tipo esperado.
-   `hasMiddlewareProperty(String key)`: Verifica se um dado existe no contexto.
-   `removeMiddlewareProperty(String key)`: Remove um dado do contexto.
-   `clearMiddlewareProperties()`: Remove todos os dados do contexto.

**Exemplo: Cadeia de Middlewares (Logging e Autenticação)**

Vamos criar uma cadeia com dois middlewares: o primeiro para logging e o segundo para autenticação.

**1. Middleware de Logging**

Este middleware registra a chegada de uma requisição e, em seguida, passa o controle para o próximo componente na cadeia.

```java
public class LoggingMiddleware extends Middleware {
    private static final Logger logger = LoggerFactory.getLogger(LoggingMiddleware.class);

    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        logger.info("Requisição recebida para: {} {}", request.getMethod(), request.getUri());

        // Chama o próximo middleware na cadeia para continuar o processamento
        super.next(request);
    }
}
```

**2. Middleware de Autenticação**

Este middleware simula a validação de um token e adiciona o ID do usuário à requisição. Se a autenticação falhar, ele interrompe a cadeia lançando uma exceção.

```java
public class AuthenticationMiddleware extends Middleware {
    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        // Em um cenário real, você validaria um token (ex: JWT)
        final String token = request.headers().getString("Authorization").orElse(null);

        if (token == null || !token.equals("Bearer valid-token")) {
            // Lança uma exceção para interromper o fluxo e retornar um erro 401
            throw new HttpMiddlewareException(HttpStatusCode.UNAUTHORIZED, "Token inválido ou ausente.");
        }

        // Token válido, extrai o ID do usuário (simulado) e o adiciona à requisição
        String userId = "user-123";
        request.addMiddlewareProperty("authenticatedUserId", userId);

        // Chama o próximo middleware na cadeia para continuar o processamento
        super.next(request);
    }
}
```

**Aplicação em um Endpoint**

O endpoint agora aplica os dois middlewares. Eles serão executados na ordem declarada: primeiro `LoggingMiddleware`, depois `AuthenticationMiddleware`.

```java
@Endpoint(
    url = "/profile", 
    method = HttpMethod.GET, 
    middlewares = {LoggingMiddleware.class, AuthenticationMiddleware.class} // Aplica os middlewares em ordem
)
public class GetProfileEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        // Recupera o ID do usuário adicionado pelo middleware de autenticação
        String userId = request.getMiddlewareProperty("authenticatedUserId", String.class);

        // Lógica para buscar o perfil do usuário
        String userProfile = "Perfil do usuário: " + userId;

        return HttpResponse
                .<String>builder()
                .statusCode(HttpStatusCode.OK)
                .body(userProfile)
                .build();
    }
}
```

# Servindo Arquivos Estáticos: Do Básico ao Avançado

O FastLeaf se destaca por servir arquivos estáticos de forma simples e eficiente, um recurso essencial para aplicações web modernas que precisam entregar assets de frontend como HTML, CSS, JavaScript e imagens.

### Como Funciona

1.  **Habilite o Recurso:** Certifique-se de que a propriedade `http.server.static.content.enabled` está como `true` (que é o valor padrão).
2.  **Crie a Pasta de Conteúdo:** Crie uma pasta dentro de `src/main/resources`. O nome padrão é `static`. Você pode customizar esse caminho alterando a propriedade `http.server.static.content.path` no seu `http-server.properties`.
3.  **Adicione seus Arquivos:** Coloque seus arquivos diretamente nesta pasta. O framework mapeia a URI da requisição diretamente para a estrutura de arquivos e pastas que você criou.

O servidor define automaticamente o cabeçalho `Content-Type` da resposta com base na extensão do arquivo (ex: `.css` se torna `text/css`), garantindo que o navegador interprete o conteúdo corretamente.

### Exemplo Prático: Criando uma Mini-Página Web

Vamos servir uma página HTML que carrega sua própria folha de estilos e uma imagem.

**1. Estrutura de Arquivos**

Primeiro, organize seus arquivos em `src/main/resources/static` da seguinte forma:

```
src/main/resources/
└── static/
    ├── index.html
    ├── css/
    │   └── style.css
    └── img/
        └── logo.png 
```

**2. Conteúdo do HTML (`index.html`)**

Este arquivo linka para o CSS e a imagem usando caminhos relativos à raiz do servidor.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Página Estática</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <h1>Bem-vindo ao FastLeaf!</h1>
    <img src="/img/logo.png" alt="Logo">
</body>
</html>
```

**3. Conteúdo do CSS (`css/style.css`)**

Um estilo simples para confirmar que o arquivo foi carregado.

```css
body {
    background-color: #f0f0f0;
    font-family: sans-serif;
    text-align: center;
}
```

**4. Imagem**

Coloque qualquer arquivo de imagem chamado `logo.png` dentro da pasta `src/main/resources/static/img/`.

**5. Acessando no Navegador**

Após iniciar seu servidor, acesse `http://localhost:9000/index.html`. O navegador irá carregar o HTML, que por sua vez fará requisições para `/css/style.css` e `/img/logo.png`, e o FastLeaf servirá todos os três arquivos.

### Regra de Precedência: Arquivos Estáticos vs. Endpoints de API

É fundamental entender como o FastLeaf decide o que servir quando uma URI pode corresponder tanto a um arquivo estático quanto a um endpoint de API.

**A regra é simples: arquivos estáticos têm prioridade sobre os endpoints de API.**

Quando uma requisição chega, o servidor executa a seguinte lógica:
1.  Primeiro, ele verifica se um arquivo correspondente à URI da requisição existe na pasta de conteúdo estático (ex: `resources/static`).
2.  **Se o arquivo existir**, ele é servido imediatamente. O sistema de roteamento que busca por `@Endpoint`s nem chega a ser consultado.
3.  **Se nenhum arquivo for encontrado**, o servidor então prossegue para o `HttpEndpointResolver` para encontrar um endpoint de API que corresponda à URI e ao método HTTP.

**Exemplo de Conflito Direto:**

O conflito real acontece quando a URI de uma requisição corresponde **exatamente** a um arquivo estático e a um endpoint.

Imagine que você tem:
*   Um arquivo estático: `src/main/resources/static/profile` (um arquivo chamado `profile`, sem extensão)
*   Um endpoint de API: `@Endpoint(url = "/profile", method = HttpMethod.GET)`

Nesse caso, uma requisição `GET` para `/profile` fará com que o servidor encontre e sirva o arquivo estático `profile`. O endpoint da API, apesar de corresponder à mesma URL, **nunca será alcançado**, pois a verificação de arquivos estáticos tem prioridade.

Uma requisição para `/profile.html` e um endpoint para `/profile` não conflitam, pois as URIs são diferentes.

> **Recomendação:** Para evitar conflitos e manter uma arquitetura clara, agrupe seus endpoints de API sob um prefixo comum (ex: `/api/v1/*`). Isso cria uma separação nítida entre as rotas de API e as URLs usadas para servir o frontend da sua aplicação.
