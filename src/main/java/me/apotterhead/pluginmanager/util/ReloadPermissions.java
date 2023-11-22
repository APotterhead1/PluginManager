// APotterhead
// 02072023-20112023

package me.apotterhead.pluginmanager.util;

import me.apotterhead.pluginmanager.PluginManager;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import org.bukkit.permissions.Permissible;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import me.apotterhead.pluginmanager.util.PermissionMap.PermissionNode;
import java.util.List;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import java.util.UUID;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;

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

        List<String> playerTruePerms = plugin.players.config.getStringList( target + ".truePerms" );
        List<String> playerFalsePerms = plugin.players.config.getStringList( target + ".falsePerms" );

        Component reloadMessage = null;

        for( String perm : playerTruePerms ) {
            PermissionNode node = null;
            for( PermissionNode mapNode : permMap.nodes ) {
                if( mapNode.perm.getName().equals( perm ) ) {
                    node = mapNode;
                    break;
                }
            }
            assert node != null;

            Component tempRM = setPerms( node, true, perm, target );

            if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
            if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
        }

        for( String perm : playerFalsePerms ) {
            PermissionNode node = null;
            for( PermissionNode mapNode : permMap.nodes ) {
                if( mapNode.perm.getName().equals( perm ) ) {
                    node = mapNode;
                    break;
                }
            }
            assert node != null;

            Component tempRM = setPerms( node, false, perm, target );

            if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
            if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
        }

        for( String group : plugin.players.config.getStringList( target + ".groups" ) ) {
            List<String> groupTruePerms = plugin.groups.config.getStringList( "group." + group + ".truePerms" );
            List<String> groupFalsePerms = plugin.groups.config.getStringList( "group." + group + ".falsePerms" );

            for( String perm : groupTruePerms ) {
                PermissionNode node = null;
                for( PermissionNode mapNode : permMap.nodes ) {
                    if( mapNode.perm.getName().equals( perm ) ) {
                        node = mapNode;
                        break;
                    }
                }
                assert node != null;

                Component tempRM = setPerms( node, true, perm, target, plugin.players.config.getInt( "group." + group + ".hierarchyValue" ) );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );

            }

            for( String perm : groupFalsePerms ) {
                PermissionNode node = null;
                for( PermissionNode mapNode : permMap.nodes ) {
                    if( mapNode.perm.getName().equals( perm ) ) {
                        node = mapNode;
                        break;
                    }
                }
                assert node != null;

                Component tempRM = setPerms( node, false, perm, target, plugin.players.config.getInt( "group." + group + ".hierarchyValue" ) );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );

            }
        }

        for( PermissionNode node : permMap.nodes ) {
            if( !node.truePerms.isEmpty() || !node.falsePerms.isEmpty() ) continue;

            setPerms( node, node.perm.getDefault() == PermissionDefault.TRUE || node.perm.getDefault() == PermissionDefault.OP && plugin.getServer().getOfflinePlayer(UUID.fromString(target)).isOp() || node.perm.getDefault() == PermissionDefault.NOT_OP && !plugin.getServer().getOfflinePlayer(UUID.fromString(target)).isOp(), node.perm.getName(), target, 0 );
        }

        if( reloadMessage == null && plugin.getServer().getPlayer( UUID.fromString( target ) ) != null ) loadPerms( permMap.root, plugin.attachments.get( plugin.getServer().getPlayer( UUID.fromString( target ) ) ) );

        return reloadMessage;
    }

    private static @Nullable Component setPerms( PermissionNode node, boolean value, String source, String uuid, int HV ) {
        return setPerms( node, value, source, uuid, false, HV );
    }

    private static @Nullable Component setPerms( PermissionNode node, boolean value, String source, String uuid ) {
        return setPerms( node, value, source, uuid, true, 0 );
    }

    private static @Nullable Component setPerms( PermissionNode node, boolean value, String source, String uuid, boolean playerPerm, int HV ) {
        if( node.playerPerm && !playerPerm ) return null;
        if( !playerPerm && node.HV > HV && !( node.truePerms.isEmpty() && node.falsePerms.isEmpty() ) ) return null;

        Component reloadMessage = null;

        if( playerPerm && !node.playerPerm ) {
            node.truePerms.clear();
            node.falsePerms.clear();

            if( value ) node.truePerms.add( source );
            else node.falsePerms.add( source );

            node.playerPerm = true;

            for( PermissionNode child : node.children.keySet() ) {
                Component tempRM;

                if( node.children.get( child ) ) tempRM = setPerms( child, value, source, uuid );
                else tempRM = setPerms( child, !value, source, uuid );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
            }

            return reloadMessage;
        }

        if( playerPerm ) {
            if( value && !node.falsePerms.isEmpty() ) {
                for( String falsePerm : node.falsePerms ) {
                    Component nameComp = Component.text( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ) );
                    Component uuidComp = Component.text( uuid ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( uuid ) );
                    Component sourceComp = Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( source ) );
                    Component falsePermComp = Component.text( falsePerm ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( falsePerm ) );
                    if( reloadMessage == null ) reloadMessage = Component.text( "" ).color( NamedTextColor.RED );
                    else reloadMessage = reloadMessage.appendNewline();
                    reloadMessage = reloadMessage.append( Component.text( "There is a contradiction for " ) ).append( nameComp ).append( Component.text( "(" ) ).append( uuidComp ).append( Component.text( ") between the permissions '" ) ).append( sourceComp ).append( Component.text( "' and '" ) ).append( falsePermComp );
                }
            }

            if( !value && !node.truePerms.isEmpty() ) {
                for( String truePerm : node.truePerms ) {
                    Component nameComp = Component.text( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ) );
                    Component uuidComp = Component.text( uuid ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( uuid ) );
                    Component sourceComp = Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( source ) );
                    Component truePermComp = Component.text( truePerm ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( truePerm ) );
                    if( reloadMessage == null ) reloadMessage = Component.text( "" ).color( NamedTextColor.RED );
                    else reloadMessage = reloadMessage.appendNewline();
                    reloadMessage = reloadMessage.append( Component.text( "There is a contradiction for " ) ).append( nameComp ).append( Component.text( "(" ) ).append( uuidComp ).append( Component.text( ") between the permissions '" ) ).append( sourceComp ).append( Component.text( "' and '" ) ).append( truePermComp );
                }
            }

            if( value ) node.truePerms.add( source );
            else node.falsePerms.add( source );

            for( PermissionNode child : node.children.keySet() ) {
                Component tempRM;
                if( node.children.get( child ) ) tempRM = setPerms( child, value, source, uuid );
                else tempRM = setPerms( child, !value, source, uuid );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
            }

            return reloadMessage;
        }

        if( node.truePerms.isEmpty() && node.falsePerms.isEmpty() || node.HV < HV ) {
            node.truePerms.clear();
            node.falsePerms.clear();

            if( value ) node.truePerms.add( source );
            else node.falsePerms.add( source );

            for( PermissionNode child : node.children.keySet() ) {
                Component tempRM;

                if( node.children.get( child ) ) tempRM = setPerms( child, value, source, uuid, HV );
                else tempRM = setPerms( child, !value, source, uuid, HV );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
            }

            return reloadMessage;
        }

        if( node.HV == HV ) {
            if( value && !node.falsePerms.isEmpty() ) {
                for( String falsePerm : node.falsePerms ) {
                    Component nameComp = Component.text( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ) );
                    Component uuidComp = Component.text( uuid ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( uuid ) );
                    Component sourceComp = Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( source ) );
                    Component falsePermComp = Component.text( falsePerm ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( falsePerm ) );
                    if( reloadMessage == null ) reloadMessage = Component.text( "" ).color( NamedTextColor.RED );
                    else reloadMessage = reloadMessage.appendNewline();
                    reloadMessage = reloadMessage.append( Component.text( "There is a contradiction for " ) ).append( nameComp ).append( Component.text( "(" ) ).append( uuidComp ).append( Component.text( ") between the permissions '" ) ).append( sourceComp ).append( Component.text( "' and '" ) ).append( falsePermComp );
                }
            }

            if( !value && !node.truePerms.isEmpty() ) {
                for( String truePerm : node.truePerms ) {
                    Component nameComp = Component.text( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( Objects.requireNonNull( Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) ).getName() ) );
                    Component uuidComp = Component.text( uuid ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( uuid ) );
                    Component sourceComp = Component.text( source ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( source ) );
                    Component truePermComp = Component.text( truePerm ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.copyToClipboard( truePerm ) );
                    if( reloadMessage == null ) reloadMessage = Component.text( "" ).color( NamedTextColor.RED );
                    else reloadMessage = reloadMessage.appendNewline();
                    reloadMessage = reloadMessage.append( Component.text( "There is a contradiction for " ) ).append( nameComp ).append( Component.text( "(" ) ).append( uuidComp ).append( Component.text( ") between the permissions '" ) ).append( sourceComp ).append( Component.text( "' and '" ) ).append( truePermComp );
                }
            }

            if( value ) node.truePerms.add( source );
            else node.falsePerms.add( source );

            for( PermissionNode child : node.children.keySet() ) {
                Component tempRM;
                if( node.children.get( child ) ) tempRM = setPerms( child, value, source, uuid );
                else tempRM = setPerms( child, !value, source, uuid );

                if( reloadMessage == null && tempRM != null ) reloadMessage = tempRM;
                if( reloadMessage != null && tempRM != null ) reloadMessage = reloadMessage.appendNewline().append( tempRM );
            }

            return reloadMessage;
        }

        return reloadMessage;
    }

    private static void loadPerms( PermissionNode node, PermissionAttachment attachment ) {
        if( node.falsePerms.isEmpty() ) attachment.setPermission( node.perm, true );
        else attachment.unsetPermission( node.perm );

        for( PermissionNode child : node.children.keySet() ) loadPerms( child, attachment );
    }
}
