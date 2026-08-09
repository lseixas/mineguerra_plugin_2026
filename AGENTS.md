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
| Times + leaderboard | `teams/` — ver [`docs/TEAMS_AND_LEADERBOARD.md`](docs/TEAMS_AND_LEADERBOARD.md) |
| Bandeiras + armas lendárias | `teams/flag/`, `weapons/WeaponOwnershipService` — [`docs/FLAGS_AND_LEGENDARY_WEAPONS.md`](docs/FLAGS_AND_LEGENDARY_WEAPONS.md) |
| Comandos de guerra | `fluxCommands/` |
| Documentação | [`docs/`](docs/) |

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

## Times e kills

- `/team` — `mineguerra.team` — criar times, assign tag (prefixo tab/nametag)
- `/mg` — `mineguerra.admin` — leaderboard on/off, kills set/reset, `weapons status|reset`
- `/team flag` — bandeira por time (respawn / eliminação) — ver [`docs/FLAGS_AND_LEGENDARY_WEAPONS.md`](docs/FLAGS_AND_LEGENDARY_WEAPONS.md)
- Kill válida: killer e vítima em times **diferentes**
- Persistência: `plugins/mineguerra_plugins/teams-data.yml`

## Ferramentas de villager (staff)

- `/grantTraderTool <tipo>` — bastão: **clique no chão** = spawn; **clique no NPC** = aplicar trades
- `/grantTraderToolKit` — kit dos 4 exploradores de armas (`oceano`, `profundezas`, `nether`, `end`)
- Perm: `mineguerra.tradertool` | Comando legado: `/spawnvillager <tipo>`

## Armas migradas

As **4 armas** usam `WeaponItemService` (lore + PDC + CMD), `WeaponMessages` (chat) e `VanillaCooldownSync` (barra cinza na hotbar nas ativas com CD).
