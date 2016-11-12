package fr.horgeon.groupmanagerapi.hooks.pm.groupmanager;

import fr.horgeon.groupmanagerapi.hooks.pm.PMHook;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupManagerHook implements PMHook {
	private GroupManager groupManager;
	private JavaPlugin plugin;

	private GroupManagerHookConfiguration config;
	private GroupManagerHookListener listener;

	public GroupManagerHook( JavaPlugin plugin ) {
		this.plugin = plugin;

		this.config = new GroupManagerHookConfiguration( this.plugin );

		this.plugin.getServer().getPluginManager().registerEvents( new GroupManagerHookListener( this ), this.plugin );
	}

	public void loadGM() {
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin( "GroupManager" );

		if( GMplugin != null && GMplugin.isEnabled() ) {
			this.groupManager = ( GroupManager ) GMplugin;
			System.out.println( "[GroupManagerAPI] GroupManager hooked." );
		}
	}

	public boolean isGMloaded() {
		return this.groupManager != null;
	}

	public void forceUnloadGM() {
		this.groupManager = null;
	}

	public User getUser( OfflinePlayer player ) {
		WorldsHolder holder = this.groupManager.getWorldsHolder();
		if( holder == null ) {
			throw new NullPointerException( "Couldn't get world holder!" );
		}

		OverloadedWorldHolder handler = holder.getDefaultWorld();
		if( handler == null ) {
			throw new NullPointerException( "Couldn't get player handler!" );
		}

		return handler.getUser( player.getUniqueId().toString() );
	}

	public Group getGroup( String group ) {
		WorldsHolder holder = this.groupManager.getWorldsHolder();
		if( holder == null ) {
			throw new NullPointerException( "Couldn't get world holder!" );
		}

		OverloadedWorldHolder handler = holder.getDefaultWorld();
		if( handler == null ) {
			throw new NullPointerException( "Couldn't get player handler!" );
		}

		return handler.getGroup( group );
	}

	public GroupManagerHookConfiguration getConfig() {
		return this.config;
	}

	@Override
	public String getGroup( OfflinePlayer player ) throws Exception {
		if( this.groupManager == null ) {
			throw new NullPointerException( "GroupManager not hooked!" );
		}

		User user = getUser( player );

		if( user == null )
			throw new NullPointerException( "Invalid user." );

		return user.getGroup().getName();
	}

	@Override
	public void setGroup( OfflinePlayer player, String groupName ) throws Exception {
		if( this.groupManager == null ) {
			throw new NullPointerException( "GroupManager not hooked!" );
		}

		User user = getUser( player );
		Group group = getGroup( groupName );

		if( user == null )
			throw new NullPointerException( "Invalid user." );

		if( group == null )
			throw new NullPointerException( "Invalid group." );

		if( player.getPlayer() != null ) {
			user.setGroup( group );
		} else {
			// Since GroupManager can't update offline player's group, we program the group update for the next time the player logs in.
			this.config.playersToUpdate.put( player.getUniqueId(), group.getName() );
			System.out.println( String.format( "Group update for player %s to %s programmed for next login.", player.getName(), group.getName() ) );
		}
	}

	@Override
	public void reload() throws Exception {
		if( this.groupManager == null ) {
			throw new NullPointerException( "GroupManager not hooked!" );
		}

		WorldsHolder holder = groupManager.getWorldsHolder();
		if( holder == null ) {
			throw new NullPointerException( "Couldn't get world holder!" );
		}

		holder.reloadAll();
	}
}
