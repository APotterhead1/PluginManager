// APotterhead
// 02072023-19112023

package me.apotterhead.pluginmanager.util;

import me.apotterhead.pluginmanager.PluginManager;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import org.bukkit.permissions.Permissible;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import me.apotterhead.pluginmanager.util.PermissionMap.PermissionNode;
import net.kyori.adventure.util.TriState;
import java.util.List;

public class ReloadPermissions {

    public enum ReloadType {
        PLAYER,
        GROUP,
        PERMISSION
    }

    public static @Nullable Component reload( ReloadType reloadType, String target, PluginManager plugin  ) {
        if( reloadType == ReloadType.PERMISSION ) {
            Component reloadMessage = null;
            for( Permissible permissible : Objects.requireNonNull( plugin.getServer().getPluginManager().getPermission( target ) ).getPermissibles() ) {
                if( permissible instanceof OfflinePlayer ) reloadMessage = reload( ReloadType.PLAYER, ( (OfflinePlayer) permissible ).getUniqueId().toString(), plugin );
            }
            return reloadMessage;
        }

        if( reloadType == ReloadType.GROUP ) {
            Component reloadMessage = null;
            for( String uuid : plugin.groups.config.getStringList( "group." + target + ".players" ) ) reloadMessage = reload( ReloadType.PLAYER, uuid, plugin );
            return reloadMessage;
        }

        PermissionMap permMap = new PermissionMap( plugin );

        List<String> truePerms = plugin.players.config.getStringList( target + ".truePerms" );
        List<String> falsePerms = plugin.players.config.getStringList( target + ".falsePerms" );

        Component reloadMessage = null;

        for( String perm : truePerms ) {
            PermissionNode node = null;
            for( PermissionNode mapNode : permMap.nodes ) {
                if( mapNode.perm.getName().equals( perm ) ) {
                    node = mapNode;
                    break;
                }
            }
            assert node != null;

           if( node.value == TriState.FALSE ) {

           }


        }
    }

    private static Component setPerms( PermissionNode node, TriState value ) {
        return setPerms( node, value, false );
    }

    private static Component setPerms( PermissionNode node, TriState value, boolean playerPerm ) {

    }
}
