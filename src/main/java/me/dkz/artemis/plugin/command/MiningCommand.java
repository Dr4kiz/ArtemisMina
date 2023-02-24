package me.dkz.artemis.plugin.command;

import me.dkz.artemis.plugin.ArtemisMina;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static me.dkz.artemis.plugin.utils.MiningUtils.getMessage;
import static org.bukkit.Bukkit.getServer;


public class MiningCommand implements CommandExecutor {

    private ArtemisMina plugin = ArtemisMina.getInstance();


    private Set<Player> inDelay = new HashSet<>();

    private int delay = plugin.getConfig().getInt("Teleport.CountDown");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cComando feito para jogadores");
            return false;
        }

        World miningWorld = plugin.getMiningWorld();

        if (args.length > 0) {
            if (args[0].equals("sair")) {
                if (!plugin.getLastLocation().containsKey(sender)) {
                    sender.sendMessage(getMessage("NotInWorld"));
                    return false;
                }
                Player player = (Player) sender;
                player.teleport(plugin.getLastLocation().get(player));
                plugin.getLastLocation().remove(player);
                player.sendMessage(getMessage("WorldLeave"));
                player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                return true;
            }
            sender.sendMessage("§cComando inválido, verifique a syntax!");
            return false;
        }


        Player player = (Player) sender;

        if (player.getWorld().getName().equals(miningWorld.getName())) {
            player.sendMessage(getMessage("AlreadyInWorld"));
            return false;
        }

        if (inDelay.contains(player)) {
            player.sendMessage(plugin.getConfig().getString("Teleport.CountDownMessage")
                    .replaceAll("&", "§")
                    .replaceAll("@delay", String.valueOf(delay))
            );
            return false;
        }

        inDelay.add(player);
        getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()
                -> inDelay.remove(player), delay * 20L);


        player.sendMessage(getMessage("WorldEnter"));
        plugin.getLastLocation().put(player, player.getLocation());
        Location spawnLocation = miningWorld.getSpawnLocation();
        int range = plugin.getConfig().getInt("Teleport.Range");
        Location teleport = plugin.getMiningWorld().getHighestBlockAt(spawnLocation.getBlockX() + new Random().nextInt(range * 2) - range, spawnLocation.getBlockZ() + new Random().nextInt(range * 2) - range).getLocation().add(0, 1, 0);
        player.teleport(teleport);
        player.addPotionEffects(plugin.getWorldEffectTypes());


        return true;
    }


}
