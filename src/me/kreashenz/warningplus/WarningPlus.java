package me.kreashenz.warningplus;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarningPlus extends JavaPlugin {
	protected List<String> reasons;

	public void onEnable(){
		saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("warnhelp")) {
			s.sendMessage("==========[ WarningPlus ]==========");
			s.sendMessage("/warnhelp : shows this");
			s.sendMessage("/warn <player> <reason> : warn a player");
			s.sendMessage("/warncheck <player> : check a player's warnings");
			s.sendMessage("=====================================");
		} else if (cmd.getName().equalsIgnoreCase("warn")) {
			if (s.hasPermission("warningplus.warn")) {
				if (args.length <= 1) s.sendMessage("Command syntax: /warn <player> <reason>");
				else {
					Player player = Bukkit.getPlayer(args[0]);

					if (player == null) {
						s.sendMessage("Player must be online");
						return true;
					} else {
						String message = "";

						for (int i = 1; i < args.length; i++) {
							message += (args[i] + " ");
						}

						setWarnings(player.getName(), getNumWarnings(player.getName()) + 1);
						addReason(player.getName(), message);
						addWarner(player.getName(), s.getName());
						saveConfig();
						player.sendMessage("You have been warned by " + s.getName() + " for " + message);
					}
				}
			} else {
				s.sendMessage("You don't have permission to do this.");
			}
		} else if (cmd.getName().equalsIgnoreCase("warncheck")) {
			if (s.hasPermission("warningplus.warncheck")) {
				if (args.length == 0) s.sendMessage("Invalid arguments. /warncheck <player>");
				else {
					List<String> warners = getConfig().getStringList(args[0] + ".warners");
					if (warners == null) {
						s.sendMessage("No warnings for that player.");
						return true;
					} else {
						Map<String, Integer> map = new HashMap<String, Integer>();
						for (String warner : warners) {
							if (map.containsKey(warner)) {
								map.put(warner, map.get(warner) + 1);
							} else {
								map.put(warner, 1);
							}
						}

						for (String warner : map.keySet()) s.sendMessage(args[0] + " has " + map.get(warner) + (map.get(warner) == 1 ? " warning" : " warnings") + " from " + warner);
					}
				}
			} else {
				s.sendMessage("You don't have permission to do this.");
			}
		}
		return true;
	}

	private void setWarnings(String target, int warnings) {
		getConfig().set(target + ".warnings", warnings);
		saveConfig();
	}

	private int getNumWarnings(String player) {
		if (getConfig().get(player + ".warnings") != null) {
			return getConfig().getInt(player + ".warnings");
		} else {
			return 0;
		}
	}

	private void addReason(String target, String reason) {
		if (getConfig().getStringList(target + ".reasons") == null) getConfig().set(target, new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".reasons");
		list.add(reason);
		getConfig().set(target + ".reasons", list);
		saveConfig();
	}

	private void addWarner(String target, String warner) {
		if (getConfig().getStringList(target + ".warners") == null) getConfig().set(target, new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".warners");
		list.add(warner);
		getConfig().set(target + ".warners", list);
		saveConfig();
	}

}
