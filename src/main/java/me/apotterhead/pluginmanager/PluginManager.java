// APotterhead
// 12062023-22062023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class PluginManager extends JavaPlugin {

    public DisabledPluginsFile disabledPlugins;
    @Override
    public void onEnable() {
        disabledPlugins = new DisabledPluginsFile( this );
        PluginCommand pluginCommand = new PluginCommand( this );
        Objects.requireNonNull( getCommand( "plugin" ) ).setExecutor( pluginCommand );
        Objects.requireNonNull( getCommand( "plugin" ) ).setTabCompleter( pluginCommand );

        getServer().getPluginManager().registerEvents( new DisablePluginsOnLoad( this ), this );
    }

}
