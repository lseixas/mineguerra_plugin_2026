# Áreas No-Spawn

Ferramenta de staff (lightning rod) para delimitar **cuboides por bloco** onde mobs não spawnam.

## Comando

| Comando | Função |
|---------|--------|
| `/grantNoSpawnTool` | Dá a ferramenta |
| `/grantNoSpawnTool list` | Lista zonas |
| `/grantNoSpawnTool clear` | Remove todas |

Permissão: `mineguerra.nospawn` (default: op).  
Aliases: `/gnst`, `/nospawntool`.

Persistência: `plugins/mineguerra_plugins/nospawn-zones.yml`.

## Uso da ferramenta

1. **Clique direito** num bloco → canto 1
2. **Clique direito** noutro bloco → canto 2 (preview em partículas ciano)
3. **Shift + clique direito** → confirma a zona (flash verde)
4. **Shift + clique esquerdo** num bloco dentro da zona → remove
5. **Clique esquerdo no ar** → limpa seleção pendente

Com a ferramenta na mão: partículas **vermelhas** nas zonas já salvas (próximas) e **ciano** na seleção atual.

## Regras de spawn

Cancelado tudo dentro da zona, **exceto**:

- `CUSTOM` (plugins)
- `COMMAND`
- `SPAWNER_EGG` (ovos de staff)

Spawn natural, spawner, breeding, reinforcements, etc. são bloqueados.

## Pacote

`main/java/.../nospawn/` — `NoSpawnRegistry`, store YAML, tool listener, spawn listener.
