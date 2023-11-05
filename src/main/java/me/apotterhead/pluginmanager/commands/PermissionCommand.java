// APotterhead
// 22072023-31072023

package me.apotterhead.pluginmanager.commands;

import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.List;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import me.apotterhead.pluginmanager.util.*;
import me.apotterhead.pluginmanager.PluginManager;
import me.apotterhead.pluginmanager.util.ReloadPermissions.ReloadType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.logging.Level;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.permissions.Permission;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import java.util.Collections;

public class PermissionCommand implements TabExecutor {

    private final PluginManager plugin;

    public PermissionCommand( PluginManager plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        if( args.length == 0 ) {
            sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
            return true;
        }

        if( args[0].equals( "setNegative" ) ) {
            if( !sender.hasPermission( "appm.commands.permission.setNegative.true" ) && !sender.hasPermission( "appm.commands.permission.setNegative.false" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length < 3 ) {
                sender.sendMessage( CommandErrorMessage.INCOMPLETE.send( label, args ) );
                return true;
            }

            if( !( args[2].equals( "true" ) && sender.hasPermission( "appm.commands.permission.setNegative.true" ) ) && !( args[2].equals( "false" ) && sender.hasPermission( "appm.commands.permission.setNegative.false" ) ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 2 ) );
                return true;
            }

            if( args.length == 3 ) {
                if( plugin.getServer().getPluginManager().getPermission( args[1] ) == null ) {
                    sender.sendMessage( CommandErrorMessage.UNKNOWN.send( label, args, 1, "permission" ) );
                    return true;
                }

                String permPath = args[1].replace( '.', ',' );

                boolean originalValue = plugin.permissions.config.getBoolean( permPath + ".negative" );

                plugin.permissions.config.set( permPath + ".negative", Boolean.valueOf( args[2] ) );
                plugin.permissions.save();

                Component reloadMessage = ReloadPermissions.reload( ReloadType.PERMISSION, args[1], plugin );

                if( reloadMessage != null ) {
                    sender.sendMessage( reloadMessage );
                    plugin.getLogger().log( Level.WARNING, ( (TextComponent) reloadMessage ).content() );

                    plugin.permissions.config.set( permPath + ".negative", originalValue );
                    plugin.permissions.save();

                    ReloadPermissions.reload( ReloadType.PERMISSION, args[1], plugin );
                    return true;
                }

                sender.sendMessage( Component.text( "Permission '" + args[1] + "' has been set to " + ( Boolean.parseBoolean( args[2] ) ? "negative" : "positive" ) ).color( NamedTextColor.GREEN ) );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 3 ) );
            return true;
        }

        if( args[0].equals( "list" ) ) {
            if( !sender.hasPermission( "appm.commands.permission.list" ) ) {
                sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
                return true;
            }

            if( args.length == 1 ) {
                Component message = Component.text( "" ).append( Component.text( "Permissions:" ).color( NamedTextColor.GOLD ) ).appendNewline();

                List<Permission> permissions = new ArrayList<>( plugin.getServer().getPluginManager().getPermissions() );
                List<String> perms = new ArrayList<>();
                for( Permission perm : permissions ) perms.add( perm.getName() );
                Collections.sort( perms );

                if( sender.hasPermission( "appm.commands.permission.get.permission" ) ) {
                    message = message.append( Component.text( "[" + perms.get( 0 ) + "]" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get permission " + perms.get( 0 ) ) ) );
                    for( int i = 1; i < perms.size(); i++ ) message = message.append( Component.text( ", " ) ).append( Component.text( "[" + perms.get( i ) + "]" ).decorate( TextDecoration.UNDERLINED ).clickEvent( ClickEvent.runCommand( "/permission get " + perms.get( i ) ) ) );
                } else {
                    message = message.append( Component.text( "[" + perms.get( 0 ) + "]" ) );
                    for( int i = 1; i < perms.size(); i++ ) message = message.append( Component.text( ", [" + perms.get( 0 ) + "]" ) );
                }

                sender.sendMessage( message );
                return true;
            }

            sender.sendMessage( CommandErrorMessage.EXTRA_ARGUMENT.send( label, args, 2 ) );
            return true;
        }

        sender.sendMessage( CommandErrorMessage.INCORRECT.send( label, args, 0 ) );
        return true;
    }

    public List<String> onTabComplete( @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args ) {
        return new ArrayList<>();
    }
}
