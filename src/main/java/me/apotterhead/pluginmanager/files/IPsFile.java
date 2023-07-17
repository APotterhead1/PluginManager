// APotterhead
// 16072023-16072023

package me.apotterhead.pluginmanager.files;

import java.io.File;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.logging.Level;

public class IPsFile {

    private final PluginManager plugin;
    private File file;
    public YamlConfiguration config;

    public IPsFile( PluginManager plugin ) {
        this.plugin = plugin;
        try {
            file = new File( plugin.getDataFolder(), "IPs.yaml" );
            config = YamlConfiguration.loadConfiguration( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not load IPs.yml" );
        }
    }

    public void save() {
        try {
            config.save( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not save IPs.yml" );
        }
    }
}
