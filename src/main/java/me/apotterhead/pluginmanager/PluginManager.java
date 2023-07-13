// APotterhead
// 12062023-13072023

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
    public Map<Player, PermissionAttachment> attachments;
    @Override
    public void onEnable() {
        attachments = new HashMap<>();
        for( Player player : getServer().getOnlinePlayers() ) attachments.put( player, player.addAttachment( this ) );

        disabledPlugins = new DisabledPluginsFile( this );
        groups = new GroupsFile( this );
        players = new PlayersFile( this );

        PluginCommand pluginCommand = new PluginCommand( this );
        Objects.requireNonNull( getCommand( "plugin" ) ).setExecutor( pluginCommand );
        Objects.requireNonNull( getCommand( "plugin" ) ).setTabCompleter( pluginCommand );

        GroupCommand groupCommand = new GroupCommand( this );
        Objects.requireNonNull( getCommand( "group" ) ).setExecutor( groupCommand );
        Objects.requireNonNull( getCommand( "group" ) ).setTabCompleter( groupCommand );

        PlayerCommand playerCommand = new PlayerCommand( this );
        Objects.requireNonNull( getCommand( "player" ) ).setExecutor( playerCommand );
        Objects.requireNonNull( getCommand( "player" ) ).setTabCompleter( playerCommand );

        getServer().getPluginManager().registerEvents( new DisablePluginsOnLoad( this ), this );
        getServer().getPluginManager().registerEvents( new GetPlayerInfoOnLogin( this ), this );
        getServer().getPluginManager().registerEvents( new RemoveAttachmentOnQuit( this ), this );
    }

    public void onDisable() {
        for( Player player : attachments.keySet() ) player.removeAttachment( attachments.get( player ) );
        attachments.clear();
    }

}
