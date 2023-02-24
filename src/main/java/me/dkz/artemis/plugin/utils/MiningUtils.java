package me.dkz.artemis.plugin.utils;

import me.dkz.artemis.plugin.ArtemisMina;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.stream.Collectors;

public class MiningUtils {

    private static ArtemisMina plugin = ArtemisMina.getInstance();

    public static String getMessage(String key) {
        return plugin.getConfig().getStringList("Messages." + key).
                stream()
                .map(s -> s.replace("&", "§"))
                .collect(Collectors.joining("\n"));
    }

    public static void generateOreAroundBlock(Block block, ItemStack item) {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 2; y++) {
                    if (block.getRelative(x, y, z).getType().equals(Material.STONE)) {
                        block.getRelative(x, y, z).setType(item.getType());
                        return;
                    }
                }
            }
        }
    }


    public static class MiningWorldPopulator extends BlockPopulator {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 256; y++) {
                        if (chunk.getBlock(x, y, z).getType().toString().toUpperCase().contains("ORE")) {
                            chunk.getBlock(x, y, z).setType(Material.STONE);
                        }
                    }
                }
            }
        }
    }

    //    private ItemStack getStackDrops(Player player, ItemStack itemStack, boolean smelt) {
//        int fortune = 0;
//        if (player.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
//            fortune = player.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
//        }
//
//
//        int amount = itemStack.getAmount() * (fortune + 1);
//
//        if (itemStack.getType().equals(Material.REDSTONE)
//                || itemStack.getType().equals(Material.GLOWSTONE_DUST)
//                || itemStack.getType().equals(Material.QUARTZ_ORE)
//                || itemStack.getType().equals(Material.INK_SACK)) {
//            amount = new Random().nextInt(amount + 1);
//        }
//
//        if (smelt) {
//            if (itemStack.getType().equals(Material.IRON_ORE)) {
//                itemStack.setType(Material.IRON_INGOT);
//            } else if (itemStack.getType().equals(Material.GOLD_ORE)) {
//                itemStack.setType(Material.GOLD_INGOT);
//            }
//        }
//
//
//        itemStack.setAmount(new Random().nextInt(amount + 1));
//        return itemStack;
//    }

}
