# Catálogo de armas

Última atualização: balance weekend — ver [`BALANCE.md`](BALANCE.md).  
Todas as 4 armas usam `WeaponItemService`, lore em `WeaponId`, `WeaponMessages`, CD vanilla na hotbar nas ativas.

Legenda: `MIGRADO` = padrão `weapons/`.

---

## Soulflayer Bow — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `BOW` |
| CustomModelData | `10001` |
| PDC `mineguerra:weapon_id` | Sim |
| Display | `§7§lSoulflayer Bow` |

### Skills

| Skill | Tipo | Ativação | CD / chance |
|-------|------|----------|-------------|
| Hellfire Rain | Ativa | Sneak + F → próximo tiro | **75s** — no tiro ultimate |
| Dante's Punishment | Passiva | hit de flecha marcada | **12%** armor-bypass |

### Eventos Bukkit

- `PlayerSwapHandItemsEvent` — toggle ultimate
- `EntityShootBowEvent` — metadata Dante / ultimate
- `ProjectileHitEvent` — HellfireRain (`setShooter`)
- `EntityDamageByEntityEvent` — Dante; Hellfire ignora mesmo time

### Arquivos

- `soulflayerbow/SoulflayerBowFactory.java`
- `soulflayerbow/SoulflayerBowListener.java`
- `soulflayerbow/skills/HellfireRain.java`
- `soulflayerbow/skills/DantesPunishment.java`
- `soulflayerbow/SoulflayerBowCommand.java`

---

## Dragon Slayer — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `NETHERITE_SWORD` |
| CustomModelData | `10002` |
| Display | `§5§lDragon Slayer` |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Dragon's Breath | Ativa | Right-click `HAND` | **35s** — no clique |
| Rage of the Dragon | Passiva | ≤10% HP; arma em qualquer slot | **100s**; efeitos **8s** amp IV |

### Arquivos

- `dragonslayer/*`

---

## Storm Rider — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `TRIDENT` |
| CustomModelData | `10003` |
| Display | `§b§lStorm Rider` |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Thunder Teleport | Ativa | Sneak + F → arremesso → hit | **25s** |
| Tempestade | Passiva | arremesso Storm Rider | Clima **1200 ticks (~60s)** |

### Risco vanilla

- Círculo pré-teleporte: `strikeLightningEffect` — **só visual**, sem dano.

### Arquivos

- `stormrider/*`

---

## Doom Hammer — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `MACE` |
| CustomModelData | `10004` |
| Display | `§6§lDoom Hammer` |

### Skills

| Skill | Tipo | Ativação | CD |
|-------|------|----------|-----|
| Power Jump | Ativa | Right-click `HAND` | **80s** |
| Queda | Passiva | main hand | imunidade FALL |

### Arquivos

- `doomhammer/*` + `weapons/WeaponId`

---

## Tabela CMD

| ID enum | CMD |
|---------|-----|
| `SOULFLAYER_BOW` | 10001 |
| `DRAGON_SLAYER` | 10002 |
| `STORM_RIDER` | 10003 |
| `DOOM_HAMMER` | 10004 |
