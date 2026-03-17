# Fraud Call Identifier

Aplicação Android desenvolvida em Kotlin que permite identificar chamadas potencialmente fraudulentas.

A aplicação permite analisar números, classificar o seu nível de risco e bloquear chamadas de acordo com as preferências do utilizador.

## Funcionalidades

- Pesquisa manual de números de telefone
- Classificação de risco
- Configuração de bloqueio
  - números suspeitos
  - números spam
- Histórico de pesquisas (últimas 10)
- Notificações automáticas para chamadas bloqueadas
- Base local de números suspeitos

## Arquitetura

A aplicação segue uma arquitetura baseada em **MVVM (Model-View-ViewModel)**, com separação de responsabilidades.

### UI / Presentation (`search`)
- Construída com Jetpack Compose
- Responsável pela interação com o utilizador
- Observa estado via `StateFlow`

### ViewModel
- Gere o estado da UI
- Coordena ações entre UI e domain
- Emite eventos (ex: notificações via `SharedFlow`)

### Domain
- Contém lógica de negócio
- UseCase responsável pela análise de números
- Interfaces de repositório

### Data
- Implementação concreta do repositório
- Atualmente em memória (`InMemoryFraudRepository`)

## Lógica de negócio

A decisão de bloqueio é baseada em:
- Nível de risco (`SAFE`, `SUSPECT`, `SPAM`)
- Preferências do utilizador

```text
shouldBlock = riskLevel + userSettings
````

## Testes

Foram implementados unit tests para validar a lógica de negócio, nomeadamente o comportamento do `AnalyzePhoneNumberUseCase`, garantindo que diferentes combinações de risco e configurações produzem o resultado esperado.
