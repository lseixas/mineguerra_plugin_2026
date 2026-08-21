# Balanceamento MineGuerra — evento fim de semana

**Postura:** meio-termo (farm ativo desbloqueia kit; lendárias e sinks ainda importam; CDs firmes).  
**Janela alvo:** ~2 dias + algumas horas.  
**Referências:** [Minecraft Wiki — Trading (JE 1.21)](https://minecraft.wiki/w/Trading), auditoria do código em `VillagerSpawner` + skills (2026-08).  
**Implementação desta página:** trades = planilha 2026-08-21 (`VillagerSpawner`); extras da planilha mantidos; CDs de arma = secção 6.

Emerald block = **9** emeralds (emerald-eq).

---

## 1. Objetivos de timing (fim de semana)

| Marco | Alvo aproximado (time com farm ativo) | Antes (problema) |
|-------|----------------------------------------|------------------|
| Kit diamante completo (ferreiro) | 3–6 h | <1–2 h (faucets infinitos) |
| Cerco (TNT/redstone útil) | custo consciente; não spam infinito | 1e → TNT |
| 1ª lendária tipicamente Storm | sabado tarde / domingo cedo (~150e+ path) | ~104e cedo demais |
| Doom Hammer | domingo com farm ~288e path | ~1056e OU soft-lock |
| Soulflayer / Dragon | gate **Nether Star** / **Dragon Egg** (progressão) | OK — manter |
| Elytra / wither kit | sink alto pós-nerf de faucet | 576e virava easy com print de emerald |

### Metas de combate

- Lendária = impacto de luta, não win-button permanente.
- Armor (diamante + Prot II) deve importar; Dante não anula set a cada 5 flechas.
- Mobilidade Storm frequente, não teleport a cada engage.
- Rage / Hellfire / Power Jump = momentos (~1–1.5×/min no pico), não uptime.

---

## 2. Diagnóstico (antes)

### Faucets vs vanilla (ordem de magnitudе)

| Fonte | Plugin (antes) | Vanilla JE aproximado | Fator |
|-------|----------------|------------------------|-------|
| Wheat → e | 5 → 1e | 20 → 1e | ~4× |
| Cod → e | 1 → 3e | 15 → 1e | ~45× |
| Chicken → e | 3 → 1e | 14 → 1e | ~4.7× |
| Rotten flesh → e | 2 → 1e | cleric 32 → 1e | ~16× |
| TNT | 1e → 1 | sem trade villager | crítico |

Cascata: faucet → engenheiro (cerco) → ferreiro (full diamond barato) → exploradores.

### Paths lendários (emerald-eq aproximado, antes)

| Arma | Path | Gate extra |
|------|------|------------|
| Storm Rider | ~104e (32 tridente + 72 heart) | — |
| Doom Hammer | ~1056e (32 mace + 64×16 deepslate) | deepslate quase só no trade |
| Soulflayer | Bow + Nether Star | wither |
| Dragon Slayer | Netherite sword + Dragon Egg | ender dragon |

### Combate (antes)

| Ability | CD / chance | Nota |
|---------|-------------|------|
| Thunder Teleport | 15s | mais spam mobile |
| Storm weather | 120s | mapa escuro longo |
| Hellfire | 90s | OK |
| Dante | 20% armor-bypass | forte demais no meta diamond |
| Breath | 30s on click | OK-ish |
| Rage | 90s / 10s / amp IV | identidade forte |
| Power Jump | 90s | OK |

---

## 3. Faucets (planilha 2026-08-21)

| Trader | Trade | Antes (weekend) | Novo |
|--------|-------|-----------------|------|
| Fazendeiro | Wheat → 1e | 15 | **5** |
| Fazendeiro | Potato / Carrot / Cane → 1e | 24 | **12** |
| Açougueiro | Chicken → 1e | 8 | **3** |
| Açougueiro | Pork / Beef / Mutton → 1e | 8 | **2** |
| Açougueiro | Rabbit → 1e | 8 | **1** |
| Mineiro | Copper → 1e | 4 | **2** |
| Mineiro | Iron → 2e | 2 | **2** (manter) |
| Mineiro | Gold → 3e | 2 | **2** (manter) |
| Mineiro | Diamond → e | 1→4e | **1→5e** |
| Caçador | Rotten flesh / Bone → 1e | 12 | **2** |
| Caçador | Spider eye → 1e | 1 | **1** (manter) |
| Caçador | Gunpowder → 1e | 4 | **1** |
| Pescador | Cod / Salmon → e | 8→1e | **1→3e** |
| Pescador | Tropical → e | 4→1e | **1→7e** |
| Pescador | Pufferfish → e | 3→1e | **1→10e** |

---

## 4. Sinks / utilidade (planilha 2026-08-21)

Extras fora da planilha **mantidos**: Heart of the Sea + Conduit (oceano), Shulker (end), Sculk Sensor + Observer (engenheiro).

| Trader | Oferta | Preço |
|--------|--------|-------|
| Engenheiro | TNT ×1 | **1e** |
| Engenheiro | Piston / Sticky ×8 | **1e** |
| Engenheiro | Repeater / Comparator / Dispenser ×16 | **1e** |
| Engenheiro | Slime / Honey ×32 | **1e** |
| Engenheiro | Calibrated Sculk ×16 | **1e** |
| Engenheiro | Sculk Sensor ×4 / Observer ×8 *(extra)* | **1e** |
| Ferreiro | Armadura diamante | **20 / 35 / 30 / 15e** |
| Ferreiro | Sword / Pick / Axe / Shovel / Hoe | **7 / 12 / 12 / 7 / 7e** |
| Ferreiro | Netherite ingot | **32 EB** |
| Bibliotecário | Eff III / Unbr I / Prot II / Sharp II / Mend / Fortune I | **32 / 16 / 32 / 32 / 32 / 32e** |
| End | Elytra | **64 EB** |
| End | Dragon Breath | **1e** |
| End | End Crystal | **32e** |
| End | Obsidian ×1 | **8e** |
| End | Ender Pearl | **16e** |
| End | Shulker box *(extra)* | **32e** |
| Nether | Wither skulls ×3 | **64 EB** |
| Nether | Netherite template | **64 EB** |
| Nether | Happy Ghast egg | **16e** |
| Oceano | Tridente / Loyalty / Channeling / Riptide | **32 / 16 / 32 / 32e** |
| Oceano | Heart of the Sea *(extra)* | **12 EB** |
| Oceano | Conduit *(extra)* | **1 heart + 8 nautilus** |
| Estilista | Armor trim ×4 | **1 disco específico** |
| Trapaceiro | Spawner | **48 EB** (1 compra) |
| Trapaceiro | Ovo hostil (zumbi/esqueleto/aranha/creeper) | **8 EB** (1 compra) |
| Trapaceiro | Ovo passivo (vaca/porco/galinha) | **32e** (1 compra) |
| Trapaceiro | Golden Bow (Power 9999, 1 dura) | **64 EB** (4×; 1 compra) |
| Trapaceiro | Graveto da Repulsão (Knockback 32, 1 dura) | **32 EB** (4×; 1 compra) |
| Trapaceiro | Maçã do Capiroto | **12 EB** (1 compra) |
| Trapaceiro | Peitoral do Pacto | **64 EB** (4×; 1 compra) |

---

## 5. Paths lendários (planilha)

| Arma | Componentes | Gate |
|------|-------------|------|
| **Storm Rider** | Tridente **32e**; path conduit *(extra)*; craft **tridente + conduit** | Unicidade claim |
| **Doom Hammer** | Mace **32e**; Reinforced Deepslate **4e** ×64; craft mace+64 | Unicidade |
| **Soulflayer** | Bow + Nether Star; skulls **64 EB**; Happy Ghast **16e** | Star + claim |
| **Dragon** | Netherite sword + Dragon Egg; elytra **64 EB** | Egg + claim |

`maxUses` (descansar economia / limitar dump):

| Categoria | maxUses |
|-----------|---------|
| Cerco engenheiro (TNT/redstone) | **64** |
| Recipe final lendária | **32** |
| Trapaceiro combate / ovos / meme | **1** (uma compra por oferta) |
| Trapaceiro spawner | **1** |
| Demais trades | **128** |

(Antes: 9999 em tudo.)

---

## 6. Armas — CD / chance — antes → novo

| Ability | Antes | Novo | Racional |
|---------|-------|------|----------|
| Thunder Teleport | 15s | **25s** | Mobilidade sem spam engage |
| Storm weather | 120s → 60s | **removida** | Clima global estragava a vila (portas, siege, raios) |
| Hellfire Rain | 90s → 75s | **45s** | Pedido pós-playtest |
| Dante's Punishment | 20% | **12%** por flecha | Armor volta a importar |
| Dragon's Breath | 30s | **35s** | Miss/click ainda queima CD |
| Rage of the Dragon | 90s / 10s / amp 3 | **100s / 8s / amp 3** | Mesma identidade, uptime menor |
| Power Jump | 90s | **80s** | Compensa Doom mais acessível |

**Docs:** círculo pré-TP usa `strikeLightning` (dano vanilla).

### Multishot no Soulflayer Bow (a observar)

O arco agora sai com **Multishot I**. Dante rola por flecha, então a chance de
ver o proc num disparo cheio sobe bastante (~32% para 3 flechas a 12% cada).
Hellfire Rain continua 1× por disparo. Se ficar forte demais, o ajuste é baixar
Dante para 6–8% em vez de tirar o Multishot.

### Peitoral do Pacto (trapaceiro)

Chainmail com Curse of Binding. Quem veste morre com qualquer dano e leva o
agressor junto. Custo **64 EB**, `maxUses` 1. Vale acompanhar se vira estratégia
de trade-kill em massa; se virar, subir ainda mais o preço.

---

## 7. Checklist review pós dia-1

- [ ] Storm ainda é a 1ª lendária padrão? (ok.) Tem time abusando TP a cada fight? → subir CD para 30s.
- [ ] TNT spam em bases? → subir TNT para 16–20e ou maxUses 32.
- [ ] Doom ainda inalcançável / trivial? → ajustar deepslate 3–6e.
- [ ] Dante still feels like true damage every fight? → 8–10%.
- [ ] Emerald flood (faucets da planilha)? → subir wheat 5→12 ou peixe 1→3e de volta.

---

## 8. Backlog de valor (não neste ciclo de números)

- Friendly-fire filter no Dragon's Breath (Hellfire já ignora aliados).
- Breath CD só no fim do channel (`ON_CHANNEL_END`).
- Dante via pipeline de dano “justa” (não `setHealth` bypass).
- Custos em `config.yml` (hoje hardcode em `VillagerSpawner`).
- Testes de integração MockBukkit para spawn de villagers.
- Compactar leaderboard com muitos times.

---

## 9. Testes automatizados (núcleo)

Ver `src/test/java` — JUnit 5 + MockBukkit:

- IDs de time válidos / inválidos
- `AbilityCooldown` (janela temporal)
- Claim offline → trade indisponível; release após “gone”
- Match de item lendário (PDC/CMD) via factory; Multishot no Soulflayer
- Itens do trapaceiro (Power 9999, Knockback 32, Peitoral do Pacto)
- Cronograma do evento: parsing do YAML e idempotência das fases
- Geometria e blocklist do raio da bandeira

Rodar: `./gradlew test` (ou `gradle test` com JDK 21).
