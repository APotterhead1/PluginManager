// APotterhead
// 13072023-18072023

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
import me.apotterhead.pluginmanager.util.CommandErrorMessage;
import java.time.temporal.ChronoUnit;
import java.lang.StringBuilder;

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

                if( args[3].equals( "default" ) ) {
                    plugin.players.config.set( uuid + ".hierarchyValue", null );
                    plugin.players.save();
                } else {
                    try {
                        plugin.players.config.set(uuid + ".hierarchyValue", Integer.parseInt(args[3]));
                        plugin.players.save();
                    } catch (Exception e) {
                        sender.sendMessage(Component.text("'" + args[3] + "' is not a whole number, is too large, or is too small").color(NamedTextColor.RED));
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

                Instant now = Instant.now();
                String ipPath = args[2].replace( '.', ',' );

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
                } else banReasons.add( null );
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
            } else banReasons.add( null );
            plugin.players.config.set( uuid + ".banReasons", banReasons );
            plugin.players.save();

            plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).banPlayer( banReasons.get( banReasons.size() - 1 ), null, banSources.get( banSources.size() - 1 ), true );

            sender.sendMessage( Component.text( plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) ).getName() + "(" + uuid + ") has been banned" ).color( NamedTextColor.RED ) );
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

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
