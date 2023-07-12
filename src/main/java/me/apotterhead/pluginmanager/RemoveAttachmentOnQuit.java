// APotterhead
// 12072023-12072023

package me.apotterhead.pluginmanager;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class RemoveAttachmentOnQuit implements Listener {

    private final PluginManager plugin;

    public RemoveAttachmentOnQuit( PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        event.getPlayer().removeAttachment( plugin.permissions.get( event.getPlayer() ) );
        plugin.permissions.remove( event.getPlayer() );
    }
}
