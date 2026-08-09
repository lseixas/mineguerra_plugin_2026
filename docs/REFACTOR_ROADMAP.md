# Roadmap de refatoração

## Fase 0 — Documentação

- [x] `AGENTS.md` + pasta `docs/`
- [x] Catálogo, arquitetura AS-IS, padrões alvo, roadmap

## Fase 1 — Núcleo `weapons/`

- [x] `WeaponId`, `WeaponConstants`, `WeaponRegistry`
- [x] `WeaponItemService` (create + matches + PDC)
- [x] `AbilityCooldown`, `CooldownStart`
- [x] `WeaponMessages`
- [x] `WeaponRegistry.init` em `Mineguerra_plugins`

## Fase 2 — Piloto Doom Hammer

- [x] Factory delega `WeaponRegistry`
- [x] Listener usa `isInMainHand(DOOM_HAMMER)`
- [x] `PowerJump` usa `AbilityCooldown` + `WeaponMessages`
- [x] Lore completa no `WeaponId`
- [x] `WEAPONS_CATALOG.md` atualizado

## Fase 3 — Par Sneak+Swap

- [x] Storm Rider: lore, `WeaponItemService`, CD + barra vanilla, `strikeLightningEffect`
- [x] Soulflayer Bow: `createSoulflayerBow`, CD + mensagens padronizadas

Checklist por arma:

```
[ ] WeaponItemService.create / matches
[ ] Listener sem isHolding* duplicado
[ ] AbilityCooldown por skill com CooldownStart explícito
[ ] WeaponMessages em todos os feedbacks de CD/ativação
[ ] Lore estática com CD
[ ] Teste: /grant*, villager trade, cada skill 1x
[ ] Atualizar WEAPONS_CATALOG.md
```

## Fase 4 — Dragon Slayer

- [x] Filtro `EquipmentSlot.HAND` no interact
- [x] Separar maps → 2× `AbilityCooldown`
- [ ] Mover CD do Breath para `ON_CHANNEL_END` (se aprovado pelo design) — hoje ainda no clique
- [x] Unificar prefixo de mensagens

## Fase 5 — Polish

- [ ] Comentários de CD corretos em todo o projeto
- [ ] Permissions nos `grant*` (opcional)
- [ ] `player.setCooldown` espelhando CD das ativas (opcional)
- [ ] Revisar itens antigos só-CMD vs PDC

## Critérios de “padronização completa”

- [ ] 4/4 armas `MIGRADO` no catálogo
- [ ] Zero CMD hardcoded fora de `WeaponId`
- [ ] Um `AbilityCooldown` documentado por skill ativa
- [ ] Lore completa nas 4 factories / `WeaponItemService`
- [ ] Prefixos de chat consistentes
- [ ] Teste manual em `runServer` documentado abaixo

### Roteiro de teste manual

1. `./gradlew runServer`
2. `/grantDoomHammer` — verificar lore, PDC (item meta), Power Jump + CD 90s
3. Repetir grants das outras armas após cada migração
4. `/spawnvillager` conforme `PROJECT_CONTEXT.md` — confirmar trade entrega item válido

## Ordem de risco

```
Documentação → Núcleo → Doom (piloto) → Storm + Bow → Dragon → Polish
```
