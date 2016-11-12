package fr.horgeon.groupmanagerapi.httphandlers;


import com.sun.net.httpserver.HttpExchange;
import fr.horgeon.apiserver.HTTPHandler;
import fr.horgeon.groupmanagerapi.hooks.pm.PMHook;
import org.apache.http.NameValuePair;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetGroupHandler extends HTTPHandler {
	private JavaPlugin plugin;
	private PMHook pm;

	public GetGroupHandler( String method, String encoding, Boolean auth, JavaPlugin plugin, PMHook pm ) {
		super( method, encoding, auth );
		this.plugin = plugin;
		this.pm = pm;
	}

	@Override
	public void handleRequest( HttpExchange t, List<NameValuePair> query ) throws IOException {
		String uuid = "";

		for( NameValuePair param : query ) {
			if( param.getName().equalsIgnoreCase( "uuid" ) )
				uuid = param.getValue();
		}

		if( uuid.equalsIgnoreCase( "" ) ) {
			responseJSON( t, 400, false, "No UUID provided." );
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
			Map<String, String> body = new HashMap<>();

			body.put( "success", "true" );
			body.put( "group", this.pm.getGroup( player ) );

			writeJSON( t, 200, body );
		} catch( Exception e ) {
			responseJSON( t, false, String.format( "Internal server error, couldn't set player's group: %s", e.getMessage() ) );
			e.printStackTrace();
		}
	}
}