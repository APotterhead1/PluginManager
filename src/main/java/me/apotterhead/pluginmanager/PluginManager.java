// APotterhead
// 12062023-12062023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class PluginManager extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand pluginCommand = new PluginCommand( this );
        Objects.requireNonNull( getCommand( "plugin" ) ).setExecutor( pluginCommand );
        Objects.requireNonNull( getCommand( "plugin" ) ).setTabCompleter( pluginCommand );
    }

}
