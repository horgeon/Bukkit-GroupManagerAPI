package fr.horgeon.groupmanagerapi.hooks.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class ProdriversAPIHookListener implements Listener {
	private ProdriversAPIHook papihook;

	public ProdriversAPIHookListener( ProdriversAPIHook gmhook ) {
		this.papihook = gmhook;
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onPluginEnable( final PluginEnableEvent event ) {
		this.papihook.loadPAPI();
		this.papihook.registerStagedHandlers();
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onPluginDisable( PluginDisableEvent event ) {
		if( this.papihook.isPAPIloaded() ) {
			if( event.getPlugin().getDescription().getName().equals( "ProdriversAPI" ) ) {
				this.papihook.forceUnloadPAPI();
				System.out.println( "[GroupManagerAPI] ProdriversAPI unhooked." );
			}
		}
	}
}
