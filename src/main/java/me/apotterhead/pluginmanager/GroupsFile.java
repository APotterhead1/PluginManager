// APotterhead
// 23062023-23062023

package me.apotterhead.pluginmanager;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.logging.Level;

public class GroupsFile {

    private final PluginManager plugin;
    private File file;
    public YamlConfiguration config;

    public GroupsFile( PluginManager plugin ) {
        this.plugin = plugin;
        try {
            file = new File( plugin.getDataFolder(), "groups.yml" );
            config = YamlConfiguration.loadConfiguration( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not load groups.yml" );
        }
    }

    public void save() {
        try {
            config.save( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not save groups.yml" );
        }
    }
}
