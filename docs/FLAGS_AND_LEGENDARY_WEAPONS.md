# Bandeiras e armas lendárias

## Bandeiras (BedWars-like)

- Cada time pode ter uma bandeira (`/team flag set <time>`).
- Enquanto a bandeira está **viva**, membros do time respawnam na localização da bandeira.
- Inimigos podem quebrar a bandeira; aliados **não** podem.
- Ao quebrar: **sem drop** de item (inclui Silk Touch), title para o time, bandeira marcada como morta.
- Após a bandeira cair, a **próxima morte** do jogador elimina-o (modo espectador, sem respawn de jogo).
- Jogadores eliminados são resetados no **restart do servidor** (não persistem).

### Comandos

| Comando | Permissão | Descrição |
|---------|-----------|-----------|
| `/team flag set <time>` | `mineguerra.team` | Coloca banner no bloco alvo |
| `/team flag remove <time>` | `mineguerra.team` | Remove registro YAML |
| `/team flag status [time]` | `mineguerra.team` | Status; coords só com `mineguerra.admin` |
| `/team flag list` | `mineguerra.team` | Lista bandeiras de todos os times |

## Armas lendárias globais

- **1 cópia por tipo** no servidor (`WeaponId`).
- Trade do villager explorador correspondente **some** enquanto existir cópia (inventário ou chão).
- `/grant*` **não** registra posse (bypass admin para testes).
- **Morte:** arma some (sem drop); trade reabre se não houver outra cópia.
- **Q (drop):** trade continua fechada; pickup atualiza o time dono no placar.
- **Trade villager:** registra posse para o time do comprador.

### Mapeamento arma ↔ explorador

| Arma | Villager |
|------|----------|
| Storm Rider | Oceano |
| Doom Hammer | Profundezas |
| Soulflayer Bow | Nether |
| Dragon Slayer | End |

### Comandos admin

| Comando | Descrição |
|---------|-----------|
| `/mg weapons status` | Dono / livre por arma |
| `/mg weapons reset` | Libera todas para trade |

### Placar

Com `/mg leaderboard on`, o sidebar mostra seções **Kills** e **Armas** (time + status livre/lockada por tipo).
