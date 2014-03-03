package me.kreashenz.warningplus.listeners;

import me.kreashenz.warningplus.WarningPlus;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.logging.Logger;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.ArrayList;

public class PlayerListener implements Listener {
	private final static Logger LOGGER = Logger.getLogger("PlayerListener");
	private WarningPlus plugin;

	public PlayerListener(WarningPlus plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player player = e.getPlayer();

		if (plugin.getNumWarnings(player.getName()) > 0) {
			List<String> warners = new ArrayList<>(new LinkedHashSet<>(plugin.getWarners(player.getName())));
					
			if (warners.size() > 3) {
				int size = warners.size();

				warners = warners.subList(0, 3);
				warners.add("+ " + (size - 3) + " more");
			}

			for (OfflinePlayer op : Bukkit.getServer().getOperators()) {
				if (op.isOnline()) op.getPlayer().sendMessage(String.format("[W++] " + ChatColor.GREEN + "%s " + ChatColor.RESET + "has " + ChatColor.RED + "%d " + ChatColor.RESET + "warnings from " + ChatColor.GOLD + "%s", player.getName(), plugin.getNumWarnings(player.getName()), plugin.listToString(warners)));
			}

			LOGGER.info(String.format("[W++] %s has %d warnings from %s", player.getName(), plugin.getNumWarnings(player.getName()), plugin.listToString(warners)));
		}
	}
}