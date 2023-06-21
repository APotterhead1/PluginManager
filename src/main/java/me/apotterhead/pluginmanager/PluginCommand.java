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
import java.util.Arrays;

public class PluginCommand implements TabExecutor {

    PluginManager plugin;

    public PluginCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
        if( args.length == 0 ) {
            if( sender.hasPermission( "appm.help-tab-complete.plugin" ) ) sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            else sender.sendMessage( CommandErrorMessage.PERMISSION.send() );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "appm.commands.plugin.list" ) ) {
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
                return true;
            }
            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 1 ) );
            return true;
        }

        if( args[0].equals( "enable" ) ) {
            if( !sender.hasPermission( "appm.commands.plugin.enable" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }
            if( args.length == 1 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }
            if( args.length == 2 ) {
                Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
                boolean matchesPlugin = false;
                for( int i = 0; i < plugins.length && !matchesPlugin; i++ ) if( plugins[i].getName().equals( args[1] ) ) matchesPlugin = true;
                if( !matchesPlugin ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "plugin" ) );
                    return true;
                }

                Plugin serverPlugin = plugin.getServer().getPluginManager().getPlugin( args[1] );

                if( !serverPlugin.isEnabled() ) plugin.getServer().getPluginManager().enablePlugin( serverPlugin );
                sender.sendMessage( Component.text( "[" + serverPlugin.getName() + "] has been enabled" ).color( NamedTextColor.GREEN ) );
                return true;
            }
            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "disable" ) ) {
            if( !sender.hasPermission( "appm.commands.plugin.disable" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }
            if( args.length == 1 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }
            if( args.length == 2 ) {
                Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
                boolean matchesPlugin = false;
                for( int i = 0; i < plugins.length && !matchesPlugin; i++ ) if( plugins[i].getName().equals( args[1] ) ) matchesPlugin = true;
                if( !matchesPlugin ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "plugin" ) );
                    return true;
                }

                Plugin serverPlugin = plugin.getServer().getPluginManager().getPlugin( args[1] );

                if( serverPlugin.isEnabled() ) plugin.getServer().getPluginManager().disablePlugin( serverPlugin );
                sender.sendMessage( Component.text( "[" + serverPlugin.getName() + "] has been disabled" ).color( NamedTextColor.RED ) );
                return true;
            }
            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "reload" ) )

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command cmd, String label, String[] args ) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        return completions;
    }
}