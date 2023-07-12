// APotterhead
// 12062023-12072023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import java.util.HashMap;

public final class PluginManager extends JavaPlugin {

    public DisabledPluginsFile disabledPlugins;
    public GroupsFile groups;
    public PlayersFile players;
    public Map<Player, PermissionAttachment> permissions;
    @Override
    public void onEnable() {
        permissions = new HashMap<>();
        for( Player player : getServer().getOnlinePlayers() ) permissions.put( player, player.addAttachment( this ) );

        disabledPlugins = new DisabledPluginsFile( this );
        groups = new GroupsFile( this );
        players = new PlayersFile( this );

        PluginCommand pluginCommand = new PluginCommand( this );
        Objects.requireNonNull( getCommand( "plugin" ) ).setExecutor( pluginCommand );
        Objects.requireNonNull( getCommand( "plugin" ) ).setTabCompleter( pluginCommand );

        GroupCommand groupCommand = new GroupCommand( this );
        Objects.requireNonNull( getCommand( "group" ) ).setExecutor( groupCommand );
        Objects.requireNonNull( getCommand( "group" ) ).setTabCompleter( groupCommand );

        getServer().getPluginManager().registerEvents( new DisablePluginsOnLoad( this ), this );
    }

    public void onDisable() {
        for( Player player : permissions.keySet() ) player.removeAttachment( permissions.get( player ) );
    }

}
