# TreinoIA — App Android nativo (Kotlin + Jetpack Compose)

Gera treinos de academia personalizados chamando a **API da Anthropic (Claude)**. O escopo é travado por system prompt: só treino de academia, nunca dieta/cardio/outras atividades. UX redesenhada no padrão atual de apps fitness (pílulas de seleção, cards numerados, CTA fixo na zona do polegar).

## 🚀 Como gerar o APK de verdade (sem instalar nada)

Meu ambiente de sandbox não tem acesso ao Maven do Google, então não consigo compilar o `.apk` aqui. Mas incluí um **workflow de GitHub Actions** (`.github/workflows/build-apk.yml`) que compila o app automaticamente na nuvem do GitHub — isso resolve o problema sem você precisar instalar Android Studio.

**Passo a passo:**
1. Crie um repositório novo no GitHub (pode ser privado).
2. Suba todo o conteúdo desta pasta (`treinoia-native`) pra ele:
   ```bash
   cd treinoia-native
   git init
   git add .
   git commit -m "TreinoIA inicial"
   git branch -M main
   git remote add origin https://github.com/SEU_USUARIO/SEU_REPO.git
   git push -u origin main
   ```
3. No GitHub, vá na aba **Actions** do repositório — o workflow "Build APK" já dispara sozinho no push.
4. Quando terminar (ícone verde ✅), abra a execução → seção **Artifacts** → baixe `treinoia-debug-apk`.
5. Dentro do zip baixado está o `app-debug.apk` — transfira pro celular e instale (pode precisar habilitar "instalar de fontes desconhecidas" no Android).

Se preferir compilar localmente em vez de usar o Actions, também funciona: abra a pasta no **Android Studio** e rode normalmente (Build → Build APK(s)).

## Redesign de UX (padrão atual de apps fitness)
- **Seletores em pílula** (`ui/components/Selectors.kt`) no lugar de dropdowns — toque único, rolagem horizontal, ícones no objetivo
- **Cards de exercício numerados** com badge circular e ícone de descanso, mais fáceis de escanear durante o treino
- **CTA fixo embaixo da tela** ("GERAR TREINO DE HOJE") — fica na zona do polegar, como Nike Training Club/Freeletics
- **Transição suave (Crossfade)** entre formulário → carregando → resultado
- Estatísticas rápidas (nº de exercícios, séries totais) no topo do resultado

## Como funciona
- **Formulário** (`FormScreen.kt`): objetivo, nível, dias/semana, foco do treino, equipamentos, restrições, tempo disponível — organizado em seções com `FormSection`.
- **Chamada à IA** (`ClaudeApiClient.kt`): usa `tool_choice` forçado pra garantir que o Claude sempre responda em JSON estruturado (nome, séries, reps, descanso, observação) — não depende de parsear texto livre.
- **Restrição de escopo**: o `system` prompt trava a IA em treino de academia — se alguém tentar pedir dieta ou cardio livre, a IA ignora e gera treino de academia mesmo assim.
- **Armazenamento da chave**: `PreferencesManager.kt` guarda em `SharedPreferences` local, excluído de backups automáticos (`backup_rules.xml` / `data_extraction_rules.xml`).

## Chave da API
1. Gere uma chave em https://console.anthropic.com/settings/keys
2. Abra o app → "Configurar chave da API do Claude" → cole → Salvar.

⚠️ **Aviso de segurança**: a chave fica embutida no app (chamada feita direto do celular pra API da Anthropic). Qualquer pessoa que descompilar o APK ou interceptar a requisição pode ver e usar essa chave. Isso é aceitável pra uso pessoal, mas **não publique esse APK pra terceiros usarem sem antes migrar a chamada pra um backend** que guarda a chave do lado do servidor.

## Modelo usado
`claude-sonnet-5` por padrão (bom equilíbrio custo/qualidade pra essa tarefa). Se quiser mais barato, troque a constante `MODEL` em `ClaudeApiClient.kt` para `claude-haiku-4-5-20251001`.

## Estrutura
```
app/src/main/java/com/treinoia/app/
├── MainActivity.kt              # entrada, monta o tema, Crossfade entre telas
├── data/
│   ├── WorkoutModels.kt         # modelos de dados (Workout, Exercise, enums do form)
│   ├── ClaudeApiClient.kt       # chamada à API da Anthropic
│   └── PreferencesManager.kt    # guarda a chave da API localmente
├── viewmodel/
│   └── WorkoutViewModel.kt      # estado da tela + lógica de geração
└── ui/
    ├── theme/                   # cores, tipografia, tema Material3 (dark, laranja/concreto)
    ├── components/
    │   └── Selectors.kt         # pílulas de seleção única/múltipla, seções do formulário
    └── screens/                 # FormScreen, LoadingScreen, ResultScreen, ConfigScreen, ErrorScreen
```

## Possíveis próximos passos
- Histórico de treinos (Room database)
- Botão de "check" por exercício durante a execução, com cronômetro de descanso
- Progressão de carga (perguntar peso usado e sugerir ajuste na próxima geração)
- Migrar a chamada da API pra um backend, se for publicar pra outras pessoas
- Publicar na Play Store (precisa de conta de desenvolvedor + assinatura release)

