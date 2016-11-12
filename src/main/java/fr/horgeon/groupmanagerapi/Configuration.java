package fr.horgeon.groupmanagerapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Configuration {
	private JavaPlugin plugin;

	private FileConfiguration config;
	private FileConfiguration messages;

	public Configuration( JavaPlugin plugin ) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();

		this.config.options().copyDefaults( true );

		this.plugin.saveConfig();

		loadMessages();
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

	public void loadMessages() {
		try {
			File pluginmessagesfile = new File( this.plugin.getDataFolder(), "messages.yml" );

			if( !pluginmessagesfile.exists() ) {
				pluginmessagesfile.getParentFile().mkdirs();
				copy( this.plugin.getResource( "messages.yml" ), pluginmessagesfile );
			}

			this.messages = YamlConfiguration.loadConfiguration( pluginmessagesfile );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void reload() {
		this.plugin.reloadConfig();

		File pluginmessagesfile = new File( this.plugin.getDataFolder(), "messages.yml" );
		this.messages = YamlConfiguration.loadConfiguration( pluginmessagesfile );
	}

	public String getString( String key ) {
		return this.config.getString( key );
	}

	public boolean getIsHandlerActivated( String endpoint ) {
		List<String> list = this.config.getStringList( "activeHandlers" );
		for( String key : list ) {
			String pair[] = key.split( ":" );

			if( pair.length > 1 ) {
				if( pair[ 0 ].equals( endpoint ) )
					return pair[ 1 ].equalsIgnoreCase( "true" );
			}
		}

		return false;
	}

	public Integer getInt( String key ) {
		return this.config.getInt( key );
	}

	public String getMessage( String key ) {
		return this.messages.getString( key );
	}
}
