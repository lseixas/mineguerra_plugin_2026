# Auditoria de cliente Fabric

Handshake Paper ↔ mod Fabric **client-side** (ainda não publicado). O servidor é Paper 1.21.8; o cliente usa Fabric só para performance/shaders.

Isto **não** é anti-cheat à prova de mentira: o cliente declara mods/packs. Barra casual (mod extra, pack xray, sem o mod de audit).

Config: `plugins/mineguerra_plugins/client-allowlist.yml` (cópia de [`main/resources/client-allowlist.yml`](../main/resources/client-allowlist.yml)).

**`enabled: true` por padrão** — mod `mineguerra-client-audit` publicado. Staff com `mineguerra.admin` ignora o handshake.

## Perfil estrito

Assinatura **exact** dos ids (nem extra, nem faltando). Versões não são pinadas. Fora da lista: Jade, minimapa, Distant Horizons, etc.

Canal: `mineguerra:client_audit`. Timeout: 100 ticks. Mode: `exact` (depois de ignorar `minecraft` / `java` / `fabricloader` / `mixinextras` / `fabric-*`).

## Payload v1

1. `short` protocol = 1  
2. UTF-8 (varint + bytes) `mcVersion`  
3. UTF-8 `loaderVersion`  
4. varint n + n × (`modId`, `version`)  
5. varint n + n × (`packId` + SHA-1 20 bytes)  
6. UTF-8 `shader` (vazio se nenhum)

## Lista publicada (ids `fabric.mod.json`)

Obrigatório: `mineguerra-client-audit`

Allowlist: `cloth-config`, `debugify`, `dynamic_fps`, `entityculling`, `ferritecore`, `immediatelyfast`, `iris`, `krypton`, `lithium`, `modmenu`, `moreculling`, `reeses-sodium-options`, `placeholder-api`, `yet_another_config_lib_v3`, `sodium`

Launcher: Prism/Modrinth App, Fabric Loader, MC 1.21.8, sem OptiFine.

Shaders permitidos: Miniature Shader by ukrech, Complementary Unbound, Complementary Reimagined (ou desligado).

Resource packs: vanilla/server + zip MineGuerra Weapons (`allowedPackSha1`).

## Pacote Java

`main/java/.../clientaudit/` — `ClientAuditCodec`, `ClientAllowlist`, `ClientAuditListener`, `ClientAuditRegistry`.

Mod Fabric (repo separado, depois): `FabricLoader.getAllMods()` + resource packs ativos + Iris.
