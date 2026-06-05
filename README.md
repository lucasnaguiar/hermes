# Teste Técnico - Sistema de Agendamento de Transferências

Optei por dar um nome à aplicação apenas para facilitar a referência, por isso "Hermes"

Como a versão do Vue não foi especificada, optei pela versão 3 com Composition API por ser a 
mais moderna e atual. Tenho experiência também com Vue 2 e Options API.

## Stack

O teste solicitou Java 11 por isso usei o Spring Boot 2.7, então essas foram as versões base do projeto.

Para persistência usei Spring Data JPA como abstração da camada de repositório, com H2 como banco 
in-memory conforme solicitado, sem dependências externas para facilitar o setup local.

O versionamento do banco e os seeds iniciais são gerenciados pelo Liquibase, com changesets em YAML.

Utilizei Lombok para redução de boilerplate em entidades e DTOs, e Spring Validation para validação 
declarativa nas requisições com Bean Validation.

- Java 11 · Spring Boot 2.7 · Liquibase · H2 · Lombok
- Vue 3 · TypeScript · Vite · Pinia · Vue Router · Naive UI · Tailwind CSS 4

## Decisões arquiteturais

### API

A API segue uma arquitetura em camadas Controller, Service e Repository com responsabilidades bem 
delimitadas, visando ser escalável mas tendo cuidado para não aumentar a complexidade além do 
necessário para o que foi solicitado no teste. As regras de cálculo de taxa estão isoladas no 
`FeeCalculationService`, desacopladas do fluxo de agendamento para reutilização no endpoint de 
simulação do agendamento com a taxa.

Implementei a validação do saldo da conta apesar de não ser citada no teste, controlando se é possível 
realizar o agendamento considerando que o saldo pode estar comprometido em outras transferências 
previamente agendadas.

Criei um campo fingerprint que gera um hash com os dados do agendamento e salva no banco de dados. 
Fiz isso pensando em garantir o mínimo de idempotência, prevenindo duplicidade acidental. Devido a 
isso, é bloqueado agendar a mesma transferência com os mesmos dados enquanto o agendamento existir 
no banco. Em um sistema real eu separaria isso de uma estratégia de idempotência mais explícita, 
provavelmente trabalhando com uma chave de idempotência enviada no header da requisição.

As regras de taxa são armazenadas no banco de dados via seed do Liquibase, seguindo o conceito de 
**Table-Driven Design**. A intenção foi evitar valores hardcoded para as taxas, já que numa 
aplicação real elas podem ser atualizadas ou removidas sem necessidade de alteração de código ou 
redeploy. Seguindo essa linha de pensamento, em cada agendamento gravei os dados da taxação 
aplicada para ter um snapshot da situação no momento do agendamento. Normalmente desenvolveria um 
ledger para registrar as movimentações financeiras, mas como o foco do teste estava no agendamento, 
optei por não implementá-lo, penso que o próprio histórico de agendamentos já cobre o extrato solicitado.

A seleção da regra aplicável é feita por uma query JPQL que busca a faixa correspondente ao número 
de dias entre a data de agendamento e a data de transferência. O cálculo em si fica isolado no 
`FeeCalculationService`, que recebe a regra já selecionada e aplica a fórmula, mantendo 
responsabilidade única e facilitando testes unitários.

### SPA

Adotei a biblioteca de componentes Naive UI para agilizar o desenvolvimento da interface.

Os componentes foram organizados seguindo Atomic Design, separando em atoms, molecules, organisms, templates e views. 
A intenção foi garantir reuso sem criar abstrações desnecessárias.

O estado das etapas de agendamento é gerenciado pelo Pinia. Cada etapa é um componente 
independente que lê e escreve diretamente na store. A navegação usa `<component :is>` com 
um array de componentes indexado pelo passo atual, minha ideia foi manter as etapas dinâmicas, 
evitando cadeias de `v-if` e facilitando a adição de novas etapas no futuro.

A camada de serviços (`services/`) concentra todas as chamadas HTTP. O interceptor do Axios 
exibe uma notificação global para erros de servidor (5xx) e de rede, enquanto erros de 
negócio (4xx) são tratados inline em cada componente. Essa centralização também facilitaria 
a implementação de autenticação futuramente, onde o interceptor seria o ponto natural para anexar 
tokens nas requisições e tratar respostas 401, trabalhando em conjunto com cookies httpOnly. 

Os testes unitários foram implementados ao final, após a conclusão de tudo que foi descrito nos requisitos do desafio. 
A decisão foi priorizar a entrega do que foi explicitamente solicitado antes de adicionar cobertura de testes, 
que apesar de eu considerar ser uma boa prática, não estava entre os critérios descritos e eu queria garantir o prazo. 
São testes que cobrem apenas as services mais importantes, do agendamento e do calculo da taxa.

## Rodando localmente

### Pré-requisitos

- Java 11+
- Node.js 18+

### Execução simplificada

Normalmente containerizaria a aplicação com Docker para simplificar o setup, mas optei por manter a execução local para não aumentar a complexidade além do que foi solicitado no teste.

Há um script na raiz do projeto que sobe a API e a SPA em paralelo com um único comando.

```bash
./start.sh
```

`Ctrl+C` encerra os dois processos juntos.

### API

```bash
cd hermes-api
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080/api/v1`.

O console do H2 fica disponível em `http://localhost:8080/api/v1/h2-console` com as credenciais:
- **JDBC URL:** `jdbc:h2:mem:hermes`
- **Usuário:** `sa`
- **Senha:** _(vazio)_

### SPA

```bash
cd hermes-spa
npm install
npm run dev
```

A aplicação abre em `http://localhost:5173`.

### Contas disponíveis para teste

O banco é populado automaticamente com as seguintes contas:

| Número | Saldo |
|---|---|
| 1234567890 | R$ 10.000,00 |
| 0987654321 | R$ 5.000,00 |
| 1122334455 | R$ 25.000,00 |
| 5566778899 | R$ 1.500,00 |
| 9988776655 | R$ 50.000,00 |
| 4433221100 | R$ 7.500,00 |


Executar testes unitários:

```bash
./mvnw test
```