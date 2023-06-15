// APotterhead
// 13062023-14062023

package me.apotterhead.pluginmanager;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum CommandErrorMessage {
    PERMISSION,
    INCOMPLETE,
    INCORRECT,
    UNKNOWN;

    public TextComponent getMessage( String label, String[] args ) {
        return getMessage( label, args, args.length );
    }
    public TextComponent getMessage( String label, String[] args, int errorArgument ) {
        return getMessage( label, args, errorArgument, null );
    }
    public TextComponent getMessage( String label, String[] args, int errorArgument, String unknownVariable ) {

        if( this == PERMISSION ) return Component.text( "Unknown command. Type \"/help\" for help." );
        if( this == UNKNOWN ) return Component.text( "Unknown " + unknownVariable + " '" + args[ errorArgument ] + "'").color( NamedTextColor.RED );
        TextComponent component;
        if( this == INCOMPLETE ) component = Component.text( "Unknown or incomplete command, see below for error" ).color( NamedTextColor.RED );
        else if( this == INCORRECT ) component = Component.text( "Incorrect argument for command" ).color( NamedTextColor.RED );
        else return null;

        String command = label;
        for( int i = 0; i < args.length && i < errorArgument; i++ ) command += " " + args[i];
        if( this == INCORRECT ) command += " ";
        if( command.length() > 10 ) command = "..." + command.substring( command.length() - 10 );

        component.append( Component.text( command ).color( NamedTextColor.GRAY ) );

        if( this == INCORRECT ) {
            String error = args[errorArgument];
            for( int i = errorArgument + 1; i < args.length; i++ ) error += " " + args[i];
            component.append( Component.text( error ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ) );
        }

        component.append( Component.text( "<--[HERE]").color( NamedTextColor.RED ) );

        return component;
    }
}
