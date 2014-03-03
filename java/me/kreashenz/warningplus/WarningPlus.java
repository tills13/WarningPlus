package me.kreashenz.warningplus;

import me.kreashenz.warningplus.listeners.PlayerListener;

import java.util.List;
import java.util.LinkedHashSet;
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
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("warnhelp")) {
			sender.sendMessage("==========[ Warnings++ ]==========");
			sender.sendMessage("/warnhelp: " + ChatColor.GREEN + "shows this");
			sender.sendMessage("/warn <player> <reason>: " + ChatColor.GREEN + "warn a player");
			sender.sendMessage("/warncheck <player>: " + ChatColor.GREEN + "check a player's warnings");
			sender.sendMessage("/warnlist <player> <page>: " + ChatColor.GREEN + "list a player's warnings");
			sender.sendMessage("/warnreset <player>: " + ChatColor.GREEN + "reset a player's warnings");
			sender.sendMessage("===================================");
		} else if (cmd.getName().equalsIgnoreCase("warn")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length <= 1) sender.sendMessage("syntax: " + ChatColor.GREEN + "[/warn <player> <reason>]");
				else {
					Player player = Bukkit.getPlayer(args[0]);

					if (player == null) sender.sendMessage("[W++] " + ChatColor.RED + "player must be online");
					else {
						String message = "";
						for (int i = 1; i < args.length; i++) message += (args[i] + (i == args.length - 1 ? "" : " "));

						warnPlayer(player, sender, message);
					}
				}
			} else sender.sendMessage("[W++] " + ChatColor.RED + "you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warncheck")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length == 0) sender.sendMessage("syntax: " + ChatColor.GREEN + "[/warncheck <player>]");
				else {
					List<String> warners = new ArrayList<>(new LinkedHashSet<>(getWarners(args[0])));
					if (warners.size() == 0) sender.sendMessage("[W++] " + ChatColor.RED + "no warnings for that player.");
					else {
						if (warners.size() > 3) {
							int size = warners.size();

							warners = warners.subList(0, 3);
							warners.add("+ " + (size - 3) + " more");
						}

						sender.sendMessage(String.format("[W++] " + ChatColor.GREEN + "%s " + ChatColor.RESET + "has " + ChatColor.RED + "%d " + ChatColor.RESET + "warnings from " + ChatColor.GOLD + "%s", args[0], getNumWarnings(args[0]), listToString(warners)));
					}
				}
			} else sender.sendMessage("[W++] " + ChatColor.RED + "you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warnlist")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length < 1) sender.sendMessage("syntax: " + ChatColor.GREEN + "[/warnlist <player> <page def:1>]");
				else {
					Map<String, String> warnings = getWarnings(args[0]);

					int page = 1, end;
                    end = warnings.size();

                    if (args.length > 1) {
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) { }

                        if (page > Math.ceil(end / 5.0)) page = (int) Math.ceil(end / 5.0);
                        else if (page <= 0) page = 1;
                    }

                    if (warnings.size() == 0) sender.sendMessage("[W++] " + ChatColor.RED + "no warnings for that player.");
                    else {
                    	sender.sendMessage("[W++] warnings for " + ChatColor.GREEN + args[0] + ChatColor.WHITE + " [page " + page + " of " + (int)Math.ceil(end / 5.0) + "]");

                    	int j = 0;
	                    for (int i = ((page - 1) * 5); i < end; i++) {
	                        if (((i % 5) == 0) && (j++ != 0)) break;

	                        sender.sendMessage("    " + (i + 1) + ". " + warnings.keySet().toArray()[i] + ChatColor.RED + " by " + ChatColor.GREEN + warnings.values().toArray()[i]);
	                    }
                    }
				}
			} else sender.sendMessage("[W++] " + ChatColor.RED + "you don't have permission to do this.");
		} else if (cmd.getName().equalsIgnoreCase("warnreset")) {
			if (sender.hasPermission("warningplus.warn")) {
				if (args.length == 0) sender.sendMessage("syntax: " + ChatColor.GREEN + "[/warnreset <player>]");
				else {
					resetWarnings(args[0]);
					sender.sendMessage("[W++] " + ChatColor.GREEN + "warnings cleared for " + ChatColor.GOLD + args[0]);
				}
			} else sender.sendMessage("[W++] " + ChatColor.RED + "you don't have permission to do this.");
		}

		return true;
	}

	public void warnPlayer(Player player, CommandSender sender, String message) {
		incrementWarnings(player.getName());
		addReason(player.getName(), message);
		addWarner(player.getName(), sender.getName());
		player.sendMessage("[W++] you have been warned by " + sender.getName() + ": " + ChatColor.RED + message);
		sender.sendMessage("[W++] you have warned " + player.getName() + ": " + ChatColor.RED + message);
	}

	public Map<String, String> getWarnings(String target) {
		Map<String, String> warnings = new HashMap<String, String>();
		List<String> reasons = getConfig().getStringList(target + ".reasons");
		List<String> warners = getConfig().getStringList(target + ".warners");

		for (String reason : reasons) warnings.put(reason, warners.get(reasons.indexOf(reason)));

		return warnings;
	}

	public List<String> getWarners(String target) {
		return getConfig().getStringList(target + ".warners");
	}

	public void incrementWarnings(String target) {
		setWarnings(target, getNumWarnings(target) + 1);
	}

	public void setWarnings(String target, int warnings) {
		getConfig().set(target + ".warnings", warnings);
		saveConfig();
	}

	public void resetWarnings(String target) {
		getConfig().set(target + ".warners", null);
		getConfig().set(target + ".reasons", null);
		getConfig().set(target + ".warnings", 0);
		saveConfig();
	}

	public int getNumWarnings(String player) {
		if (getConfig().contains(player + ".warnings")) return getConfig().getInt(player + ".warnings");

		return 0;
	}

	public void addReason(String target, String reason) {
		if (getConfig().getStringList(target + ".reasons") == null) getConfig().set(target, new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".reasons");

		list.add(reason);
		getConfig().set(target + ".reasons", list);

		saveConfig();
	}

	public void addWarner(String target, String warner) {
		if (getConfig().getStringList(target + ".warners") == null) getConfig().set(target + ".warners", new ArrayList<String>());
		List<String> list = getConfig().getStringList(target + ".warners");

		list.add(warner);
		getConfig().set(target + ".warners", list);

		saveConfig();
	}

	public String listToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (String string : list) sb.append(((index == 0) ? "[" : "") + string + (++index == list.size() ? "]" : ", "));

		return sb.toString();
	}
}
