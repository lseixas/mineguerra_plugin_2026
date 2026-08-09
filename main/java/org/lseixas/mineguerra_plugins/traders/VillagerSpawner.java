package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Merchant;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.lseixas.mineguerra_plugins.doomhammer.DoomHammerFactory;
import org.lseixas.mineguerra_plugins.dragonslayer.DragonSlayerFactory;
import org.lseixas.mineguerra_plugins.soulflayerbow.SoulflayerBowFactory;
import org.lseixas.mineguerra_plugins.stormrider.StormRiderFactory;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

import java.util.ArrayList;
import java.util.List;

public class VillagerSpawner {

    private static void numbifyVillager(Villager villager, Villager.Profession prof) {
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCollidable(false);
        villager.setRemoveWhenFarAway(false);
        villager.setProfession(prof);

        List<MerchantRecipe> emptyRecipes = new ArrayList<>();
        villager.setRecipes(emptyRecipes);
    }

    // Para Wandering Traders
    private static void numbifyTrader(WanderingTrader trader) {
        trader.setAI(false);
        trader.setInvulnerable(true);
        trader.setCollidable(false);
        trader.setRemoveWhenFarAway(false);
        trader.setDespawnDelay(Integer.MAX_VALUE);

        List<MerchantRecipe> emptyRecipes = new ArrayList<>();
        trader.setRecipes(emptyRecipes);
    }

    private static void prepareVillager(Villager villager, Villager.Profession profession) {
        numbifyVillager(villager, profession);
        villager.setAdult();
        villager.setVillagerLevel(5);
    }

    private static void setMerchantName(Merchant merchant, String name) {
        if (merchant instanceof LivingEntity living) {
            living.setCustomName(name);
            living.setCustomNameVisible(true);
        }
    }

    private static ItemStack makeEnchantedBook(
            int stackAmount,
            Enchantment enchantment,
            int level
    ) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, stackAmount);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(meta);
        return book;
    }

    public static void spawnExplOceano(Location loc) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        applyOceanExplorer(trader);
    }

    private static List<MerchantRecipe> buildOceanExplorerTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(new ItemStack(Material.TRIDENT, 1), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD, 32));
        trades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(makeEnchantedBook(1, Enchantment.LOYALTY, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 16));
        trades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(makeEnchantedBook(1, Enchantment.CHANNELING, 1), 9999);
        t3.addIngredient(new ItemStack(Material.EMERALD, 32));
        trades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(makeEnchantedBook(1, Enchantment.RIPTIDE, 1), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 32));
        trades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(new ItemStack(Material.HEART_OF_THE_SEA, 1), 9999);
        t5.addIngredient(new ItemStack(Material.EMERALD_BLOCK, 8));
        trades.add(t5);

        MerchantRecipe t6 = new MerchantRecipe(StormRiderFactory.createStormRider(), 9999);
        t6.addIngredient(new ItemStack(Material.TRIDENT, 1));
        t6.addIngredient(new ItemStack(Material.HEART_OF_THE_SEA, 1));
        trades.add(t6);

        return filterWeaponTrades(trades);
    }

    private static void applyOceanExplorer(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.DARK_BLUE + "Explorador do Oceano");
        merchant.setRecipes(buildOceanExplorerTrades());
    }

    private static void prepareMerchantEntity(Merchant merchant) {
        if (merchant instanceof WanderingTrader trader) {
            numbifyTrader(trader);
            trader.setAdult();
        } else if (merchant instanceof Villager villager) {
            prepareVillager(villager, Villager.Profession.NONE);
        }
    }

    public static void spawnExplProfundo(Location loc) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        applyDeepExplorer(trader);
    }

    private static List<MerchantRecipe> buildDeepExplorerTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(new ItemStack(Material.REINFORCED_DEEPSLATE, 1), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD, 16));
        trades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(new ItemStack(Material.MACE, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 32));
        trades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(DoomHammerFactory.createDoomHammer(), 9999);
        t3.addIngredient(new ItemStack(Material.MACE, 1));
        t3.addIngredient(new ItemStack(Material.REINFORCED_DEEPSLATE, 64));
        trades.add(t3);

        return filterWeaponTrades(trades);
    }

    private static void applyDeepExplorer(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.DARK_BLUE + "Explorador das Profundezas");
        merchant.setRecipes(buildDeepExplorerTrades());
    }

    public static void spawnExplNether(Location loc) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        applyNetherExplorer(trader);
    }

    private static List<MerchantRecipe> buildNetherExplorerTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(new ItemStack(Material.WITHER_SKELETON_SKULL, 3), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD_BLOCK, 64));
        trades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD_BLOCK, 64));
        trades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(SoulflayerBowFactory.createSoulflayerBow(), 9999);
        t3.addIngredient(new ItemStack(Material.BOW, 1));
        t3.addIngredient(new ItemStack(Material.NETHER_STAR, 1));
        trades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(new ItemStack(Material.HAPPY_GHAST_SPAWN_EGG, 1), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 16));
        trades.add(t4);

        return filterWeaponTrades(trades);
    }

    private static void applyNetherExplorer(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.DARK_RED + "Explorador do Nether");
        merchant.setRecipes(buildNetherExplorerTrades());
    }

    public static void spawnExplEnd(Location loc) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        applyEndExplorer(trader);
    }

    private static List<MerchantRecipe> buildEndExplorerTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(new ItemStack(Material.ELYTRA, 1), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD_BLOCK, 64));
        trades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(new ItemStack(Material.DRAGON_BREATH, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 1));
        trades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(new ItemStack(Material.END_CRYSTAL, 1), 9999);
        t3.addIngredient(new ItemStack(Material.EMERALD, 32));
        trades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(new ItemStack(Material.OBSIDIAN, 1), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 8));
        trades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(new ItemStack(Material.ENDER_PEARL, 1), 9999);
        t5.addIngredient(new ItemStack(Material.EMERALD, 16));
        trades.add(t5);

        MerchantRecipe t6 = new MerchantRecipe(DragonSlayerFactory.createDragonSlayer(), 9999);
        t6.addIngredient(new ItemStack(Material.NETHERITE_SWORD, 1));
        t6.addIngredient(new ItemStack(Material.DRAGON_EGG, 1));
        trades.add(t6);

        return filterWeaponTrades(trades);
    }

    private static void applyEndExplorer(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.DARK_PURPLE + "Explorador do End");
        merchant.setRecipes(buildEndExplorerTrades());
    }

    public static void spawnCacadorMonstros(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        applyMonsterHunter(villager);
    }

    private static void applyMonsterHunter(Villager villager) {
        prepareVillager(villager, Villager.Profession.ARMORER);
        setMerchantName(villager, ChatColor.YELLOW + "Caçador de Monstros");

        List<MerchantRecipe> trades = new ArrayList<>();
        MerchantRecipe t1 = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 9999);
        t1.addIngredient(new ItemStack(Material.ROTTEN_FLESH, 2));
        trades.add(t1);
        MerchantRecipe t2 = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 9999);
        t2.addIngredient(new ItemStack(Material.BONE, 2));
        trades.add(t2);
        MerchantRecipe t3 = new MerchantRecipe(new ItemStack(Material.EMERALD, 3), 9999);
        t3.addIngredient(new ItemStack(Material.SPIDER_EYE, 1));
        trades.add(t3);
        MerchantRecipe t4 = new MerchantRecipe(new ItemStack(Material.EMERALD, 1), 9999);
        t4.addIngredient(new ItemStack(Material.GUNPOWDER, 1));
        trades.add(t4);
        villager.setRecipes(trades);
    }

    public static void spawnEngenheiro(Location loc) {

        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FLETCHER);

        villager.setCustomName(ChatColor.BLACK + "Engenheiro");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> engineerTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.TNT, 1), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.PISTON, 8), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.STICKY_PISTON, 8), 9999);
        t3.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.REPEATER, 16), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(
                new ItemStack(Material.COMPARATOR, 16), 9999);
        t5.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t5);

        MerchantRecipe t6 = new MerchantRecipe(
                new ItemStack(Material.DISPENSER, 16), 9999);
        t6.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t6);

        MerchantRecipe t7 = new MerchantRecipe(
                new ItemStack(Material.SLIME_BLOCK, 32), 9999);
        t7.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t7);

        MerchantRecipe t8 = new MerchantRecipe(
                new ItemStack(Material.HONEY_BLOCK, 32), 9999);
        t8.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t8);

        MerchantRecipe t9 = new MerchantRecipe(
                new ItemStack(Material.SCULK_SENSOR, 16), 9999);
        t9.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t9);

        MerchantRecipe t10 = new MerchantRecipe(
                new ItemStack(Material.OBSERVER, 8), 9999);
        t10.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t10);

        MerchantRecipe t11 = new MerchantRecipe(
                new ItemStack(Material.CALIBRATED_SCULK_SENSOR, 8), 9999);
        t11.addIngredient(new ItemStack(Material.EMERALD, 1));
        engineerTrades.add(t11);

        villager.setRecipes(engineerTrades);

    }

    public static void spawnFerreiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.TOOLSMITH);

        villager.setCustomName(ChatColor.DARK_GRAY + "Ferreiro");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> smithTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_HELMET, 1), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD, 20));
        smithTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_CHESTPLATE, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 35));
        smithTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_LEGGINGS, 1), 9999);
        t3.addIngredient(new ItemStack(Material.EMERALD, 30));
        smithTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_BOOTS, 1), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 15));
        smithTrades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_SWORD, 1), 9999);
        t5.addIngredient(new ItemStack(Material.EMERALD, 7));
        smithTrades.add(t5);

        MerchantRecipe t6 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_PICKAXE, 1), 9999);
        t6.addIngredient(new ItemStack(Material.EMERALD, 12));
        smithTrades.add(t6);

        MerchantRecipe t7 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_AXE, 1), 9999);
        t7.addIngredient(new ItemStack(Material.EMERALD, 12));
        smithTrades.add(t7);

        MerchantRecipe t8 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_SHOVEL, 1), 9999);
        t8.addIngredient(new ItemStack(Material.EMERALD, 7));
        smithTrades.add(t8);

        MerchantRecipe t9 = new MerchantRecipe(
                new ItemStack(Material.DIAMOND_HOE, 1), 9999);
        t9.addIngredient(new ItemStack(Material.EMERALD, 7));
        smithTrades.add(t9);

        MerchantRecipe t10 = new MerchantRecipe(
                new ItemStack(Material.NETHERITE_INGOT, 1), 9999);
        t10.addIngredient(new ItemStack(Material.EMERALD_BLOCK, 32));
        smithTrades.add(t10);

        villager.setRecipes(smithTrades);
    }

    public static void spwanBibliotecario(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.LIBRARIAN);

        villager.setCustomName(ChatColor.LIGHT_PURPLE + "Bibliotecário");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> libraryTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.EFFICIENCY, 3), 9999);
        t1.addIngredient(new ItemStack(Material.EMERALD, 32));
        libraryTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.UNBREAKING, 1), 9999);
        t2.addIngredient(new ItemStack(Material.EMERALD, 16));
        libraryTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.PROTECTION, 2), 9999);
        t3.addIngredient(new ItemStack(Material.EMERALD, 32));
        libraryTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.SHARPNESS, 2), 9999);
        t4.addIngredient(new ItemStack(Material.EMERALD, 32));
        libraryTrades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.MENDING, 1), 9999);
        t5.addIngredient(new ItemStack(Material.EMERALD, 32));
        libraryTrades.add(t5);

        MerchantRecipe t6 = new MerchantRecipe(
                makeEnchantedBook(1, Enchantment.FORTUNE, 1), 9999);
        t6.addIngredient(new ItemStack(Material.EMERALD, 32));
        libraryTrades.add(t6);

        villager.setRecipes(libraryTrades);
    }

    public static void spawnPescador(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FISHERMAN);

        villager.setCustomName(ChatColor.AQUA + "Pescador");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> fisherTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 3), 9999);
        t1.addIngredient(new ItemStack(Material.COD, 1));
        fisherTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 3), 9999);
        t2.addIngredient(new ItemStack(Material.SALMON, 1));
        fisherTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 7), 9999);
        t3.addIngredient(new ItemStack(Material.TROPICAL_FISH, 1));
        fisherTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 10), 9999);
        t4.addIngredient(new ItemStack(Material.PUFFERFISH, 1));
        fisherTrades.add(t4);

        villager.setRecipes(fisherTrades);
    }

    public static void spawnMineiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.WEAPONSMITH);

        villager.setCustomName(ChatColor.GRAY + "Mineiro");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> minerTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t1.addIngredient(new ItemStack(Material.COPPER_INGOT, 2));
        minerTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 2), 9999);
        t2.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        minerTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 3), 9999);
        t3.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        minerTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 5), 9999);
        t4.addIngredient(new ItemStack(Material.DIAMOND, 1));
        minerTrades.add(t4);

        villager.setRecipes(minerTrades);
    }

    public static void spawnFazendeiro(Location loc) {

        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FARMER);

        villager.setCustomName(ChatColor.GREEN + "Fazendeiro");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> farmerTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t1.addIngredient(new ItemStack(Material.WHEAT, 5));
        farmerTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t2.addIngredient(new ItemStack(Material.POTATO, 12));
        farmerTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t3.addIngredient(new ItemStack(Material.CARROT, 12));
        farmerTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t4.addIngredient(new ItemStack(Material.SUGAR_CANE, 12));
        farmerTrades.add(t4);

        villager.setRecipes(farmerTrades);
    }

    public static void spawnAcougueiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.BUTCHER);

        villager.setCustomName(ChatColor.WHITE + "Açougueiro");
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> butcherTrades = new ArrayList<>();

        MerchantRecipe t1 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t1.addIngredient(new ItemStack(Material.CHICKEN, 3));
        butcherTrades.add(t1);

        MerchantRecipe t2 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t2.addIngredient(new ItemStack(Material.PORKCHOP, 2));
        butcherTrades.add(t2);

        MerchantRecipe t3 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t3.addIngredient(new ItemStack(Material.BEEF, 2));
        butcherTrades.add(t3);

        MerchantRecipe t4 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t4.addIngredient(new ItemStack(Material.MUTTON, 2));
        butcherTrades.add(t4);

        MerchantRecipe t5 = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1), 9999);
        t5.addIngredient(new ItemStack(Material.RABBIT, 1));
        butcherTrades.add(t5);

        villager.setRecipes(butcherTrades);
    }

    public static void spawn(TraderType type, Location location) {
        switch (type) {
            case OCEANO -> spawnExplOceano(location);
            case PROFUNDEZAS -> spawnExplProfundo(location);
            case NETHER -> spawnExplNether(location);
            case END -> spawnExplEnd(location);
            case BIBLIOTECARIO -> spwanBibliotecario(location);
            case MONSTROS -> spawnCacadorMonstros(location);
            case ACOUGUEIRO -> spawnAcougueiro(location);
            case FERREIRO -> spawnFerreiro(location);
            case ENGENHEIRO -> spawnEngenheiro(location);
            case PESCADOR -> spawnPescador(location);
            case MINEIRO -> spawnMineiro(location);
            case FAZENDEIRO -> spawnFazendeiro(location);
        }
    }

    public static boolean configureMerchant(TraderType type, Merchant merchant) {
        if (merchant == null) {
            return false;
        }

        switch (type) {
            case OCEANO -> applyOceanExplorer(merchant);
            case PROFUNDEZAS -> applyDeepExplorer(merchant);
            case NETHER -> applyNetherExplorer(merchant);
            case END -> applyEndExplorer(merchant);
            case MONSTROS -> {
                if (!(merchant instanceof Villager villager)) {
                    return false;
                }
                applyMonsterHunter(villager);
            }
            default -> {
                if (!(merchant instanceof Villager villager)) {
                    return false;
                }
                return configureVillager(type, villager);
            }
        }
        return true;
    }

    private static boolean configureVillager(TraderType type, Villager villager) {
        Location loc = villager.getLocation();
        switch (type) {
            case BIBLIOTECARIO -> {
                villager.remove();
                spwanBibliotecario(loc);
            }
            case ACOUGUEIRO -> {
                villager.remove();
                spawnAcougueiro(loc);
            }
            case FERREIRO -> {
                villager.remove();
                spawnFerreiro(loc);
            }
            case ENGENHEIRO -> {
                villager.remove();
                spawnEngenheiro(loc);
            }
            case PESCADOR -> {
                villager.remove();
                spawnPescador(loc);
            }
            case MINEIRO -> {
                villager.remove();
                spawnMineiro(loc);
            }
            case FAZENDEIRO -> {
                villager.remove();
                spawnFazendeiro(loc);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static List<MerchantRecipe> filterWeaponTrades(List<MerchantRecipe> trades) {
        try {
            return TeamRegistry.weapons().filterAvailableTrades(trades);
        } catch (IllegalStateException ex) {
            return trades;
        }
    }
}

