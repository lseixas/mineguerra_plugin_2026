# Pre-event harden — status para agentes

Tracking do endurecimento pré-Guerra. Atualizar este arquivo a cada bloco concluído.

## Status

| Bloco | Status | Commit |
|-------|--------|--------|
| 1. Perms `grant*` + `startGuerra` | done | `d7bc4a6` |
| 2. Neutralizar `/startGuerra` | done | `d7bc4a6` |
| 3. Ownership offline (trade não reabre) | done | `6842293` |
| 4. Flag vs TNT/explosão | done | `c4d086a` |
| 5. Ops: revive + flag repair + cascade delete | done | `c4d086a` |
| 6. Nametag sync cross-player | done | `c4d086a` |
| 7. Tweaks Storm/Hellfire | done | `2ea9a88` |
| 8. Docs + checklist playtest | done | `3455adf` |
| Push remote | **bloqueado** — sem credencial git/gh neste ambiente; 6 commits locais ahead de `origin/main` | rode `git push` no seu PC |

## Lançamento — motor do evento

| Bloco | Status |
|-------|--------|
| Pistões liberados dentro do no-break | done |
| Tempestade global do tridente removida | done |
| Multishot I no Soulflayer (1 Hellfire por disparo) | done |
| Bandeira: raio 3 limpo + protegido, banner sem física | done |
| Peitoral do Pacto no trapaceiro | done |
| `/startGuerra` com cronograma de fases | done |
| Skins offline (SkinsRestorer, plugin externo) | documentado |

Substituiu o bloco 2 (`/startGuerra` no-op). Ver [`WAR_SCHEDULE.md`](WAR_SCHEDULE.md).

## Regras deste trabalho

- Commits **locais** por bloco; **push só no fim**
- Não mudar balanceamento (CD/dano/chance) salvo duração de clima Storm (reduzir, não buff)
- Vanilla first; `mineguerra.admin` para ops

## Comandos novos / mudados

- `/grant*` + `/startGuerra` → `mineguerra.admin`
- `/startGuerra <start|stop|status|phase|reload>` (aliases `/guerra`, `/sg`)
- `/mg revive <player|all>`
- `/team flag repair <time>`
- `/team flag clear <time>` — relimpa o raio 3
- Delete team remove flag

## Playtest checklist

1. Perms: player sem op **não** roda `grant*` / `startGuerra`
2. Trade lendária → logout holder → reopen trade **não** vende segunda cópia
3. Morte com arma → some do drop → trade volta
4. Flag: quebra inimiga → título; TNT na flag → mesmo fluxo; aliado não quebra; untagged não captura
5. Eliminação: flag morta → morte → spectator; `/mg revive` funciona; `/team flag repair`
6. Tab/nametag: 2+ players, times diferentes, prefixes visíveis cruzados
7. Cada arma 1 ciclo: Doom Power Jump, Storm throw+TP (clima **não** muda), Bow Hellfire+Dante, Dragon Breath+Rage
8. Villagers: tool spawn + survival não mata / creative remove
9. Bow com Multishot: 3 flechas, Hellfire cai **uma vez** só, Dante procca em qualquer flecha
10. No-break: pistão dentro da zona funciona; quebra à mão continua bloqueada
11. Flag set: raio 3 fica limpo, banner não cai, ninguém constrói/cava no raio, staff consegue
12. Peitoral do Pacto: vestir → tomar 1 hit de jogador → os dois morrem
13. `/startGuerra status` lista as 6 fases; `phase pvp-on` libera PvP; restart mantém fases aplicadas

## Notas técnicas

- Ownership: `isAvailable` trata claim com dono como indisponível; sem `rescanAll` no quit
- Flag: explosions removem banner da blast list + `onFlagDestroyed`
- Teams: entries propagadas em todos os scoreboards; board name `mg_` + 8 hex do id
