// APotterhead
// 12062023-16072023

package me.apotterhead.pluginmanager;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import java.util.HashMap;
import java.util.List;

public final class PluginManager extends JavaPlugin {

    public DisabledPluginsFile disabledPlugins;
    public GroupsFile groups;
    public PlayersFile players;
    public IPsFile ips;
    public Map<Player, PermissionAttachment> attachments;
    @Override
    public void onEnable() {
        disabledPlugins = new DisabledPluginsFile( this );
        groups = new GroupsFile( this );
        players = new PlayersFile( this );
        ips = new IPsFile( this );

        attachments = new HashMap<>();

        for( Player player : getServer().getOnlinePlayers() ) {
            attachments.put( player, player.addAttachment( this ) );

            List<String> currentPlayers = ips.config.getStringList( "ip." + players.config.getString( player.getUniqueId() + ".lastIP" ) + ".currentPlayers" );
            currentPlayers.add( player.getUniqueId().toString() );
            ips.config.set( "ip." + players.config.getString( player.getUniqueId() + ".lastIP" ) + ".currentPlayers", currentPlayers );
            ips.save();
        }

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
        getServer().getPluginManager().registerEvents( new UpdatePlayerInfoOnLogin( this ), this );
        getServer().getPluginManager().registerEvents( new UpdatePlayerInfoOnQuit( this ), this );
    }

    public void onDisable() {
        for( Player player : attachments.keySet() ) player.removeAttachment( attachments.get( player ) );
        attachments.clear();

        for( String ip : ips.config.getStringList( "ips" ) ) ips.config.set( "ip." + ip + ".currentPlayers", null );
        ips.save();
    }

}
