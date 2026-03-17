Fraud Call Identifier


Aplicação Android desenvilva na linguagem Kotlin que permite identificar chamadas fradulentas
A app permite analisar números, classificar o seu nível de risco e bloquear chamadas




.Funcionalidades

-Pesquisa Manual de números de telefone;
-Classificação de Risco;
-Configuração de Bloqueio ( tanto para números suspeitos como spam);
-Histórico de Pesquisas ( as últimas 10);
-Notificações automáticas para chamadas bloqueadas;
-Base Local de números suspeitos


.Arquitetura

A aplicação segue uma arquitertura baseada em MVM (Model-View-ViewModel), com separação de responsabilidades:

UI / Presentation (search)
-Construída com JetPack Compose
-Responsável pela interação com o utilizador
-Observa estado via StateFlow

ViewModel
-Gere estado da UI
-Coordena ações entre UI e domain
-Emite eventos (ex: notificações via SharedFlow)

Domain
-Contém lógica de negócio
-UseCase responsável pela análise de números
-Interfaces de repositório

Data
-Implementação concreta do repositório
-Atualmente em memória (InMemoryFraudRepository)




.Lógica de negócio

A decisão de bloqueio é baseada em:
-Nível de risco (SAFE, SUSPECT, SPAM)
-Preferências do user

shouldBlock=riskLevel + userSettings




.Tecnologias Utilizadas
-Kotlin
-Jetpack Compose
-MVVM
-Coroutines
-StateFlow / SharedFlow
-Notification API (Android)
