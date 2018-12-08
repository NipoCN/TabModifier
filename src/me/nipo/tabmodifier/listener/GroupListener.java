package me.nipo.tabmodifier.listener;

import me.nipo.tabmodifier.commands.CommandRefresh;
import me.nipo.tabmodifier.config.Config;

import java.util.ArrayList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.event.group.GroupDataRecalculateEvent;
import me.lucko.luckperms.api.event.EventBus;

public class GroupListener {
	
	public GroupListener(LuckPermsApi api){
		EventBus eventbus = api.getEventBus();
		eventbus.subscribe(GroupDataRecalculateEvent.class, e -> {
			ArrayList<Player> groupplayer = new ArrayList<Player>();
			for (Player player : Sponge.getServer().getOnlinePlayers()){
				User user = api.getUserManager().getUser(player.getUniqueId());
				if (user == null) continue;
				if (user.getPrimaryGroup().equals(e.getGroup().getName())){
					groupplayer.add(player);
				}
			}
			CommandRefresh.updateTab4All(groupplayer, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
		});
	}
	private int toInteger(boolean boolvalue){
		return (boolvalue == true) ? 1 : 0;
	}
}
