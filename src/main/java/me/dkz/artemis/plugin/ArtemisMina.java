package me.dkz.artemis.plugin;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ArtemisMina extends JavaPlugin implements Listener {

    private World miningWorld;
    private Set<String> ores = new HashSet<>();
    private Set<PotionEffect> worldEffectTypes = new HashSet<>();
    private Map<Player, Location> lastLocation = new HashMap<>();

    public static ArtemisMina getInstance() {
        return getPlugin(ArtemisMina.class);
    }


    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().getStringList("Mining.Effects").forEach(effect -> {
            String[] effectData = effect.replaceAll(" ", "").split(",");
            String name = effectData[0];
            int level = Integer.parseInt(effectData[1]) - 1;
            worldEffectTypes.add(new PotionEffect(PotionEffectType.getByName(name), Integer.MAX_VALUE, level));
        });
        getConfig().getStringList("Mining.Ores").forEach(ore -> {
            ores.add(ore);
        });
        getLogger().info("Iniciando a criação do mundo de mineração...");
        getServer().getPluginManager().registerEvents(this, this);
        createMiningWorld();
        getLogger().info("Mundo de mineração criado com sucesso!");
        getCommand("mina").setExecutor(this);

    }


    @Override
    public void onDisable() {
        getLogger().info("Desativando e resetando o mundo de mineração...");
        lastLocation.forEach((player, location) -> player.teleport(location));
        Bukkit.unloadWorld(miningWorld, true);
        File worldFolder = new File(Bukkit.getWorldContainer(), miningWorld.getName());
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            getLogger().severe("Erro ao deletar o mundo de mineração!");
        }

    }

    private void createMiningWorld() {
        WorldCreator worldCreator = new WorldCreator(getConfig().getString("Mining.WorldName"));
        worldCreator.generateStructures(false);
        miningWorld = getServer().createWorld(worldCreator);
        miningWorld.setKeepSpawnInMemory(false);
        miningWorld.setAutoSave(false);
        miningWorld.setDifficulty(getConfig().getBoolean("Mining.Rules.MobSpawn") ? Difficulty.NORMAL : Difficulty.PEACEFUL);
        miningWorld.setPVP(getConfig().getBoolean("Mining.Rules.Damage"));
        miningWorld.setGameRuleValue("doDaylightCycle", String.valueOf(getConfig().getBoolean("Mining.Rules.Time")));
        miningWorld.setGameRuleValue("doMobSpawning", String.valueOf(getConfig().getBoolean("Mining.Rules.MobSpawn")));
        miningWorld.setGameRuleValue("doWeatherCycle", String.valueOf(getConfig().getBoolean("Mining.Rules.Weather")));
    }


    public Map<Player, Location> getLastLocation() {
        return lastLocation;
    }

    public World getMiningWorld() {
        return miningWorld;
    }

    public Set<PotionEffect> getWorldEffectTypes() {
        return worldEffectTypes;
    }

    public Set<String> getOres() {
        return ores;
    }
}
