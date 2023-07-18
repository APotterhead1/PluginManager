// APotterhead
// 02072023-18072023

package me.apotterhead.pluginmanager.files;

import java.io.File;

import me.apotterhead.pluginmanager.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.logging.Level;

public class PlayersFile {

    private final PluginManager plugin;
    private File file;
    public YamlConfiguration config;

    public PlayersFile( PluginManager plugin ) {
        this.plugin = plugin;
        try{
            file = new File( plugin.getDataFolder(), "players.yml" );
            config = YamlConfiguration.loadConfiguration( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not load players.yml" );
        }
    }

    public void save() {
        try{
            config.save( file );
        } catch( Exception e ) {
            plugin.getLogger().log( Level.SEVERE, "Could not save players.yml" );
        }
        config = YamlConfiguration.loadConfiguration( file );
    }
}
