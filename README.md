# API_Transacoes_Bancarias

## Configurando o ambiente

É necessário instalar Java, PostgreSQL e Maven. Para configurar o PostgreSQL basta colocar o usuário e senha no application.proprierties

## Iniciando a aplicação

Para instalar os pacotes das bibliotecas, execute:

`mvn clean install`

depois, para executar a aplicação execute:

`mvn spring-boot:run`

## Swagger

Para acessar a documentacao da API acesse:

`http://localhost:8080/swagger-ui/index.html#/`

## Jacoco
Para rodar a biblioteca de cobertura de testes, execute o comando:

`mvn clean test jacoco:report`

O relatório será gerado no arquivo 

`target/site/jacoco/index.html`
