package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        Damageable damageable = (Damageable) meta;
        assertEquals(1, damageable.getMaxDamage());
        assertEquals(0, damageable.getDamage());
        assertTrue(TrapaceiroItems.isOneUseItem(bow));
        Enchantment power = TrapaceiroItems.minecraftEnchantment("power");
        assertEquals(9999, bow.getEnchantmentLevel(power));
    }

    @Test
    void knockbackStickIsOneUse() {
        ItemStack stick = TrapaceiroItems.knockbackStick();
        ItemMeta meta = stick.getItemMeta();
        assertTrue(meta instanceof Damageable);
        Damageable damageable = (Damageable) meta;
        assertEquals(1, damageable.getMaxDamage());
        assertEquals(0, damageable.getDamage());
        assertTrue(TrapaceiroItems.isOneUseItem(stick));
        Enchantment knockback = TrapaceiroItems.minecraftEnchantment("knockback");
        assertEquals(32, stick.getEnchantmentLevel(knockback));
    }

    @Test
    void pactChestplateCarriesThornsAndNegativeProtection() {
        ItemStack chestplate = TrapaceiroItems.pactChestplate();

        assertEquals(Material.CHAINMAIL_CHESTPLATE, chestplate.getType());
        assertEquals(1, chestplate.getEnchantmentLevel(
                TrapaceiroItems.minecraftEnchantment("binding_curse")));
        assertEquals(9999, chestplate.getEnchantmentLevel(
                TrapaceiroItems.minecraftEnchantment("thorns")));
        assertEquals(-9999, chestplate.getEnchantmentLevel(
                TrapaceiroItems.minecraftEnchantment("protection")));
    }

    @Test
    void pactChestplateIsIdentifiedByPersistentData() {
        assertTrue(TrapaceiroItems.isPactChestplate(TrapaceiroItems.pactChestplate()));
    }

    @Test
    void plainArmourIsNotMistakenForThePact() {
        assertFalse(TrapaceiroItems.isPactChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE)));
        assertFalse(TrapaceiroItems.isPactChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE)));
        assertFalse(TrapaceiroItems.isPactChestplate(null));
    }
}
