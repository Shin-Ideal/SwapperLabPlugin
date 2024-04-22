package com.github.shin_ideal.swapperlabplugin;

import com.github.shin_ideal.swapperlabplugin.Cmds.GetSwapItemCmd;
import com.github.shin_ideal.swapperlabplugin.Listeners.SwapperListener;
import com.github.shin_ideal.swapperlabplugin.Runnables.SwapperRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class SwapperLabPlugin extends JavaPlugin {

    private static SwapperLabPlugin Instance;

    private Map<Player, Integer> swapDisableTimeMap;
    private Map<Player, Integer> fallDamageDisableMap;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("SwapperLabPlugin");
        getLogger().info("By Shin");
        Instance = this;
        swapDisableTimeMap = new HashMap<>();
        fallDamageDisableMap = new HashMap<>();
        getCommand("getswapitem").setExecutor(new GetSwapItemCmd());
        getServer().getPluginManager().registerEvents(new SwapperListener(), this);
        new SwapperRunnable().runTaskTimer(Instance, 0L, 20L);//1秒毎に動かす
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SwapperLabPlugin getInstance() {
        return Instance;
    }

    public Map<Player, Integer> getSwapDisableTimeMap() {
        return swapDisableTimeMap;
    }

    public Map<Player, Integer> getFallDamageDisableMap() {
        return fallDamageDisableMap;
    }
}
