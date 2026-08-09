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
| Colocar jogador | `/team join Leo vermelho` |
| Tirar jogador | `/team leave Leo` |
| Listar | `/team list` |
| Detalhes | `/team info vermelho` |

- **ID do time:** só `a-z`, `0-9`, `_`, `-` (máx. 32 caracteres, máx. 12 times)
- **Tag visual:** prefixo colorido no tab e nametag (scoreboard team)
- Dados salvos em `plugins/mineguerra_plugins/teams-data.yml`

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
- Seções **Kills** e **Armas** (ver [`FLAGS_AND_LEGENDARY_WEAPONS.md`](FLAGS_AND_LEGENDARY_WEAPONS.md))
- Uma linha por time (ordenado por kills)
- Times com 0 kills aparecem
- Atualiza ao matar, criar/apagar time, editar kills, ligar placar

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

1. Prefixo no tab após `/team join`
2. `/mg leaderboard on` — sidebar com 0 kills
3. PvP cross-team — +1 kill e sidebar atualiza
4. Mesmo time — não incrementa
5. `/mg kills set` / `reset` — reflete no placar
6. `/mg leaderboard off` — sidebar some
7. `/team create` terceiro time — aparece no placar
8. Restart — `teams-data.yml` mantém dados
