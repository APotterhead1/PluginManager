// APotterhead
// 23062023-11072023

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
import org.bukkit.OfflinePlayer;
import java.util.UUID;
import me.apotterhead.pluginmanager.ReloadPermissions.ReloadType;
import java.util.logging.Level;
import java.util.Objects;
import java.util.Collections;
import org.bukkit.util.StringUtil;

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

                if( plugin.groups.config.getStringList( "group." + args[1] + ".players" ).size() != 0 ) {
                    sender.sendMessage( Component.text( "Group '" + args[1] + "' is not empty. Groups must be empty in order to be deleted" ).color( NamedTextColor.RED ) );
                    return true;
                }

                if( plugin.groups.config.contains( "defaultGroup" ) && Objects.requireNonNull( plugin.groups.config.getString( "defaultGroup" ) ).equals( args[1] ) )
                    plugin.groups.config.set( "defaultGroup", null );
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
                Component component = Component.text( "Groups: " ).color( NamedTextColor.GOLD );
                List<String> groups = plugin.groups.config.getStringList( "groups" );
                if( groups.size() == 0 ) {
                    sender.sendMessage( component );
                    return true;
                }
                if( sender.hasPermission( "appm.commands.group.get" ) ) {
                    component = component.appendNewline().append( Component.text( "[" + groups.get( 0 ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/group get " + groups.get( 0 ) ) ) );
                    for( int i = 1; i < groups.size(); i++ ) {
                        component = component.append( Component.text( "," ).color( NamedTextColor.WHITE ) );
                        component = component.append( Component.text( "[" + groups.get( i ) + "]" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/group get " + groups.get( i ) ) ) );
                    }
                } else {
                    component = component.appendNewline().append( Component.text( "[" + groups.get( 0 ) + "]" ).color( NamedTextColor.AQUA ) );
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
                    sender.sendMessage( Component.text( "'" + args[2] + "' is not a whole number, is too large, or is too small" ).color( NamedTextColor.RED ) );
                    return true;
                }

                int oldHierarchyValue = plugin.groups.config.getInt( "group" + args[1] + ".hierarchyValue" );

                plugin.groups.config.set( "group." + args[1] + ".hierarchyValue", Integer.parseInt( args[2] ) );
                plugin.groups.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.GROUP, args[1], plugin );
                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    plugin.groups.config.set( "group." + args[1] + ".hierarchyValue", oldHierarchyValue );
                    plugin.groups.save();

                    ReloadPermissions.reload( ReloadType.GROUP, args[1], plugin );
                    return true;
                }

                sender.sendMessage( Component.text( "The hierarchy value for '" + args[1] + "' has been set to " + args[2] ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        if( args[0].equals( "join" ) ) {
            if( !sender.hasPermission( "appm.commands.group.join.name" ) && !sender.hasPermission( "appm.commands.group.join.uuid" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 4 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 4 ) {
                if( !( args[2].equals( "name" ) && sender.hasPermission( "appm.commands.group.join.name" ) ) && !( args[2].equals( "uuid" ) && sender.hasPermission( "appm.commands.group.join.uuid" ) ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 2 ) );
                    return true;
                }

                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
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
                    for( OfflinePlayer player : players )
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[3] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 3, "player" ) );
                    return true;
                }

                List<String> groupPlayers = plugin.groups.config.getStringList( "group." + args[1] + ".players" );

                if( groupPlayers.contains( uuid ) ) {
                    sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") is already a member of the group '" + args[1] + "'" ).color( NamedTextColor.RED ) );
                    return true;
                }

                groupPlayers.add( uuid );
                plugin.groups.config.set( "group." + args[1] + ".players", groupPlayers );
                plugin.groups.save();

                List<String> playerGroups = plugin.players.config.getStringList( uuid + ".groups" );
                playerGroups.add( args[1] );
                plugin.players.config.set( uuid + ".groups", playerGroups );
                plugin.players.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );
                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    groupPlayers.remove( uuid );
                    plugin.groups.config.set( "group." + args[1] + ".players", groupPlayers );
                    plugin.groups.save();

                    playerGroups.remove( args[1] );
                    plugin.players.config.set( uuid + ".groups", playerGroups );
                    plugin.players.save();

                    ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );
                    return true;
                }

                sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") has joined group '" + args[1] + "'" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 4 ) );
            return true;
        }

        if( args[0].equals( "leave" ) ) {
            if( !sender.hasPermission( "appm.commands.group.leave.name" ) && !sender.hasPermission( "appm.commands.group.leave.uuid" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label,args, 0 ) );
                return true;
            }

            if( args.length < 4 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 4 ) {
                if( !( args[2].equals( "name" ) && sender.hasPermission( "appm.commands.group.leave.name" ) ) && !( args[2].equals( "uuid" ) && sender.hasPermission( "appm.commands.group.leave.uuid" ) ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 2 ) );
                    return true;
                }

                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
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

                List<String> groupPlayers = plugin.groups.config.getStringList( "group." + args[1] + ".players" );

                if( !groupPlayers.contains( uuid ) ) {
                    sender.sendMessage( Component.text( "Group '" + args[1] + "' does not contain " + plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ")" ) );
                    return true;
                }

                groupPlayers.remove( uuid );
                plugin.groups.config.set( "group." + args[1] + ".players", groupPlayers );
                plugin.groups.save();

                List<String> playerGroups = plugin.players.config.getStringList( uuid + ".groups" );
                playerGroups.remove( args[1] );
                plugin.players.config.set( uuid + ".groups", playerGroups );
                plugin.players.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );

                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    groupPlayers.add( uuid );
                    plugin.groups.config.set( "group." + args[1] + ".players", groupPlayers );
                    plugin.groups.save();

                    playerGroups.add( args[1] );
                    plugin.players.config.set( uuid + ".groups", playerGroups );
                    plugin.players.save();

                    ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );
                    return true;
                }

                sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") has left group '" + args[1] + "'" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 4 ) );
            return true;
        }

        if( args[0].equals( "empty" ) ) {
            if( !sender.hasPermission( "appm.commands.group.empty" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 2 ) {
                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
                    return true;
                }

                List<String> players = plugin.groups.config.getStringList( "group." + args[1] + ".players" );
                for( String player : players ) {
                    List<String> groups = plugin.players.config.getStringList( player + ".groups" );
                    groups.remove( args[1] );
                    plugin.players.config.set( player + ".groups", groups );
                    plugin.players.save();
                }

                plugin.groups.config.set( "group." + args[1] + ".players", null );
                plugin.groups.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.GROUP, args[1], plugin );
                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    for( String player : players ) {
                        List<String> groups = plugin.players.config.getStringList( player + ".groups" );
                        groups.add( args[1] );
                        plugin.groups.config.set( player + ".groups", groups );
                        plugin.groups.save();
                    }
                    plugin.groups.config.set( "group." + args[1] + ".players", players );
                    plugin.groups.save();

                    ReloadPermissions.reload( ReloadType.GROUP, args[1], plugin );
                    return true;
                }

                sender.sendMessage( Component.text( players.size() + ( players.size() == 1 ? " player has" : " players have" ) + " been removed from group '" + args[1] + "'" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "get" ) ) {
            if( !sender.hasPermission( "appm.commands.group.get" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 2 ) {
                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
                    return true;
                }

                Component component = Component.text( args[1] + ":" ).color( NamedTextColor.GOLD ).appendNewline();
                if( plugin.groups.config.contains( "defaultGroup" ) && Objects.requireNonNull( plugin.groups.config.getString( "defaultGroup" ) ).equals( args[1] ) )
                    component = component.append( Component.text( "Default Group" ).color( NamedTextColor.GREEN ) ).appendNewline();
                component = component.append( Component.text( "Hierarchy Value:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                component = component.append( Component.text( Integer.toString( plugin.groups.config.getInt( "group." + args[1] + ".hierarchyValue" ) ) ).color( NamedTextColor.WHITE ) );
                List<String> players = plugin.groups.config.getStringList( "group." + args[1] + ".players" );

                if( players.size() > 0 ) {
                    if( players.size() == 1 ) component = component.appendNewline().append( Component.text( "1 Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    else component = component.appendNewline().append( Component.text( players.size() + " Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    if( sender.hasPermission( "appm.commands.player.get.uuid" ) ) {
                        component = component.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( 0 ) + ")" ).color( NamedTextColor.WHITE ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/player get uuid " + players.get( 0 ) ) ) );
                        for (int i = 1; i < players.size(); i++)
                            component = component.append( Component.text( ", " + plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( i ) + ")" ).color( NamedTextColor.WHITE ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/player get uuid " + players.get( 0 ) ) ) );
                    } else if( sender.hasPermission( "appm.commands.player.get.name" ) ) {
                        component = component.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( 0 ) + ")" ).color( NamedTextColor.WHITE ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() ) ) );
                        for (int i = 1; i < players.size(); i++)
                            component = component.append( Component.text( ", " + plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( i ) + ")" ).color( NamedTextColor.WHITE ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() ) ) );
                    } else {
                        component = component.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( 0 ) + ")" ).color( NamedTextColor.WHITE ) );
                        for (int i = 1; i < players.size(); i++)
                            component = component.append( Component.text( ", " + plugin.getServer().getOfflinePlayer( UUID.fromString( players.get( 0 ) ) ).getName() + "(" + players.get( i ) + ")" ).color( NamedTextColor.WHITE ) );
                    }
                }

                sender.sendMessage( component );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        if( args[0].equals( "setDefaultGroup" ) ) {
            if( !sender.hasPermission( "appm.commands.group.setDefaultGroup" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 2 ) {
                if( !plugin.groups.config.getStringList( "groups" ).contains( args[1] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "group" ) );
                    return true;
                }

                plugin.groups.config.set( "defaultGroup", args[1] );
                plugin.groups.save();

                sender.sendMessage( Component.text( "Group '" + args[1] + "' has been made the default group" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if( args.length == 1 ) {
            if( sender.hasPermission( "appm.commands.group.create" ) ) commands.add( "create" );
            if( sender.hasPermission( "appm.commands.group.delete" ) ) commands.add( "delete" );
            if( sender.hasPermission( "appm.commands.group.list" ) ) commands.add( "list" );
            if( sender.hasPermission( "appm.commands.group.setHierarchy" ) ) commands.add( "setHierarchy" );
            if( sender.hasPermission( "appm.commands.group.join.name" ) || sender.hasPermission( "appm.commands.group.join.uuid" ) ) commands.add( "join" );
            if( sender.hasPermission( "appm.commands.group.leave.name" ) || sender.hasPermission( "appm.commands.group.leave.uuid" ) ) commands.add( "leave" );
            if( sender.hasPermission( "appm.commands.group.empty" ) ) commands.add( "empty" );
            if( sender.hasPermission( "appm.commands.group.get" ) ) commands.add( "get" );
            if( sender.hasPermission( "appm.commands.group.setDefaultGroup" ) ) commands.add( "setDefaultGroup" );
            StringUtil.copyPartialMatches( args[0], commands, completions );
        }

        if( args.length == 2 ) {
            if( args[0].equals( "delete" ) && sender.hasPermission( "appm.commands.group.delete" ) ) for( String group : plugin.groups.config.getStringList( "groups" ) ) if( plugin.groups.config.getStringList( "group." + group + ".players" ).size() == 0 ) commands.add( group );
            if( args[0].equals( "setHierarchy" ) && sender.hasPermission( "appm.commands.group.setHierarchy" ) ) commands.addAll( plugin.groups.config.getStringList( "groups" ) );
            if( args[0].equals( "join" ) && ( sender.hasPermission( "appm.commands.group.join.name" ) || sender.hasPermission( "appm.commands.group.join.uuid" ) ) ) for( String group : plugin.groups.config.getStringList( "groups" ) ) if( plugin.groups.config.getStringList( "group." + group + ".players").size() != plugin.getServer().getOfflinePlayers().length ) commands.add( group );
            if( args[0].equals( "leave" ) && ( sender.hasPermission( "appm.commands.group.leave.name" ) || sender.hasPermission( "appm.commands.group.leave" ) ) ) for( String group : plugin.groups.config.getStringList( "groups" ) ) if( plugin.groups.config.getStringList( "group." + group + ".players" ).size() != 0 ) commands.add( group );
            if( args[0].equals( "empty" ) && sender.hasPermission( "appm.commands.group.empty" ) ) for( String group : plugin.groups.config.getStringList( "groups" ) ) if( plugin.groups.config.getStringList( "group." + group + ".players" ).size() != 0 ) commands.add( group );
            if( args[0].equals( "get" ) && sender.hasPermission( "appm.commands.group.get" ) ) commands.addAll( plugin.groups.config.getStringList( "groups" ) );
            if( args[0].equals( "setDefaultGroup" ) && sender.hasPermission( "appm.commands.group.setDefaultGroup" ) ) for( String group : plugin.groups.config.getStringList( "groups" ) ) if( !plugin.groups.config.contains( "defaultGroup" ) || !Objects.requireNonNull( plugin.groups.config.getString( "defaultGroup" ) ).equals( group ) ) commands.add( group );
            StringUtil.copyPartialMatches( args[1], commands, completions );
        }

        if( args.length == 3 ) {
            if( args[0].equals( "join" ) && sender.hasPermission( "appm.commands.group.join.name" ) ) commands.add( "name" );
            if( args[0].equals( "join" ) && sender.hasPermission( "appm.commands.group.join.uuid" ) ) commands.add( "uuid" );
            if( args[0].equals( "leave" ) && sender.hasPermission( "appm.commands.group.leave.name" ) ) commands.add( "name" );
            if( args[0].equals( "leave" ) && sender.hasPermission( "appm.commands.group.leave.uuid" ) ) commands.add( "uuid" );
            StringUtil.copyPartialMatches( args[2], commands, completions );
        }

        if( args.length == 4 ) {
            if( args[0].equals( "join" ) && args[2].equals( "name" ) && sender.hasPermission( "appm.commands.group.join.name" ) ) for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) if( !plugin.groups.config.getStringList( "group." + args[1] + ".players" ).contains( player.getUniqueId().toString() ) ) commands.add( player.getName() );
            if( args[0].equals( "join" ) && args[2].equals( "uuid" ) && sender.hasPermission( "appm.commands.group.join.uuid" ) ) for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) if( !plugin.groups.config.getStringList( "group." + args[1] + ".players" ).contains( player.getUniqueId().toString() ) ) commands.add( player.getUniqueId().toString() );
            if( args[0].equals( "leave" ) && args[2].equals( "name" ) && sender.hasPermission( "appm.commands.group.leave.name" ) ) for( String player : plugin.groups.config.getStringList( "group." + args[1] + ".players" ) ) commands.add( plugin.getServer().getOfflinePlayer( UUID.fromString( player ) ).getName() );
            if( args[0].equals( "leave" ) && args[2].equals( "uuid" ) && sender.hasPermission( "appm.commands.group.leave.uuid" ) ) commands.addAll( plugin.groups.config.getStringList( "group." + args[1] + ".players" ) );
            StringUtil.copyPartialMatches( args[3], commands, completions );
        }

        Collections.sort( completions );
        return completions;
    }
}
