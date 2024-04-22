package com.github.shin_ideal.swapperlabplugin.Cmds;

import com.github.shin_ideal.swapperlabplugin.Items.SwapperItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetSwapItemCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).getInventory().addItem(SwapperItem.getSwapItem());
            return true;
        } else {
            return false;
        }
    }
}
