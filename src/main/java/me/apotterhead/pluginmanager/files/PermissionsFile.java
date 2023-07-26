// APotterhead
// 25072023-25072023

package me.apotterhead.pluginmanager.files;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.logging.Level;
import me.apotterhead.pluginmanager.PluginManager;

public class PermissionsFile {

    public YamlConfiguration config;
    private File file;
    private final PluginManager plugin;

    public PermissionsFile( PluginManager plugin ) {
        this.plugin = plugin;
        try {
            file = new File( plugin.getDataFolder(), "permission.yml" );
            config = YamlConfiguration.loadConfiguration( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not load permissions.yml" );
        }
    }

    public void save() {
        try {
            config.save( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not save permissions.yml" );
        }
    }
}
