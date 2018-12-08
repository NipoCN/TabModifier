package me.nipo.tabmodifier.listener;

import me.nipo.tabmodifier.tabmodifier;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.user.UserDataRecalculateEvent;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;
import me.lucko.luckperms.api.event.user.track.UserDemoteEvent;

public class UserListener {
	private static tabmodifier plugin;
	
	public UserListener(tabmodifier _plugin, LuckPermsApi api){
		plugin = _plugin;
		EventBus eventbus = api.getEventBus();
		
		eventbus.subscribe(UserDataRecalculateEvent.class, e -> {
			Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUuid());
			if (player.isPresent()){
				plugin.refreshtablist(player.get());
			}
		});
		
		eventbus.subscribe(UserDemoteEvent.class, e -> {
			Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUuid());
			if (player.isPresent()){
				plugin.refreshtablist(player.get());
			}
		});
		
		eventbus.subscribe(UserPromoteEvent.class, e -> {
			Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUuid());
			if (player.isPresent()){
				plugin.refreshtablist(player.get());
			}
		});
	}
}
