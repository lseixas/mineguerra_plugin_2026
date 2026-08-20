# Balanceamento MineGuerra — evento fim de semana

**Postura:** meio-termo (farm ativo desbloqueia kit; lendárias e sinks ainda importam; CDs firmes).  
**Janela alvo:** ~2 dias + algumas horas.  
**Referências:** [Minecraft Wiki — Trading (JE 1.21)](https://minecraft.wiki/w/Trading), auditoria do código em `VillagerSpawner` + skills (2026-08).  
**Implementação desta página:** valores **NOVO** = o que o código deve refletir após o ciclo de balance.

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

## 3. Faucets — antes → novo

| Trader | Trade | Antes | Novo | Racional |
|--------|-------|-------|------|----------|
| Fazendeiro | Wheat → 1e | 5 | **15** | Ainda melhor que vanilla 20; farm importa |
| Fazendeiro | Potato/Carrot/Cane → 1e | 12 | **24** | |
| Pescador | Cod/Salmon → 1e | 1→3e | **8→1e** | Acaba impressora de peixe |
| Pescador | Tropical → 1e | 1→7e | **4→1e** | |
| Pescador | Puffer → 1e | 1→10e | **3→1e** | |
| Açougueiro | qualquer carne → 1e | 1–3 | **8** | |
| Caçador | Flesh/Bone → 1e | 2 | **12** | |
| Caçador | Spider eye → e | 1→3e | **1→1e** | |
| Caçador | Gunpowder → 1e | 1 | **4** | |
| Mineiro | Copper → 1e | 2 | **4** | |
| Mineiro | Iron → 2e | 2 ing | **manter 2→2e** | |
| Mineiro | Gold → 3e | 2 | **manter** | |
| Mineiro | Diamond → e | 1→5e | **1→4e** | leve sink inverso |

---

## 4. Sinks / utilidade — antes → novo

| Trader | Oferta | Antes | Novo |
|--------|--------|-------|------|
| Engenheiro | TNT ×1 | 1e | **12e** |
| Engenheiro | Piston/Sticky ×8 | 1e | **4e** |
| Engenheiro | Observer ×8 | 1e | **4e** |
| Engenheiro | Repeater/Comparator/Dispenser ×8 | 1e→16 | **3e → 8** |
| Engenheiro | Sculk Sensor ×4 | 1e→16 | **6e → 4** |
| Engenheiro | Calibrated Sculk ×4 | 1e→8 | **8e → 4** |
| Engenheiro | Sticky slime/honey ×16 | 1e→32 | **6e → 16** |
| Ferreiro | Diamond sword | 7e | **14e** |
| Ferreiro | Pick/Axe | 12e | **16e** |
| Ferreiro | Shovel/Hoe | 7e | **11e** |
| Ferreiro | Armadura diamante | 20/35/30/15 | **manter** |
| Ferreiro | Netherite ingot | 32 EB (288e) | **manter** |
| Bibliotecário | livros | 16–32e | **manter** |
| End | Happy — n/a | — | — |
| Nether | Happy Ghast egg | 16e | **48e** |
| End | Dragon Breath | 1e | **8e** |
| End | End Crystal | 32e | **48e** |
| End | Obsidian ×4 | 8e→1 | **12e → 4** |
| End | Ender Pearl | 16e | **10e** |
| End | Shulker box | — | **32e** |
| Nether | Wither skulls ×3 | 64 EB | **48 EB** |
| Nether | Netherite template | 64 EB | **48 EB** |
| End | Elytra | 64 EB | **manter 64 EB** |
| Oceano | Storm Rider | tridente + heart | **tridente + conduit** |
| Oceano | Conduit | — | **1 heart + 8 nautilus** |
| Estilista | Armor trim ×4 | — | **1 disco específico** |
| Trapaceiro | Spawner | — | **48 EB** |
| Trapaceiro | Ovo hostil (zumbi/esqueleto/aranha/creeper) | — | **8 EB** |
| Trapaceiro | Ovo passivo (vaca/porco/galinha) | — | **32e** |
| Trapaceiro | Golden Bow (Power 9999, 1 dura) | — | **16 EB** |
| Trapaceiro | Graveto da Repulsão (Knockback 32, 1 dura) | — | **8 EB** |
| Trapaceiro | Maçã do Capiroto | — | **12 EB** |
| Trapaceiro | Peitoral do Pacto (Thorns 9999 / Prot -9999) | — | **16 EB** |

---

## 5. Paths lendários — antes → novo

| Arma | Componentes novos | Emerald-eq path | Gate |
|------|-------------------|-----------------|------|
| **Storm Rider** | Tridente **48e**; Heart **12 EB**; conduit (heart + 8 nautilus); craft final **tridente + conduit** | Path via conduit | Unicidade claim |
| **Doom Hammer** | Mace **48e**; Reinforced Deepslate **4e cad** ×64 = 256e; craft mace+64 | ~**304e** | Unicidade |
| **Soulflayer** | Bow + Nether Star; skulls **48 EB**; egg ghast **48e** | Progressão wither | Star + claim |
| **Dragon** | Netherite sword + Dragon Egg; elytra 64 EB | Progressão dragon | Egg + claim |

`maxUses` (descansar economia / limitar dump):

| Categoria | maxUses |
|-----------|---------|
| Cerco engenheiro (TNT/redstone) | **64** |
| Recipe final lendária | **32** |
| Trapaceiro combate / ovos hostis | **16** |
| Trapaceiro spawner | **8** |
| Demais trades | **128** |

(Antes: 9999 em tudo.)

---

## 6. Armas — CD / chance — antes → novo

| Ability | Antes | Novo | Racional |
|---------|-------|------|----------|
| Thunder Teleport | 15s | **25s** | Mobilidade sem spam engage |
| Storm weather | 120s → 60s | **removida** | Clima global estragava a vila (portas, siege, raios) |
| Hellfire Rain | 90s | **75s** | Compensa Dante mais raro |
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

Item de troca: quem veste morre com qualquer dano e leva o agressor junto.
Custo **16 EB**, `maxUses` 16. Vale acompanhar se vira estratégia de trade-kill
em massa contra o time com lendária; se virar, subir para 32 EB ou `maxUses` 4.

---

## 7. Checklist review pós dia-1

- [ ] Storm ainda é a 1ª lendária padrão? (ok.) Tem time abusando TP a cada fight? → subir CD para 30s.
- [ ] TNT spam em bases? → subir TNT para 16–20e ou maxUses 32.
- [ ] Doom ainda inalcançável / trivial? → ajustar deepslate 3–6e.
- [ ] Dante still feels like true damage every fight? → 8–10%.
- [ ] Emerald scarcity choking gear? → baixar farmer wheat 15→12.

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
