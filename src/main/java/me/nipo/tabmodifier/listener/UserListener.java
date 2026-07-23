package me.nipo.tabmodifier.listener;

import me.nipo.tabmodifier.TabModifier;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class UserListener {
    private static TabModifier plugin;

    public UserListener(TabModifier _plugin) {
        plugin = _plugin;
        LuckPerms lp = _plugin.getLuckPerms();
        EventBus eventbus = lp.getEventBus();

        eventbus.subscribe(UserDataRecalculateEvent.class, e -> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            if (player.isPresent()) {
                plugin.refreshtablist(player.get());
            }
        });

        eventbus.subscribe(UserDemoteEvent.class, e -> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            if (player.isPresent()) {
                plugin.refreshtablist(player.get());
            }
        });

        eventbus.subscribe(UserPromoteEvent.class, e -> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            if (player.isPresent()) {
                plugin.refreshtablist(player.get());
            }
        });
    }
}
