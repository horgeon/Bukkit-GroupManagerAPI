package fr.horgeon.groupmanagerapi;

import fr.horgeon.groupmanagerapi.hooks.api.ProdriversAPIHook;
import fr.horgeon.groupmanagerapi.hooks.pm.PMHook;
import fr.horgeon.groupmanagerapi.hooks.pm.groupmanager.GroupManagerHook;
import fr.horgeon.groupmanagerapi.httphandlers.GetGroupHandler;
import fr.horgeon.groupmanagerapi.httphandlers.ReloadHandler;
import fr.horgeon.groupmanagerapi.httphandlers.SetGroupHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {
	private Chat chat;
	private Configuration config;
	private ProdriversAPIHook papi;
	private PMHook pm;

	private final Logger logger = Logger.getLogger( "Minecraft" );

	@Override
	public void onDisable() {
		PluginDescriptionFile plugindescription = this.getDescription();
		this.logger.info( plugindescription.getName() + " has been disabled!" );
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents( this, this );

		PluginDescriptionFile plugindescription = this.getDescription();
		this.chat = new Chat( plugindescription.getName() );
		this.config = new Configuration( this );

		getCommand( "groupmanagerapi" ).setExecutor( this );

		this.logger.info( plugindescription.getName() + " has been enabled!" );

		hookPM();
		hookProdriversAPI();
		registerHandlers();
	}

	private void hookPM() {
		this.pm = new GroupManagerHook( this );
	}

	private void hookProdriversAPI() {
		this.papi = new ProdriversAPIHook( this );
	}

	private void registerHandlers() {
		if( this.config.getIsHandlerActivated( "/getgroup" ) )
			this.papi.registerHandler( "/getgroup", new GetGroupHandler( "GET", "UTF-8", false, this, pm ) );
		if( this.config.getIsHandlerActivated( "/setgroup" ) )
			this.papi.registerHandler( "/setgroup", new SetGroupHandler( "POST", "UTF-8", true, this, pm ) );
		if( this.config.getIsHandlerActivated( "/reload" ) )
			this.papi.registerHandler( "/reload", new ReloadHandler( "GET", "UTF-8", true, this, pm ) );
	}

	private void unregisterHandlers() {
		this.papi.unregisterHandler( "/getgroup" );
		this.papi.unregisterHandler( "/setgroup" );
		this.papi.unregisterHandler( "/reload" );
	}

	private void reloadCommand( CommandSender sender ) {
		if( sender.hasPermission( "groupmanagerapi.reload" ) ) {
			this.config.reload();
			unregisterHandlers();
			registerHandlers();

			chat.success( sender, this.config.getMessage( "configurationreloaded" ) );
		} else {
			chat.error( sender, this.config.getMessage( "nopermission" ) );
		}
	}

	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if( label.equalsIgnoreCase( "groupmanagerapi" ) ) {
			if( args.length > 0 ) {
				switch( args[ 0 ] ) {
					case "reload":
						reloadCommand( sender );
						break;
				}
			} else {
				chat.send( sender, "Usage:" );
				chat.send( sender, "/groupmanagerapi <reload>" );
			}

			return true;
		}

		return false;
	}

}
