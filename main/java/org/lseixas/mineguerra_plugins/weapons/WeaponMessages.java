package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.entity.Player;

/**
 * Mensagens de chat padronizadas: {@code §cor§l[Arma] §7mensagem}.
 */
public final class WeaponMessages {

    private WeaponMessages() {
    }

    public static void sendCooldown(Player player, WeaponId weapon, String abilityName, long secondsRemaining) {
        player.sendMessage(weapon.getMessagePrefix() + " §cCooldown §7(" + abilityName + "): §e" + secondsRemaining + "s");
    }

    public static void sendInfo(Player player, WeaponId weapon, String message) {
        player.sendMessage(weapon.getMessagePrefix() + " §7" + message);
    }

    public static void sendActivated(Player player, WeaponId weapon, String abilityName) {
        player.sendMessage(weapon.getMessagePrefix() + " §a" + abilityName + "!");
    }

    public static void sendModeEnabled(Player player, WeaponId weapon, String abilityName, String hint) {
        sendInfo(player, weapon, "§e" + abilityName + " §7ativado. " + hint);
    }

    public static void sendModeDisabled(Player player, WeaponId weapon, String abilityName) {
        sendInfo(player, weapon, "§e" + abilityName + " §7desativado.");
    }

    public static void sendGrant(Player player, WeaponId weapon) {
        sendInfo(player, weapon, "Voce recebeu o item.");
    }
}
