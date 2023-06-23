// APotterhead
// 22062023-22062023

package me.apotterhead.pluginmanager;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.logging.Level;

public class DisabledPlugins {

    private final PluginManager plugin;
    private File file;
    public YamlConfiguration config;

    public DisabledPlugins( PluginManager plugin ) {
        this.plugin = plugin;
        try {
            file = new File( plugin.getDataFolder(), "disabledPlugins.yml" );
            config = YamlConfiguration.loadConfiguration( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not load disabledPlugins.yml" );
        }

    }

    public void save() {
        try{
            config.save( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not save disabledPlugins.yml" );
        }
    }
}
