# Cronograma do evento (`/startGuerra`)

Motor de fases do MineGuerra. Compara o **wall-clock** do servidor com `war-schedule.yml`
a cada segundo e aplica cada fase uma única vez.

Pacote: `main/java/.../war/`.

## Comando

Permissão: `mineguerra.admin`. Aliases: `/guerra`, `/sg`.

| Comando | Função |
|---------|--------|
| `/startGuerra start` | Liga o ticker e aplica o que já venceu |
| `/startGuerra stop` | Pausa o ticker (nenhuma fase dispara) |
| `/startGuerra status` | Fases, horários, PvP, hardcore, próxima fase |
| `/startGuerra phase <fase>` | Força uma fase fora de hora (teste) |
| `/startGuerra reload` | Recarrega `war-schedule.yml` |
| `/startGuerra reset` | Desfaz o evento: para ticker, limpa fases, PvP on, hardcore off, border padrão, remove Trapaceiro(s), revive eliminados |

## Fases

| Chave | Horário do evento | Efeito |
|-------|-------------------|--------|
| `inicio` | Sexta 18:00 | PvP **off** + broadcast + kit de abertura (ferramentas de ferro, peitoral/bota de couro, escudo, 64 pães) |
| `pvp-on` | Sábado 00:00 | PvP **on** + lista times sem bandeira de pé |
| `trapaceiro` | Sábado 18:00 | Spawna o Trapaceiro nas coords configuradas |
| `julgamento` | Domingo 12:00 | 1 bandeira viva = vitória; 2+ = coords liberadas no chat |
| `hardcore` | Domingo 18:00 | Morte passa a eliminar de vez (spectator) |
| `fechar-centro` | Domingo 20:00 | World border encolhe para o centro |

## Config: `plugins/mineguerra_plugins/war-schedule.yml`

Copiado do JAR na primeira subida. Formato de data: `yyyy-MM-dd HH:mm`.

```yaml
timezone: America/Sao_Paulo
world: Guerra3

phases:
  inicio:
    at: "2026-08-21 18:00"
  pvp-on:
    at: "2026-08-22 00:00"
  trapaceiro:
    at: "2026-08-22 18:00"
    x: 47
    y: 67
    z: -39
  julgamento:
    at: "2026-08-23 12:00"
  hardcore:
    at: "2026-08-23 18:00"
  fechar-centro:
    at: "2026-08-23 20:00"
    centerX: 47
    centerZ: -39
    fromSize: 3000
    toSize: 200
    durationSeconds: 220
```

Datas inválidas e chaves desconhecidas são reportadas no console e em
`/startGuerra reload` — a fase é ignorada, o resto do cronograma segue.

A borda encolhe a ~**6,4 blocos/s** (um pouco abaixo de sprint+pulo ≈ 7,1).
De 3000→200 isso leva **220s** (~3,7 min).

## Estado: `plugins/mineguerra_plugins/war-state.yml`

```yaml
running: true
pvpEnabled: false
hardcore: false
appliedPhases:
  - inicio
```

No `onEnable`, se o cronograma estiver **rodando**, tudo que já venceu é reaplicado
**em silêncio** (sem repetir broadcast) e o border volta a encolher se `fechar-centro`
já tinha rodado. Fase já aplicada não roda de novo.

A fase atual (última aplicada) aparece no leaderboard (`/mg leaderboard on`),
junto com o horário atual e o countdown até a próxima fase agendada.
Antes de qualquer fase: `Aguardando`.

Depois de um `/startGuerra reset`, o catch-up **não** reaplica fases no restart
(precisa de `/startGuerra start` de novo).

## Avisos automáticos

O ticker anuncia a próxima fase em **60, 10, 5 e 1 minuto**. Os avisos vivem em
memória: depois de um restart, os que já passaram não repetem.

## Detalhes de implementação

- **Kit de abertura** (`inicio`): ferramentas de ferro completas, peitoral e bota de
  couro, escudo e 64 pães — entregue a todos online na aplicação da fase e a quem
  entrar depois (uma vez por jogador, persistido em `war-state.yml`).
- **PvP off** (`PvpToggleListener`) cancela `EntityDamageByEntityEvent` entre
  jogadores (inclusive projétil) e zera intensidade de poção splash em terceiros.
  Não mexe em gamerule nem na flag PvP do mundo.
- **Hardcore** (`HardcoreDeathListener`) reaproveita a eliminação das bandeiras:
  spectator, e `/mg revive` continua funcionando. Como a eliminação vive em
  memória, restart limpa (comportamento herdado das bandeiras).
- **Julgamento** conta `TeamFlag.alive`. Não encerra o evento nem para as fases
  seguintes — hardcore e border continuam agendados.
- **Border** usa `WorldBorder.setSize(toSize, durationSeconds)`; o encolhimento
  é interpolado pelo servidor.

## Testes

`src/test/java/.../war/` — `WarScheduleTest` (parsing, timezone, ordem, fases
vencidas) e `WarStateStoreTest` (idempotência e persistência).
