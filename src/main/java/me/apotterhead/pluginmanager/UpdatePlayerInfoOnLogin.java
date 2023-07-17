// APotterhead
// 12072023-14072023

package me.apotterhead.pluginmanager;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import me.apotterhead.pluginmanager.ReloadPermissions.ReloadType;
import java.util.Objects;
import java.util.List;

public class UpdatePlayerInfoOnLogin implements Listener {

    private final PluginManager plugin;

    public UpdatePlayerInfoOnLogin(PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin( PlayerLoginEvent event ) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if( !plugin.players.config.contains( uuid ) && plugin.groups.config.contains( "defaultGroup" ) ) {
            String group = plugin.groups.config.getString( "defaultGroup" );

            List<String> groupPlayers = plugin.groups.config.getStringList( "group." + group + ".players" );
            groupPlayers.add( uuid );
            plugin.groups.config.set( "group." + group + ".players", groupPlayers );
            plugin.groups.save();

            List<String> playerGroups = plugin.players.config.getStringList( uuid + ".groups" );
            playerGroups.add( group );
            plugin.players.config.set( uuid + ".groups", playerGroups );
            plugin.players.save();
        }

        plugin.attachments.put( player, player.addAttachment( plugin ) );
        ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );

        if( plugin.players.config.contains( uuid + ".lastName" ) && !Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastName" ) ).equals( player.getName() ) ) {
            List<String> names = plugin.players.config.getStringList( uuid + ".pastNames" );
            names.add( plugin.players.config.getString( uuid + ".lastName" ) );
            plugin.players.config.set( uuid + ".pastNames", names );
            plugin.players.save();
        }
        plugin.players.config.set( uuid + ".lastName", player.getName() );
        plugin.players.save();

        String currentIP = event.getRealAddress().getAddress()[0] + "." + event.getRealAddress().getAddress()[1] + "." + event.getRealAddress().getAddress()[2] + "." + event.getRealAddress().getAddress()[3];
        if( plugin.players.config.contains( uuid + ".lastIP" ) && !Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ).equals( currentIP ) ) {
            List<String> ips = plugin.players.config.getStringList( uuid + ".pastIPs" );
            ips.add( plugin.players.config.getString( uuid + ".lastIP" ) );
            plugin.players.config.set( uuid + ".pastIPs", ips );
            plugin.players.save();
        }
        plugin.players.config.set( uuid + ".lastIP", currentIP );
        plugin.players.save();

        List<String> ips = plugin.ips.config.getStringList( "ips" );
        if( !ips.contains( currentIP ) ) ips.add( currentIP );
        plugin.ips.config.set( "ips", ips );
        plugin.ips.save();

        List<String> currentPlayers = plugin.ips.config.getStringList( "ip." + currentIP + ".currentPlayers" );
        currentPlayers.add( uuid );
        plugin.ips.config.set( "ip." + currentIP + ".currentPlayers", currentPlayers );
        plugin.ips.save();

        List<String> allPlayers = plugin.ips.config.getStringList( "ip." + currentIP + ".allPlayers" );
        allPlayers.add( uuid );
        plugin.ips.config.set( "ip." + currentIP + ".allPlayers", allPlayers );
        plugin.ips.save();
    }
}
