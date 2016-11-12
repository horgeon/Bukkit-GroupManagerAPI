package fr.horgeon.groupmanagerapi.hooks.pm.groupmanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class GroupManagerHookConfiguration {
	JavaPlugin plugin;
	YamlConfiguration config;

	Map<UUID, String> playersToUpdate;

	public GroupManagerHookConfiguration( JavaPlugin plugin ) {
		this.plugin = plugin;
		this.playersToUpdate = new HashMap<UUID, String>();
		load();
	}

	private void copy( InputStream in, File file ) {
		try {
			OutputStream out = new FileOutputStream( file );
			byte[] buf = new byte[ 1024 ];
			int len;
			while( ( len = in.read( buf ) ) > 0 ) {
				out.write( buf, 0, len );
			}
			out.close();
			in.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void load() {
		try {
			File configfile = new File( this.plugin.getDataFolder(), "gmhook.yml" );

			if( !configfile.exists() ) {
				configfile.getParentFile().mkdirs();
				copy( this.plugin.getResource( "gmhook.yml" ), configfile );
			}

			this.config = YamlConfiguration.loadConfiguration( configfile );
		} catch( Exception e ) {
			e.printStackTrace();
		}

		if( this.playersToUpdate == null ) {
			this.playersToUpdate = new HashMap<UUID, String>();
		} else {
			this.playersToUpdate.clear();
		}

		unserialize( this.config.getStringList( "playersToUpdate" ) );
	}

	public void unserialize( List<String> serializedPlayersToUpdate ) {
		if( serializedPlayersToUpdate == null )
			return;

		for( String playerToUpdate : serializedPlayersToUpdate ) {
			String pair[] = playerToUpdate.split( ":" );

			if( pair.length > 1 ) {
				try {
					this.playersToUpdate.put( UUID.fromString( pair[ 0 ] ), pair[ 1 ] );
				} catch( Exception e ) {
					System.err.println( String.format( "Invalid UUID in playersToUpdate configuration: %s", pair[ 0 ] ) );
				}
			}
		}
	}

	public void save() {
		this.config.set( "playersToUpdate", serialize() );

		try {
			File configfile = new File( this.plugin.getDataFolder(), "gmhook.yml" );

			if( !configfile.exists() ) {
				configfile.getParentFile().mkdirs();
				copy( this.plugin.getResource( "gmhook.yml" ), configfile );
			}

			this.config.save( configfile );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public List<String> serialize() {
		List<String> serializedPlayersToUpdate = new ArrayList<String>();

		for( Map.Entry<UUID, String> entry : this.playersToUpdate.entrySet() ) {
			serializedPlayersToUpdate.add( String.format( "%s:%s", entry.getKey().toString(), entry.getValue() ) );
		}

		return serializedPlayersToUpdate;
	}
}
