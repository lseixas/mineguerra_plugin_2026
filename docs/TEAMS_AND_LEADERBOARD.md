# Times e leaderboard de kills

## Permissões

| Permissão | Quem usa |
|-----------|----------|
| `mineguerra.team` | Staff — criar times, assign/remover jogadores |
| `mineguerra.admin` | Master — ligar/desligar leaderboard, editar kills |

Dar ao OP ou via plugin de permissões:
```
/lp user <nick> permission set mineguerra.team true
/lp user <nick> permission set mineguerra.admin true
```

## Comandos `/team`

| Comando | Exemplo |
|---------|---------|
| Criar time | `/team create vermelho "Time Vermelho" RED` |
| Remover time | `/team delete vermelho` |
| Colocar jogador (online ou offline) | `/team join Leo vermelho` |
| Tirar jogador (online ou offline) | `/team leave Leo` |
| Limpar todos os times | `/team clear confirm` |
| Listar | `/team list` |
| Detalhes | `/team info vermelho` |

- **ID do time:** só `a-z`, `0-9`, `_`, `-` (máx. 32 caracteres, máx. 12 times)
- **Tag visual:** prefixo colorido no tab; nametag acima da cabeça só para **aliados**
  (`FOR_OTHER_TEAMS` — rivais não veem o nick)
- **Locator Bar:** desligada em todos os mundos (`gamerule locatorBar false`) — sem bússola
  de jogadores na barra de XP
- **Offline / pré-login:** `/team join <nick> <time>` aceita nick de quem ainda não entrou;
  a atribuição fica pendente por nick (case-insensitive) e aplica no primeiro login
- Dados salvos em `plugins/mineguerra_plugins/teams-data.yml` (`playerTeams` + `pendingByName`)
- `/team clear confirm` apaga **todos** os times, membros, kills e bandeiras

## Comandos `/mg` (master)

| Comando | Efeito |
|---------|--------|
| `/mg leaderboard on` | Mostra placar lateral para todos |
| `/mg leaderboard off` | Esconde placar |
| `/mg kills set vermelho 10` | Define kills do time |
| `/mg kills reset vermelho` | Zera kills de um time |
| `/mg kills resetall` | Zera todos os times |
| `/mg weapons status` | Estado das armas lendárias (livre / dono) |
| `/mg weapons reset` | Libera todas as trades de arma |

## Bandeiras (`/team flag`)

Ver [`FLAGS_AND_LEGENDARY_WEAPONS.md`](FLAGS_AND_LEGENDARY_WEAPONS.md).

| Comando | Efeito |
|---------|--------|
| `/team flag set <time>` | Coloca bandeira no bloco alvo |
| `/team flag remove <time>` | Remove registro |
| `/team flag status [time]` | Vivo/morto (coords com admin) |
| `/team flag list` | Lista todos os times |

## Leaderboard (sidebar)

- Título: **MineGuerra**
- Primeira linha: **fase atual** da guerra (`Abertura`, `PvP liberado`, …); `Aguardando` se nenhuma fase rodou
- **Agora:** horário wall-clock (`HH:mm`, timezone de `war-schedule.yml`)
- **Prox:** próxima fase + countdown (`2h18m` / `5m09s` / `42s`); `fim` se não houver mais fases
- Seções **Kills** e **Armas** (ver [`FLAGS_AND_LEGENDARY_WEAPONS.md`](FLAGS_AND_LEGENDARY_WEAPONS.md))
- Uma linha por time (ordenado por kills)
- Times com 0 kills aparecem
- Relógio/countdown atualizam a cada segundo; kills/armas/fase atualizam ao matar, criar/apagar time, editar kills, mudar de fase, ligar placar

## Regra de kill

Conta **+1** para o time do killer quando:

1. Killer é jogador
2. Vítima é jogador
3. **Ambos** estão em um time
4. Times são **diferentes**

Não conta: suicídio, sem time, mesmo time, morte por mob/ambiente.

## Setup rápido do evento

```
/team create vermelho "Vermelhos" RED
/team create azul "Azuis" BLUE
/team join Player1 vermelho
/team join Player2 azul
/mg leaderboard on
```

## Testes manuais

1. Prefixo no tab após `/team join`; nick acima da cabeça só para o próprio time
2. `/mg leaderboard on` — sidebar com fase (`Aguardando` se o cronograma não começou), horário atual, countdown da próxima fase e 0 kills
3. Locator Bar ausente (sem ícones de jogador na barra de XP)
4. PvP cross-team — +1 kill e sidebar atualiza
5. Mesmo time — não incrementa
6. `/mg kills set` / `reset` — reflete no placar
7. `/mg leaderboard off` — sidebar some
8. `/team create` terceiro time — aparece no placar
9. Restart — `teams-data.yml` mantém dados
10. `/startGuerra phase pvp-on` — sidebar mostra `Fase: PvP liberado`
11. Countdown da próxima fase desce a cada segundo no placar
