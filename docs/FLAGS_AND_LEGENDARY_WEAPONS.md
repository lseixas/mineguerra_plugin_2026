# Bandeiras e armas lendárias

## Bandeiras (BedWars-like)

- Cada time pode ter uma bandeira (`/team flag set <time>`).
- Enquanto a bandeira está **viva**, membros do time respawnam na localização da bandeira.
- Inimigos podem quebrar a bandeira (ou destruí-la com **explosão**); aliados e jogadores **sem time** não capturam.
- Ao destruir: **sem drop** de item, title para o time, bandeira marcada como morta.
- Após a bandeira cair, a **próxima morte** do jogador elimina-o (modo espectador, sem respawn de jogo).
- Jogadores eliminados são resetados no **restart do servidor** (não persistem). Staff: `/mg revive`.

### Comandos

| Comando | Permissão | Descrição |
|---------|-----------|-----------|
| `/team flag set <time>` | `mineguerra.team` | Coloca banner no bloco alvo |
| `/team flag remove <time>` | `mineguerra.team` | Remove registro YAML |
| `/team flag repair <time>` | `mineguerra.team` | Reposiciona banner + marca viva |
| `/team flag status [time]` | `mineguerra.team` | Status; coords só com `mineguerra.admin` |
| `/team flag list` | `mineguerra.team` | Lista bandeiras de todos os times |
| `/mg revive <jogador\|all>` | `mineguerra.admin` | Tira modo eliminado / survival |

## Armas lendárias globais

- **1 cópia por tipo** no servidor (`WeaponId`).
- Trade do villager explorador correspondente **some** enquanto houver **claim** (inclui holder **offline**) ou cópia online/no chão.
- `/grant*` **não** registra posse (bypass admin para testes) — exige `mineguerra.admin`.
- **Morte:** arma some (sem drop); trade reabre se não houver outra cópia.
- **Logout:** claim **permanece**; trade não reabre só porque o holder saiu.
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
| `/mg revive <jogador\|all>` | Revive eliminados da bandeira |

### Placar

Com `/mg leaderboard on`, o sidebar mostra seções **Kills** e **Armas** (time + status livre/lockada por tipo).
