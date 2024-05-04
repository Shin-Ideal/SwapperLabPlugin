package com.github.shin_ideal.swapperlabplugin.Listeners;

import com.github.shin_ideal.swapperlabplugin.Items.SwapperItem;
import com.github.shin_ideal.swapperlabplugin.SwapperLabPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.Map;

public class SwapperListener implements Listener {

    private final SwapperLabPlugin Instance;

    private final Scoreboard scoreboard;
    private final Map<Player, Integer> swapDisableTimeMap;
    private final Map<Player, Integer> fallDamageDisableMap;

    public SwapperListener() {
        Instance = SwapperLabPlugin.getInstance();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        swapDisableTimeMap = Instance.getSwapDisableTimeMap();
        fallDamageDisableMap = Instance.getFallDamageDisableMap();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        EquipmentSlot equipmentSlot = event.getHand();
        if (equipmentSlot == null) {
            return;
        }
        ItemStack item = player.getInventory().getItem(equipmentSlot);
        ItemMeta itemMeta = item.getItemMeta();

        Team playerTeam = null;
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getDisplayName())) {
                playerTeam = team;
            }
        }

        if (itemMeta == null || itemMeta.getDisplayName() == null || !item.equals(SwapperItem.getSwapItem())) {
            //Cooltime Item Check
            for (int i = 1; i <= 20; i++) {
                if (itemMeta != null && itemMeta.getDisplayName() != null && item.getItemMeta().getDisplayName().equals(SwapperItem.getSwapItemCoolTime(i).getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                    break;
                }
            }
            return;
        }

        event.setCancelled(true);

        Material playerBlockUp = player.getEyeLocation().getBlock().getType();
        Material playerBlockDown = player.getLocation().getBlock().getType();
        Material playerUnderBlock = player.getLocation().clone().add(0, -1, 0).getBlock().getType();
        if (isNotSolid(playerUnderBlock) && isNotSolid(playerBlockDown)) {
            player.sendMessage(ChatColor.RED + "Can only swap when standing on solid ground!");
            return;
        }

        Team finalPlayerTeam = playerTeam;
        RayTraceResult swapResult = player.getWorld().rayTraceEntities(player.getPlayer().getEyeLocation(), player.getPlayer().getEyeLocation().getDirection(), 20,
                entity -> entity instanceof Player
                        && !entity.equals(player)
                        && !((Player) entity).getGameMode().equals(GameMode.SPECTATOR)
                        && (finalPlayerTeam == null || !finalPlayerTeam.hasEntry(entity.getName()))
                        && (!((Player) entity).hasPotionEffect(PotionEffectType.INVISIBILITY) || ((Player) entity).getInventory().getArmorContents()[0] != null || ((Player) entity).getInventory().getArmorContents()[1] != null || ((Player) entity).getInventory().getArmorContents()[2] != null || ((Player) entity).getInventory().getArmorContents()[3] != null)
        );
        if (swapResult == null) {
            return;
        }

        Player swapplayer = (Player) swapResult.getHitEntity();
        Location playerlocation = player.getLocation();
        Location swapplayerlocation;

        if (swapplayer == null) {
            return;
        }

        if (swapDisableTimeMap.containsKey(swapplayer)) {
            player.sendMessage(ChatColor.RED + swapplayer.getName() + " was recently swapped and cannot be again for " + swapDisableTimeMap.get(swapplayer) + " seconds.");
            return;
        }

        if (!isNotSolid(playerBlockUp) || !isNotSolid(playerBlockDown)) {
            player.sendMessage(ChatColor.RED + "You cannot swap while inside blocks.");
            return;
        }

        if (player.getLocation().getBlock().equals(player.getEyeLocation().getBlock())) {
            player.sendMessage(ChatColor.RED + "You cannot swap while in a confined space.");
            return;
        }

        if (playerBlockUp.equals(Material.WATER)
                || Arrays.asList(
                Material.OAK_FENCE, Material.OAK_FENCE_GATE,
                Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE,
                Material.BIRCH_FENCE, Material.BIRCH_FENCE,
                Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE,
                Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE,
                Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE,
                Material.CRIMSON_FENCE, Material.CRIMSON_FENCE_GATE,
                Material.WARPED_FENCE, Material.WARPED_FENCE_GATE,
                Material.NETHER_BRICK_FENCE,
                Material.COBBLESTONE_WALL,
                Material.MOSSY_COBBLESTONE_WALL,
                Material.BRICK_WALL,
                Material.PRISMARINE_WALL,
                Material.RED_SANDSTONE_WALL,
                Material.MOSSY_STONE_BRICK_WALL,
                Material.GRANITE_WALL,
                Material.MOSSY_STONE_BRICK_WALL,
                Material.NETHER_BRICK_WALL,
                Material.ANDESITE_WALL,
                Material.RED_NETHER_BRICK_WALL,
                Material.SANDSTONE_WALL,
                Material.END_STONE_BRICK_WALL,
                Material.DIORITE_WALL,
                Material.BLACKSTONE_WALL,
                Material.POLISHED_BLACKSTONE_WALL,
                Material.POLISHED_BLACKSTONE_BRICK_WALL
        ).contains(playerUnderBlock)) {
            player.sendMessage("Illegal swap detected, If you believe this is an error please post a bug report and a screenshot of your trap to the forums");
            return;
        }

        swapDisableTimeMap.put(swapplayer, 3);
        fallDamageDisableMap.put(swapplayer, 10);

        //tp
        swapplayerlocation = swapplayer.getLocation();
        player.teleport(swapplayerlocation);
        swapplayer.teleport(playerlocation);
        //PotionEffect
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1));
        swapplayer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1));
        swapplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4 * 20, 1));
        //sound
        player.playSound(swapplayerlocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
        swapplayer.playSound(playerlocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
        //Effect
        player.getWorld().spawnParticle(Particle.CRIT, playerlocation, 30);
        swapplayer.getWorld().spawnParticle(Particle.CRIT, swapplayerlocation, 30);

        itemMeta.setDisplayName(SwapperItem.getSwapItemCoolTime(20).getItemMeta().getDisplayName());
        item.setItemMeta(itemMeta);
        player.getInventory().setItem(equipmentSlot, item);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (Instance.getFallDamageDisableMap().containsKey(player)) {
            event.setCancelled(true);
        }
    }

    private boolean isNotSolid(Material material) {
        if (!material.isSolid() || material.equals(Material.COBWEB) || material.equals(Material.BAMBOO_SAPLING)) {
            return true;
        }
        return false;
    }
}
