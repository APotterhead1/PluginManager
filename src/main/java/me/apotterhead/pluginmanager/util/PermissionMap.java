// APotterhead
// 19112023-19112023

package me.apotterhead.pluginmanager.util;

import org.bukkit.permissions.Permission;
import me.apotterhead.pluginmanager.PluginManager;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.permissions.PermissionDefault;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import net.kyori.adventure.util.TriState;

public class PermissionMap {
    public static class PermissionNode {
        public final Permission perm;
        public TriState value;
        public int HV;
        public boolean playerPerm;

        public final Map<PermissionNode, Boolean> children;
        public final Map<PermissionNode, Boolean> parents;

        public PermissionNode( Permission perm ) {
            this.perm = perm;
            children = new HashMap<>();
            parents = new HashMap<>();
            value = TriState.NOT_SET;
            HV = 0;
            playerPerm = false;
        }

    }
    public PermissionNode root;
    public List<Permission> perms;
    private final PluginManager plugin;
    public Set<PermissionNode> nodes;
    private final List<PermissionNode> topPerms;

    public PermissionMap( PluginManager plugin ) {
        this.plugin = plugin;

        Permission rootPerm = new Permission( "*", "Root of all Permissions", PermissionDefault.FALSE );
        plugin.getServer().getPluginManager().addPermission( rootPerm );

        perms = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
        perms.remove( plugin.getServer().getPluginManager().getPermission( "*" ) );
        nodes = new HashSet<>();
        topPerms = new ArrayList<>();

        while( !perms.isEmpty() ) {
            Permission perm = perms.get( 0 );

            topPerms.add( loadChildren( perm, new HashSet<>() ) );
        }

        root = new PermissionNode(plugin.getServer().getPluginManager().getPermission( "*" ) );
        nodes.add( root );

        root.perm.getChildren().clear();

        for( PermissionNode topPerm : topPerms ) {
            topPerm.perm.addParent( root.perm, true );
            root.children.put( topPerm, true );
            topPerm.parents.put( root, true );
        }

        perms = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
    }

    private PermissionNode loadChildren( Permission perm, Set<Permission> circlePerms ) {
        circlePerms.add( perm );

        perms.remove( perm );
        PermissionNode permNode = new PermissionNode( perm );
        nodes.add( permNode );

        for ( String childStr : perm.getChildren().keySet() ) {
            Permission child = plugin.getServer().getPluginManager().getPermission( childStr );
            assert child != null;

            if ( circlePerms.contains( child ) ) {
                plugin.getLogger().log( Level.SEVERE, "PluginManager can't map out permissions due to permission '" + child.getName() + "'" );
                plugin.getServer().getPluginManager().disablePlugin( plugin );
                return null;
            }

            PermissionNode childNode = null;

            if ( !perms.contains( child ) ) {
                for ( PermissionNode node : nodes ) {
                    if ( node.perm.getName().equals( child.getName() ) ) {
                        childNode = node;
                        topPerms.remove( node );
                        break;
                    }
                }
            } else {
                childNode = loadChildren( child, circlePerms );
            }

            assert childNode != null;
            childNode.parents.put( permNode, perm.getChildren().get( childStr ) );
            permNode.children.put( childNode, perm.getChildren().get( childStr ) );
        }

        return permNode;
    }
}
