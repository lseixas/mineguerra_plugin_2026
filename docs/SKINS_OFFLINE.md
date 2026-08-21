# Skins em servidor offline

O servidor do evento roda em `online-mode=false`, então a Mojang não entrega skin
nenhuma: todo mundo aparece como Steve/Alex. A solução é um plugin externo —
**não há código de skin no mineguerra_plugins**.

## SkinsRestorer

Plugin de referência para servidores offline/proxy. Cada jogador escolhe a skin
por comando, e o plugin guarda o resultado por nome.

### Instalação

1. Baixar o JAR em [skinsrestorer.net](https://skinsrestorer.net/) ou
   [Modrinth](https://modrinth.com/plugin/skinsrestorer) (build para Bukkit/Spigot/Paper 1.21).
2. Copiar para `plugins/`.
3. Reiniciar o servidor (não usar `/reload`).
4. Conferir `plugins/SkinsRestorer/config.yml` e `plugins/SkinsRestorer/`
   (o banco padrão é um arquivo local, sem MySQL).

No servidor do evento (`mine_guerra_bukkit_2026`) o **SkinsRestorer 15.12.5** já está
em `plugins/SkinsRestorer.jar` e no `MODRINTH_PROJECTS` do
`setup/mine-guerra-bukkit-2026/docker-compose.yaml` (atualiza sozinho no recreate).

### Comandos para os jogadores

| Comando | Função |
|---------|--------|
| `/skin set <nome>` | Aplica a skin da conta premium com esse nome |
| `/skin set <url>` | Aplica skin a partir de uma URL de imagem |
| `/skin clear` | Volta para a skin padrão |
| `/skin update` | Rebusca a skin atual do nome salvo |

Permissão típica: `skinsrestorer.command.set` (dar para o grupo padrão, senão
ninguém consegue trocar).

### Notas para o evento

- A skin é ligada ao **nome** que o jogador usa para entrar, não à conta Mojang.
  Se alguém trocar de nick, precisa rodar `/skin set` de novo.
- Skin não afeta o `clientaudit`: o handshake olha mods e resource pack, não perfil.
- Se a staff quiser padronizar por time, o caminho é `/skin set <url>` com as
  imagens hospedadas em algum lugar público.
- Vale avisar o pessoal para aplicar a skin **antes** de sexta 18:00, porque a
  primeira aplicação precisa de rede e leva alguns segundos.
