# Bandeiras e armas lendárias

## Bandeiras (BedWars-like)

- Cada time pode ter uma bandeira (`/team flag set <time>`).
- Enquanto a bandeira está **viva**, membros do time respawnam na localização da bandeira.
- Inimigos podem quebrar a bandeira (ou destruí-la com **explosão**); aliados e jogadores **sem time** não capturam.
- Ao destruir: **sem drop** de item, title para o time, bandeira marcada como morta.
- Após a bandeira cair, a **próxima morte** do jogador elimina-o (modo espectador, sem respawn de jogo).
- Eliminado: só especta **aliados vivos** do próprio time (sem free-cam nem visão de outros times). **Shift** troca de aliado. Sem aliados vivos: fica parado na bandeira.
- Jogadores eliminados são resetados no **restart do servidor** (não persistem). Staff: `/mg revive`.
- Na fase **hardcore** do evento, qualquer morte elimina — bandeira viva ou não. Ver [`WAR_SCHEDULE.md`](WAR_SCHEDULE.md).

### Área limpa (raio 3)

Colocar a bandeira **limpa um cubo 7x7x7** em volta dela (raio 3 nos três eixos),
incluindo o bloco de suporte abaixo do banner. Depois disso o raio fica protegido:
ninguém constrói nem cava lá dentro, exceto staff com `mineguerra.team`.

- O banner fica **flutuando**: `FlagPhysicsListener` cancela `BlockPhysicsEvent`
  no bloco da bandeira, senão ela cairia sem suporte.
- A limpeza nunca remove bedrock, barrier, portais nem command blocks
  (`FlagAreaService.UNCLEARABLE`).
- Sem drop: os blocos removidos simplesmente somem.
- Explosões continuam funcionando como antes — inclusive para capturar a bandeira.
- O bloco do banner segue as regras de captura normais (aliado não quebra,
  inimigo captura).

### Comandos

| Comando | Permissão | Descrição |
|---------|-----------|-----------|
| `/team flag set <time>` | `mineguerra.team` | Limpa o raio 3 e coloca o banner no bloco alvo |
| `/team flag remove <time>` | `mineguerra.team` | Remove registro YAML |
| `/team flag repair <time>` | `mineguerra.team` | Relimpa o raio, repõe banner + marca viva |
| `/team flag clear <time>` | `mineguerra.team` | Só relimpa o raio, sem mexer no banner |
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

Com `/mg leaderboard on`, o sidebar mostra a **fase atual**, horário (`Agora`) e countdown da **próxima fase**, depois seções **Kills** e **Armas** (time + status livre/lockada por tipo).
