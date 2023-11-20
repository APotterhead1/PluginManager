// APotterhead
// 13072023-18112023

package me.apotterhead.pluginmanager.commands;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.BanList;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.bukkit.OfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.UUID;
import java.time.Instant;
import org.bukkit.entity.Player;
import me.apotterhead.pluginmanager.util.*;
import java.time.temporal.ChronoUnit;
import java.lang.StringBuilder;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import java.util.Objects;
import java.util.Collections;
import org.bukkit.util.StringUtil;

public class PlayerCommand implements TabExecutor {

    private final PluginManager plugin;

    public PlayerCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        if( args.length == 0 ) {
            if( sender.hasPermission( "appm.help-tab-complete.player" ) ) sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            else sender.sendMessage( CommandErrorMessage.PERMISSION.send() );
            return true;
        }

        if( args[0].equals( "setHierarchy" ) ) {
            if( !sender.hasPermission( "appm.commands.player.setHierarchy.name" ) && !sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.setHierarchy.name" ) ) && !( args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                return true;
            }

            if( args.length < 4 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 4 ) {
                String uuid = null;
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( args[1].equals( "name" ) ) {
                    for( OfflinePlayer player : players ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[1].equals( "uuid" ) ) {
                    for( OfflinePlayer player : players ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "player" ) );
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

                if( args[3].equals( "default" ) ) {
                    if( sender instanceof Player ) {
                        int senderHV = 0;
                        if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                            senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        int targetHV = 0;
                        if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                            targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        if( !( senderHV > targetHV ) ) {
                            sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                            return true;
                        }
                    }
                    plugin.players.config.set( uuid + ".hierarchyValue", null );
                    plugin.players.save();
                } else {
                    try {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            if( !( senderHV > Integer.parseInt( args[3] ) ) ) {
                                sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                                return true;
                            }
                        }
                        plugin.players.config.set( uuid + ".hierarchyValue", Integer.parseInt( args[3] ) );
                        plugin.players.save();
                    } catch ( Exception e ) {
                        sender.sendMessage( Component.text( "'" + args[3] + "' is not a whole number, is too large, or is too small" ).color( NamedTextColor.RED ) );
                        return true;
                    }
                }

                sender.sendMessage( Component.text( "The hierarchy value for " + plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") has been set to " + args[3] ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 4 ) );
            return true;
        }

        if( args[0].equals( "ban" ) ) {
            if( !sender.hasPermission( "appm.commands.player.ban.name" ) && !sender.hasPermission( "appm.commands.player.ban.uuid" ) && !sender.hasPermission( "appm.commands.player.ban.ip" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.ban.name" ) ) && !( args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.ban.uuid" ) ) && !( args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.ban.ip" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                return true;
            }

            if( args.length < 4 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !args[3].equals( "minute" ) && !args[3].equals( "hour" ) && !args[3].equals( "day" ) && !args[3].equals( "infinite" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 3 ) );
                return true;
            }

            if( args.length == 4  && !args[3].equals( "infinite" ) ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length > 4 && ( args[3].equals( "minute" ) || args[3].equals( "hour" ) || args[3].equals( "day" ) ) ) {
                try {
                    Integer.parseInt( args[4] );
                } catch( Exception e ) {
                    sender.sendMessage( Component.text( "'" + args[4] + "' is not a whole number, is too large, or is too small" ).color( NamedTextColor.RED ) );
                    return true;
                }
            }

            if( args[1].equals( "ip" ) ) {
                if( !plugin.ips.config.getStringList( "ips" ).contains( args[2] ) ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "ip" ) );
                    return true;
                }

                String ipPath = args[2].replace( '.', ',' );

                if( sender instanceof Player ) {
                    int senderHV = 0;
                    if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                    else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                        senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                        for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                    }

                    int targetHV = 0;
                    boolean modified = false;
                    for( String uuid : plugin.ips.config.getStringList( "ip." + ipPath + ".allPlayers" ) ) {
                        if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) {
                            if( !modified ) {
                                targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                modified = true;
                            }
                            else if( targetHV < plugin.players.config.getInt( uuid + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                        }
                        else if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                            if( !modified ) {
                                targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                modified = true;
                            }
                            else if( targetHV < plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }
                    }

                    if( !( senderHV > targetHV ) ) {
                        sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                        return true;
                    }
                }

                Instant now = Instant.now();

                if( plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                    plugin.ips.config.set( "ip." + ipPath + ".totalBanTime", plugin.ips.config.getLong( "ip." + ipPath  + ".totalBanTime" ) + ( now.getEpochSecond() - plugin.ips.config.getLong( "ip." + ipPath + ".banStart" ) ) );
                    plugin.ips.save();
                }

                plugin.ips.config.set( "ip." + ipPath + ".isBanned", true );
                plugin.ips.config.set( "ip." + ipPath + ".banStart", now.getEpochSecond() );
                plugin.ips.save();

                if( args[3].equals( "infinite" ) ) {
                    plugin.ips.config.set( "ip." + ipPath + ".banEnd", null );
                    plugin.ips.save();

                    List<String> banSentenceLengths = plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" );
                    banSentenceLengths.add( "infinite" );
                    plugin.ips.config.set( "ip." + ipPath + ".banSentenceLengths", banSentenceLengths );
                    plugin.ips.save();
                } else {
                    plugin.ips.config.set( "ip." + ipPath + ".banEnd", now.plus( Integer.parseInt( args[4] ), ChronoUnit.valueOf( args[3].toUpperCase() + "S" ) ).getEpochSecond() );
                    plugin.ips.save();

                    List<String> banSentenceLengths = plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" );
                    banSentenceLengths.add( args[4] + args[3].charAt( 0 ) );
                    plugin.ips.config.set( "ip." + ipPath + ".banSentenceLengths", banSentenceLengths );
                    plugin.ips.save();
                }

                List<String> banSources = plugin.ips.config.getStringList( "ip." + ipPath + ".banSources" );
                if( sender instanceof Player ) banSources.add( ( (Player) sender ).getUniqueId().toString() );
                else banSources.add( "console" );
                plugin.ips.config.set( "ip." + ipPath + ".banSources", banSources );
                plugin.ips.save();

                List<String> banReasons = plugin.ips.config.getStringList( "ip." + ipPath + ".banReasons" );
                if( args.length > 5 || args[3].equals( "infinite" ) && args.length > 4 ) {
                    StringBuilder banReason = new StringBuilder( args[ args[3].equals( "infinite" ) ? 4: 5 ] );
                    for( int i = args[3].equals( "infinite" ) ? 5 : 6; i < args.length; i++ ) banReason.append( " " ).append( args[i] );
                    banReasons.add( banReason.toString() );
                } else banReasons.add( "No Reason Specified" );
                plugin.ips.config.set( "ip." + ipPath + ".banReasons", banReasons );
                plugin.ips.save();

                plugin.getServer().banIP( args[2] );

                sender.sendMessage( Component.text( "The IP '" + args[2] + "' has been banned" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            String uuid = null;
            OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

            if( args[1].equals( "name" ) ) {
                for( OfflinePlayer player : players) {
                    assert player.getName() != null;
                    if( player.getName().equals( args[2] ) ) {
                        uuid = player.getUniqueId().toString();
                        break;
                    }
                }
            }

            if( args[1].equals( "uuid" ) ) {
                for( OfflinePlayer player : players ) {
                    if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                        uuid = player.getUniqueId().toString();
                        break;
                    }
                }
            }

            if( uuid == null ) {
                sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "player" ) );
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

            Instant now = Instant.now();

            if( plugin.players.config.getBoolean( uuid + ".isBanned" ) ) {
                plugin.players.config.set( uuid + ".totalBanTime", plugin.players.config.getLong( uuid + ".totalBanTime" ) + ( now.getEpochSecond() - plugin.players.config.getLong( uuid + ".banStart" ) ) );
                plugin.players.save();
            }

            plugin.players.config.set( uuid + ".isBanned", true );
            plugin.players.config.set( uuid + ".banStart", now.getEpochSecond() );
            plugin.players.save();

            if( args[3].equals( "infinite" ) ) {
                plugin.players.config.set( uuid + ".banEnd", null );
                plugin.players.save();

                List<String> banSentenceLengths = plugin.players.config.getStringList( uuid + ".banSentenceLengths" );
                banSentenceLengths.add( "infinite" );
                plugin.players.config.set( uuid + ".banSentenceLengths", banSentenceLengths );
                plugin.players.save();
            } else {
                plugin.players.config.set( uuid + ".banEnd", now.plus( Integer.parseInt( args[4] ), ChronoUnit.valueOf( args[3].toUpperCase() + "S" ) ).getEpochSecond() );
                plugin.players.save();

                List<String> banSentenceLengths = plugin.players.config.getStringList( uuid + ".banSentenceLengths" );
                banSentenceLengths.add( args[4] + args[3].charAt( 0 ) );
                plugin.players.config.set( uuid + ".banSentenceLengths", banSentenceLengths );
                plugin.players.save();
            }

            List<String> banSources = plugin.players.config.getStringList( uuid + ".banSources" );
            if( sender instanceof Player ) banSources.add( ( (Player) sender ).getUniqueId().toString() );
            else banSources.add( "console" );
            plugin.players.config.set( uuid + ".banSources", banSources );
            plugin.players.save();

            List<String> banReasons = plugin.players.config.getStringList( uuid + ".banReasons" );
            if( args.length > 5 || args[3].equals( "infinite" ) && args.length > 4 ) {
                StringBuilder banReason = new StringBuilder( args[ args[3].equals( "infinite" ) ? 4 : 5 ] );
                for( int i = args[3].equals( "infinite" ) ? 5 : 6; i < args.length; i++ ) banReason.append( " " ).append( args[i] );
                banReasons.add( banReason.toString() );
            } else banReasons.add( "No Reason Specified" );
            plugin.players.config.set( uuid + ".banReasons", banReasons );
            plugin.players.save();

            plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).banPlayer( banReasons.get( banReasons.size() - 1 ), null, banSources.get( banSources.size() - 1 ), true );

            sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") has been banned" ).color( NamedTextColor.GREEN ) );
            return true;
        }

        if( args[0].equals( "pardon" ) ) {
            if( !sender.hasPermission( "appm.commands.player.pardon.name" ) && !sender.hasPermission( "appm.commands.player.pardon.uuid" ) && !sender.hasPermission( "appm.commands.player.pardon.ip" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.pardon.name" ) ) && !( args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.pardon.uuid" ) ) && !( args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.pardon.ip" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                return true;
            }

            if( args.length < 3 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 3 ) {
                if( args[1].equals( "ip" ) ) {
                    if( !plugin.ips.config.getStringList( "ips" ).contains( args[2] ) ) {
                        sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "ip" ) );
                        return true;
                    }

                    String ipPath = args[2].replace( '.', ',' );

                    if( sender instanceof Player ) {
                        int senderHV = 0;
                        if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                            senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        int targetHV = 0;
                        boolean modified = false;
                        for( String uuid : plugin.ips.config.getStringList( "ip." + ipPath + ".allPlayers" ) ) {
                            if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) {
                                if( !modified ) {
                                    targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                    modified = true;
                                }
                                else if( targetHV < plugin.players.config.getInt( uuid + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                            }
                            else if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                                if( !modified ) {
                                    targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                    modified = true;
                                }
                                for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }
                        }

                        if( !( senderHV > targetHV ) ) {
                            sender.sendMessage( CommandErrorMessage.HIERARCHY_VALUE.send() );
                            return true;
                        }
                    }

                    if( !plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                        sender.sendMessage( Component.text( "The IP '" + ipPath + "' is not banned" ).color( NamedTextColor.RED ) );
                        return true;
                    }

                    plugin.ips.config.set( "ip." + ipPath + ".totalBanTime", plugin.ips.config.getLong( "ip." + ipPath + ".totalBanTime" ) + ( Instant.now().getEpochSecond() - plugin.ips.config.getLong( "ip." + ipPath + ".banStart" ) ) );
                    plugin.ips.save();

                    plugin.ips.config.set( "ip." + ipPath + ".isBanned", false );
                    plugin.ips.config.set( "ip." + ipPath + ".banStart", null );
                    plugin.ips.config.set( "ip." + ipPath + ".banEnd", null );
                    plugin.ips.save();

                    plugin.getServer().unbanIP( args[2] );

                    sender.sendMessage( Component.text( "The IP '" + args[2] + "' has been pardoned" ).color( NamedTextColor.GREEN ) );
                    return true;
                }

                String uuid = null;
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( args[1].equals( "name" ) ) {
                    for( OfflinePlayer player : players ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[1].equals( "uuid" ) ) {
                    for( OfflinePlayer player : players ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "player" ) );
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

                if( !plugin.players.config.getBoolean( uuid + ".isBanned" ) ) {
                    sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid) ).getName() + "(" + uuid + ") is not banned" ).color( NamedTextColor.RED ) );
                    return true;
                }

                plugin.players.config.set( uuid + ".totalBanTime", plugin.players.config.getLong( uuid + ".totalBanTime" ) + ( Instant.now().getEpochSecond() - plugin.players.config.getLong( uuid + ".banStart" ) ) );
                plugin.players.save();

                plugin.players.config.set( uuid + ".isBanned", false );
                plugin.players.config.set( uuid + ".banStart", null );
                plugin.players.config.set( uuid + ".banEnd", null );
                plugin.players.save();

                plugin.getServer().getBanList( BanList.Type.NAME ).pardon( uuid );

                sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid) ).getName() + "(" + uuid + ") has been pardoned" ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        if( args[0].equals( "get" ) ) {
            if( !sender.hasPermission( "appm.commands.player.get.name.this" ) && !sender.hasPermission( "appm.commands.player.get.name.banHistory" ) && !sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) && !sender.hasPermission( "appm.commands.player.get.ip.this" ) && !sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[1].equals( "name" ) && ( sender.hasPermission( "appm.commands.player.get.name.this" ) || sender.hasPermission( "appm.commands.player.get.name.banHistory" ) ) ) && !( args[1].equals( "uuid" ) && ( sender.hasPermission( "appm.commands.player.get.uuid.this" ) || sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) ) ) && !( args[1].equals( "ip" ) && ( sender.hasPermission( "appm.commands.player.get.ip.this" ) || sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                return true;
            }

            if( args.length < 3 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 3 ) {
                if( !sender.hasPermission( "appm.commands.player.get.name.this" ) && !sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !sender.hasPermission( "appm.commands.player.get.ip.this" ) ) {
                    sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                    return true;
                }

                if( args[1].equals( "ip" ) ) {
                    if( !plugin.ips.config.getStringList( "ips" ).contains( args[2] ) ) {
                        sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "IP" ) );
                        return true;
                    }

                    String ipPath = args[2].replace( '.', ',' );
                    Component message = Component.text( "" ).append( Component.text( args[2]  + ":" ).color( NamedTextColor.GOLD ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( args[2] ) ) );

                    if( plugin.ips.config.contains( "ip." + ipPath + ".banEnd" ) && plugin.ips.config.getLong( "ip." + ipPath + ".banEnd" ) <= Instant.now().getEpochSecond() ) {
                        plugin.ips.config.set( "ip." + ipPath + ".totalBanTime", plugin.ips.config.getLong( "ip." + ipPath + ".totalBanTime" ) + ( plugin.ips.config.getLong( "ip." + ipPath + ".banEnd" ) - plugin.ips.config.getLong( "ip." + ipPath + ".banStart" ) ) );
                        plugin.ips.save();

                        plugin.ips.config.set( "ip." + ipPath + ".isBanned", false );
                        plugin.ips.config.set( "ip." + ipPath + ".banStart", null );
                        plugin.ips.config.set( "ip." + ipPath + ".banEnd", null );
                        plugin.ips.save();

                        plugin.getServer().unbanIP( args[2] );
                    }

                    if( plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) message = message.appendNewline().append( Component.text( "BANNED" ).color( NamedTextColor.RED ) );
                    if( !plugin.ips.config.getStringList( "ip." + ipPath + ".currentPlayers" ).isEmpty() ) message = message.appendNewline().append( Component.text( "ONLINE" ).color( NamedTextColor.GREEN ) );

                    List<String> currentPlayers = plugin.ips.config.getStringList( "ip." + ipPath + ".currentPlayers" );
                    List<String> allPlayers = plugin.ips.config.getStringList( "ip." + ipPath + ".allPlayers" );
                    if( sender.hasPermission( "appm.commands.player.get.uuid.this" ) ) {
                        if( currentPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Current Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + currentPlayers.get( 0 ) ) ) );
                        }
                        if( currentPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( currentPlayers.size() + " Current Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + currentPlayers.get( 0 ) ) ) );
                            for( int i = 1; i < currentPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( i ) ) ).getName() + "(" + currentPlayers.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + currentPlayers.get( i ) ) ) );
                        }

                        if( allPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Total Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + allPlayers.get( 0 ) ) ) );
                        }
                        if( allPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( allPlayers.size() + " Total Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + allPlayers.get( 0 ) ) ) );
                            for( int i = 1; i < allPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( i ) ) ).getName() + "(" + allPlayers.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + allPlayers.get( i ) ) ) );
                        }
                    } else if( sender.hasPermission( "appm.commands.player.get.name.this" ) ) {
                        if( currentPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Current Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() ) ) );
                        }
                        if( currentPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( currentPlayers.size() + " Current Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 )  ) ).getName() ) ) );
                            for( int i = 1; i < currentPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( i ) ) ).getName() + "(" + currentPlayers.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( i ) ) ).getName() ) ) );
                        }

                        if( allPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Total Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() ) ) );
                        }
                        if( allPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( allPlayers.size() + " Total Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() ) ) );
                            for( int i = 1; i < allPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( i ) ) ).getName() + "(" + allPlayers.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( i ) ) ).getName() ) ) );
                        }
                    } else {
                        if( currentPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Current Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ) );
                        }
                        if( currentPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( currentPlayers.size() + " Current Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( 0 ) ) ).getName() + "(" + currentPlayers.get( 0 ) + ")" ) );
                            for( int i = 1; i < currentPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( currentPlayers.get( i ) ) ).getName() + "(" + currentPlayers.get( i ) ) );
                        }

                        if( allPlayers.size() == 1 ) {
                            message = message.appendNewline().append( Component.text( "1 Total Player:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ) );
                        }
                        if( allPlayers.size() > 1 ) {
                            message = message.appendNewline().append( Component.text( allPlayers.size() + " Total Players:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( 0 ) ) ).getName() + "(" + allPlayers.get( 0 ) + ")" ) );
                            for( int i = 1; i < allPlayers.size(); i++ ) message = message.append( Component.text(", " + plugin.getServer().getOfflinePlayer( UUID.fromString( allPlayers.get( i ) ) ).getName() + "(" + allPlayers.get( i ) ) );
                        }
                    }

                    if( plugin.ips.config.contains( "ip." + ipPath + ".totalBanTime" ) || plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                        message = message.appendNewline().append( Component.text( "Total Ban Time:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        long seconds = plugin.ips.config.getLong( "ip." + ipPath + ".totalBanTime" );
                        if( plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) seconds += Instant.now().getEpochSecond() - plugin.ips.config.getLong( "ip." + ipPath + ".banStart" );
                        long days = seconds / 60 / 60 / 24;
                        long hours = seconds / 60 / 60 - days * 24;
                        long minutes = seconds / 60 - hours * 60 - days * 24 * 60;
                        if( seconds - minutes * 60 - hours * 60 * 60 - days * 24 * 60 * 60 >= 30 ) minutes++;
                        if( days > 0 ) message = message.append( Component.text( days + "d " ) );
                        if( hours > 0 ) message = message.append( Component.text( hours + "h " ) );
                        if( minutes > 0 ) message = message.append( Component.text( minutes + "m" ) );

                        if( sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) message = message.appendNewline().append( Component.text( "View Ban History" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get ip " + args[2] + " banHistory" ) ) );
                    }

                    if( plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                        message = message.appendNewline().append( Component.text( "Current Ban Sentence:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" ).get( plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" ).size() - 1 ) ) );

                        message = message.appendNewline().append( Component.text( "Current Ban Source:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        String source = plugin.ips.config.getStringList( "ip." + ipPath + ".banSources" ).get( plugin.ips.config.getStringList( "ip." + ipPath + ".banSources" ).size() - 1 );
                        if( sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !source.equals( "console" ) ) message = message.append( Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + source ) ) );
                        else if( sender.hasPermission( "appm.commands.player.get.name.this" ) && !source.equals( "console" ) ) message = message.append( Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( source ) ) ) ) );
                        else message = message.append( Component.text( source ) );

                        if( plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" ).size() == plugin.ips.config.getStringList( "ip." + ipPath + ".banReasons" ).size() ) {
                            message = message.appendNewline().append( Component.text( "Current Ban Reason:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                            message = message.append( Component.text( plugin.ips.config.getStringList( "ip." + ipPath + ".banReasons" ).get( plugin.ips.config.getStringList( "ip." + ipPath + ".banReasons" ).size() - 1 ) ) );
                        }
                    }

                    sender.sendMessage( message );
                    return true;
                }

                String uuid = null;
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( args[1].equals( "name" ) ) {
                    for( OfflinePlayer player : players ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[1].equals( "uuid" ) ) {
                    for( OfflinePlayer player : players ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "player" ) );
                    return true;
                }


                Component message = Component.text( "" ).append( Component.text().append( Component.text( Objects.requireNonNull( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( Objects.requireNonNull( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() ) ) ) ).append( Component.text( "(" ) ).append( Component.text( uuid ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( uuid ) ) ).append( Component.text( "):" ) ).color( NamedTextColor.GOLD ) );

                if( plugin.players.config.contains( uuid + ".banEnd" ) && plugin.players.config.getLong( uuid + ".banEnd" ) <= Instant.now().getEpochSecond() ) {
                    plugin.players.config.set( uuid + ".totalBanTime", plugin.players.config.getLong( uuid + ".totalBanTime" ) + ( plugin.players.config.getLong( uuid + ".banEnd" ) - plugin.players.config.getLong( uuid + ".banStart" ) ) );
                    plugin.players.save();

                    plugin.players.config.set( uuid + ".isBanned", false );
                    plugin.players.config.set( uuid + ".banStart", null );
                    plugin.players.config.set( uuid + ".banEnd", null );
                    plugin.players.save();

                    plugin.getServer().getBanList( BanList.Type.NAME ).pardon( uuid );
                }

                if( plugin.players.config.getBoolean( uuid + ".isBanned" ) ) message = message.appendNewline().append( Component.text( "BANNED" ).color( NamedTextColor.RED ) );
                if( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).isOnline() ) {
                    message = message.appendNewline().append( Component.text( "ONLINE" ).color( NamedTextColor.GREEN ) );
                    message = message.appendNewline().append( Component.text( "IP Address:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    if( sender.hasPermission( "appm.commands.player.get.ip.this" ) ) message = message.append( Component.text( Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get ip " + plugin.players.config.getString( uuid + ".lastIP" ) ) ) );
                    else message = message.append( Component.text( Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ) ) );
                }

                if( plugin.players.config.contains( uuid + ".totalBanTime" )  || plugin.players.config.getBoolean( uuid + ".isBanned" ) ) {
                    long seconds = plugin.players.config.getLong( uuid + ".totalBanTime" );
                    if( plugin.players.config.getBoolean( uuid + ".isBanned" ) ) seconds += Instant.now().getEpochSecond() - plugin.players.config.getLong( uuid + ".banStart" );
                    long days = seconds / 60 / 60 / 24;
                    long hours = seconds / 60 / 60 - days * 24;
                    long minutes = seconds / 60 - hours * 60 - days * 24 * 60;
                    if( seconds - minutes * 60 - hours * 60 * 60 - days * 24 * 60 * 60 >= 30 ) minutes++;
                    message = message.appendNewline().append( Component.text( "Total Ban Time:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    if( days > 0 ) message = message.append( Component.text( days + "d " ) );
                    if( hours > 0 ) message = message.append( Component.text( hours + "h " ) );
                    if( minutes > 0 ) message = message.append( Component.text( minutes + "m" ) );

                    if( sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) ) message = message.appendNewline().append( Component.text( "View Ban History" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + uuid + " banHistory" ) ) );
                    else if( sender.hasPermission( "appm.commands.player.get.name.banHistory" ) ) message = message.appendNewline().append( Component.text( "View Ban History" ).color( NamedTextColor.AQUA ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + " banHistory" ) ) );
                }

                if( plugin.players.config.getBoolean( uuid + ".isBanned" ) ) {
                    message = message.appendNewline().append( Component.text( "Current Ban Sentence:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".banSentenceLengths" ).get( plugin.players.config.getStringList( uuid + ".banSentenceLengths" ).size() - 1 ) ) );

                    message = message.appendNewline().append( Component.text( "Current Ban Source:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    String source = plugin.players.config.getStringList( uuid + ".banSources" ).get( plugin.players.config.getStringList( uuid + ".banSources" ).size() - 1 );
                    if( sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !source.equals( "console" ) ) message = message.append( Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + source )  ) );
                    else if( sender.hasPermission( "appm.commands.player.get.name.this" ) && !source.equals( "console" ) ) message = message.append( Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( source ) ).getName() ) ) );
                    else message = message.append( Component.text( source ) );

                    if( plugin.players.config.getStringList( uuid + ".banSentenceLengths" ).size() == plugin.players.config.getStringList( uuid + ".banReasons" ).size() ) {
                        message = message.appendNewline().append( Component.text( "Current Ban Reason:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".banReasons" ).get( plugin.players.config.getStringList( uuid + ".banReasons" ).size() - 1 ) ) );
                    }
                }

                message = message.appendNewline().append( Component.text( "Hierarchy Value:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) message = message.append( Component.text( plugin.players.config.getInt( uuid + ".hierarchyValue" ) ) );
                else message = message.append( Component.text( "default" ) );

                if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                    if( plugin.players.config.getStringList( uuid + ".groups" ).size() == 1 ) message = message.appendNewline().append( Component.text( "1 Group:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    else message = message.appendNewline().append( Component.text( plugin.players.config.getStringList( uuid + ".groups" ).size() + " Groups:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    if( sender.hasPermission( "appm.commands.group.get" ) ) {
                        message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:group get " + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) ) ) );
                        for( int i = 1; i < plugin.players.config.getStringList( uuid + ".groups" ).size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( plugin.players.config.getStringList( uuid + ".groups" ).get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:group get " + plugin.players.config.getStringList( uuid + ".groups" ).get( i ) ) ) );
                    } else {
                        message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) ) );
                        for( int i = 1; i < plugin.players.config.getStringList( uuid + ".groups" ).size(); i++ ) message = message.append( Component.text( ", " + plugin.players.config.getStringList( uuid + ".groups" ).get( i ) ) );
                    }
                }

                if( !plugin.players.config.getStringList( uuid + ".pastNames" ).isEmpty() ) {
                    if( plugin.players.config.getStringList( uuid + ".pastNames" ).size() == 1 ) message = message.appendNewline().append( Component.text( "1 Past Name:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    else message = message.appendNewline().append( Component.text( plugin.players.config.getStringList( uuid + ".pastNames" ).size() + " Past Names:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".pastNames" ).get( 0 ) ) );
                    for( int i = 1; i < plugin.players.config.getStringList( uuid + ".pastNames" ).size(); i++ ) message = message.append( Component.text( ", " + plugin.players.config.getStringList( uuid + ".pastNames" ).get( i ) ) );
                }

                if( sender.hasPermission( "appm.commands.player.get.ip.this" ) ) {
                    if( !plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).isOnline() ) {
                        message = message.appendNewline().append( Component.text( "Last IP:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get ip " + plugin.players.config.getString( uuid + ".lastIP" ) ) ) );
                    }

                    if( !plugin.players.config.getStringList( uuid + ".pastIPs" ).isEmpty() ) {
                        if( plugin.players.config.getStringList( uuid + ".pastIPs" ).size() == 1 ) message = message.appendNewline().append( Component.text( "1 Past IP:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        else message = message.appendNewline().append( Component.text( plugin.players.config.getStringList( uuid + ".pastIPs" ).size() + " Past IPs:" ).color( NamedTextColor.AQUA ) ).appendSpace();

                        message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".pastIPs" ).get( 0 ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get ip " + plugin.players.config.getStringList( uuid + ".pastIPs" ).get( 0 ) ) ) );
                        for( int i = 1; i < plugin.players.config.getStringList( uuid + ".pastIPs" ).size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( plugin.players.config.getStringList( uuid + ".pastIPs" ).get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get ip " + plugin.players.config.getStringList( uuid + ".pastIPs" ).get( i ) ) ) );
                    }
                } else {
                    if( !plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).isOnline() ) {
                        message = message.appendNewline().append( Component.text( "Last IP:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ) ) );
                    }

                    if( !plugin.players.config.getStringList( uuid + ".pastIPs" ).isEmpty() ) {
                        if( plugin.players.config.getStringList( uuid + ".pastIPs" ).size() == 1 ) message = message.appendNewline().append( Component.text( "1 Past IP:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        else message = message.appendNewline().append( Component.text( plugin.players.config.getStringList( uuid + ".pastIPs" ).size() + " Past IPs:" ).color( NamedTextColor.AQUA ) ).appendSpace();

                        message = message.append( Component.text( plugin.players.config.getStringList( uuid + ".pastIPs" ).get( 0 ) ) );
                        for( int i = 1; i < plugin.players.config.getStringList( uuid + ".pastIPs" ).size(); i++ ) message = message.append( Component.text( ", " + plugin.players.config.getStringList( uuid + ".pastIPs" ).get( i ) ) );
                    }
                }

                sender.sendMessage( message );
                return true;
            }

            if( args.length == 4 ) {
                if( !sender.hasPermission( "appm.commands.player.get.name.banHistory" ) && !sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) && !sender.hasPermission( "appm.commands.player.ip.banHistory" ) ) {
                    sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
                    return true;
                }

                if( !args[3].equals( "banHistory" ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 3 ) );
                    return true;
                }

                if( args[1].equals( "ip" ) ) {
                    if( !plugin.ips.config.contains( "ips" ) ) {
                        sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "IP" ) );
                        return true;
                    }

                    String ipPath = args[2].replace( '.', ',' );

                    Component message = Component.text( "" ).append( Component.text( args[2] + " Ban History:" ).color( NamedTextColor.GOLD ).decorate( TextDecoration.BOLD ) );
                    List<String> banSentenceLengths = plugin.ips.config.getStringList( "ip." + ipPath + ".banSentenceLengths" );
                    List<String> banSources = plugin.ips.config.getStringList( "ip." + ipPath + ".banSources" );
                    List<String> banReasons = plugin.ips.config.getStringList( "ip." + ipPath + ".banReasons" );

                    for( int i = 0; i < banReasons.size(); i++ ) {
                        message = message.appendNewline().append( Component.text( "Ban #" + ( i + 1 ) + ":" ).color( NamedTextColor.GOLD ) );

                        message = message.appendNewline().append( Component.text( "Sentence Length:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( banSentenceLengths.get( i ) ) );

                        message = message.appendNewline().append( Component.text( "Source:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        if( sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !banSources.get( i ).equals( "console" ) ) message = message.append( Component.text( banSources.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + banSources.get( i ) ) ) );
                        else if( sender.hasPermission( "appm.commands.player.get.name.this" ) && !banSources.get(i).equals( "console" ) ) message = message.append( Component.text( banSources.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( banSources.get( i ) ) ) ) ) );
                        else message = message.append( Component.text( banSources.get( i ) ) );

                        message = message.appendNewline().append( Component.text( "Reason:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                        message = message.append( Component.text( banReasons.get( i ) ) );
                    }

                    sender.sendMessage( message );
                    return true;
                }

                String uuid = null;
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( args[1].equals( "name" ) ) {
                    for( OfflinePlayer player : players ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[1].equals( "uuid" ) ) {
                    for( OfflinePlayer player : players ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( uuid == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 2, "player" ) );
                    return true;
                }

                Component message = Component.text( "" ).append( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") Ban History:" ).color( NamedTextColor.GOLD ).decorate( TextDecoration.BOLD ) );
                List<String> banSentenceLengths = plugin.players.config.getStringList( uuid + ".banSentenceLengths" );
                List<String> banSources = plugin.players.config.getStringList( uuid + ".banSources" );
                List<String> banReasons = plugin.players.config.getStringList( uuid + ".banReasons" );

                for( int i = 0; i < banReasons.size(); i++ ) {
                    message = message.appendNewline().append( Component.text( "Ban #" + ( i + 1 ) + ":" ).color( NamedTextColor.GOLD ) );

                    message = message.appendNewline().append( Component.text( "Sentence Length:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    message = message.append( Component.text( banSentenceLengths.get( i ) ) );

                    message = message.appendNewline().append( Component.text( "Source:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    if( sender.hasPermission( "appm.commands.player.get.uuid.this" ) && !banSources.get( i ).equals( "console" ) ) message = message.append( Component.text( banSources.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + banSources.get( i ) ) ) );
                    else if( sender.hasPermission( "appm.commands.player.get.name.this" ) && !banSources.get( i ).equals( "console" ) ) message = message.append( Component.text( banSources.get( i ) ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + plugin.getServer().getOfflinePlayer( UUID.fromString( banSources.get( i ) ) ) ) ) );
                    else message = message.append( Component.text( banSources.get( i ) ) );

                    message = message.appendNewline().append( Component.text( "Reason:" ).color( NamedTextColor.AQUA ) ).appendSpace();
                    message = message.append( Component.text( banReasons.get( i ) ) );
                }

                sender.sendMessage( message );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "appm.commands.player.list.all" ) && !sender.hasPermission( "appm.commands.player.list.online" ) && !sender.hasPermission( "appm.commands.player.list.offline" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 2 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[1].equals( "all" ) && sender.hasPermission( "appm.commands.player.list.all" ) ) && !( args[1].equals( "online" ) && sender.hasPermission( "appm.commands.player.list.online" ) ) && !( args[1].equals( "offline" ) && sender.hasPermission( "appm.commands.player.list.offline" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
                return true;
            }

            if( args.length == 2 ) {
                Component message = Component.text( "" );
                OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

                if( sender.hasPermission( "appm.commands.player.get.uuid" ) ) {
                    if( args[1].equals( "all" ) ) {
                        if( players.length == 1 ) message = message.append( Component.text( "1 Total Player:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( players.length + " Total Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( first ) {
                                message = message.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                                first = false;
                            } else message = message.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                        }
                    }

                    if( args[1].equals( "online" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Online Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Online Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }

                    if( args[1].equals( "offline" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( !player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get uuid " + player.getUniqueId() ) ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Offline Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Offline Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }
                } else if( sender.hasPermission( "appm.commands.player.get.name" ) ) {
                    if( args[1].equals( "all" ) ) {
                        if( players.length == 1 ) message = message.append( Component.text( "1 Total Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( players.length + " Total Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( first ) {
                                message = message.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                                first = false;
                            } else message = message.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                        }
                    }

                    if( args[1].equals( "online" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Online Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Online Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }

                    if( args[1].equals( "offline" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( !player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/pluginmanager:player get name " + player.getName() ) ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Offline Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Offline Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }
                } else {
                    if( args[1].equals( "all" ) ) {
                        if( players.length == 1 ) message = message.append( Component.text( "1 Total Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( players.length + " Total Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( first ) {
                                message = message.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                                first = false;
                            } else message = message.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                        }
                    }

                    if( args[1].equals( "online" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Online Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Online Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }

                    if( args[1].equals( "offline" ) ) {
                        Component temp = Component.text( "" );
                        int count = 0;
                        boolean first = true;
                        for( OfflinePlayer player : players ) {
                            if( !player.isOnline() ) {
                                if( first ) {
                                    temp = temp.append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                                    first = false;
                                } else temp = temp.append( Component.text( ", " ) ).append( Component.text( player.getName() + "(" + player.getUniqueId() + ")" ) );
                                count++;
                            }
                        }

                        if( players.length == 1 ) message = message.append( Component.text( "1 Offline Player:").color( NamedTextColor.GOLD ) ).appendNewline();
                        else message = message.append( Component.text( count + " Offline Players:" ).color( NamedTextColor.GOLD ) ).appendNewline();
                        message = message.append( temp );
                    }
                }

                sender.sendMessage( message );
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
            if( sender.hasPermission( "appm.commands.player.get.name.this" ) || sender.hasPermission( "appm.commands.player.get.name.banHistory" ) || sender.hasPermission( "appm.commands.player.get.uuid.this" ) || sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) || sender.hasPermission( "appm.commands.player.get.ip.this" ) || sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) commands.add( "get" );
            if( sender.hasPermission( "appm.commands.player.ban.name" ) || sender.hasPermission( "appm.commands.player.ban.uuid" ) || sender.hasPermission( "appm.commands.player.ban.ip" ) ) commands.add( "ban" );
            if( sender.hasPermission( "appm.commands.player.pardon.name" ) || sender.hasPermission( "appm.commands.player.pardon.uuid" ) || sender.hasPermission( "appm.commands.player.pardon.ip" ) ) commands.add( "pardon" );
            if( sender.hasPermission( "appm.commands.player.setHierarchy.name" ) || sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) commands.add( "setHierarchy" );
            if( sender.hasPermission( "appm.commands.players.list.all" ) || sender.hasPermission( "appm.commands.player.list.online" ) || sender.hasPermission( "appm.commands.player.list.offline" ) ) commands.add( "list" );

            StringUtil.copyPartialMatches( args[0], commands, completions );
        }

        if( args.length == 2 ) {
            if( args[0].equals( "get" ) && ( sender.hasPermission( "appm.commands.player.get.name.this" ) || sender.hasPermission( "appm.player.get.name.banHistory" ) ) ) commands.add( "name" );
            if( args[0].equals( "get" ) && ( sender.hasPermission( "appm.commands.player.get.uuid.this" ) || sender.hasPermission( "appm.player.get.uuid.banHistory" ) ) ) commands.add( "uuid" );
            if( args[0].equals( "get" ) && ( sender.hasPermission( "appm.commands.player.get.ip.this" ) || sender.hasPermission( "appm.player.get.ip.banHistory" ) ) ) commands.add( "ip" );

            if( args[0].equals( "ban" ) && sender.hasPermission( "appm.commands.player.ban.name" ) ) commands.add( "name" );
            if( args[0].equals( "ban" ) && sender.hasPermission( "appm.commands.player.ban.uuid" ) ) commands.add( "uuid" );
            if( args[0].equals( "ban" ) && sender.hasPermission( "appm.commands.player.ban.ip" ) ) commands.add( "ip" );

            if( args[0].equals( "pardon" ) && sender.hasPermission( "appm.commands.player.pardon.name" ) ) commands.add( "name" );
            if( args[0].equals( "pardon" ) && sender.hasPermission( "appm.commands.player.pardon.uuid" ) ) commands.add( "uuid" );
            if( args[0].equals( "pardon" ) && sender.hasPermission( "appm.commands.player.pardon.ip" ) ) commands.add( "ip" );

            if( args[0].equals( "setHierarchy" ) && sender.hasPermission( "appm.commands.player.setHierarchy.name" ) ) commands.add( "name" );
            if( args[0].equals( "setHierarchy" ) && sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) commands.add( "uuid" );

            if( args[0].equals( "list" ) && sender.hasPermission( "appm.commands.player.list.all" ) ) commands.add( "all" );
            if( args[0].equals( "list" ) && sender.hasPermission( "appm.commands.player.list.online" ) ) commands.add( "online" );
            if( args[0].equals( "list" ) && sender.hasPermission( "appm.commands.player.list.offline" ) ) commands.add( "offline" );

            StringUtil.copyPartialMatches( args[1], commands, completions );
        }

        if( args.length == 3 ) {
            if( args[0].equals( "get" ) && args[1].equals( "name" ) && ( sender.hasPermission( "appm.commands.player.get.name.this" ) || sender.hasPermission( "appm.commands.player.get.name.banHistory" ) ) ) for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) commands.add( player.getName() );
            if( args[0].equals( "get" ) && args[1].equals( "uuid" ) && ( sender.hasPermission( "appm.commands.player.get.uuid.this" ) || sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) ) ) for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) commands.add( player.getUniqueId().toString() );
            if( args[0].equals( "get" ) && args[1].equals( "ip" ) && ( sender.hasPermission( "appm.commands.player.get.ip.this" ) || sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) ) commands.addAll( plugin.ips.config.getStringList( "ips" ) );

            if( args[0].equals( "ban" ) && args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.ban.name" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( !plugin.players.config.getBoolean( player.getUniqueId() + ".isBanned" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                                targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( player.getName() );
                    }
                }
            }
            if( args[0].equals( "ban" ) && args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.ban.uuid" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( !plugin.players.config.getBoolean( player.getUniqueId() + ".getBoolean" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                                targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( player.getUniqueId().toString() );
                    }
                }
            }
            if( args[0].equals( "ban" ) && args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.ban.ip" ) ) {
                for( String ip : plugin.ips.config.getStringList( "ips" ) ) {
                    String ipPath = ip.replace( '.', ',' );
                    if( !plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            boolean modified = false;
                            for( String uuid : plugin.ips.config.getStringList( "ip." + ipPath + ".allPlayers" ) ) {
                                if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) {
                                    if( !modified ) {
                                        targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                        modified = true;
                                    }
                                    else if( targetHV < plugin.players.config.getInt( uuid + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                }
                                else if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                                    if( !modified ) {
                                        targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                        modified = true;
                                    }
                                    for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                                }
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( ip );
                    }
                }
            }

            if( args[0].equals( "pardon" ) && args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.pardon.name" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( plugin.players.config.contains( player.getUniqueId() + ".banEnd" ) && plugin.players.config.getLong( player.getUniqueId() + ".banEnd" ) <= Instant.now().getEpochSecond() ) {
                        plugin.players.config.set( player.getUniqueId() + ".totalBanTime", plugin.players.config.getLong( player.getUniqueId() + ".totalBanTime" ) + ( plugin.players.config.getLong( player.getUniqueId() + ".banEnd" ) - plugin.players.config.getLong( player.getUniqueId() + ".banStart" ) ) );
                        plugin.players.save();

                        plugin.players.config.set( player.getUniqueId() + ".isBanned", false );
                        plugin.players.config.set( player.getUniqueId() + ".banStart", null );
                        plugin.players.config.set( player.getUniqueId() + ".banEnd", null );
                        plugin.players.save();

                        plugin.getServer().getBanList( BanList.Type.NAME ).pardon( player.getUniqueId().toString() );
                    }
                    if( plugin.players.config.getBoolean( player.getUniqueId() + ".isBanned" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                                targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( player.getName() );
                    }
                }
            }
            if( args[0].equals( "pardon" ) && args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.pardon.uuid" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( plugin.players.config.contains( player.getUniqueId() + ".banEnd" ) && plugin.players.config.getLong( player.getUniqueId() + ".banEnd" ) <= Instant.now().getEpochSecond() ) {
                        plugin.players.config.set( player.getUniqueId() + ".totalBanTime", plugin.players.config.getLong( player.getUniqueId() + ".totalBanTime" ) + ( plugin.players.config.getLong( player.getUniqueId() + ".banEnd" ) - plugin.players.config.getLong( player.getUniqueId() + ".banStart" ) ) );
                        plugin.players.save();

                        plugin.players.config.set( player.getUniqueId() + ".isBanned", false );
                        plugin.players.config.set( player.getUniqueId() + ".banStart", null );
                        plugin.players.config.set( player.getUniqueId() + ".banEnd", null );
                        plugin.players.save();

                        plugin.getServer().getBanList( BanList.Type.NAME ).pardon( player.getUniqueId().toString() );
                    }
                    if( plugin.players.config.getBoolean( player.getUniqueId() + ".isBanned" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                                targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( player.getUniqueId().toString() );
                    }
                }
            }
            if( args[0].equals( "pardon" ) && args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.pardon.ip" ) ) {
                for( String ip : plugin.ips.config.getStringList( "ips" ) ) {
                    String ipPath = ip.replace( '.', ',' );
                    if( plugin.ips.config.contains( "ip." + ipPath + ".banEnd" ) && plugin.ips.config.getLong( "ip." + ipPath + ".banEnd" ) <= Instant.now().getEpochSecond() ) {
                        plugin.ips.config.set( "ip." + ipPath + ".totalBanTime", plugin.ips.config.getLong( "ip." + ipPath + ".totalBanTime" ) + ( plugin.ips.config.getLong( "ip." + ipPath + ".banEnd" ) - plugin.ips.config.getLong( "ip." + ipPath + ".banStart" ) ) );
                        plugin.ips.save();

                        plugin.ips.config.set( "ip." + ipPath + ".isBanned", false );
                        plugin.ips.config.set( "ip." + ipPath + ".banStart", null );
                        plugin.ips.config.set( "ip." + ipPath + ".banEnd", null );
                        plugin.ips.save();

                        plugin.getServer().unbanIP( ip );
                    }
                    if( plugin.ips.config.getBoolean( "ip." + ipPath + ".isBanned" ) ) {
                        if( sender instanceof Player ) {
                            int senderHV = 0;
                            if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                            else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                                senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                            }

                            int targetHV = 0;
                            boolean modified = false;
                            for( String uuid : plugin.ips.config.getStringList( "ip." + ipPath + ".allPlayers" ) ) {
                                if( plugin.players.config.contains( uuid + ".hierarchyValue" ) ) {
                                    if( !modified ) {
                                        targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                        modified = true;
                                    }
                                    else if( targetHV < plugin.players.config.getInt( uuid + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( uuid + ".hierarchyValue" );
                                }
                                else if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                                    if( !modified ) {
                                        targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                                        modified = true;
                                    }
                                    for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                                }
                            }

                            if( !( senderHV > targetHV ) ) continue;
                        }
                        commands.add( ip );
                    }
                }
            }

            if( args[0].equals( "setHierarchy" ) && args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.setHierarchy.name" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( sender instanceof Player ) {
                        int senderHV = 0;
                        if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                            senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        int targetHV = 0;
                        if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                            targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        if( !( senderHV > targetHV ) ) continue;
                    }
                    commands.add( player.getName() );
                }
            }
            if( args[0].equals( "setHierarchy" ) && args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) {
                for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                    if( sender instanceof Player ) {
                        int senderHV = 0;
                        if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                            senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        int targetHV = 0;
                        if( plugin.players.config.contains( player.getUniqueId() + ".hierarchyValue" ) ) targetHV = plugin.players.config.getInt( player.getUniqueId() + ".hierarchyValue" );
                        else if( !plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).isEmpty() ) {
                            targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( player.getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                            for( String group : plugin.players.config.getStringList( player.getUniqueId() + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                        }

                        if( !( senderHV > targetHV ) ) continue;
                    }
                    commands.add( player.getUniqueId().toString() );
                }
            }

            StringUtil.copyPartialMatches( args[2], commands, completions );
        }

        if( args.length == 4 ) {
            if( args[0].equals( "get" ) && ( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.get.name.banHistory" ) || args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.get.uuid.banHistory" ) || args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.get.ip.banHistory" ) ) ) commands.add( "banHistory" );
            if( args[0].equals( "ban" ) && ( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.ban.name" ) || args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.ban.uuid" ) || args[1].equals( "ip" ) && sender.hasPermission( "appm.commands.player.ban.ip" ) ) ) {
                commands.add( "minute" );
                commands.add( "hour" );
                commands.add( "day" );
                commands.add( "infinite" );
            }

            if( args[0].equals( "setHierarchy" ) && ( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.setHierarchy.name" ) || args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) ) {
                String uuid = null;

                if( args[1].equals( "name" ) ) {
                    for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                        assert player.getName() != null;
                        if( player.getName().equals( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                if( args[1].equals( "uuid" ) ) {
                    for( OfflinePlayer player : plugin.getServer().getOfflinePlayers() ) {
                        if( player.getUniqueId().toString().equalsIgnoreCase( args[2] ) ) {
                            uuid = player.getUniqueId().toString();
                            break;
                        }
                    }
                }

                boolean valid = uuid != null;

                if( sender instanceof Player && valid ) {
                    int senderHV = 0;
                    if( plugin.players.config.contains( ( (Player) sender ).getUniqueId() + ".hierarchyValue" ) ) senderHV = plugin.players.config.getInt( ( (Player) sender ).getUniqueId() + ".hierarchyValue" );
                    else if( !plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).isEmpty() ) {
                        senderHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ).get( 0 ) + ".hierarchyValue" );
                        for( String group : plugin.players.config.getStringList( ( (Player) sender ).getUniqueId() + ".groups" ) ) if( senderHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) senderHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                    }

                    int targetHV = 0;
                    if( !plugin.players.config.getStringList( uuid + ".groups" ).isEmpty() ) {
                        targetHV = plugin.groups.config.getInt( "group." + plugin.players.config.getStringList( uuid + ".groups" ).get( 0 ) + ".hierarchyValue" );
                        for( String group : plugin.players.config.getStringList( uuid + ".groups" ) ) if( targetHV < plugin.groups.config.getInt( "group." + group + ".hierarchyValue" ) ) targetHV = plugin.groups.config.getInt( "group." + group + ".hierarchyValue" );
                    }

                    if( ( senderHV > targetHV ) ) commands.add( "default" );
                } else if( valid ) commands.add( "default" );
            }

            StringUtil.copyPartialMatches( args[3], commands, completions );
        }

        Collections.sort( completions );
        return completions;
    }
}
