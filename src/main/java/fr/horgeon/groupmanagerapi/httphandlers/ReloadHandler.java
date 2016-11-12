package fr.horgeon.groupmanagerapi.httphandlers;


import com.sun.net.httpserver.HttpExchange;
import fr.horgeon.apiserver.HTTPHandler;
import fr.horgeon.groupmanagerapi.hooks.pm.PMHook;
import org.apache.http.NameValuePair;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class ReloadHandler extends HTTPHandler {
	private JavaPlugin plugin;
	private PMHook pm;

	public ReloadHandler( String method, String encoding, Boolean auth, JavaPlugin plugin, PMHook pm ) {
		super( method, encoding, auth );
		this.plugin = plugin;
		this.pm = pm;
	}

	@Override
	public void handleRequest( HttpExchange t, List<NameValuePair> query ) throws IOException {
		try {
			this.pm.reload();
			responseJSON( t, true, "Permissions reloaded." );
		} catch( Exception e ) {
			responseJSON( t, false, String.format( "Internal server error, couldn't reload permissions: %s", e.getMessage() ) );
			e.printStackTrace();
		}
	}
}