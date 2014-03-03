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
import org.bukkit.ChatColor;

public class WarningPlus extends JavaPlugin {
	public void onEnable(){
		saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("warnhelp")) {
			sender.sendMessage("==========[ Warnings++ ]==========");
			sender.sendMessage("/warnhelp: shows this");
			sender.sendMessage("/warn <player> <reason>: warn a player");
			sender.sendMessage("/warncheck <player>: check a player'sender warnings");
			sender.sendMessage("/warnlist <player> <number>: list a player'sender most recent <number> warnings");
			sender.sendMessage("/warnreset <player>: reset a player'sender warnings");
			sender.sendMessage("===================================");
		} else if (cmd.getName().equalsIgnoreCase("warn")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length <= 1) sender.sendMessage("Command syntax: /warn <player> <reason>");
				else {
					Player player = Bukkit.getPlayer(args[0]);

					if (player == null) {
						sender.sendMessage("Player must be online");
						return true;
					} else {
						String message = "";

						for (int i = 1; i < args.length; i++) {
							message += (args[i] + " ");
						}

						incrementWarnings(player.getName());
						addReason(player.getName(), message);
						addWarner(player.getName(), sender.getName());
						player.sendMessage("You have been warned by " + sender.getName() + ": " + message);
					}
				}
			} else sender.sendMessage("[W++] you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warncheck")) {
			if (sender.hasPermission("warningplus.warncheck")) {
				if (args.length == 0) sender.sendMessage("Invalid arguments. /warncheck <player>");
				else {
					List<String> warners = getConfig().getStringList(args[0] + ".warners");
					if (warners.size() == 0) sender.sendMessage("No warnings for that player.");
					else {
						Map<String, Integer> map = new HashMap<String, Integer>();
						for (String warner : warners) {
							if (map.containsKey(warner)) {
								map.put(warner, map.get(warner) + 1);
							} else {
								map.put(warner, 1);
							}
						}

						for (String warner : map.keySet()) sender.sendMessage(args[0] + " has " + map.get(warner) + (map.get(warner) == 1 ? " warning" : " warnings") + " from " + warner);
					}
				}
			} else sender.sendMessage("[W++] you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warnlist")) {
			if (sender.hasPermission("warningplus.warnlist")) {
				if (args.length != 2) sender.sendMessage("syntax: [/warnlist <player> <num>]");
				else {
					List<String> warnings = getWarnings(args[0], Integer.parseInt(args[1]));
					if (warnings.size() == 0) sender.sendMessage(args[0] + " has no warnings");
					else {
						for (String warning : warnings) sender.sendMessage(warning);
					}
				}
			} else sender.sendMessage("[W++] you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warnreset")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length == 0) sender.sendMessage("Invalid arguments. /warnreset <player>");
				else {
					getConfig().set(args[0] + ".warners", null);
					getConfig().set(args[0] + ".reasons", null);
					getConfig().set(args[0] + ".warnings", 0);
					sender.sendMessage("[W++] warnings cleared for " + args[0]);
				}
			} else sender.sendMessage("[W++] you don't have permission to do this.");
		}

		saveConfig();
		return true;
	}

	private List<String> getWarnings(String target, int numWarnings) {
		List<String> reasons = getConfig().getStringList(target + ".reasons");
		List<String> warners = getConfig().getStringList(target + ".warners");
		List<String> warnings = new ArrayList<String>();

		for (String reason : reasons) {
			if (numWarnings-- <= 0) break;
			warnings.add(ChatColor.WHITE + reason + ChatColor.RED + " by " + warners.get(reasons.indexOf(reason)));
		}

		return warnings;
	}

	private void incrementWarnings(String target) {
		setWarnings(target, getNumWarnings(target) + 1);
	}

	private void setWarnings(String target, int warnings) {
		getConfig().set(target + ".warnings", warnings);
		saveConfig();
	}

	private int getNumWarnings(String player) {
		if (getConfig().contains(player + ".warnings")) return getConfig().getInt(player + ".warnings");

		return 0;
	}

	private void addReason(String target, String reason) {
		if (getConfig().getStringList(target + ".reasons") == null) getConfig().set(target, new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".reasons");

		list.add(reason);
		getConfig().set(target + ".reasons", list);

		saveConfig();
	}

	private void addWarner(String target, String warner) {
		if (getConfig().getStringList(target + ".warners") == null) getConfig().set(target + ".warners", new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".warners");

		list.add(warner);
		getConfig().set(target + ".warners", list);

		saveConfig();
	}

}
