package fr.horgeon.groupmanagerapi.hooks.pm.groupmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.UUID;

public class GroupManagerHookListener implements Listener {
	private GroupManagerHook gmhook;

	public GroupManagerHookListener( GroupManagerHook gmhook ) {
		this.gmhook = gmhook;
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onPluginEnable( final PluginEnableEvent event ) {
		this.gmhook.loadGM();
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onPluginDisable( PluginDisableEvent event ) {
		this.gmhook.getConfig().save();

		if( this.gmhook.isGMloaded() ) {
			if( event.getPlugin().getDescription().getName().equals( "GroupManager" ) ) {
				this.gmhook.forceUnloadGM();
				System.out.println( "[GroupManagerAPI] GroupManager unhooked." );
			}
		}
	}

	@EventHandler( priority = EventPriority.HIGHEST )
	public void onPlayerJoin( PlayerJoinEvent event ) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if( this.gmhook.getConfig().playersToUpdate.containsKey( uuid ) ) {
			String group = this.gmhook.getConfig().playersToUpdate.get( uuid );
			this.gmhook.getConfig().playersToUpdate.remove( uuid );

			try {
				this.gmhook.setGroup( player, group );
			} catch( Exception e ) {
				System.err.println( String.format( "Couldn't update player %s's group to %s!", player.getName(), group ) );
				e.printStackTrace();
			}
		}
	}
}
