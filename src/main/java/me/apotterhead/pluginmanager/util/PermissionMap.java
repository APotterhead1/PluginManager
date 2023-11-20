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

public class PermissionMap {
    public static class PermissionNode {
        public final Permission perm;
        public boolean neutral;
        public int HV;
        public boolean strong;

        public final Map<PermissionNode, Boolean> children;
        public final Map<PermissionNode, Boolean> parents;

        public PermissionNode( Permission perm ) {
            this.perm = perm;
            children = new HashMap<>();
            parents = new HashMap<>();
            neutral = true;
            HV = 0;
            strong = false;
        }

        private PermissionNode( Permission perm, Map<PermissionNode, Boolean> children, Map<PermissionNode, Boolean> parents ) {
            this.perm = perm;
            this.children = children;
            this.parents = parents;
            neutral = true;
        }

    }
    public PermissionNode root;
    public List<Permission> perms;
    private final PluginManager plugin;
    private Set<PermissionNode> nodes;
    private List<PermissionNode> topPerms;

    public PermissionMap( PluginManager plugin ) {
        this.plugin = plugin;

        Permission rootPerm = new Permission( "*", "Root of all Permissions", PermissionDefault.FALSE );
        plugin.getServer().getPluginManager().addPermission( rootPerm );

        loadMap();
    }

    public void loadMap() {
        perms = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
        perms.remove( plugin.getServer().getPluginManager().getPermission( "*" ) );
        nodes = new HashSet<>();

        topPerms = new ArrayList<>();

        while( !perms.isEmpty() ) {
            Permission perm = perms.get( 0 );

            topPerms.add( loadChildren( perm, new HashSet<>() ) );
        }

        root = new PermissionNode(plugin.getServer().getPluginManager().getPermission( "*" ) );

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
