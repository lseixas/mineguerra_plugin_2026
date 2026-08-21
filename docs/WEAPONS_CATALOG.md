# Catálogo de armas

Última atualização: balance weekend — ver [`BALANCE.md`](BALANCE.md).  
Todas as 4 armas usam `WeaponItemService`, lore em `WeaponId`, `WeaponMessages`. CD vanilla na hotbar nas ativas (exceto Soulflayer Bow: `setCooldown(BOW)` bloqueia tiro).

Legenda: `MIGRADO` = padrão `weapons/`.

---

## Soulflayer Bow — `MIGRADO`

| Campo | Valor |
|-------|--------|
| Material | `BOW` |
| CustomModelData | `10001` |
| PDC `mineguerra:weapon_id` | Sim |
| Display | `§7§lSoulflayer Bow` |
| Enchantments | **Multishot I** (aplicado por `WeaponItemService`) |

### Skills

| Skill | Tipo | Ativação | CD / chance |
|-------|------|----------|-------------|
| Hellfire Rain | Ativa | Sneak + F → próximo tiro | **45s** — no tiro ultimate (só `AbilityCooldown`; sem barra vanilla) |
| Dante's Punishment | Passiva | hit de flecha marcada | **12%** armor-bypass + fogo |

### Multishot

O arco sai com **Multishot I** (3 flechas por disparo). Consequências no código:

- Todas as flechas do disparo recebem `is_dante_arrow` — as extras via varredura
  no tick seguinte, porque com Multishot elas podem não passar por
  `EntityShootBowEvent`. Dante rola **por flecha**, então a chance efetiva de
  proc por disparo sobe (12% cada).
- Hellfire Rain dispara **uma vez por disparo**, não uma por flecha: as flechas
  compartilham um `soulflayer_shot_id` e só o primeiro impacto invoca a chuva.
- Arcos já distribuídos **não** ganham o enchant retroativamente — reemitir via
  `/grantSoulflayerBow` ou trade do explorador do Nether.

### Eventos Bukkit

- `PlayerSwapHandItemsEvent` — toggle ultimate
- `EntityShootBowEvent` — abre o disparo e marca as flechas (Dante / ultimate)
- `ProjectileHitEvent` — HellfireRain (`setShooter`), 1× por disparo
- `EntityDamageByEntityEvent` — Dante; Hellfire ignora mesmo time
- `ExplosionPrimeEvent` / `EntityExplodeEvent` — caveiras da Hellfire: sem fogo e sem quebra de bloco (`HellfireSkullProtectListener`). O raio da explosão **não** é zerado, para o dano em jogadores continuar.

**Nota:** não é por causa do criativo. As caveiras são spawnadas pelo plugin; o Paper frequentemente ignora `setYield(0)` na entidade WitherSkull. Não dá para sobrescrever a classe NMS com a Spigot API — o guard é no evento.

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

### Tempestade removida

A passiva que ligava `setStorm`/`setThundering` no mundo inteiro por ~60s **não
existe mais**. Ela afetava tudo: villagers fechando portas, spawn de superfície,
zombie siege na vila e raios naturais em qualquer lugar do mapa. Só o teleporte
sobrou.

### Risco vanilla

- Círculo pré-teleporte: 8 `strikeLightning` em raio de 5 blocos — **raios reais**,
  com dano vanilla e sem filtro de aliado.
- Raio que cai perto de vila converte **villager em bruxa** e **creeper em charged
  creeper**. Se isso virar problema no evento, o conserto é cancelar
  `EntityTransformEvent` com causa `LIGHTNING`.

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

| ID enum | CMD | Resource pack |
|---------|-----|---------------|
| `SOULFLAYER_BOW` | 10001 | Elite Power Bow |
| `DRAGON_SLAYER` | 10002 | Fantasy 3D Ravenous Blade |
| `STORM_RIDER` | 10003 | Fantasy 3D Heavenly Partisan |
| `DOOM_HAMMER` | 10004 | Fantasy 3D Treacherous Bludgeon |

Pack: [`resourcepack/MineGuerra_Weapons/`](../resourcepack/MineGuerra_Weapons/) — ver [`RESOURCE_PACK.md`](RESOURCE_PACK.md).
