package fr.horgeon.groupmanagerapi.httphandlers;


import com.sun.net.httpserver.HttpExchange;
import fr.horgeon.apiserver.HTTPHandler;
import fr.horgeon.groupmanagerapi.hooks.pm.PMHook;
import org.apache.http.NameValuePair;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class SetGroupHandler extends HTTPHandler {
	private JavaPlugin plugin;
	private PMHook pm;

	public SetGroupHandler( String method, String encoding, Boolean auth, JavaPlugin plugin, PMHook pm ) {
		super( method, encoding, auth );
		this.plugin = plugin;
		this.pm = pm;
	}

	@Override
	public void handleRequest( HttpExchange t, List<NameValuePair> query ) throws IOException {
		String uuid = "";
		String group = "";

		for( NameValuePair param : query ) {
			if( param.getName().equalsIgnoreCase( "uuid" ) )
				uuid = param.getValue();
			else if( param.getName().equalsIgnoreCase( "group" ) )
				group = param.getValue();
		}

		if( uuid.equalsIgnoreCase( "" ) ) {
			responseJSON( t, 400, false, "No UUID provided." );
			return;
		}

		if( group.equalsIgnoreCase( "" ) ) {
			responseJSON( t, 400, false, "No group provided." );
			return;
		}

		OfflinePlayer player = null;

		try {
			player = ( OfflinePlayer ) this.plugin.getServer().getPlayer( UUID.fromString( uuid ) );
			if( player == null )
				player = this.plugin.getServer().getOfflinePlayer( UUID.fromString( uuid ) );
		} catch( IllegalArgumentException e ) {
			responseJSON( t, 400, false, "Invalid UUID provided." );
			return;
		} catch( Exception e ) {
			responseJSON( t, false, String.format( "Internal server error, couldn't get player: %s", e.getMessage() ) );
			return;
		}

		if( player == null ) {
			responseJSON( t, 400, false, "UUID doesn't correspond to a known online player on the server." );
			return;
		}

		try {
			this.pm.setGroup( player, group );
			responseJSON( t, true, "Player's group changed." );
		} catch( Exception e ) {
			responseJSON( t, false, String.format( "Internal server error, couldn't set player's group: %s", e.getMessage() ) );
			e.printStackTrace();
		}
	}
}