package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrapaceiroItemTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void goldenBowIsOneUseWithPower() {
        ItemStack bow = TrapaceiroItems.goldenBow();
        ItemMeta meta = bow.getItemMeta();
        assertTrue(meta instanceof Damageable);
        assertEquals(1, ((Damageable) meta).getMaxDamage());
        Enchantment power = TrapaceiroItems.minecraftEnchantment("power");
        assertEquals(9999, bow.getEnchantmentLevel(power));
    }

    @Test
    void knockbackStickIsOneUse() {
        ItemStack stick = TrapaceiroItems.knockbackStick();
        ItemMeta meta = stick.getItemMeta();
        assertTrue(meta instanceof Damageable);
        assertEquals(1, ((Damageable) meta).getMaxDamage());
        Enchantment knockback = TrapaceiroItems.minecraftEnchantment("knockback");
        assertEquals(32, stick.getEnchantmentLevel(knockback));
    }
}
