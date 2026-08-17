# Resource pack das armas lendárias

O plugin já grava CustomModelData `10001`–`10004` nas armas. Sem o pack, elas parecem o item vanilla encantado.

Pasta: [`resourcepack/MineGuerra_Weapons/`](../resourcepack/MineGuerra_Weapons/)  
Créditos/licenças: [`CREDITS.md`](../resourcepack/MineGuerra_Weapons/CREDITS.md)

Alvo: **Minecraft 1.21.6–1.21.8** (item definitions 1.21.4+). Arcos/espadas/tridentes/maces **sem** esses CMDs continuam vanilla.

## Modelos

| Arma | CMD | Visual |
|------|-----|--------|
| Soulflayer Bow | 10001 | Elite Power Bow (textura 2D + pulling) |
| Dragon Slayer | 10002 | ThirtyFangs Demon Lord Sword (3D) |
| Storm Rider | 10003 | rainbow's 3D Trident (GUI / mão / arremesso) |
| Doom Hammer | 10004 | Mace Fusion (3D na mão + ícone GUI) |

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

Em `server.properties`:

```properties
resource-pack=<URL pública do zip>
resource-pack-sha1=<sha1 do zip>
require-resource-pack=true
```

Jogadores precisam aceitar o pack para ver os modelos. Itens já grantados não precisam ser refeitos.

## Não incluso

- [Trident Revamp](https://modrinth.com/resourcepack/trident-revamp) — só OptiFine/CEM; mudaria todos os tridentes.
