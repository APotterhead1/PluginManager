// APotterhead
// 13062023-20062023

package me.apotterhead.pluginmanager;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;

public class PluginCommand implements TabExecutor {

    PluginManager plugin;

    public PluginCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
        if( args.length == 0 ) {
            if( sender.hasPermission( "pm.help-tab-complete.plugin" ) ) sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            else sender.sendMessage( CommandErrorMessage.UNKNOWN.send() );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "pm.commands.plugin.list" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }
            if( args.length == 1 ) {
                TextComponent component = Component.text( "Plugins:\n" ).color( NamedTextColor.GOLD );

                Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
                if( plugins[0].isEnabled() ) component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.GREEN ) );
                else component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.RED ) );

                for( int i = 1; i < plugins.length; i++ ) {
                    component = component.append( Component.text( "," ) ).color( NamedTextColor.WHITE );
                    if( plugins[i].isEnabled() ) component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.GREEN ) );
                    else component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.RED ) );
                }

                sender.sendMessage( component );
            } else {
                sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 1 ) );
            }
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( CommandSender sender, Command cmd, String label, String[] args ) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        return completions;
    }
}