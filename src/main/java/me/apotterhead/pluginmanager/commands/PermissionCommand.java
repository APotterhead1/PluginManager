// APotterhead
// 22072023-22072023

package me.apotterhead.pluginmanager.commands;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import me.apotterhead.pluginmanager.util.CommandErrorMessage;

public class PermissionCommand implements TabExecutor {
    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        if( args.length == 0 ) {
            sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
