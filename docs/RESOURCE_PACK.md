# Resource pack das armas lendárias

O plugin já grava CustomModelData `10001`–`10004` nas armas. Sem o pack, elas parecem o item vanilla encantado.

Pasta: [`resourcepack/MineGuerra_Weapons/`](../resourcepack/MineGuerra_Weapons/)  
Créditos/licenças: [`CREDITS.md`](../resourcepack/MineGuerra_Weapons/CREDITS.md)

Alvo: **Minecraft 1.21.6–1.21.8** (item definitions 1.21.4+). Arcos/espadas/tridentes/maces **sem** esses CMDs continuam vanilla.

## Modelos

| Arma | CMD | Visual |
|------|-----|--------|
| Soulflayer Bow | 10001 | Elite Power Bow (textura 2D + pulling) |
| Dragon Slayer | 10002 | Fantasy 3D — Ravenous Blade (3D) |
| Storm Rider | 10003 | Fantasy 3D — Heavenly Partisan (3D) |
| Doom Hammer | 10004 | Fantasy 3D — Treacherous Bludgeon (3D) |

## Instalar no cliente

1. Compactar a pasta `MineGuerra_Weapons` (o arquivo precisa ter `pack.mcmeta` na raiz do zip):

```bash
cd resourcepack
python3 -c "import shutil; shutil.make_archive('MineGuerra_Weapons', 'zip', 'MineGuerra_Weapons')"
```

(equivalente: `zip -r MineGuerra_Weapons.zip MineGuerra_Weapons` se `zip` estiver instalado)

2. Copiar o zip para `.minecraft/resourcepacks/`
3. Ativar **MineGuerra Weapons** em Opções → Resource Packs (acima do vanilla)

## Instalar no servidor

O servidor de evento **exige** o pack no join (`require-resource-pack=true`).

Zip servido só no Tailscale: `http://100.94.48.24:25568/MineGuerra_Weapons.zip` (nginx `mine-guerra-resourcepack`, bind `100.94.48.24`).  
SHA-1 atual: `b5d400b5b99d32a7b55ef22beeac475dbd70293b`

```properties
resource-pack=http://100.94.48.24:25568/MineGuerra_Weapons.zip
resource-pack-sha1=b5d400b5b99d32a7b55ef22beeac475dbd70293b
require-resource-pack=true
```

Jogadores precisam aceitar o pack para entrar. Recalcular SHA-1 se o zip mudar e atualizar `client-allowlist.yml` (`allowedPackSha1`).

## Não incluso

- [Trident Revamp](https://modrinth.com/resourcepack/trident-revamp) — só OptiFine/CEM; mudaria todos os tridentes.
