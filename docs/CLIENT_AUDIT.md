# Auditoria de cliente Fabric

Handshake Paper ↔ mod Fabric **client-side** (ainda não publicado). O servidor é Paper 1.21.8; o cliente usa Fabric só para performance/shaders.

Isto **não** é anti-cheat à prova de mentira: o cliente declara mods/packs. Barra casual (mod extra, pack xray, sem o mod de audit).

Config: `plugins/mineguerra_plugins/client-allowlist.yml` (cópia de [`main/resources/client-allowlist.yml`](../main/resources/client-allowlist.yml)).

**`enabled: true` por padrão** — mod `mineguerra-client-audit` publicado. Staff com `mineguerra.admin` ignora o handshake.

## Perfil estrito

Performance + Iris + QoL sem HUD extra. Fora: Jade, Light Overlay, minimapa, Distant Horizons, AppleSkin, MiniHUD, Sodium Extra, cheats.

Canal: `mineguerra:client_audit`. Timeout: 100 ticks. Mode: `exact` (depois de ignorar `minecraft` / `java` / `fabricloader` / `fabric-*`).

## Payload v1

1. `short` protocol = 1  
2. UTF-8 (varint + bytes) `mcVersion`  
3. UTF-8 `loaderVersion`  
4. varint n + n × (`modId`, `version`)  
5. varint n + n × (`packId` + SHA-1 20 bytes)  
6. UTF-8 `shader` (vazio se nenhum)

## Lista publicada (ids `fabric.mod.json`)

Obrigatório: `mineguerra-client-audit`

Allowlist: `modmenu`, `yet_another_config_lib_v3`, `sodium`, `lithium`, `ferritecore`, `entityculling`, `immediatelyfast`, `moreculling`, `dynamic_fps`, `krypton`, `reeses-sodium-options`, `iris`, `nochatreports`, `debugify`, `ok_zoomer`

Launcher: Prism/Modrinth App, Fabric Loader, MC 1.21.8, sem OptiFine. Shaders sugeridos: Complementary Unbound ou BSL (pasta `shaderpacks/`). Packs de recurso: vanilla; o zip MineGuerra entra em `allowedPackSha1` quando for servido.

## Pacote Java

`main/java/.../clientaudit/` — `ClientAuditCodec`, `ClientAllowlist`, `ClientAuditListener`, `ClientAuditRegistry`.

Mod Fabric (repo separado, depois): `FabricLoader.getAllMods()` + resource packs ativos + Iris.
