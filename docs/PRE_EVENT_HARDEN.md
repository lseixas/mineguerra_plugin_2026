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

## Regras deste trabalho

- Commits **locais** por bloco; **push só no fim**
- Não mudar balanceamento (CD/dano/chance) salvo duração de clima Storm (reduzir, não buff)
- Vanilla first; `mineguerra.admin` para ops

## Comandos novos / mudados

- `/grant*` + `/startGuerra` → `mineguerra.admin`
- `/startGuerra` → no-op com mensagem
- `/mg revive <player|all>`
- `/team flag repair <time>`
- Delete team remove flag

## Playtest checklist

1. Perms: player sem op **não** roda `grant*` / `startGuerra`
2. Trade lendária → logout holder → reopen trade **não** vende segunda cópia
3. Morte com arma → some do drop → trade volta
4. Flag: quebra inimiga → título; TNT na flag → mesmo fluxo; aliado não quebra; untagged não captura
5. Eliminação: flag morta → morte → spectator; `/mg revive` funciona; `/team flag repair`
6. Tab/nametag: 2+ players, times diferentes, prefixes visíveis cruzados
7. Cada arma 1 ciclo: Doom Power Jump, Storm throw+TP (~2 min storm), Bow Hellfire+Dante, Dragon Breath+Rage
8. Villagers: tool spawn + survival não mata / creative remove

## Notas técnicas

- Ownership: `isAvailable` trata claim com dono como indisponível; sem `rescanAll` no quit
- Flag: explosions removem banner da blast list + `onFlagDestroyed`
- Teams: entries propagadas em todos os scoreboards; board name `mg_` + 8 hex do id
