# Padrões alvo (normativo)

Documento de contrato para código novo e migrações. Implementação de referência: pacote `weapons/` + **Doom Hammer** (piloto).

---

## 1. Identidade de arma

### `WeaponId` (enum)

Cada arma define:

- `material` — tipo Bukkit
- `customModelData` — resource pack
- `displayName` — nome com códigos `§` se necessário
- `loreLines` — descrição flavor + **CD das ativas em segundos**
- `messagePrefix` — ex.: `§6§l[Doom Hammer]`

### `WeaponItemService`

Responsabilidades:

- `create(WeaponId)` — item com CMD + PDC `mineguerra:weapon_id` (STRING = nome do enum)
- `matches(ItemStack, WeaponId)` — material + (PDC ou CMD fallback para itens legados)
- `isInMainHand(Player, WeaponId)`

```java
// Pseudocódigo
boolean matches(ItemStack item, WeaponId id) {
    if (item == null || item.getType() != id.material) return false;
    if (pdc has weapon_id) return pdc equals id.name();
    return meta.customModelData == id.cmd; // fallback legado
}
```

### `WeaponRegistry`

Inicializado em `onEnable`: `WeaponRegistry.init(plugin)`.

Factories legadas (`DoomHammerFactory`) delegam para `WeaponRegistry.items().create(...)`.

---

## 2. Ativação de habilidades

### `ActivationMode` (conceito)

| Valor | Uso |
|-------|-----|
| `RIGHT_CLICK` | Dragon Slayer, Doom Hammer |
| `SNEAK_SWAP` | Soulflayer, Storm Rider |
| `PROJECTILE_CHAIN` | Efeito no hit após “armar” |

### Regras de listener

- `PlayerInteractEvent`: filtrar `Action.RIGHT_CLICK_*` e `EquipmentSlot.HAND`
- Sneak swap: `event.setCancelled(true)` quando for toggle de skill
- Estado “armado”: `Set<UUID>` por listener ou classe `ArmedMode` dedicada
- Projéteis: metadata com `FixedMetadataValue(plugin, true)` e chaves constantes em classe da skill

### `WeaponAbility` (futuro opcional)

```java
interface WeaponAbility {
    String id();
    ActivationMode activation();
    CooldownStart cooldownStart();
    long cooldownMillis();
    boolean canActivate(Player player, ActivationContext ctx);
    void activate(Player player, ActivationContext ctx);
}
```

Migração incremental: listeners podem chamar skills diretamente até extrair interfaces.

---

## 3. Cooldown

### `CooldownStart`

| Política | Quando gravar timestamp |
|----------|-------------------------|
| `ON_ACTIVATE` | Ao iniciar (clique / armar modo) — usar só se intencional |
| `ON_SUCCESS` | Após efeito aplicado com sucesso (pulo, teleporte, tiro ultimate) |
| `ON_CHANNEL_END` | Após fim de canal (ex.: 3s Dragon's Breath) — **alvo para Dragon** |

### `AbilityCooldown`

```java
class AbilityCooldown {
    long durationMillis;

    boolean isOnCooldown(Player player);
    long getRemainingSeconds(Player player);

    /** @return true se pode usar (não está em CD) */
    boolean tryUse(Player player);

    /** Inicia CD — chamar conforme CooldownStart */
    void commit(Player player);
}
```

Mensagem de bloqueio: `WeaponMessages.sendCooldown(player, weaponId, abilityName, seconds)`.

Opcional (vanilla UX): após `commit`, `player.setCooldown(weaponMaterial, ticks)`.
Não usar em arco/crossbow — o CD vanilla bloqueia o disparo normal.

### Uma instância por skill

Não compartilhar um `Map` entre skills com políticas diferentes. Dragon Slayer deve ter 2× `AbilityCooldown` (Breath, Rage), não um map híbrido.

---

## 4. Visual e mensagens

### `WeaponMessages`

- `sendCooldown(player, weaponId, abilityName, secondsRemaining)`
- `sendInfo(player, weaponId, message)` — corpo sem prefixo duplicado
- `sendActivated(player, weaponId, abilityName)` — skill disparada

Formato: `{prefix} §7mensagem` ou `{prefix} §cCooldown §eXs §7(AbilityName)`.

Preferir strings `§` ou `Component` consistente — evitar misturar `ChatColor` no mesmo fluxo.

### `PlayerFeedback` (opcional)

Encapsular `playSound` + `spawnParticle` + mensagem curta para combos repetidos.

### Lore de item

Toda arma deve listar na lore:

- Nome da skill ativa e como ativar (ex.: “Agachar + F”)
- CD em segundos
- Passiva em uma linha

Lore é **estática** (não atualizar CD em tempo real na tooltip na v1).

---

## 5. Matriz arma → padrão alvo

| Arma | ActivationMode | CD skills | Status |
|------|----------------|-----------|--------|
| Doom Hammer | RIGHT_CLICK | Power Jump `ON_SUCCESS` | Migrado |
| Storm Rider | SNEAK_SWAP + PROJECTILE_CHAIN | Teleport `ON_SUCCESS`; check ao armar | Legado |
| Soulflayer Bow | SNEAK_SWAP + PROJECTILE_CHAIN | Hellfire `ON_SUCCESS` no tiro | Legado |
| Dragon Slayer | RIGHT_CLICK + passiva HP | Breath → `ON_CHANNEL_END` (alvo); Rage `ON_SUCCESS` silencioso | Legado |

---

## 6. O que não fazer

- PlaceholderAPI / scoreboards sem necessidade
- Mudar valores de balanceamento durante migração estrutural
- Big-bang: apagar pacotes `doomhammer/` etc. antes das 4 armas migrarem
- Duplicar CMD fora de `WeaponId`
