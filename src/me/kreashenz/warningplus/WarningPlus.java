package me.kreashenz.warningplus;

import java.util.List;

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
		if(cmd.getName().equalsIgnoreCase("warnhelp")){
			if(s instanceof Player){
				Player p = (Player)s;
				if(p.hasPermission("warningplus.help")){
					if(args.length >= 0){
						p.sendMessage("§9==========[ §6WarningPlus ]==========");
						p.sendMessage("§b/warnhelp §8: §3Shows this.");
						p.sendMessage("§b/warn <player> <reason> §8: §3Warn a player.");
						p.sendMessage("§b/warncheck <player> §8: §3Check a player's warnings.");
						p.sendMessage("§9==========[ §6WarningPlus ]==========");
					}
				} else p.sendMessage("§cYou don't have permission to do this.");
			} else {
				if(args.length >= 0){
					s.sendMessage("§9==========[ §6WarningPlus ]==========");
					s.sendMessage("§b/warnhelp §8: §3Shows this.");
					s.sendMessage("§b/warn <player> <reason> §8: §3Warn a player.");
					s.sendMessage("§b/warncheck <player> §8: §3Check a player's warnings.");
					s.sendMessage("§9==========[ §6WarningPlus ]==========");
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("warn")){
			if(s instanceof Player){
				Player p = (Player)s;
				if(p.hasPermission("warningplus.warn")){
					if(args.length <= 1)p.sendMessage("§cInvalid arguments. §f/warn <player> <reason>");
					else {
						Player t = Bukkit.getPlayer(args[0]);
						if(t.isOnline() && t != null){
							String message = "";
							for (int i = 1; i < args.length; i++)
								message = message + args[i] + ' ';
							setWarnings(t, getWarnings(t)+1);
							addReason(t, message);
							addWarner(t.getName(), p.getName());
							saveConfig();
							t.sendMessage("§cYou have been warned by "+p.getName()+" for " + message);
						} else p.sendMessage("§cThat player can't be found.");
					}
				} else p.sendMessage("§cYou don't have permission to do this.");
			} else {
				if(args.length <= 1)s.sendMessage("§cInvalid arguments. §f/warn <player> <reason>");
				else {
					Player t = Bukkit.getPlayer(args[0]);
					if(t.isOnline() && t != null){
						String message = "";
						for (int i = 1; i < args.length; i++)
							message = message + args[i] + ' ';
						setWarnings(t, getWarnings(t)+1);
						addReason(t, message);
						addWarner(t.getName(), s.getName());
						saveConfig();
						t.sendMessage("§cYou have been warned by "+s.getName()+" for " + message);
					} else s.sendMessage("§cThat player can't be found.");
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("warncheck")){
			if(s instanceof Player){
				Player p = (Player)s;
				if(p.hasPermission("warningplus.warncheck")){
					if(args.length == 0)p.sendMessage("§cInvalid arguments. §c/warncheck <player>");
					else {
						Player t = Bukkit.getPlayer(args[0]);
						if(t.isOnline() && t != null){
							String a = "";
							for(String i : getConfig().getStringList(t.getName() + ".warners")){
								i = "§a"+i+"§e, ";
								a = a + i + ", ";
							}
							p.sendMessage("§cFrom §9"+a);
							p.sendMessage("§9"+t.getName()+" §chas §9"+getWarnings(t)+" §cwarnings.");
						} else p.sendMessage("§cThat player can't be found.");
					}
				} else p.sendMessage("§cYou don't have permission to use this command.");
			} else {
				if(args.length == 0)s.sendMessage("§cInvalid arguments. §c/warncheck <player>");
				else {
					Player t = Bukkit.getPlayer(args[0]);
					if(t.isOnline() && t != null){
						for(String i : getConfig().getStringList(t.getName() + ".warners"))
							s.sendMessage("§cFrom §9"+i.substring(i.length()-1));
						s.sendMessage("§9"+t.getName()+" §chas §9"+getWarnings(t)+" §cwarnings.");
					} else s.sendMessage("§cThat player can't be found.");
				}
			}
		}
		return true;
	}

	private void setWarnings(Player p, int warnings){
		getConfig().set(p.getName() + ".warnings", warnings);
		saveConfig();
	}

	private int getWarnings(Player p){
		if(getConfig().get(p.getName() + ".warnings") != null){
			return getConfig().getInt(p.getName() + ".warnings");
		} else {
			return 0;
		}
	}

	private void addReason(Player p, String reason){
		getConfig().set(p.getName() + ".reasons", reason);
	}

	private void addWarner(String target, String warner){
		getConfig().getStringList(target + ".warners").add(warner);
	}

}
