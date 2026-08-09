package org.lseixas.mineguerra_plugins.weapons;

/**
 * Momento em que o cooldown de uma habilidade deve ser gravado.
 */
public enum CooldownStart {
    /** Ao iniciar a habilidade (clique ou armar modo). */
    ON_ACTIVATE,
    /** Após o efeito ser aplicado com sucesso. */
    ON_SUCCESS,
    /** Após o fim de um canal (ex.: 3s de Dragon's Breath). */
    ON_CHANNEL_END
}
