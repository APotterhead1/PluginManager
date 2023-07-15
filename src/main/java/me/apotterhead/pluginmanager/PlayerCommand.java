// APotterhead
// 13072023-13072023

package me.apotterhead.pluginmanager;

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

            if( args.length < 4 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( args.length == 4 ) {
                if( !( args[1].equals( "name" ) && sender.hasPermission( "appm.commands.player.setHierarchy.name" ) ) && !( args[1].equals( "uuid" ) && sender.hasPermission( "appm.commands.player.setHierarchy.uuid" ) ) ) {
                    sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 1 ) );
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

        if( args[0].equals( "ban" ) ) {}

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
