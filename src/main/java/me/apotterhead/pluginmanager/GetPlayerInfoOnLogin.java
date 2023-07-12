// APotterhead
// 12072023-12072023

package me.apotterhead.pluginmanager;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import me.apotterhead.pluginmanager.ReloadPermissions.ReloadType;
import java.util.Objects;
import java.util.List;

public class GetPlayerInfoOnLogin implements Listener {

    private final PluginManager plugin;

    public GetPlayerInfoOnLogin(PluginManager plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin( PlayerLoginEvent event ) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        plugin.permissions.put( player, player.addAttachment( plugin ) );
        ReloadPermissions.reload( ReloadType.PLAYER, uuid, plugin );

        if( plugin.players.config.contains( uuid + ".lastName" ) && !Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastName" ) ).equals( player.getName() ) ) {
            List<String> names = plugin.players.config.getStringList( uuid + ".pastNames" );
            names.add( plugin.players.config.getString( uuid + ".lastName" ) );
            plugin.players.config.set( uuid + ".pastNames", names );
            plugin.players.save();
        }
        plugin.players.config.set( uuid + ".lastName", player.getName() );
        plugin.players.save();

        String currentIP = event.getRealAddress().getAddress()[0] + "." + event.getRealAddress().getAddress()[1] + "." + event.getRealAddress().getAddress()[2] + "." + event.getRealAddress().getAddress()[3];
        if( plugin.players.config.contains( uuid + ".lastIP" ) && !Objects.requireNonNull( plugin.players.config.getString( uuid + ".lastIP" ) ).equals( currentIP ) ) {
            List<String> ips = plugin.players.config.getStringList( uuid + ".pastIPs" );
            ips.add( plugin.players.config.getString( uuid + ".lastIP" ) );
            plugin.players.config.set( uuid + ".pastIPs", ips );
            plugin.players.save();
        }
        plugin.players.config.set( uuid + ".lastIP", currentIP );
        plugin.players.save();
    }
}
