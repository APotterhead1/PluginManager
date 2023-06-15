// APotterhead
// 13062023-13062023

package me.apotterhead.pluginmanager;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;

public class PluginCommand implements TabExecutor {

    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
        if( args.length == 0 );
        return true;
    }

    public List<String> onTabComplete( CommandSender sender, Command cmd, String label, String[] args ) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        return completions;
    }
}
