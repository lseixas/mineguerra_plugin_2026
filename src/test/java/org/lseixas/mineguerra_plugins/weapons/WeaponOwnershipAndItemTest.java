package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lseixas.mineguerra_plugins.teams.TeamsDataStore;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponOwnershipAndItemTest {

    private ServerMock server;
    private JavaPlugin plugin;
    private WeaponItemService items;
    private TeamsDataStore store;
    private WeaponOwnershipService ownership;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        items = new WeaponItemService(plugin);
        store = new TeamsDataStore(plugin);
        ownership = new WeaponOwnershipService(store, items);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void createdLegendaryMatchesByPdc() {
        ItemStack bow = items.create(WeaponId.SOULFLAYER_BOW);
        assertTrue(items.matches(bow, WeaponId.SOULFLAYER_BOW));
        assertFalse(items.matches(bow, WeaponId.STORM_RIDER));
        assertEquals(WeaponId.SOULFLAYER_BOW, items.identify(bow).orElseThrow());
    }

    @Test
    void claimKeepsTradeUnavailableEvenWithoutOnlineItem() {
        assertTrue(ownership.isAvailable(WeaponId.DOOM_HAMMER));

        WeaponClaim claim = new WeaponClaim("deep");
        store.getWeaponClaims().put(WeaponId.DOOM_HAMMER, claim);

        assertFalse(ownership.isAvailable(WeaponId.DOOM_HAMMER));
        assertEquals("deep", ownership.getOwnerTeamId(WeaponId.DOOM_HAMMER).orElseThrow());
    }

    @Test
    void filterAvailableTradesHidesClaimedLegendary() {
        store.getWeaponClaims().put(WeaponId.STORM_RIDER, new WeaponClaim("ocean"));

        MerchantRecipe stormTrade = new MerchantRecipe(items.create(WeaponId.STORM_RIDER), 32);
        stormTrade.addIngredient(new ItemStack(Material.TRIDENT));
        stormTrade.addIngredient(new ItemStack(Material.CONDUIT));

        MerchantRecipe filler = new MerchantRecipe(new ItemStack(Material.EMERALD), 32);
        filler.addIngredient(new ItemStack(Material.COD, 8));

        List<MerchantRecipe> filtered = ownership.filterAvailableTrades(List.of(stormTrade, filler));
        assertEquals(1, filtered.size());
        assertEquals(Material.EMERALD, filtered.get(0).getResult().getType());
    }

    @Test
    void releaseIfGoneClearsClaimWhenWeaponAbsentOnline() {
        store.getWeaponClaims().put(WeaponId.DRAGON_SLAYER, new WeaponClaim("end"));
        assertFalse(ownership.isAvailable(WeaponId.DRAGON_SLAYER));

        ownership.releaseIfGone(WeaponId.DRAGON_SLAYER);

        assertTrue(ownership.isAvailable(WeaponId.DRAGON_SLAYER));
        assertTrue(ownership.getOwnerTeamId(WeaponId.DRAGON_SLAYER).isEmpty());
    }
}
