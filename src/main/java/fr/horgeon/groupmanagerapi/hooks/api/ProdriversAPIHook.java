package fr.horgeon.groupmanagerapi.hooks.api;

import fr.horgeon.apiserver.HTTPHandler;
import fr.horgeon.prodrivers.api.ProdriversAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ProdriversAPIHook {
	private JavaPlugin plugin;
	private ProdriversAPI prodriversAPI;
	private Map<String, HTTPHandler> handlers;

	public ProdriversAPIHook( JavaPlugin plugin ) {
		this.plugin = plugin;
		this.handlers = new HashMap<>();

		this.plugin.getServer().getPluginManager().registerEvents( new ProdriversAPIHookListener( this ), this.plugin );
	}

	public boolean isPAPIloaded() {
		return this.prodriversAPI != null;
	}

	public void forceUnloadPAPI() {
		for( Map.Entry<String, HTTPHandler> entry: this.handlers.entrySet() ) {
			this.prodriversAPI.unregisterHandler( entry.getKey() );
			System.out.println( "[GroupManagerAPI] Unregistering handler: " + entry.getKey() );
		}

		System.out.println( "[GroupManagerAPI] Handlers unregistered." );

		this.prodriversAPI = null;
		System.out.println( "[GroupManagerAPI] ProdriversAPI unhooked." );
	}

	public void loadPAPI() {
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		final Plugin ProdriversAPIplugin = pluginManager.getPlugin( "ProdriversAPI" );

		if( ProdriversAPIplugin != null && ProdriversAPIplugin.isEnabled() ) {
			this.prodriversAPI = ( ProdriversAPI ) ProdriversAPIplugin;
			System.out.println( "[GroupManagerAPI] ProdriversAPI hooked." );
		}
	}

	public void registerStagedHandlers() throws NullPointerException {
		if( this.prodriversAPI != null ) {
			for( Map.Entry<String, HTTPHandler> entry : this.handlers.entrySet() ) {
				System.out.println( "[GroupManagerAPI] Registering handler: " + entry.getKey() );
				this.prodriversAPI.registerHandler( entry.getKey(), entry.getValue() );
			}

			System.out.println( "[GroupManagerAPI] Handlers registered." );
		} else {
			throw new NullPointerException( "Handler registration failed! Invalid ProdriversAPI hook." );
		}
	}

	public void registerHandler( String endpoint, HTTPHandler handler ) {
		this.handlers.put( endpoint, handler );
	}

	public void unregisterHandler( String endpoint ) {
		this.prodriversAPI.unregisterHandler( endpoint );
	}
}
