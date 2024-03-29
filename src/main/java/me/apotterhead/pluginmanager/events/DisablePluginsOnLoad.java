// APotterhead
// 22062023-16072023

package me.apotterhead.pluginmanager.events;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;
import java.util.Objects;
import java.util.logging.Level;
import java.util.List;

public class DisablePluginsOnLoad implements Listener {

    private final PluginManager plugin;

    public DisablePluginsOnLoad( PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoad( ServerLoadEvent event ) {
        for( String serverPluginName : plugin.disabledPlugins.config.getStringList( "plugins" ) ) {
            if (!serverPluginName.equals("PluginManager"))
                plugin.getServer().getPluginManager().disablePlugin(Objects.requireNonNull(plugin.getServer().getPluginManager().getPlugin(serverPluginName)));
            else {
                plugin.getServer().getLogger().log( Level.WARNING, "Cannot disable [PluginManager] on server restart or reload" );
                List<String> plugins = plugin.disabledPlugins.config.getStringList( "plugins" );
                plugins.remove( "PluginManager" );
                plugin.disabledPlugins.config.set( "plugins", plugins );
                plugin.disabledPlugins.save();
            }
        }
    }
}
