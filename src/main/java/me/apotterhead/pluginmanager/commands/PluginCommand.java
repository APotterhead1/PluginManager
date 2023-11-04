// APotterhead
// 13062023-21072023

package me.apotterhead.pluginmanager.commands;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.util.StringUtil;
import java.util.Collections;
import me.apotterhead.pluginmanager.util.CommandErrorMessage;

public class PluginCommand implements TabExecutor {

    private final PluginManager plugin;

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
                Component component = Component.text( "" ).append( Component.text( "Plugins:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();

                if( !sender.hasPermission( "appm.commands.plugin.get" ) ) {
                    if( plugins[0].isEnabled() ) component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.GREEN ) );
                    else component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.RED ) );

                    for( int i = 1; i < plugins.length; i++ ) {
                        component = component.append( Component.text( "," ) );
                        if( plugins[i].isEnabled() ) component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.GREEN ) );
                        else component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.RED ) );
                    }
                } else {
                    if( plugins[0].isEnabled() ) component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.GREEN ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin get " + plugins[0].getName() ) ) );
                    else component = component.append( Component.text( "[" + plugins[0].getName() + "]" ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin get " + plugins[0].getName() ) ) );

                    for( int i = 1; i < plugins.length; i++ ) {
                        component = component.append( Component.text( "," ) );
                        if( plugins[i].isEnabled() ) component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.GREEN ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin get " + plugins[i].getName() ) ) );
                        else component = component.append( Component.text( "[" + plugins[i].getName() + "]" ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin get " + plugins[i].getName() ) ) );
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

                if( !serverPlugin.isEnabled() ) {
                    plugin.getServer().getPluginManager().enablePlugin( serverPlugin );
                    List<String> disabledPlugins = plugin.disabledPlugins.config.getStringList( "plugins" );
                    disabledPlugins.remove( serverPlugin.getName() );
                    plugin.disabledPlugins.config.set( "plugins", disabledPlugins );
                    plugin.disabledPlugins.save();
                }
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

                if( serverPlugin.isEnabled() ) {
                    plugin.getServer().getPluginManager().disablePlugin( serverPlugin );
                    List<String> disabledPlugins = plugin.disabledPlugins.config.getStringList( "plugins" );
                    disabledPlugins.add( serverPlugin.getName() );
                    if( !plugin.disabledPlugins.config.getStringList( "plugins" ).contains( serverPlugin.getName() ) ) plugin.disabledPlugins.config.set( "plugins", disabledPlugins );
                    plugin.disabledPlugins.save();
                }
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

                Component component = Component.text( "" ).append( Component.text( serverPlugin.getName() ).color( NamedTextColor.GOLD ) );
                if( serverPlugin.getPluginMeta().getLoggerPrefix() != null )
                    component = component.append( Component.text( "(" + serverPlugin.getPluginMeta().getLoggerPrefix() + ")" ).color( NamedTextColor.GOLD ) );
                component = component.append( Component.text( ":" ).color( NamedTextColor.GOLD ) ).appendNewline();
                component = component.append( Component.text( "Status:").color( NamedTextColor.AQUA ) ).appendSpace();
                if( serverPlugin.isEnabled() ) {
                    if( sender.hasPermission( "appm.commands.plugin.disable" ) ) component = component.append( Component.text( "Enabled" ).color( NamedTextColor.GREEN ).decorate( TextDecoration.BOLD ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin disable " + serverPlugin.getName() ) ) );
                    else component = component.append( Component.text( "Enabled" ).color( NamedTextColor.GREEN ) );
                }
                else {
                    if( sender.hasPermission( "appm.commands.plugin.enable" ) ) component = component.append( Component.text( "Disabled" ).color( NamedTextColor.RED ).decorate( TextDecoration.BOLD ).clickEvent( ClickEvent.runCommand( "/pluginmanager:plugin enable " + serverPlugin.getName() ) ) );
                    else component = component.append( Component.text( "Disabled" ).color( NamedTextColor.RED ) );
                }
                component = component.appendNewline().append( Component.text( "Version:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                component = component.append( Component.text( serverPlugin.getPluginMeta().getVersion() ) );
                if( serverPlugin.getPluginMeta().getDescription() != null ) {
                    component = component.appendNewline().append( Component.text( "Description:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    component = component.append( Component.text( serverPlugin.getPluginMeta().getDescription() ) );
                }
                List<String> authors = serverPlugin.getPluginMeta().getAuthors();
                if( authors.size() == 1 ) {
                    component = component.appendNewline().append( Component.text( "Author:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    component = component.append( Component.text( authors.get( 0 ) ) );
                } else if( authors.size() > 1 ) {
                    component = component.appendNewline().append( Component.text( "Authors:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    StringBuilder authorString = new StringBuilder( authors.get( 0 ) );
                    for( int i = 1; i < authors.size(); i++ ) authorString.append( ", " ).append( authors.get( i ) );
                    component = component.append( Component.text( authorString.toString() ) );
                }
                if( serverPlugin.getPluginMeta().getWebsite() != null ) {
                    component = component.appendNewline().append( Component.text( "Website:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    component = component.append( Component.text( serverPlugin.getPluginMeta().getWebsite() ).decoration( TextDecoration.UNDERLINED, true ).clickEvent( ClickEvent.openUrl( serverPlugin.getPluginMeta().getWebsite() ) ) );
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
            if( args[0].equals( "enable" ) && sender.hasPermission( "appm.commands.plugin.enable" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) if( !serverPlugin.isEnabled() ) commands.add( serverPlugin.getName() );
            if( args[0].equals( "enable" ) && sender.hasPermission( "appm.commands.plugin.enable" ) && plugin.disabledPlugins.config.getStringList( "plugins" ).contains( "PluginManager" ) ) commands.add( "PluginManager" );
            if( args[0].equals( "disable" ) && sender.hasPermission( "appm.commands.plugin.disable" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) if( serverPlugin.isEnabled() ) commands.add( serverPlugin.getName() );
            if( args[0].equals( "get" ) && sender.hasPermission( "appm.commands.plugin.get" ) ) for( Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins() ) commands.add( serverPlugin.getName() );
            StringUtil.copyPartialMatches( args[1], commands, completions );
        }

        Collections.sort( completions );
        return completions;
    }
}