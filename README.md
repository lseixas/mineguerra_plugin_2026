# MineGuerra Plugins

Plugin Spigot/Paper para o evento **Minecraft Guerra** — survival/PvP próximo do vanilla, com armas lendárias, times, bandeiras estilo BedWars e villagers de comércio.

| | |
|---|---|
| **Minecraft** | 1.21 |
| **Java** | 21 |
| **API** | Spigot `1.21.8-R0.1-SNAPSHOT` |
| **JAR** | `plugin_mineguerra.jar` |
| **Versão** | `0.1` |

---

## O que o plugin faz

- **4 armas lendárias** com skill ativa + passiva (cooldown na hotbar vanilla quando aplicável)
- **1 cópia de cada arma no servidor** — trade some enquanto a arma existir; morte remove a arma e reabre a trade
- **Times** com tag no tab/nametag e placar de kills
- **Bandeiras de time** — respawn na base enquanto a bandeira existir; após ser quebrada, a próxima morte elimina o jogador (espectador)
- **Villagers / exploradores** para progresso e craft indireto das armas

Filosofia: manter progressão e combates o mais vanilla possível; o plugin só adiciona skills limitadas e regras de evento.

---

## Requisitos

- JDK **21**
- Gradle (sistema ou wrapper, se disponível)
- Servidor **Paper/Spigot 1.21** (ou usar o servidor de teste embutido)

---

## Build

```bash
gradle clean jar
```

Artefato:

```
build/libs/plugin_mineguerra.jar
```

Copie o JAR para a pasta `plugins/` do servidor e reinicie (ou `/reload` — preferível restart completo).

### Servidor de teste local

```bash
gradle runServer
```

Sobe um Paper 1.21 em `run/` e carrega o plugin automaticamente.

---

## Setup rápido do evento

```text
# Times
/team create vermelho "Vermelhos" RED
/team create azul "Azuis" BLUE
/team join Player1 vermelho
/team join Player2 azul

# Bases (olhe para o bloco da bandeira)
/team flag set vermelho
/team flag set azul

# Placar
/mg leaderboard on

# Villagers de arma (staff)
/grantTraderToolKit
```

Com a ferramenta (bastão): **clique no chão** = spawn do NPC; **clique no villager** = aplicar/atualizar trades.

---

## Permissões

| Permissão | Uso |
|-----------|-----|
| `mineguerra.team` | Criar/gerir times e bandeiras |
| `mineguerra.admin` | Leaderboard, kills, status/reset de armas |
| `mineguerra.tradertool` | Ferramentas de villager |
| `mineguerra.nospawn` | Delimitador de áreas sem spawn |
| `mineguerra.nobreak` | Delimitador de áreas sem quebra de bloco |
| `mineguerra.spawnvillager` | Comando legado `/spawnvillager` |

Com LuckPerms:

```text
/lp user <nick> permission set mineguerra.team true
/lp user <nick> permission set mineguerra.admin true
/lp user <nick> permission set mineguerra.tradertool true
/lp user <nick> permission set mineguerra.nospawn true
/lp user <nick> permission set mineguerra.nobreak true
```

Os comandos `/grant*` de armas (testes) tipicamente são usados por **OP**.

---

## Comandos

### Times (`/team`, aliases: `/t`, `/time`)

| Comando | Descrição |
|---------|-----------|
| `/team create <id> [nome] [cor]` | Cria time (máx. 12). Cores: `RED`, `BLUE`, `GREEN`, … |
| `/team delete <id>` | Remove time |
| `/team join <jogador> <time>` | Coloca jogador no time |
| `/team leave <jogador>` | Remove tag |
| `/team list` | Lista times + kills |
| `/team info <id>` | Detalhes e membros |
| `/team flag set <time>` | Coloca bandeira no bloco alvo (até 5 blocos) |
| `/team flag remove <time>` | Remove registro da bandeira |
| `/team flag status [time]` | Viva / morta (coordenadas com admin) |
| `/team flag list` | Status de todas as bandeiras |

ID do time: apenas `a-z`, `0-9`, `_`, `-` (até 32 caracteres).

### Admin (`/mg`)

| Comando | Descrição |
|---------|-----------|
| `/mg leaderboard on\|off` | Liga/desliga placar lateral |
| `/mg kills set <time> <n>` | Define kills |
| `/mg kills reset <time>` | Zera kills de um time |
| `/mg kills resetall` | Zera todos |
| `/mg weapons status` | Qual arma está livre ou com time |
| `/mg weapons reset` | Força todas as armas disponíveis para trade |

### Staff / debug

| Comando | Descrição |
|---------|-----------|
| `/grantTraderTool <tipo>` | Bastão do villager (`oceano`, `profundezas`, `nether`, `end`, …) |
| `/grantTraderToolKit` | Kit dos 4 exploradores de armas |
| `/grantNoSpawnTool [tool\|list\|clear]` | Lightning rod: área sem spawn de mobs |
| `/grantNoBreakTool [tool\|list\|clear]` | Pincel: área onde blocos não quebram |
| `/spawnvillager <tipo>` | Spawn direto (legado) |
| `/grantSoulflayerBow` | Dá o arco (teste; **não** registra posse lendária) |
| `/grantDragonSlayer` | Dá a espada |
| `/grantStormRider` | Dá o tridente |
| `/grantDoomHammer` | Dá a mace |
| `/startGuerra` | Timer / fluxo do evento |

---

## Armas lendárias

Somente **uma** instância de cada arma no servidor.

| Arma | Item | Explorador (trade) | Skill ativa | Passiva |
|------|------|--------------------|-------------|---------|
| **Storm Rider** | Tridente | Oceano | Thunder Teleport — agachar + F, arremesso (CD 15s) | Tempestade ao arremessar |
| **Doom Hammer** | Mace | Profundezas | Power Jump — clique direito (CD 90s) | Imunidade a dano de queda |
| **Soulflayer Bow** | Arco | Nether | Hellfire Rain — agachar + F, próximo tiro (CD 90s) | Dante's Punishment (~20% no hit) |
| **Dragon Slayer** | Espada netherite | End | Dragon's Breath — clique direito (CD 30s) | Rage of the Dragon (≤10% vida, CD 90s) |

### Regras de posse

| Situação | Efeito |
|----------|--------|
| Compra no villager | Time do comprador vira dono; trade some para todos |
| Drop (Q) | Arma continua no mundo; trade permanece fechada |
| Outro time pega no chão | Display de dono muda; trade continua fechada |
| Morte com a arma | Item some (sem drop); se não houver outra cópia, trade reabre |
| `/grant*` (admin) | Não conta como posse / não trava trade |

O placar (`/mg leaderboard on`) mostra kills e seção de armas.

---

## Bandeiras (estilo BedWars)

1. Staff define a bandeira com `/team flag set <time>` (banner na cor do time).
2. Enquanto a bandeira está **viva**, membros do time **respawnam** nela.
3. Só **inimigos** podem quebrá-la. Aliados não.
4. Ao quebrar: **não dropa item** (nem com Silk Touch); o time vê title de captura.
5. Depois que a bandeira caiu, a **próxima morte** do jogador o **elimina** (modo espectador).
6. Eliminados **resetam no restart** do servidor (não ficam salvos entre boots).

---

## Times e kills

Uma kill conta no placar quando:

1. Killer e vítima são jogadores  
2. Ambos estão em um time  
3. Os times são **diferentes**

Não conta: mesmo time, sem time, suicídio, morte por mob/ambiente.

Persistência: `plugins/mineguerra_plugins/teams-data.yml` (times, membros, kills, bandeiras, claims de armas).

---

## Estrutura do projeto

```text
main/java/org/lseixas/mineguerra_plugins/
  weapons/          # identificação, CD, mensagens, posse lendária
  teams/            # times, kills, leaderboard
  teams/flag/       # bandeiras e eliminação
  traders/          # villagers e ferramentas de staff
  soulflayerbow/    # arco
  dragonslayer/     # espada
  stormrider/       # tridente
  doomhammer/       # mace
  fluxCommands/     # fluxo do evento
docs/               # documentação técnica extra
```

Cada arma costuma ter `*Factory` (item), `*Listener` (eventos) e `skills/` (efeitos).

---

## Documentação adicional

Para detalhes técnicos (catálogo fino de skills, arquitetura, flags):

- [`docs/PROJECT_CONTEXT.md`](docs/PROJECT_CONTEXT.md)
- [`docs/WEAPONS_CATALOG.md`](docs/WEAPONS_CATALOG.md)
- [`docs/TEAMS_AND_LEADERBOARD.md`](docs/TEAMS_AND_LEADERBOARD.md)
- [`docs/FLAGS_AND_LEGENDARY_WEAPONS.md`](docs/FLAGS_AND_LEGENDARY_WEAPONS.md)

---

## Troubleshooting

| Problema | O que checar |
|----------|----------------|
| Comando sem efeito | Permissão / OP |
| Trade da arma sumiu e não volta | `/mg weapons status` → se preciso `/mg weapons reset` (há item no mundo ou claim preso?) |
| Placar vazio | `/mg leaderboard on` e existir pelo menos um time |
| Respawn não vai para a base | Time tem bandeira **viva**? `/team flag status` |
| Jogador “preso” em espectador após restart | Eliminação reseta no restart; se ainda estiver, `/gamemode survival` |
| Build falha | JDK 21 ativo (`java -version`) |

---

## Licença / uso

Projeto do evento MineGuerra. Contribuições e forks: use este README como ponto de entrada; issues e PRs no repositório do GitHub.
