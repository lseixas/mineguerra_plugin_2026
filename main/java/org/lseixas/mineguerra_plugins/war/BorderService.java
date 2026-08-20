package org.lseixas.mineguerra_plugins.war;

import org.bukkit.World;
import org.bukkit.WorldBorder;

/**
 * Encolhimento do world border ("centro fechando").
 */
public class BorderService {

    /** Encolhe do tamanho inicial até o final ao longo da duração configurada. */
    public void startShrink(World world, WarSchedule.BorderSettings settings) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(settings.centerX(), settings.centerZ());
        border.setSize(settings.fromSize());
        border.setSize(settings.toSize(), settings.durationSeconds());
    }

    /** Devolve o border ao padrão do mundo. */
    public void reset(World world) {
        world.getWorldBorder().reset();
    }
}
