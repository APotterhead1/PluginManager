// APotterhead
// 12072023-18072023

package me.apotterhead.pluginmanager.events;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.List;
import java.util.Objects;

public class UpdatePlayerInfoOnQuit implements Listener {

    private final PluginManager plugin;

    public UpdatePlayerInfoOnQuit(PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        event.getPlayer().removeAttachment( plugin.attachments.get( event.getPlayer() ) );
        plugin.attachments.remove( event.getPlayer() );

        String ipPath = Objects.requireNonNull( plugin.players.config.getString( event.getPlayer().getUniqueId() + ".lastIP" ) ).replace( '.', ',' );
        List<String> currentPlayers = plugin.ips.config.getStringList( "ip." + ipPath + ".currentPlayers" );
        currentPlayers.remove( event.getPlayer().getUniqueId().toString() );
        plugin.ips.config.set( "ip." + ipPath + ".currentPlayers", currentPlayers );
        plugin.ips.save();
    }
}
