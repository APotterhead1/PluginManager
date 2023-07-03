// APotterhead
// 23062023-02072023

package me.apotterhead.pluginmanager;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;

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
                sender.sendMessage( Component.text( "Group '" + args[1] + "' successfully created" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "delete" ) ) {
            if( !sender.hasPermission( "appm.commands.group.delete" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length == 1 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 2 ) {
                List<String> groups = plugin.groups.config.getStringList( "groups" );
                if( !groups.contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
                    return true;
                }
                groups.remove( args[1] );
                plugin.groups.config.set( "groups", groups );
                plugin.groups.config.set( "group." + args[1], null );
                plugin.groups.save();
                sender.sendMessage( Component.text( "Group '" + args[1] + "' has been deleted" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "appm.commands.group.list" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }
            if( args.length == 1 ) {
                TextComponent component = Component.text( "Groups: " ).color( NamedTextColor.GOLD );
                List<String> groups = plugin.groups.config.getStringList( "groups" );
                if( groups.size() == 0 ) {
                    sender.sendMessage( component );
                    return true;
                }
                if( sender.hasPermission( "appm.commands.group.get" ) ) {
                    component = component.append( Component.text( "\n[" + groups.get( 0 ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/group get " + groups.get( 0 ) ) ) );
                    for( int i = 1; i < groups.size(); i++ ) {
                        component = component.append( Component.text( "," ).color( NamedTextColor.WHITE ) );
                        component = component.append( Component.text( "[" + groups.get( i ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/group get " + groups.get( i ) ) ) );
                    }
                } else {
                    component = component.append( Component.text( "\n[" + groups.get( 0 ) + "]" ).color( NamedTextColor.AQUA ) );
                    for( int i = 1; i < groups.size(); i++ ) {
                        component = component.append( Component.text( "," ).color( NamedTextColor.WHITE ) );
                        component = component.append( Component.text( "[" + groups.get( i ) + "]" ).color( NamedTextColor.AQUA ) );
                    }
                }
                sender.sendMessage( component );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 1 ) );
            return true;
        }

        if( args[0].equals( "setHierarchy" ) ) {
            if( !sender.hasPermission( "appm.commands.group.setHierarchy" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 3 )  {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 3 ) {
                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
                    return true;
                }

                try{
                    Integer.parseInt( args[2] );
                } catch( Exception e ) {
                    sender.sendMessage( Component.text( "'" + args[2] + "' is not a number, too large, or too small" ).color( NamedTextColor.RED ) );
                    return true;
                }

                plugin.groups.config.set( "group." + args[1] + ".hierarchyValue", Integer.parseInt( args[2] ) );
                plugin.groups.save();
                sender.sendMessage( Component.text( "The hierarchy value for '" + args[1] + "' has been set to " + args[2] ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
