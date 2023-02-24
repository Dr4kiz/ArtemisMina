package me.dkz.artemis.plugin.listener;

import me.dkz.artemis.plugin.ArtemisMina;
import me.dkz.artemis.plugin.utils.MiningUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

public class MiningListener implements Listener {


    private ArtemisMina plugin = ArtemisMina.getInstance();
    private World miningWorld = plugin.getMiningWorld();

    @EventHandler
    void onWorldCreationInit(WorldInitEvent e) {
        World world = e.getWorld();
        if (!world.getName().equals(miningWorld.getName())) return;
        if (!plugin.getConfig().getString("Mining.OreGeneration").equalsIgnoreCase("NORMAL"))
            world.getPopulators().add(new MiningUtils.MiningWorldPopulator());
    }


    @EventHandler
    void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (!block.getWorld().getName().equals(miningWorld.getName())) return;
        boolean canDropStone = !plugin.getConfig().getBoolean("Mining.Rules.DropStone");

        if (block.getType().equals(Material.STONE)) {
            if (canDropStone) {
                block.setType(Material.AIR);
            }
        }


        if (plugin.getConfig().
                getString("Mining.OreGeneration")
                .equalsIgnoreCase("NORMAL")) return;

        double playerY = player.getLocation().getY();
        if (plugin.getConfig()
                .getString("Mining.OreGeneration")
                .equalsIgnoreCase("AROUND")) aroundOreGenerator(block, playerY);

    }


    private void aroundOreGenerator(Block block, double playerY) {
        plugin.getOres().stream().map(ore -> ore.replaceAll(" ", "")).map(ore -> ore.split(",")).forEach(oreData -> {
            Material material = Material.getMaterial(oreData[0]);
            int minY = Integer.parseInt(oreData[1]);
            int chance = Integer.parseInt(oreData[2]);
            ItemStack item = new ItemStack(material);
            if (playerY <= minY && Math.random() * 200.0D <= chance)
                MiningUtils.generateOreAroundBlock(block, item);
        });
    }


}
