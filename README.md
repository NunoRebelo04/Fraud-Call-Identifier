# Fraud Call Identifier

Aplicação Android desenvolvida em Kotlin que permite identificar chamadas potencialmente fraudulentas.

O objetivo é fornecer uma forma simples e rápida de avaliar o risco de um número e automatizar decisões de bloqueio com base nas preferências do utilizador.

## Funcionalidades

### Pesquisa e análise
- Pesquisa manual de números de telefone
- Normalização automática do número (remoção de caracteres não numéricos)
- Classificação de risco:
  - SAFE
  - SUSPECT
  - SPAM

### Informação detalhada
- Número de denúncias
- Categoria do spam
- Última data reportada

### Configuração do utilizador
- Bloquear números suspeitos
- Bloquear números spam
- Ativar/desativar dark mode

### Bloqueio inteligente
- Decisão automática de bloqueio baseada em:
  - nível de risco
  - preferências do utilizador

### Notificações
- Notificação automática quando uma chamada seria bloqueada

### Histórico
- Histórico das pesquisas realizadas (em memória)
- Atualização reativa via Flow

---

## Arquitetura

A aplicação segue uma arquitetura baseada em **MVVM (Model-View-ViewModel)**, com separação de responsabilidades.

### UI / Presentation (`search`)
- Construída com Jetpack Compose
- `SearchScreen` responsável pela UI
- Observa estado via `StateFlow`
- Eventos one-shot via `SharedFlow` (ex:notificações)
- Suporte a Dark Mode dinâmico
- Material Design
- Feedback visual baseado no risco (cores por nível)

### ViewModel
- `SearchViewModel`
- Gestão de estado (`SearchUiState`)
- Orquestração entre UI e domínio
- Validação de Input
- Emissão de eventos de bloqueio

### Domain
-Entidades:
  -`PhoneNumberInfo`
  -`RiskLevel`
  -`UserCallSettings`
  
-Use Case:
  -`AnalyzePhoneNumberUseCase`
  
-Interface:
  -`FraudRepository`

### Data
- Implementação concreta do repositório : `JsonFraudRepository`
- Fonte de dados local via JSON (assets)
- Conversão e normalização dos dados

## Lógica de negócio

A lógica principal está encapsulada no use case:

`AnalyzePhoneNumberUseCase`

Responsável por:
- Obter informação do número
- Definir o nível de risco
- Calcular se deve ser bloqueado


```text
shouldBlock = riskLevel + userSettings
````

---
## Fontes de dados

Os números fraudulentos são carregados a partir de um ficheiro JSON local (`assets/fraud_numbers.json`):
-Parsing manual com `org.json`
-Mapeamento para modelo de domínio
-Lazy loading da lista

## Gestão de Estado

Os números fraudulentos são carregados a partir de um ficheiro JSON local (`assets/fraud_numbers.json`):
-`StateFlow` -> Estado da UI
-`SharedFlow` -> Eventos (ex: notificações)
-Atualizações reativas do histórico


## Notificações

Implementadas via `NotificationManager`:
-Canal Dedicado (`Fraud Alerts`)
-Notificação disparada quando `shouldBlock = true`

## Testes

Foram implementados unit tests para validar a lógica de negócio, nomeadamente o comportamento do `AnalyzePhoneNumberUseCase`, garantindo que diferentes combinações de risco e configurações produzem o resultado esperado.


## Limitações

-Persistência em memória (não usa Room/DataStore)
-Histórico limitado ao ciclo de vida da app
-Base de dados estática (sem sync periódico)

---

## Decisões Técnicas

-Uso de MVVM + Clean Architecture para escalabilidade
-`Flow` em vez de LiveData para maior controlo reativo
-JSON local para simplificação do challenge
-Compose para UI moderna e declarativa

