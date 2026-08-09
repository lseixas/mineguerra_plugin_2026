# Contexto do projeto

## Evento

**Minecraft Guerra (MineGuerra)** — evento PvP/survival com regra de design: permanecer o mais próximo possível da **progressão vanilla**. O plugin adiciona armas temáticas com habilidades limitadas, distribuídas no mundo via villagers e comandos de staff.

## Plugin

| Campo | Valor |
|-------|--------|
| Nome Bukkit | `mineguerra_plugins` |
| Versão | `0.1` |
| API | `1.21` |
| Grupo Maven | `org.lseixas` |

## Comandos (`plugin.yml`)

| Comando | Função |
|---------|--------|
| `/spawnvillager` (`sv`, `villager`) | Spawna villagers customizados (perm: `mineguerra.spawnvillager`) |
| `/startGuerra` | Inicia timer/fluxo da guerra |
| `/grantSoulflayerBow` | Dá Soulflayer Bow |
| `/grantDragonSlayer` | Dá Dragon Slayer |
| `/grantStormRider` | Dá Storm Rider |
| `/grantTraderTool <tipo>` | Bastão: clique no chão = spawn NPC; clique no villager = aplicar trades (perm: `mineguerra.tradertool`) |
| `/grantTraderToolKit` | Dá as 4 ferramentas dos exploradores de armas (oceano, profundezas, nether, end) |
| `/team` (`t`, `time`) | Gestão de times — perm: `mineguerra.team` |
| `/mg` | Leaderboard e kills — perm: `mineguerra.admin` |

Ver detalhes em [TEAMS_AND_LEADERBOARD.md](TEAMS_AND_LEADERBOARD.md).
| `/grantDoomHammer` | Dá Doom Hammer |

Os `grant*` não definem permission no YAML — hoje qualquer OP pode executar.

## Distribuição de armas (villagers)

Arquivo: `main/java/org/lseixas/mineguerra_plugins/traders/VillagerSpawner.java`

| Villager / explorador | Arma (trade) |
|----------------------|--------------|
| Explorador do Oceano | Storm Rider |
| Explorador das Profundezas | Doom Hammer |
| Explorador do Nether | Soulflayer Bow |
| Explorador do End | Dragon Slayer |

## Build e teste local

```bash
./gradlew build
./gradlew runServer
```

Copiar `plugin_mineguerra.jar` para `plugins/` do servidor de teste.

## Escopo fora das armas (referência)

- `fluxCommands/StartGuerraCommand` — orquestração do evento
- Traders com livros encantados e outros itens além das 4 armas

## Decisões em aberto (registrar ao decidir)

| Tópico | Opções | Nota |
|--------|--------|------|
| CD Dragon's Breath | No clique vs fim do canal 3s | Hoje: no clique — pode ser bug de UX |
| Raios Storm Rider | `strikeLightning` vs `strikeLightningEffect` | Hoje: lightning real (dano vanilla) |
| PDC obrigatório em itens antigos | Re-tag em grant vs só itens novos | Itens já dados antes da migração podem só ter CMD |
