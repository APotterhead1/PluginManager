// APotterhead
// 12062023-12062023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginManager extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand pluginCommand = new PluginCommand( this );
        getCommand( "plugin" ).setExecutor( pluginCommand );
        getCommand( "plugin" ).setTabCompleter( pluginCommand );
    }

    @Override
    public void onDisable() {

    }
}
