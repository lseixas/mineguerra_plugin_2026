# Documentação — mineguerra_plugins

Pasta de contexto para desenvolvedores e agentes de IA. Leia na ordem sugerida ao entrar no projeto.

## Ordem de leitura

1. [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) — o que é o evento e como o plugin se encaixa
2. [WEAPONS_CATALOG.md](WEAPONS_CATALOG.md) — inventário factual das 4 armas (skills, CD, arquivos)
3. [ARCHITECTURE_AS_IS.md](ARCHITECTURE_AS_IS.md) — padrões atuais e dívidas técnicas
4. [STANDARDS_TARGET.md](STANDARDS_TARGET.md) — como o código **deve** ficar (alvo)
5. [REFACTOR_ROADMAP.md](REFACTOR_ROADMAP.md) — fases, ordem de migração, critérios de pronto
6. [TEAMS_AND_LEADERBOARD.md](TEAMS_AND_LEADERBOARD.md) — times, kills, placar lateral
7. [FLAGS_AND_LEGENDARY_WEAPONS.md](FLAGS_AND_LEGENDARY_WEAPONS.md) — bandeiras e unicidade das armas
8. [PRE_EVENT_HARDEN.md](PRE_EVENT_HARDEN.md) — checklist e status do endurecimento pré-evento
9. [BALANCE.md](BALANCE.md) — balanceamento weekend (esmeraldas, CDs, paths lendários)
10. [RESOURCE_PACK.md](RESOURCE_PACK.md) — modelos 3D/2D das armas lendárias (CMD 10001–10004)

## Raiz do repositório

- [AGENTS.md](../AGENTS.md) — resumo rápido para Cursor e outros agentes

## Manutenção

- Ao migrar uma arma para `weapons/`, marque o status em `WEAPONS_CATALOG.md` e `REFACTOR_ROADMAP.md`.
- Não duplicar o plano Cursor em `.cursor/plans/` aqui; esta pasta é a fonte de verdade do projeto.
