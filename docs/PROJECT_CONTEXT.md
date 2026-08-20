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
| `/startGuerra` (`guerra`, `sg`) | Cronograma do evento — perm: `mineguerra.admin` — ver [WAR_SCHEDULE.md](WAR_SCHEDULE.md) |
| `/grantSoulflayerBow` | Dá Soulflayer Bow — perm: `mineguerra.admin` |
| `/grantDragonSlayer` | Dá Dragon Slayer — perm: `mineguerra.admin` |
| `/grantDoomHammer` | Dá Doom Hammer — perm: `mineguerra.admin` |
| `/grantStormRider` | Dá Storm Rider — perm: `mineguerra.admin` |
| `/grantTraderTool <tipo>` | Bastão: clique no chão = spawn NPC; clique no villager = aplicar trades (perm: `mineguerra.tradertool`) |
| `/grantTraderToolKit` | Dá as 4 ferramentas dos exploradores de armas (oceano, profundezas, nether, end) |
| `/grantNoSpawnTool [tool\|list\|clear]` | Lightning rod: delimita área sem spawn de mobs (perm: `mineguerra.nospawn`) |
| `/grantNoBreakTool [tool\|list\|clear]` | Pincel: delimita área onde blocos não quebram (perm: `mineguerra.nobreak`) |
| `/team` (`t`, `time`) | Gestão de times / flags — perm: `mineguerra.team` |
| `/mg` | Leaderboard, kills, weapons, revive — perm: `mineguerra.admin` |

Ver detalhes em [TEAMS_AND_LEADERBOARD.md](TEAMS_AND_LEADERBOARD.md) e [PRE_EVENT_HARDEN.md](PRE_EVENT_HARDEN.md).

## Distribuição de armas (villagers)

Arquivo: `main/java/org/lseixas/mineguerra_plugins/traders/VillagerSpawner.java`

| Villager / explorador | Arma (trade) |
|----------------------|--------------|
| Explorador do Oceano | Storm Rider (1 tridente + 1 conduit) |
| Explorador das Profundezas | Doom Hammer |
| Explorador do Nether | Soulflayer Bow |
| Explorador do End | Dragon Slayer; shulker box (32e) |
| Estilista (`trim`) | 1 disco de música → 4 armor trims (disco específico por trim) |
| Trapaceiro (`trapaceiro` / `cheat`) | Spawner, ovos, Golden Bow, graveto knockback, maçã do capiroto |

## Build e teste local

```bash
./gradlew build
./gradlew runServer
```

Copiar `plugin_mineguerra.jar` para `plugins/` do servidor de teste.

Resource pack das lendárias: [`RESOURCE_PACK.md`](RESOURCE_PACK.md) (`resourcepack/MineGuerra_Weapons/`).

Allowlist Fabric (handshake futuro): [`CLIENT_AUDIT.md`](CLIENT_AUDIT.md).

## Escopo fora das armas (referência)

- `fluxCommands/StartGuerraCommand` — orquestração do evento
- Traders com livros encantados e outros itens além das 4 armas

## Decisões em aberto (registrar ao decidir)

| Tópico | Opções | Nota |
|--------|--------|------|
| CD Dragon's Breath | No clique vs fim do canal 3s | Hoje: no clique — pode ser bug de UX |
| Raios Storm Rider | `strikeLightning` vs `strikeLightningEffect` | Hoje: lightning real (dano vanilla) |
| PDC obrigatório em itens antigos | Re-tag em grant vs só itens novos | Itens já dados antes da migração podem só ter CMD |
