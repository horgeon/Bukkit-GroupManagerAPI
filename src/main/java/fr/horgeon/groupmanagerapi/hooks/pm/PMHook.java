package fr.horgeon.groupmanagerapi.hooks.pm;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public interface PMHook {
	public String getGroup( OfflinePlayer player ) throws Exception;
	public void setGroup( OfflinePlayer player, String group ) throws Exception;

	public void reload() throws Exception;
}
