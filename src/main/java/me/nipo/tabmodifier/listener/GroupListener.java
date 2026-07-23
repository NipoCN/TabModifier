package me.nipo.tabmodifier.listener;

import me.nipo.tabmodifier.TabModifier;
import me.nipo.tabmodifier.commands.CommandRefresh;
import me.nipo.tabmodifier.config.Config;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;

public class GroupListener {

    public GroupListener(TabModifier _plugin) {
        LuckPerms lp = _plugin.getLuckPerms();
        EventBus eventbus = lp.getEventBus();
        eventbus.subscribe(GroupDataRecalculateEvent.class, e -> {
            ArrayList<Player> groupplayer = new ArrayList<Player>();
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                User user = lp.getUserManager().getUser(player.getUniqueId());
                if (user == null) continue;
                if (user.getPrimaryGroup().equals(e.getGroup().getName())) {
                    groupplayer.add(player);
                }
            }
            CommandRefresh.updateTab4All(groupplayer, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
        });
    }

    private int toInteger(boolean boolvalue) {
        return (boolvalue) ? 1 : 0;
    }
}
