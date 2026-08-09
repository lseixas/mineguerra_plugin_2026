# Catálogo de armas

Última atualização: **todas as 4 armas** usam `WeaponItemService`, lore centralizada em `WeaponId`, mensagens via `WeaponMessages`, CD vanilla na hotbar nas ativas.

Legenda: `MIGRADO` = padrão `weapons/`.

---

## Soulflayer Bow — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `BOW` |
| CustomModelData | `10001` |
| PDC `mineguerra:weapon_id` | Sim |
| Display | `§7Soulflayer Bow` |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Hellfire Rain | Ativa | Sneak + F (swap hands) → próximo tiro | 90s — aplicado **no tiro** ultimate |
| Dante's Punishment | Passiva | 20% em hit de flecha marcada | — |

### Eventos Bukkit

- `PlayerSwapHandItemsEvent` — toggle ultimate
- `EntityShootBowEvent` — metadata `is_dante_arrow`, `is_ultimate_arrow`
- `ProjectileHitEvent` — HellfireRain (WitherSkulls com `setShooter`)
- `EntityDamageByEntityEvent` — Dante; Hellfire ignora aliados do mesmo time

### Arquivos

- `soulflayerbow/SoulflayerBowFactory.java` — `createStarShooter()` (nome legado)
- `soulflayerbow/SoulflayerBowListener.java`
- `soulflayerbow/skills/HellfireRain.java`
- `soulflayerbow/skills/DantesPunishment.java`
- `soulflayerbow/SoulflayerBowCommand.java`

### Mensagens (chat)

- `§c🔥 Hellfire Rain desativada!` / `§6🔥 Hellfire Rain ativada!...`
- `§c🔥 Hellfire Rain!` (tiro)
- `§c⏰ Hellfire Rain em cooldown! §7(Xs)`
- `§5⚡ DANTE'S PUNISHMENT!`

---

## Dragon Slayer — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `NETHERITE_SWORD` |
| CustomModelData | `10002` |
| Display | `Dragon Slayer` (sem códigos de cor no nome) |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Dragon's Breath | Ativa | Right-click (sem filtro `HAND`) | 30s — inicia **no clique** (`checkCooldown` faz `put`) |
| Rage of the Dragon | Passiva | ≤10% HP após dano, arma **em qualquer slot do inventário** | 90s — silencioso, map separado na skill |

### Eventos Bukkit

- `PlayerInteractEvent` — Breath
- `EntityDamageEvent` — Rage

### Arquivos

- `dragonslayer/DragonSlayerFactory.java`
- `dragonslayer/DragonSlayerListener.java`
- `dragonslayer/skills/DragonsBreath.java`
- `dragonslayer/skills/RageOfTheDragon.java`
- `dragonslayer/DragonSlayerCommand.java`

### Mensagens

- Breath cooldown: `§5§l[Bafo Dracônico] §cCooldown: §eXs`
- Rage: `§4🐉 DRAGON RAGE!`

---

## Storm Rider — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `TRIDENT` |
| CustomModelData | `10003` |
| Lore factory | Ausente |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Thunder Teleport | Ativa | Sneak + F → arremesso → hit | 15s — verificado ao **armar**; gravado no **teleporte** |
| Tempestade | Passiva | Qualquer arremesso com arma (mão ou item do tridente) | Duração clima **2400 ticks (~2 min)** storm + thunder |

### Eventos Bukkit

- `PlayerSwapHandItemsEvent`
- `ProjectileLaunchEvent` / `ProjectileHitEvent`
- `PlayerDropItemEvent` — cancela modo se dropar CMD 10003

### Arquivos

- `stormrider/StormRiderFactory.java`
- `stormrider/StormRiderListener.java`
- `stormrider/skills/ThunderTeleport.java`
- `stormrider/StormRiderCommand.java`

### Mensagens

- Modo on/off, `ChatColor` + `§` misturados
- `⏰ Thunder Teleport em cooldown!`
- `§b⚡ A tempestade foi invocada!`

### Risco vanilla

- `World.strikeLightning` no círculo pré-teleporte — **dano real**

---

## Doom Hammer — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `MACE` |
| CustomModelData | `10004` |
| PDC | `mineguerra:weapon_id` = `DOOM_HAMMER` |
| Display | `§6Doom Hammer` |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Power Jump | Ativa | Right-click, `EquipmentSlot.HAND` | 90s — `AbilityCooldown` política `ON_SUCCESS` |
| Queda | Passiva | Segurando na mão principal | — |

### Eventos Bukkit

- `PlayerInteractEvent`
- `EntityDamageEvent` (FALL cancelado)

### Arquivos

- `weapons/WeaponId.java`, `WeaponItemService.java`, …
- `doomhammer/DoomHammerFactory.java` — delega para `WeaponRegistry`
- `doomhammer/DoomHammerListener.java`
- `doomhammer/skills/PowerJump.java`
- `doomhammer/DoomHammerCommand.java`

### Mensagens (padronizadas)

- Prefixo: `§6§l[Doom Hammer]`
- Cooldown e ativação via `WeaponMessages`

### Lore (estática)

Descreve Power Jump e CD 90s — ver `WeaponId.DOOM_HAMMER` em código.

---

## Tabela CMD central

| ID enum | CMD |
|---------|-----|
| `SOULFLAYER_BOW` | 10001 |
| `DRAGON_SLAYER` | 10002 |
| `STORM_RIDER` | 10003 |
| `DOOM_HAMMER` | 10004 |

Fonte única após migração completa: `WeaponId` / `WeaponConstants`.
