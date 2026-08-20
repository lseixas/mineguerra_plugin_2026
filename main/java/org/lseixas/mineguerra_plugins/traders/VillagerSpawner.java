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

/**
 * Spawn/config de villagers do evento. Preços e maxUses: ver {@code docs/BALANCE.md}.
 */
public class VillagerSpawner {

    /** Trades comuns (faucets, gear, livros). */
    private static final int USES_DEFAULT = 128;
    /** Cerco / redstone bulk. */
    private static final int USES_SIEGE = 64;
    /** Recipe final de arma lendária. */
    private static final int USES_LEGENDARY = 32;
    /** Itens meme/combate do Trapaceiro (1 uso real no item). */
    private static final int USES_CHEAT = 16;
    /** Spawner: sink pesado, poucas compras. */
    private static final int USES_SPAWNER = 8;

    private static MerchantRecipe recipe(ItemStack result, int maxUses, ItemStack... ingredients) {
        MerchantRecipe trade = new MerchantRecipe(result, maxUses);
        for (ItemStack ingredient : ingredients) {
            trade.addIngredient(ingredient);
        }
        return trade;
    }

    private static void numbifyVillager(Villager villager, Villager.Profession prof) {
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCollidable(false);
        villager.setRemoveWhenFarAway(false);
        villager.setProfession(prof);

        List<MerchantRecipe> emptyRecipes = new ArrayList<>();
        villager.setRecipes(emptyRecipes);
    }

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
            living.setCustomNameVisible(false);
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
        trades.add(recipe(new ItemStack(Material.TRIDENT, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 48)));
        trades.add(recipe(makeEnchantedBook(1, Enchantment.LOYALTY, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 24)));
        trades.add(recipe(makeEnchantedBook(1, Enchantment.CHANNELING, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 40)));
        trades.add(recipe(makeEnchantedBook(1, Enchantment.RIPTIDE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 40)));
        trades.add(recipe(new ItemStack(Material.HEART_OF_THE_SEA, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD_BLOCK, 12)));
        trades.add(recipe(new ItemStack(Material.CONDUIT, 1), USES_DEFAULT,
                new ItemStack(Material.HEART_OF_THE_SEA, 1),
                new ItemStack(Material.NAUTILUS_SHELL, 8)));
        trades.add(recipe(StormRiderFactory.createStormRider(), USES_LEGENDARY,
                new ItemStack(Material.TRIDENT, 1),
                new ItemStack(Material.CONDUIT, 1)));
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
        trades.add(recipe(new ItemStack(Material.REINFORCED_DEEPSLATE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 4)));
        trades.add(recipe(new ItemStack(Material.MACE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 48)));
        trades.add(recipe(DoomHammerFactory.createDoomHammer(), USES_LEGENDARY,
                new ItemStack(Material.MACE, 1),
                new ItemStack(Material.REINFORCED_DEEPSLATE, 64)));
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
        trades.add(recipe(new ItemStack(Material.WITHER_SKELETON_SKULL, 3), USES_DEFAULT,
                new ItemStack(Material.EMERALD_BLOCK, 48)));
        trades.add(recipe(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD_BLOCK, 48)));
        trades.add(recipe(SoulflayerBowFactory.createSoulflayerBow(), USES_LEGENDARY,
                new ItemStack(Material.BOW, 1),
                new ItemStack(Material.NETHER_STAR, 1)));
        trades.add(recipe(new ItemStack(Material.HAPPY_GHAST_SPAWN_EGG, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 48)));
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
        trades.add(recipe(new ItemStack(Material.ELYTRA, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD_BLOCK, 64)));
        trades.add(recipe(new ItemStack(Material.DRAGON_BREATH, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 8)));
        trades.add(recipe(new ItemStack(Material.END_CRYSTAL, 1), USES_SIEGE,
                new ItemStack(Material.EMERALD, 48)));
        trades.add(recipe(new ItemStack(Material.OBSIDIAN, 4), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 12)));
        trades.add(recipe(new ItemStack(Material.ENDER_PEARL, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 10)));
        trades.add(recipe(new ItemStack(Material.SHULKER_BOX, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        trades.add(recipe(DragonSlayerFactory.createDragonSlayer(), USES_LEGENDARY,
                new ItemStack(Material.NETHERITE_SWORD, 1),
                new ItemStack(Material.DRAGON_EGG, 1)));
        return filterWeaponTrades(trades);
    }

    private static void applyEndExplorer(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.DARK_PURPLE + "Explorador do End");
        merchant.setRecipes(buildEndExplorerTrades());
    }

    public static void spawnTrapaceiro(Location loc) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        applyTrapaceiro(trader);
    }

    private static List<MerchantRecipe> buildTrapaceiroTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();
        trades.add(recipe(new ItemStack(Material.SPAWNER, 1), USES_SPAWNER,
                new ItemStack(Material.EMERALD_BLOCK, 48)));
        trades.add(recipe(new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 8)));
        trades.add(recipe(new ItemStack(Material.SKELETON_SPAWN_EGG, 1), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 8)));
        trades.add(recipe(new ItemStack(Material.SPIDER_SPAWN_EGG, 1), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 8)));
        trades.add(recipe(new ItemStack(Material.CREEPER_SPAWN_EGG, 1), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 8)));
        trades.add(recipe(new ItemStack(Material.COW_SPAWN_EGG, 1), USES_LEGENDARY,
                new ItemStack(Material.EMERALD, 32)));
        trades.add(recipe(new ItemStack(Material.PIG_SPAWN_EGG, 1), USES_LEGENDARY,
                new ItemStack(Material.EMERALD, 32)));
        trades.add(recipe(new ItemStack(Material.CHICKEN_SPAWN_EGG, 1), USES_LEGENDARY,
                new ItemStack(Material.EMERALD, 32)));
        trades.add(recipe(TrapaceiroItems.goldenBow(), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 16)));
        trades.add(recipe(TrapaceiroItems.knockbackStick(), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 8)));
        trades.add(recipe(TrapaceiroItems.capirotoApple(), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 12)));
        trades.add(recipe(TrapaceiroItems.pactChestplate(), USES_CHEAT,
                new ItemStack(Material.EMERALD_BLOCK, 16)));
        return trades;
    }

    private static void applyTrapaceiro(Merchant merchant) {
        prepareMerchantEntity(merchant);
        setMerchantName(merchant, ChatColor.GOLD + "Trapaceiro");
        merchant.setRecipes(buildTrapaceiroTrades());
    }

    /**
     * 1 disco específico → 4 templates daquele trim (1.21.8: 18 trims).
     */
    private static final Material[][] ARMOR_TRIM_DISC_TRADES = {
            {Material.MUSIC_DISC_CAT, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_BLOCKS, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_FAR, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_CHIRP, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_WARD, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_RELIC, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_TEARS, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_PIGSTEP, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_MELLOHI, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_OTHERSIDE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_STAL, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_MALL, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_WAIT, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_11, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_5, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_CREATOR, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_CREATOR_MUSIC_BOX, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE},
            {Material.MUSIC_DISC_PRECIPICE, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE},
    };

    public static void spawnEstilista(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        applyArmorTrimSmith(villager);
    }

    private static void applyArmorTrimSmith(Villager villager) {
        prepareVillager(villager, Villager.Profession.CARTOGRAPHER);
        setMerchantName(villager, ChatColor.GOLD + "Estilista");

        List<MerchantRecipe> trades = new ArrayList<>();
        for (Material[] pair : ARMOR_TRIM_DISC_TRADES) {
            trades.add(recipe(new ItemStack(pair[1], 4), USES_DEFAULT, new ItemStack(pair[0], 1)));
        }
        villager.setRecipes(trades);
    }

    public static void spawnCacadorMonstros(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        applyMonsterHunter(villager);
    }

    private static void applyMonsterHunter(Villager villager) {
        prepareVillager(villager, Villager.Profession.ARMORER);
        setMerchantName(villager, ChatColor.YELLOW + "Caçador de Monstros");

        List<MerchantRecipe> trades = new ArrayList<>();
        trades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.ROTTEN_FLESH, 12)));
        trades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.BONE, 12)));
        trades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.SPIDER_EYE, 1)));
        trades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.GUNPOWDER, 4)));
        villager.setRecipes(trades);
    }

    public static void spawnEngenheiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FLETCHER);
        villager.setCustomName(ChatColor.BLACK + "Engenheiro");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> engineerTrades = new ArrayList<>();
        engineerTrades.add(recipe(new ItemStack(Material.TNT, 1), USES_SIEGE,
                new ItemStack(Material.EMERALD, 12)));
        engineerTrades.add(recipe(new ItemStack(Material.PISTON, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 4)));
        engineerTrades.add(recipe(new ItemStack(Material.STICKY_PISTON, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 4)));
        engineerTrades.add(recipe(new ItemStack(Material.REPEATER, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 3)));
        engineerTrades.add(recipe(new ItemStack(Material.COMPARATOR, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 3)));
        engineerTrades.add(recipe(new ItemStack(Material.DISPENSER, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 3)));
        engineerTrades.add(recipe(new ItemStack(Material.SLIME_BLOCK, 16), USES_SIEGE,
                new ItemStack(Material.EMERALD, 6)));
        engineerTrades.add(recipe(new ItemStack(Material.HONEY_BLOCK, 16), USES_SIEGE,
                new ItemStack(Material.EMERALD, 6)));
        engineerTrades.add(recipe(new ItemStack(Material.SCULK_SENSOR, 4), USES_SIEGE,
                new ItemStack(Material.EMERALD, 6)));
        engineerTrades.add(recipe(new ItemStack(Material.OBSERVER, 8), USES_SIEGE,
                new ItemStack(Material.EMERALD, 4)));
        engineerTrades.add(recipe(new ItemStack(Material.CALIBRATED_SCULK_SENSOR, 4), USES_SIEGE,
                new ItemStack(Material.EMERALD, 8)));
        villager.setRecipes(engineerTrades);
    }

    public static void spawnFerreiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.TOOLSMITH);
        villager.setCustomName(ChatColor.DARK_GRAY + "Ferreiro");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> smithTrades = new ArrayList<>();
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_HELMET, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 20)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 35)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_LEGGINGS, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 30)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_BOOTS, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 15)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_SWORD, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 14)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_PICKAXE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 16)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_AXE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 16)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_SHOVEL, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 11)));
        smithTrades.add(recipe(new ItemStack(Material.DIAMOND_HOE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 11)));
        smithTrades.add(recipe(new ItemStack(Material.NETHERITE_INGOT, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD_BLOCK, 32)));
        villager.setRecipes(smithTrades);
    }

    public static void spwanBibliotecario(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.LIBRARIAN);
        villager.setCustomName(ChatColor.LIGHT_PURPLE + "Bibliotecário");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> libraryTrades = new ArrayList<>();
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.EFFICIENCY, 3), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.UNBREAKING, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 16)));
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.PROTECTION, 2), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.SHARPNESS, 2), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.MENDING, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        libraryTrades.add(recipe(makeEnchantedBook(1, Enchantment.FORTUNE, 1), USES_DEFAULT,
                new ItemStack(Material.EMERALD, 32)));
        villager.setRecipes(libraryTrades);
    }

    public static void spawnPescador(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FISHERMAN);
        villager.setCustomName(ChatColor.AQUA + "Pescador");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> fisherTrades = new ArrayList<>();
        fisherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.COD, 8)));
        fisherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.SALMON, 8)));
        fisherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.TROPICAL_FISH, 4)));
        fisherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.PUFFERFISH, 3)));
        villager.setRecipes(fisherTrades);
    }

    public static void spawnMineiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.WEAPONSMITH);
        villager.setCustomName(ChatColor.GRAY + "Mineiro");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> minerTrades = new ArrayList<>();
        minerTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.COPPER_INGOT, 4)));
        minerTrades.add(recipe(new ItemStack(Material.EMERALD, 2), USES_DEFAULT,
                new ItemStack(Material.IRON_INGOT, 2)));
        minerTrades.add(recipe(new ItemStack(Material.EMERALD, 3), USES_DEFAULT,
                new ItemStack(Material.GOLD_INGOT, 2)));
        minerTrades.add(recipe(new ItemStack(Material.EMERALD, 4), USES_DEFAULT,
                new ItemStack(Material.DIAMOND, 1)));
        villager.setRecipes(minerTrades);
    }

    public static void spawnFazendeiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.FARMER);
        villager.setCustomName(ChatColor.GREEN + "Fazendeiro");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> farmerTrades = new ArrayList<>();
        farmerTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.WHEAT, 15)));
        farmerTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.POTATO, 24)));
        farmerTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.CARROT, 24)));
        farmerTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.SUGAR_CANE, 24)));
        villager.setRecipes(farmerTrades);
    }

    public static void spawnAcougueiro(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        numbifyVillager(villager, Villager.Profession.BUTCHER);
        villager.setCustomName(ChatColor.WHITE + "Açougueiro");
        villager.setCustomNameVisible(false);
        villager.setAdult();
        villager.setVillagerLevel(5);

        List<MerchantRecipe> butcherTrades = new ArrayList<>();
        butcherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.CHICKEN, 8)));
        butcherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.PORKCHOP, 8)));
        butcherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.BEEF, 8)));
        butcherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.MUTTON, 8)));
        butcherTrades.add(recipe(new ItemStack(Material.EMERALD, 1), USES_DEFAULT,
                new ItemStack(Material.RABBIT, 8)));
        villager.setRecipes(butcherTrades);
    }

    public static void spawn(TraderType type, Location location) {
        switch (type) {
            case OCEANO -> spawnExplOceano(location);
            case PROFUNDEZAS -> spawnExplProfundo(location);
            case NETHER -> spawnExplNether(location);
            case END -> spawnExplEnd(location);
            case TRAPACEIRO -> spawnTrapaceiro(location);
            case BIBLIOTECARIO -> spwanBibliotecario(location);
            case TRIM -> spawnEstilista(location);
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
            case TRAPACEIRO -> applyTrapaceiro(merchant);
            case TRIM -> {
                if (!(merchant instanceof Villager villager)) {
                    return false;
                }
                applyArmorTrimSmith(villager);
            }
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
            case TRIM -> {
                villager.remove();
                spawnEstilista(loc);
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
