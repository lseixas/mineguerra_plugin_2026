# MineGuerra Plugins — guia para agentes

Plugin Spigot/Paper para o evento **Minecraft Guerra**. Foco atual: armas customizadas com habilidades, mantendo progressão **vanilla** sempre que possível.

## Stack

- Java 21
- Spigot API `1.21.8-R0.1-SNAPSHOT` (ver [`build.gradle`](build.gradle))
- Gradle + `run-paper` (`./gradlew runServer`, Minecraft 1.21)
- JAR: `plugin_mineguerra.jar`
- Main: [`main/java/org/lseixas/mineguerra_plugins/Mineguerra_plugins.java`](main/java/org/lseixas/mineguerra_plugins/Mineguerra_plugins.java)

## Onde está o quê

| Área | Caminho |
|------|---------|
| Armas (legado por pacote) | `main/java/.../soulflayerbow/`, `dragonslayer/`, `stormrider/`, `doomhammer/` |
| Núcleo compartilhado (padrão novo) | `main/java/.../weapons/` |
| Villagers / trades | `traders/VillagerSpawner.java`, `TraderToolListener` (bastão blaze rod) |
| No-spawn (áreas) | `nospawn/` — lightning rod delimita cuboide sem spawn de mobs |
| No-break (áreas) | `nobreak/` — pincel delimita cuboide sem quebra/colocação de blocos |
| Times + leaderboard | `teams/` — ver [`docs/TEAMS_AND_LEADERBOARD.md`](docs/TEAMS_AND_LEADERBOARD.md) |
| Bandeiras + armas lendárias | `teams/flag/`, `weapons/WeaponOwnershipService` — [`docs/FLAGS_AND_LEGENDARY_WEAPONS.md`](docs/FLAGS_AND_LEGENDARY_WEAPONS.md) |
| Comandos de guerra | `fluxCommands/` — `/startGuerra` |
| Cronograma do evento | `war/` — fases, PvP toggle, hardcore, world border — [`docs/WAR_SCHEDULE.md`](docs/WAR_SCHEDULE.md) |
| Métricas do evento | `metrics/` — JSONL + snapshot só na guerra ativa — [`docs/METRICS.md`](docs/METRICS.md) |
| Documentação | [`docs/`](docs/) |
| Resource pack (CMD 10001–10004) | [`resourcepack/MineGuerra_Weapons/`](resourcepack/MineGuerra_Weapons/) — [`docs/RESOURCE_PACK.md`](docs/RESOURCE_PACK.md) |
| Client audit (Fabric allowlist) | `clientaudit/` — [`docs/CLIENT_AUDIT.md`](docs/CLIENT_AUDIT.md) (`enabled: false` até o mod existir) |

Cada arma: `*Factory` (item), `*Listener` (eventos + estado), `skills/` (efeitos).

## Regras de trabalho

1. **Não refatorar todas as armas de uma vez** — seguir [`docs/REFACTOR_ROADMAP.md`](docs/REFACTOR_ROADMAP.md).
2. **Não alterar balanceamento** (CD, dano, chance) sem pedido explícito; padronizar mecanismo e UX.
3. **Vanilla first** — preferir `player.setCooldown(Material)`, dano vanilla, PDC + CMD; evitar dependências externas.
4. Código novo de identificação/CD/mensagens deve usar [`docs/STANDARDS_TARGET.md`](docs/STANDARDS_TARGET.md) e o pacote `weapons`.
5. Após mudar comportamento de uma arma, atualizar [`docs/WEAPONS_CATALOG.md`](docs/WEAPONS_CATALOG.md).

## Documentos essenciais

- [`docs/README.md`](docs/README.md) — índice
- [`docs/PROJECT_CONTEXT.md`](docs/PROJECT_CONTEXT.md) — evento, comandos, distribuição
- [`docs/WEAPONS_CATALOG.md`](docs/WEAPONS_CATALOG.md) — catálogo factual das armas
- [`docs/ARCHITECTURE_AS_IS.md`](docs/ARCHITECTURE_AS_IS.md) — como o código está hoje
- [`docs/STANDARDS_TARGET.md`](docs/STANDARDS_TARGET.md) — contratos alvo (normativo)
- [`docs/REFACTOR_ROADMAP.md`](docs/REFACTOR_ROADMAP.md) — ordem de migração e checklist

## Cronograma do evento

- `/startGuerra <start|stop|status|phase|reload>` — `mineguerra.admin` (aliases `/guerra`, `/sg`)
- 6 fases wall-clock em `plugins/mineguerra_plugins/war-schedule.yml`: `inicio` (PvP off),
  `pvp-on`, `trapaceiro` (spawn agendado), `julgamento` (bandeiras vivas), `hardcore`,
  `fechar-centro` (world border)
- Estado em `war-state.yml`; fases já vencidas são reaplicadas em silêncio no restart
- Detalhes: [`docs/WAR_SCHEDULE.md`](docs/WAR_SCHEDULE.md)

## Times e kills

- `/team` — `mineguerra.team` — criar times, assign tag (prefixo tab/nametag)
- `/mg` — `mineguerra.admin` — leaderboard on/off, kills set/reset, `weapons status|reset`, `metrics status|dump|open`
- `/team flag` — bandeira por time (respawn / eliminação) — ver [`docs/FLAGS_AND_LEGENDARY_WEAPONS.md`](docs/FLAGS_AND_LEGENDARY_WEAPONS.md)
- Kill válida: killer e vítima em times **diferentes**
- Persistência: `plugins/mineguerra_plugins/teams-data.yml`
- Métricas (análise): `plugins/mineguerra_plugins/metrics/<session>/` — ver [`docs/METRICS.md`](docs/METRICS.md)

## Ferramentas de villager (staff)

- `/grantTraderTool <tipo>` — bastão: **clique no chão** = spawn; **clique no NPC** = aplicar trades (`oceano`, `profundezas`, `nether`, `end`, `trim`, `trapaceiro`, …)
- `/grantTraderToolKit` — kit dos 4 exploradores de armas (`oceano`, `profundezas`, `nether`, `end`)
- `/grantNoSpawnTool` — lightning rod: delimita cuboide sem spawn (`mineguerra.nospawn`); `list` / `clear`
- `/grantNoBreakTool` — pincel: delimita cuboide sem quebra/colocação de bloco (`mineguerra.nobreak`); `list` / `clear`
- Perm: `mineguerra.tradertool` | Comando legado: `/spawnvillager <tipo>`

## Armas migradas

As **4 armas** usam `WeaponItemService` (lore + PDC + CMD), `WeaponMessages` (chat) e `VanillaCooldownSync` (barra cinza na hotbar nas ativas com CD).

## Pre-event harden (em andamento / pos-evento)

Ver tracking e checklist: [`docs/PRE_EVENT_HARDEN.md`](docs/PRE_EVENT_HARDEN.md).

- Grants e `/startGuerra` exigem `mineguerra.admin`
- Claim de arma lendária persiste com holder offline (trade não reabre no logout)
- Bandeiras: captura via break **e** explosão; `/mg revive`; `/team flag repair`
- Bandeira limpa e protege um raio 3; banner fica sem física (não cai sem suporte)
- Nametags sincronizadas entre scoreboards por jogador
- Skins em modo offline: SkinsRestorer (plugin externo) — [`docs/SKINS_OFFLINE.md`](docs/SKINS_OFFLINE.md)

## Balanceamento (fim de semana)

Fonte de verdade de preços/CDs: [`docs/BALANCE.md`](docs/BALANCE.md).  
Testes: `./gradlew test` (JUnit 5 + MockBukkit).
