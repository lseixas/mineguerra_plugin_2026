# Áreas No-Break

Ferramenta de staff (pincel / brush) para delimitar **cuboides por bloco** onde
blocos não podem ser quebrados nem colocados por jogadores.

## Comando

| Comando | Função |
|---------|--------|
| `/grantNoBreakTool` | Dá a ferramenta |
| `/grantNoBreakTool list` | Lista zonas |
| `/grantNoBreakTool clear` | Remove todas |

Permissão: `mineguerra.nobreak` (default: op).  
Aliases: `/gnbt`, `/nobreaktool`.

Persistência: `plugins/mineguerra_plugins/nobreak-zones.yml`.

## Uso da ferramenta

Igual ao [no-spawn](NO_SPAWN_ZONES.md):

1. **Clique direito** num bloco → canto 1
2. **Clique direito** noutro bloco → canto 2 (preview em partículas douradas)
3. **Shift + clique direito** → confirma a zona (flash verde)
4. **Shift + clique esquerdo** num bloco dentro da zona → remove
5. **Clique esquerdo no ar** → limpa seleção pendente

Com a ferramenta na mão: partículas **roxas** nas zonas já salvas (próximas) e **douradas** na seleção atual.

## Regras de proteção

Cancelado dentro da zona:

- Quebra por jogador **sem** `mineguerra.nobreak` (staff/OP ainda pode quebrar)
- Colocação de bloco por jogador **sem** `mineguerra.nobreak` (staff/OP ainda pode construir)
- Explosão (TNT, creeper, crystal, cama, etc.) — bloco some da lista, não dropa
- Fogo queimando o bloco
- Entidade mudando bloco (enderman, wither, falling block, etc.)

## Pistões (não bloqueados)

Pistões funcionam normalmente dentro e fora da zona. O bloqueio anterior congelava
qualquer redstone construída na área, então foi removido. Colocação por jogador é
bloqueada via `BlockPlaceEvent` — eventos de pistão (`BlockPistonExtend` /
`BlockPistonRetract`) **não** são cancelados.

Consequência aceita: um pistão pode empurrar/puxar blocos protegidos. Se precisar
travar um mecanismo específico, prefira o posicionamento físico da build.

## Pacote

`main/java/.../nobreak/` — `NoBreakRegistry`, store YAML, tool listener, protect listener.
