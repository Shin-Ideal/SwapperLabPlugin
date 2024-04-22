package com.github.shin_ideal.swapperlabplugin.Runnables;

import com.github.shin_ideal.swapperlabplugin.Items.SwapperItem;
import com.github.shin_ideal.swapperlabplugin.SwapperLabPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SwapperRunnable extends BukkitRunnable {

    private final SwapperLabPlugin Instance;

    private Map<Player, Integer> swapDisableTimeMap;
    private Map<Player, Integer> fallDamageDisableMap;

    public SwapperRunnable() {
        Instance = SwapperLabPlugin.getInstance();
        swapDisableTimeMap = Instance.getSwapDisableTimeMap();
        fallDamageDisableMap = Instance.getFallDamageDisableMap();
    }

    @Override
    public void run() {
        Set<Player> removeKeys = new HashSet<>();

        for (Player player : swapDisableTimeMap.keySet()) {
            if (swapDisableTimeMap.get(player) > 1) {
                swapDisableTimeMap.put(player, swapDisableTimeMap.get(player) - 1);
            } else {
                removeKeys.add(player);
            }
        }
        removeKeys.forEach(removeKey -> swapDisableTimeMap.remove(removeKey));

        for (Player player : fallDamageDisableMap.keySet()) {
            if (fallDamageDisableMap.get(player) > 1) {
                fallDamageDisableMap.put(player, fallDamageDisableMap.get(player) - 1);
            } else {
                removeKeys.add(player);
            }
        }
        removeKeys.forEach(removeKey -> fallDamageDisableMap.remove(removeKey));

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getLore() != null) {
                    for (int i = 1; i <= 20; i++) {
                        if (item.getItemMeta().getDisplayName().equals(SwapperItem.getSwapItemCoolTime(i).getItemMeta().getDisplayName())) {
                            if (i != 1) {
                                ItemMeta itemMeta = item.getItemMeta();
                                itemMeta.setDisplayName(SwapperItem.getSwapItemCoolTime(i - 1).getItemMeta().getDisplayName());
                                item.setItemMeta(itemMeta);
                            } else {
                                ItemMeta itemMeta = item.getItemMeta();
                                itemMeta.setDisplayName(SwapperItem.getSwapItem().getItemMeta().getDisplayName());
                                item.setItemMeta(itemMeta);
                            }
                        }
                    }
                }
            }
        }
    }
}
