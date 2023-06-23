// APotterhead
// 13062023-22062023

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
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.util.StringUtil;

public class PluginCommand implements TabExecutor {

    PluginManager plugin;

    public PluginCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args ) {
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

                if( !sender.hasPermission( "appm.commands.plugin.get" ) ) {
                    if( plugins[0].isEnabled() ) component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.GREEN ) );
                    else component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.RED ) );

                    for( int i = 1; i < plugins.length; i++ ) {
                        component = component.append( Component.text( "," ) ).color( NamedTextColor.WHITE );
                        if( plugins[i].isEnabled() ) component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.GREEN ) );
                        else component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.RED ) );
                    }
                } else {
                    if( plugins[0].isEnabled() ) component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.GREEN ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/plugin get " + plugins[0].getName() ) ) );
                    else component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/plugin get " + plugins[0].getName() ) ) );

                    for( int i = 1; i < plugins.length; i++ ) {
                        component = component.append( Component.text( "," ) ).color( NamedTextColor.WHITE );
                        if( plugins[i].isEnabled() ) component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.GREEN ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/plugin get " + plugins[i].getName() ) ) );
                        else component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/plugin get " + plugins[i].getName() ) ) );
                    }
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
                assert serverPlugin != null;

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
                assert serverPlugin != null;

                if( serverPlugin.isEnabled() ) plugin.getServer().getPluginManager().disablePlugin( serverPlugin );
                sender.sendMessage( Component.text( "[" + serverPlugin.getName() + "] has been disabled" ).color( NamedTextColor.RED ) );
                return true;
            }
            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "get" ) ) {
            if( !sender.hasPermission( "appm.commands.plugin.get" ) ) {
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
                assert serverPlugin != null;

                TextComponent component = Component.text( serverPlugin.getName() + " (" + serverPlugin.getPluginMeta().getLoggerPrefix() + "):").color( NamedTextColor.GOLD );
                component = component.append( Component.text( "\nStatus: ").color( NamedTextColor.AQUA ) );
                if( serverPlugin.isEnabled() ) {
                    if( sender.hasPermission( "appm.commands.plugin.disable" ) ) component = component.append( Component.text( "Enabled" ).color( NamedTextColor.GREEN ) ).clickEvent( ClickEvent.runCommand( "/plugin disable " + serverPlugin.getName() ) );
                    else component = component.append( Component.text( "Enabled" ).color( NamedTextColor.GREEN ) );
                }
                else {
                    if( sender.hasPermission( "appm.commands.plugin.enable" ) ) component = component.append( Component.text( "Disabled" ).color( NamedTextColor.RED ).clickEvent( ClickEvent.runCommand( "/plugin enable " + serverPlugin.getName() ) ) );
                    else component = component.append( Component.text( "Disabled" ).color( NamedTextColor.RED ) );
                }
                component = component.append( Component.text( "\nVersion: " ).color( NamedTextColor.AQUA ) );
                component = component.append( Component.text( serverPlugin.getPluginMeta().getVersion() ).color( NamedTextColor.WHITE ) );
                if( serverPlugin.getPluginMeta().getDescription() != null ) {
                    component = component.append( Component.text( "\nDescription: " ).color( NamedTextColor.AQUA ) );
                    component = component.append( Component.text( serverPlugin.getPluginMeta().getDescription() ).color( NamedTextColor.WHITE ) );
                }
                List<String> authors = serverPlugin.getPluginMeta().getAuthors();
                if( authors.size() == 1 ) {
                    component = component.append(Component.text( "\nAuthor: " ).color( NamedTextColor.AQUA ) );
                    component = component.append( Component.text( authors.get( 0 ) ).color( NamedTextColor.WHITE ) );
                } else if( authors.size() > 1 ) {
                    component = component.append(Component.text( "\nAuthors: " ).color( NamedTextColor.AQUA ) );
                    StringBuilder authorString = new StringBuilder( authors.get( 0 ) );
                    for( int i = 1; i < authors.size(); i++ ) authorString.append( " " ).append( authors.get( i ) );
                    component = component.append( Component.text( authorString.toString() ).color( NamedTextColor.WHITE ) );
                }
                if( serverPlugin.getPluginMeta().getWebsite() != null ) {
                    component = component.append( Component.text( "\nWebsite: " ).color( NamedTextColor.AQUA ) );
                    component = component.append( Component.text( serverPlugin.getPluginMeta().getWebsite() ).color( NamedTextColor.WHITE ).decoration( TextDecoration.UNDERLINED, true ).clickEvent( ClickEvent.openUrl( serverPlugin.getPluginMeta().getWebsite() ) ) );
                }

                sender.sendMessage( component );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    @Override
    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args ) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if ( args.length == 1 ) {
            if( sender.hasPermission( "appm.commands.plugin.list" ) ) commands.add( "list" );
            if( sender.hasPermission( "appm.commands.plugin.enable" ) ) commands.add( "enable" );
            if( sender.hasPermission( "appm.commands.plugin.disable" ) ) commands.add( "disable" );
            if( sender.hasPermission( "appm.commands.plugin.get" ) ) commands.add( "get" );
            StringUtil.copyPartialMatches( args[0], commands, completions );
        }
        if( args.length == 2 ) {
            if( args[0].equals( "enable" ) && sender.hasPermission( "appm.commands.plugin.enable" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) commands.add( serverPlugin.getName() );
            if( args[0].equals( "disable" ) && sender.hasPermission( "appm.commands.plugin.disable" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) commands.add( serverPlugin.getName() );
            if( args[0].equals( "get" ) && sender.hasPermission( "appm.commands.plugin.get" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) commands.add( serverPlugin.getName() );
            StringUtil.copyPartialMatches( args[1], commands, completions );
        }

        return completions;
    }
}