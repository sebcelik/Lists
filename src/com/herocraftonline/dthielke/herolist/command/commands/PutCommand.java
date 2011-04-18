package com.herocraftonline.dthielke.herolist.command.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.HeroList.Permission;
import com.herocraftonline.dthielke.herolist.PrivilegedList.Level;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class PutCommand extends BaseCommand {

	public PutCommand(HeroList plugin) {
		super(plugin);
		name = "Put Player";
		description = "Adds or modifies a player to an existing list";
		usage = "§e/ls put §9<list> §9<player> §8-[n|v|m|o]";
		minArgs = 2;
		maxArgs = 3;
		identifiers.add("ls put");
		notes.add("Privileges:");
		notes.add("  -n: NONE (default)");
		notes.add("  -v: VIEW");
		notes.add("  -m: MODIFY");
		notes.add("  -o: OWNER");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
	    Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.PUT) && !plugin.hasPermission(player, Permission.ADMIN_PUT)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }
	    
		Map<String, PrivilegedList> lists = plugin.getLists();
		if (!lists.containsKey(args[0])) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		Level privilege = Level.NONE;
		if (args.length == 3) {
			privilege = Level.parse(args[2].substring(1));
			privilege = (privilege == null) ? Level.NONE : privilege;
		}

		PrivilegedList list = lists.get(args[0]);

		if (player != null && !plugin.hasPermission(player, Permission.ADMIN_PUT)) {
			String name = player.getName();

			if (!list.contains(name)) {
				Messaging.send(plugin, sender, "You are not a member of $1.", args[0]);
				return;
			}

			if (name.equalsIgnoreCase(args[1])) {
				Messaging.send(plugin, sender, "You cannot modify your own privilege level.");
				return;
			}

			Level senderPrivilege = list.get(name);

			if (!senderPrivilege.clears(Level.MODIFIER)) {
				Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
				return;
			}

			if (senderPrivilege == Level.MODIFIER && privilege.clears(senderPrivilege)) {
				Messaging.send(plugin, sender, "You cannot set a privilege equal to or higher than your own.");
				return;
			}
		}

		list.put(args[1], privilege);
		plugin.saveList(list);
		Messaging.send(plugin, sender, "Added $1 to $2 with $3 privileges.", args[1], args[0], privilege.name());

	}

}
