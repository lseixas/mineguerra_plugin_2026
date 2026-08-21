# Métricas do evento

Coleta de gameplay **somente enquanto a guerra está ativa** (`/startGuerra start` → `stop`), para análise pós-evento.

## Onde fica

```
plugins/mineguerra_plugins/metrics/
  active-session.txt          # ponteiro da sessão em gravação (apagado no stop)
  <sessionId>/
    events.jsonl              # timeline append-only
    snapshot.json             # agregados por player/time (reescrito ~30s)
```

`sessionId` = timestamp UTC `yyyyMMdd'T'HHmmss'Z'`.

No restart com o cronograma ainda rodando, a sessão **retoma** o mesmo diretório (via `active-session.txt` + `snapshot.json`).

## Lifecycle

| Momento | Ação |
|---------|------|
| `/startGuerra start` | Nova sessão + `session_start` |
| Restart com guerra running | `session_resume` no mesmo `sessionId` |
| `/startGuerra stop` | `session_stop`, flush, para de gravar |
| `/startGuerra reset` | `session_reset`, flush, limpa ponteiro |
| Plugin disable | `session_shutdown` + flush |

## JSONL (eventos)

Uma linha JSON por evento. Tipos principais:

| type | Quando |
|------|--------|
| `session_start` / `session_stop` / `session_resume` / `session_reset` / `session_shutdown` | Lifecycle |
| `phase` | Fase aplicada (`WarService`) |
| `death` | Morte de jogador |
| `pvp_kill` | Kill PvP válida (times diferentes) |
| `mob_kill` | Mob morto por jogador |
| `item_gain` / `item_spend` | Materiais tracked (pickup, trade, craft) |
| `crop_plant` / `crop_harvest` | Plantio / colheita |
| `flag_capture` | Bandeira destruída |
| `tick_delta` | A cada ~60s: deltas agregados do intervalo |

Blocos quebrados/colocados **não** geram uma linha por bloco (volume); entram só nos counters do snapshot e em `tick_delta`.

Exemplo:

```json
{"ts":"2026-08-21T20:15:03Z","type":"item_gain","player":"<uuid>","name":"leo","team":"vermelho","item":"EMERALD","amount":5,"source":"pickup"}
```

## Snapshot

Agregados por UUID e por `teamId` (soma dos players com aquele time no flush):

- `kills`, `deaths`, `mobKills`
- `blocksBroken`, `blocksPlaced` (+ `blocksBrokenByCategory` / `blocksPlacedByCategory`: `ORE`, `STONE`, `WOOD`, `DIRT`, `OTHER`)
- `cropsPlanted`, `cropsHarvested`
- `emeraldsGained`, `emeraldsSpent` (`EMERALD_BLOCK` = ×9)
- `flagCaptures`
- `playTimeMs`, `distanceCm` (via `Statistic` vanilla, atualizado no flush)
- `itemsGained` / `itemsSpent` por material

## Esmeraldas

Soma de `item_gain` de `EMERALD` e `EMERALD_BLOCK` (×9). Fontes: pickup, trade de villager, craft. Não há diff genérico de inventário.

## Comandos

Permissão `mineguerra.admin`:

| Comando | Efeito |
|---------|--------|
| `/mg metrics status` | Sessão, gravando?, contagem, paths |
| `/mg metrics dump` | Força escrever `snapshot.json` agora |
| `/mg metrics open` | Mostra path absoluto da sessão |

## Código

Pacote `org.lseixas.mineguerra_plugins.metrics`:

- `MetricsRegistry` — init / start / stop / reset
- `MetricsService` — counters + gate `WarRegistry.state().isRunning()`
- `MetricsSessionStore` — JSONL + snapshot (Gson)
- `MetricsListener` — eventos Bukkit `MONITOR`

Leaderboard de kills (`teams-data.yml`) **não** é substituído; métricas são canal paralelo para análise.
