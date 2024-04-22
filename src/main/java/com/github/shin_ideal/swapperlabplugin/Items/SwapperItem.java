package com.github.shin_ideal.swapperlabplugin.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SwapperItem {
    public static ItemStack getSwapItem() {
        ItemStack item = new ItemStack(Material.MUSIC_DISC_CAT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Swapper " + ChatColor.GREEN + "READY");
        meta.setLore(Arrays.asList(ChatColor.GOLD + "Soulbound", ChatColor.GOLD + "Class Item"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSwapItemCoolTime(int cooltime) {
        ItemStack item = getSwapItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Swapper " + ChatColor.RED + cooltime);
        item.setItemMeta(meta);
        return item;
    }
}
