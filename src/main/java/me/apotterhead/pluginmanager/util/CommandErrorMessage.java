// APotterhead
// 13062023-16072023

package me.apotterhead.pluginmanager.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum CommandErrorMessage {
    PERMISSION,
    INCOMPLETE,
    INCORRECT,
    UNKNOWN,
    EXTRA_ARGUMENT;

    public TextComponent send() {
        return send( "", new String[0] );
    }
    public TextComponent send( String label, String[] args ) {
        return send( label, args, args.length );
    }
    public TextComponent send( String label, String[] args, int errorArgument ) {
        return send( label, args, errorArgument, null );
    }
    public TextComponent send( String label, String[] args, int errorArgument, String unknownVariable ) {

        if( this == PERMISSION ) return Component.text( "Unknown command. Type \"/help\" for help." ).color( NamedTextColor.WHITE );
        if( this == UNKNOWN ) return Component.text( "Unknown " + unknownVariable + " '" + args[ errorArgument ] + "'").color( NamedTextColor.RED );
        TextComponent component = Component.text("");
        if( this == INCOMPLETE ) component = Component.text( "Unknown or incomplete command, see below for error" ).color( NamedTextColor.RED );
        else if( this == INCORRECT ) component = Component.text( "Incorrect argument for command" ).color( NamedTextColor.RED );
        else if( this == EXTRA_ARGUMENT ) component = Component.text( "Expected whitespace to end one argument, but found trailing data" ).color( NamedTextColor.RED );

        StringBuilder command = new StringBuilder( label );
        for( int i = 0; i < args.length && i < errorArgument; i++ ) command.append( " " ).append( args[i] );
        if( this == INCORRECT || this == EXTRA_ARGUMENT ) command.append( " " );
        if( command.length() > 10 ) command = new StringBuilder("..." + command.substring( command.length() - 10 ) );

        component = component.append( Component.text( "\n" + command ).color( NamedTextColor.GRAY ) );

        if( this == INCORRECT || this == EXTRA_ARGUMENT ) {
            StringBuilder error = new StringBuilder( args[errorArgument] );
            for( int i = errorArgument + 1; i < args.length; i++ ) error.append( " " ).append( args[i] );
            component = component.append( Component.text( error.toString() ).color( NamedTextColor.RED ).decorate( TextDecoration.UNDERLINED ) );
        }

        component = component.append( Component.text( "<--[HERE]").color( NamedTextColor.RED ) );

        return component;
    }
}
