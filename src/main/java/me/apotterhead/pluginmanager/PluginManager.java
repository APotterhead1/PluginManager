// APotterhead
// 12062023-22062023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class PluginManager extends JavaPlugin {

    public DisabledPluginsFile disabledPlugins;
    public GroupsFile groups;
    @Override
    public void onEnable() {
        disabledPlugins = new DisabledPluginsFile( this );
        groups = new GroupsFile( this );

        PluginCommand pluginCommand = new PluginCommand( this );
        Objects.requireNonNull( getCommand( "plugin" ) ).setExecutor( pluginCommand );
        Objects.requireNonNull( getCommand( "plugin" ) ).setTabCompleter( pluginCommand );

        GroupCommand groupCommand = new GroupCommand( this );
        Objects.requireNonNull( getCommand( "group" ) ).setExecutor( groupCommand );
        Objects.requireNonNull( getCommand( "group" ) ).setTabCompleter( groupCommand );

        getServer().getPluginManager().registerEvents( new DisablePluginsOnLoad( this ), this );
    }

}
