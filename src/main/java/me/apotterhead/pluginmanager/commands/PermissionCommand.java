// APotterhead
// 22072023-18112023

package me.apotterhead.pluginmanager.commands;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import me.apotterhead.pluginmanager.util.*;
import me.apotterhead.pluginmanager.PluginManager;
import me.apotterhead.pluginmanager.util.ReloadPermissions.ReloadType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.logging.Level;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.permissions.Permission;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import java.util.Collections;
import org.bukkit.util.StringUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.UUID;

public class PermissionCommand implements TabExecutor {

    private final PluginManager plugin;

    public PermissionCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        if( args.length == 0 ) {
            sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            return true;
        }

        if( args[0].equals( "setNegative" ) ) {
            if( !sender.hasPermission( "appm.commands.permission.setNegative.true" ) && !sender.hasPermission( "appm.commands.permission.setNegative.false" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 3 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[2].equals( "true" ) && sender.hasPermission( "appm.commands.permission.setNegative.true" ) ) && !( args[2].equals( "false" ) && sender.hasPermission( "appm.commands.permission.setNegative.false" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 2 ) );
                return true;
            }

            if( args.length == 3 ) {
                if( plugin.getServer().getPluginManager().getPermission( args[1] ) == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "permission" ) );
                    return true;
                }

                String permPath = args[1].replace( '.', ',' );

                boolean originalValue = plugin.permissions.config.getBoolean( permPath + ".negative" );

                plugin.permissions.config.set( permPath + ".negative", Boolean.valueOf( args[2] ) );
                plugin.permissions.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.PERMISSION, args[1], plugin );

                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    plugin.permissions.config.set( permPath + ".negative", originalValue );
                    plugin.permissions.save();

                    return true;
                }

                sender.sendMessage( Component.text( "Permission '" + args[1] + "' has been set to " + ( Boolean.parseBoolean( args[2] ) ? "negative" : "positive" ) ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "appm.commands.permission.list" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length == 1 ) {
                Component message = Component.text( "" ).append( Component.text( "Permissions:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                List<Permission> permissions = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
                List<String> perms = new ArrayList<>();
                for( Permission perm : permissions ) perms.add( perm.getName() );
                Collections.sort( perms );

                if( sender.hasPermission( "appm.commands.permission.get.permission" ) ) {
                    message = message.append( Component.text( "[" + perms.get( 0 ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get permission " + perms.get( 0 ) ) ) );
                    for( int i = 1; i < perms.size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( "[" + perms.get( i ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get " + perms.get( i ) ) ) );
                } else {
                    message = message.append( Component.text( "[" + perms.get( 0 ) + "]" ).color( NamedTextColor.AQUA ) );
                    for( int i = 1; i < perms.size(); i++ ) message = message.append( Component.text( ", [" + perms.get( 0 ) + "]" ).color( NamedTextColor.AQUA ) );
                }

                sender.sendMessage( message );
                return true;
            }

            if( args.length == 2 ) {
                Component message = Component.text( "" ).append( Component.text( "Permissions: ").color( NamedTextColor.GOLD ).appendNewline() );

                List<Permission> permissions = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
                List<String> perms = new ArrayList<>();
                for( Permission perm : permissions ) perms.add( perm.getName() );
                List<String> matches = new ArrayList<>();
                StringUtil.copyPartialMatches( args[1], perms, matches );
                Collections.sort( matches );

                if( sender.hasPermission( "appm.commands.permission.get.permission" ) ) {
                    if( !matches.isEmpty() )message = message.append( Component.text( "[" + matches.get( 0 ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get permission " + matches.get( 0 ) ) ) );
                    for( int i = 1; i < matches.size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( "[" + matches.get( i ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get permission " + matches.get( i ) ) ) );
                } else {
                    if( !matches.isEmpty() ) message = message.append( Component.text( "[" + matches.get( 0 ) + "]" ).color( NamedTextColor.AQUA ) );
                    for( int i = 1; i < matches.size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( "[" + matches.get( i ) + "]" ) );
                }

                sender.sendMessage( message );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "set" ) ) {
            if( !sender.hasPermission( "appm.commands.permission.set.player.name.true" ) && !sender.hasPermission( "appm.commands.permission.set.player.name.false" ) && !sender.hasPermission( "appm.commands.permission.set.player.name.neutral" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.true" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.false" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.neutral" ) && !sender.hasPermission( "appm.commands.permission.set.group.true" ) && !sender.hasPermission( "appm.commands.permission.set.group.false" ) && !sender.hasPermission( "appm.commands.permission.set.group.neutral" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args[1].equals( "player" ) ) {
                if( !sender.hasPermission( "appm.commands.permission.set.player.name.true" ) && !sender.hasPermission( "appm.commands.permission.set.player.name.false" ) && !sender.hasPermission( "appm.commands.permission.set.player.name.neutral" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.true" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.false" ) && !sender.hasPermission( "appm.commands.permission.set.player.uuid.neutral" ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                    return true;
                }

                if( args.length < 3 ) {
                    sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                    return true;
                }

                if( !( args[2].equals( "name" ) && ( sender.hasPermission( "appm.commands.permission.set.player.name.true" ) || sender.hasPermission( "appm.commands.permission.set.player.name.false" ) || sender.hasPermission( "appm.commands.permission.set.player.name.neutral" ) ) ) && !( args[2].equals( "uuid" ) && ( sender.hasPermission( "appm.commands.permission.set.player.uuid.true") || sender.hasPermission( "appm.commands.permission.set.player.uuid.false" ) || sender.hasPermission( "appm.commands.permission.set.player.uuid.neutral" ) ) ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 2 ) );
                    return true;
                }

                if( args.length < 6 ) {
                    sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                    return true;
                }

                if( !( args[5].equals( "true" ) && ( sender.hasPermission( "appm.commands.permission.set.player.name.true" ) || sender.hasPermission( "appm.commands.permission.set.player.uuid.true" ) ) ) && !( args[5].equals( "false" ) && ( sender.hasPermission( "appm.commands.permission.set.player.name.false" ) || sender.hasPermission( "appm.commands.permission.set.player.uuid.false" ) ) ) && !( args[5].equals( "neutral" ) && ( sender.hasPermission( "appm.commands.permission.set.player.name.neutral" ) || sender.hasPermission( "appm.commands.permission.set.player.uuid.neutral" ) ) ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 5 ) );
                    return true;
                }

                String uuid = null;
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( args[2].equals( "name" ) ) {
                    for( OfflinePlayer player : players ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[3] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[2].equals( "uuid" ) ) {
                    for( OfflinePlayer player : players ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[3] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 3, "player" ) );
                    return true;
                }

                if( plugin.getServer().getPluginManager().getPermission( args[4] ) == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 4, "permission" ) );
                    return true;
                }

                if( args.length != 6 ) {
                    sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 6 ) );
                    return true;
                }

                if( sender instanceof Player ) {
                    int senderHV = 0;
                    if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                    else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                        senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                        for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                    }

                    int targetHV = 0;
                    if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                    else if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                        targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                        for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                    }

                    if( !( senderHV > targetHV ) ) {
                        sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                        return true;
                    }
                }

                String permPath = args[4].replace( ".", "," );

                if( !plugin.permissions.config.getBoolean( permPath + ".negative" ) && !sender.hasPermission( args[4] ) || plugin.permissions.config.getBoolean( permPath + ".negative" ) && sender.hasPermission( args[4] ) ) {
                    sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                    return true;
                }

                List<String> truePerms = plugin.players.config.getStringList( uuid + ".truePerms" );
                List<String> falsePerms = plugin.players.config.getStringList( uuid + ".falsePerms" );

                List<String> originalTruePerms = new ArrayList<>( truePerms );
                List<String> originalFalsePerms = new ArrayList<>( falsePerms );

                truePerms.remove( args[4] );
                falsePerms.remove( args[4] );

                if( args[5].equals( "true" ) ) truePerms.add( args[4] );
                if( args[5].equals( "false" ) ) falsePerms.add( args[4] );

                plugin.players.config.set( uuid + ".truePerms", truePerms );
                plugin.players.config.set( uuid + ".falsePerms", falsePerms );
                plugin.players.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );

                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    plugin.players.config.set( uuid + ".truePerms", originalTruePerms );
                    plugin.players.config.set( uuid + ".falsePerms", originalFalsePerms );
                    plugin.players.save();

                    return true;
                }

                sender.sendMessage( Component.text( "The permission '" + args[4] + "' has been set to " + args[5] + " for " + plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ")" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            if( args[1].equals( "group" ) ) {
                if( !sender.hasPermission( "appm.commands.permission.set.group.true" ) && !sender.hasPermission( "appm.commands.permission.set.group.false" ) && !sender.hasPermission( "appm.commands.permission.set.group.neutral" ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                    return true;
                }

                if( args.length < 5 ) {
                    sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                    return true;
                }

                if( !plugin.groups.config.getStringList( "groups" ).contains( args[2] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "group" ) );
                    return true;
                }

                if( plugin.getServer().getPluginManager().getPermission( args[3] ) == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 3, "permission" ) );
                    return true;
                }

                if( args.length == 5 ) {
                    if( sender instanceof Player ) {
                        int senderHV = 0;
                        if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + "groups" ).isEmpty() ) {
                            senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) < senderHV ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        if( !( senderHV > plugin.groups.config.getInt( "group." + args[2] + "hierarchyValue" ) ) ) {
                            sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                            return true;
                        }
                    }

                    String permPath = args[3].replace( ".", "," );
                    if( !plugin.permissions.config.getBoolean( permPath + ".negative" ) && !sender.hasPermission( args[3] ) || plugin.permissions.config.getBoolean( permPath + ".negative" ) && sender.hasPermission( args[3] ) ) {
                        sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                        return true;
                    }

                    List<String> truePerms = plugin.groups.config.getStringList( "group." + args[2] + ".truePerms" );
                    List<String> falsePerms = plugin.groups.config.getStringList( "group." + args[2] + ".falsePerms" );

                    List<String> originalTruePerms = new ArrayList<>( truePerms );
                    List<String> originalFalsePerms = new ArrayList<>( falsePerms );

                    truePerms.remove( args[3] );
                    falsePerms.remove( args[3] );

                    plugin.groups.config.set( "group." + args[2] + ".truePerms", truePerms );
                    plugin.groups.config.set( "group." + args[2] + ".falsePerms", falsePerms );
                    plugin.groups.save();

                    Component reloadMessage = ReloadPermissions.reload( ReloadType.GROUP, args[2], plugin );

                    if( reloadMessage != null ) {
                        sender.sendMessage( reloadMessage );
                        plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                        plugin.groups.config.set( "group." + args[2] + ".truePerms", originalTruePerms );
                        plugin.groups.config.set( "group." + args[2] + ".falsePerms", originalFalsePerms );
                        plugin.groups.save();

                        return true;
                    }

                    sender.sendMessage( Component.text( "The permission '" + args[3] + "' has been set to " + args[4] + " for the group " + args[2] ).color( NamedTextColor.GREEN ) );
                    return true;
                }

                sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 5 ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
