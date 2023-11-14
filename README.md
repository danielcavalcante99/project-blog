# blog spring
API utilizando Spring Boot com PostgreSQL para reformularmos nosso blog.

### Introdução
1) Tecnologias utilizadas
2) Requisitos para montagem de ambiente
3) Como configurar o projeto no ambiente local
4) Como Autenticar para acessar os demais endpoints
5) Como Inserir/Atualzar Post e seus comentários
6) Como Inserir/Atualzar Album e seus fotos
7) OpenAPI | Swagger (Documentação dos endpoints)


##
### 1) Tecnologias utilizadas
- Spring Web
- Spring Hateoas
- Spring Actuator 
- Spring Data JPA
- Spring Security
- Documentação OpenApi (Swagger)
- Driver para conexão Postgresql
- Mapper Structs
- Teste de unidade com Junit
- PostgreSQL
- JWT
- Logger Sl4j

##
### 2) Requisitos para montagem de ambiente
- Java 17;
- Postgresql;

##
### 3) Como configurar o projeto no ambiente local
- Após clonar e importar o projeto como maven, é necessário entrar na pasta raiz do projeto e rodar esse comando:
~~~
mvn clean compile -DskipTests
~~~
Depois da o refresh no projeto para exibir o que foi gerado no target

![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/d8124197-d8b9-4849-bdd6-96a10766f524)

- Se necessário setar as propriedades de conexão que está no arquivo application-dev.properties referente o banco:

![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/eb44ad59-324a-42d2-880d-b79d8a7e6786)

##
### 4) Como Autenticar para acessar os demais endpoints
Acesse primeiramente o Swagger http://localhost:8080/swagger-ui/swagger-ui/index.html

#### METHOD: POST
#### ENDPOINT: "/v1/users/register"
Primeiro vocês irão criar um usuário, deixei esse endpoint liberado para facilitar, mas em ambiente de produção seria diferente.
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/7174f900-d288-4851-9fa1-56b82a25054b)
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/b766b6af-9c38-437e-bda4-d9884bf9d828)

#### METHOD: POST
#### ENDPOINT: "/v1/auth/login"
Vamos agorar gerar o token e copia-lo.
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/3d28200b-8ec8-4285-8fd6-d1cb2575bbbc)
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/29db8c32-2adf-48d2-9987-43e629e0769b)

No inicio da pagina clicar no botão "Authorize".
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/6774347d-f494-4563-89d6-d60687460f4b)

Colar o token e clicar no botão "Authorize".
![image](https://github.com/danielcavalcante99/blog-spring/assets/74054701/1b937b4e-fca9-40f9-8b9d-34d302caff16)

Pronto agora você já consegue acessar os demais endpoints.

##
### 5) Como Inserir/Atualzar Post e seus comentários

- Criando Post
#### METHOD: POST
#### ENDPOINT: "/v1/posts/register"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/f6b7cdbc-9613-41af-841f-67b7eaabb84e)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/f33a63be-1ee5-4824-9fbf-a4ecac660d89)

- Comentando Post
#### METHOD: POST
#### ENDPOINT: "/v1/comments/register"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/f14b2206-c01d-4e64-b08c-ee28c801afce)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/1c7daafb-4815-4c13-86cf-e3ca99d87237)


- Buscando Post pelo ID
#### METHOD: GET
#### ENDPOINT: "/v1/posts/{id}"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/9a409130-2477-4c33-b007-b016e539a566)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/68da8c06-c8d1-4a2f-8f3a-8fffc8f9d981)

Atenção: dei um exemplo aqui mostrando alguns serviço referente o Post e Comentário, mas tem outros serviços referente o Post e dos comentários.

##
### 6) Como Inserir/Atualzar Album e seus fotos


- Criando Album
#### METHOD: POST
#### ENDPOINT: "/v1/albums/register"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/9005173b-008d-4f54-9ede-fd568e683344)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/d4abbd79-90e0-45d9-8751-3c160277bcb1)

- Criando foto no album
#### METHOD: POST
#### ENDPOINT: "/v1/photos/register"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/bc531ae0-24c6-404e-a837-c03727227002)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/34785593-434c-4b54-9683-b4840b5114fc)

- Buscando Album pelo ID
#### METHOD: GET
#### ENDPOINT: "/v1/albums/{id}"
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/7e92469b-e112-4131-8891-7491f5e0c618)
![image](https://github.com/danielcavalcante99/project-blog/assets/74054701/4c764bc0-f17d-4c4e-a2eb-a9d0818ce616)

Atenção: dei um exemplo aqui mostrando alguns serviço referente o Album e fotos, mas tem outros serviços referente o Album e suas fotos.

##
### 7) OpenAPI | Swagger (Documentação dos endpoints)
Documentei todos os endpoints, podem acessar para facilitar o uso.
link: http://localhost:8080/swagger-ui/swagger-ui/index.html

