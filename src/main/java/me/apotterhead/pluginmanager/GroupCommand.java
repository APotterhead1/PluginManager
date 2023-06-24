// APotterhead
// 23062023-23062023

package me.apotterhead.pluginmanager;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GroupCommand implements TabExecutor {

   private final PluginManager plugin;

   public GroupCommand( PluginManager plugin ) {
       this.plugin = plugin;
   }

    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        if( args.length == 0 ) {
            if( sender.hasPermission( "appm.help-tab-complete.group" ) ) sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            else sender.sendMessage( CommandErrorMessage.PERMISSION.send() );
            return true;
        }

        if( args[0].equals( "create" ) ) {
            if( !sender.hasPermission( "appm.commands.group.create" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length == 1 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 2 ) {
                List<String> groups = plugin.groups.config.getStringList( "groups" );
                if( groups.contains( args[1] ) ) {
                    sender.sendMessage( Component.text( "A group named '" + args[1] + "' already exists"  ).color( NamedTextColor.RED ) );
                    return true;
                }
                groups.add( args[1] );
                plugin.groups.config.set( "groups", groups );
                plugin.groups.config.set( "group." + args[1] + ".hierarchyValue", 0 );
                plugin.groups.save();
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
