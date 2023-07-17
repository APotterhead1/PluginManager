// APotterhead
// 12072023-12072023

package me.apotterhead.pluginmanager;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.List;

public class UpdatePlayerInfoOnQuit implements Listener {

    private final PluginManager plugin;

    public UpdatePlayerInfoOnQuit(PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        event.getPlayer().removeAttachment( plugin.attachments.get( event.getPlayer() ) );
        plugin.attachments.remove( event.getPlayer() );

        List<String> currentPlayers = plugin.ips.config.getStringList( "ip." + plugin.players.config.getString( event.getPlayer().getUniqueId() + ".lastIP" ) + ".currentPlayers" );
        currentPlayers.remove( event.getPlayer().getUniqueId().toString() );
        plugin.ips.config.set( "ip." + plugin.players.config.getString( event.getPlayer().getUniqueId() + ".lastIP" ) + ".currentPlayers", currentPlayers );
        plugin.ips.save();
    }
}
