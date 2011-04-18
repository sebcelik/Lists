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

public class AddCommand extends BaseCommand {

    public AddCommand(HeroList plugin) {
        super(plugin);
        name = "Add Players";
        description = "Adds multiple players to existing list with no privileges";
        usage = "§e/ls add §9<list> §8[player1] §8[player2] ... §8[playerN] ";
        minArgs = 2;
        maxArgs = 10000;
        identifiers.add("ls add");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.ADD) && !plugin.hasPermission(player, Permission.ADMIN_ADD)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }

        Map<String, PrivilegedList> lists = plugin.getLists();
        if (!lists.containsKey(args[0])) {
            Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
            return;
        }

        PrivilegedList list = lists.get(args[0]);

        if (player != null && !plugin.hasPermission(player, Permission.ADMIN_ADD)) {
            String name = player.getName();

            if (!list.contains(name)) {
                Messaging.send(plugin, sender, "You are not a member of $1.", args[0]);
                return;
            }

            if (name.equalsIgnoreCase(args[1])) {
                Messaging.send(plugin, sender, "You cannot add yourself.");
                return;
            }

            Level senderPrivilege = list.get(name);

            if (!senderPrivilege.clears(Level.MODIFIER)) {
                Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
                return;
            }
        }

        for (int i = 1; i < args.length; i++) {
            if (!list.contains(args[i])) {
                list.put(args[i], Level.NONE);
            }
        }
        plugin.saveList(list);
        Messaging.send(plugin, sender, "Added players to $1 with no privileges.", args[0]);

    }

}
