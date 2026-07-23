package me.nipo.tabmodifier.commands;

import me.nipo.tabmodifier.config.Config;
import me.nipo.tabmodifier.TabModifier;
import me.rojo8399.placeholderapi.PlaceholderService;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.Optional;

public class CommandRefresh {

    /*On Player Join Event, Update Other players' tab list, simply insert*/
    public static void refreshOthers(Player player, int prefix, int suffix, int displayname) {
        int sum = prefix * 100 + suffix * 10 + displayname;
        for (Player players : Sponge.getServer().getOnlinePlayers()) {
            if (players.equals(player)) continue;
            TabList tablist = players.getTabList();
            Optional<TabListEntry> sysentry = tablist.getEntry(player.getUniqueId());  //minecraft thread added this entry?
            if (sysentry.isPresent()) {
                String strname = genDisplayname(player, sum);
                if (strname == null || strname.isEmpty()) continue;  //player is not online
                Text name = applyPlaceholders(strname, player, players);
                sysentry.get().setDisplayName(name);
            }
        }
    }

    /* On Player Join Event, Update player, get all online players' data */
    public static void refreshSelf(Player player, int prefix, int suffix, int displayname) {
        if (player.isOnline()) {
            int sum = prefix * 100 + suffix * 10 + displayname;
            TabList tablist = player.getTabList();
            for (Player players : Sponge.getServer().getOnlinePlayers()) {
                String strname = genDisplayname(players, sum);
                if (strname == null || strname.isEmpty()) continue;  //player is not online, skip
                Text name = applyPlaceholders(strname, player, players);
                Optional<TabListEntry> entry = tablist.getEntry(players.getUniqueId());
                if (entry.isPresent()) entry.get().setDisplayName(name);
            }
            tablist.setHeaderAndFooter(applyPlaceholders(Config.getheaderValue(), player, null),
                    applyPlaceholders(Config.getfooterValue(), player, null));
        }
    }

    public static void updatahdAndft() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.getTabList().setHeaderAndFooter(applyPlaceholders(Config.getheaderValue(), player, null),
                    applyPlaceholders(Config.getfooterValue(), player, null));
        }
    }

    public static void updateTab4All(ArrayList<Player> groupplayer, int prefix, int suffix, int displayname) {
        int sum = prefix * 100 + suffix * 10 + displayname;
        for (Player players : Sponge.getServer().getOnlinePlayers()) {
            TabList tablist = players.getTabList();
            for (Player groplayer : groupplayer) {
                Optional<TabListEntry> entry = tablist.getEntry(groplayer.getUniqueId());
                if (entry.isPresent()) {
                    String strname = genDisplayname(groplayer, sum);
                    if (strname == null || strname.isEmpty()) continue;  //player is not online
                    Text name = applyPlaceholders(strname, groplayer, players);
                    entry.get().setDisplayName(name);
                }
            }
        }
    }

    public static void updateAllTab(int prefix, int suffix, int displayname) {
        int sum = prefix * 100 + suffix * 10 + displayname;
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            TabList tablist = player.getTabList();
            for (Player players : Sponge.getServer().getOnlinePlayers()) {
                Optional<TabListEntry> entry = tablist.getEntry(players.getUniqueId());
                if (entry.isPresent()) {
                    String strname = genDisplayname(players, sum);
                    if (strname == null || strname.isEmpty()) continue;  //player is not online
                    Text name = applyPlaceholders(strname, players, player);
                    entry.get().setDisplayName(name);
                }
            }
            tablist.setHeaderAndFooter(applyPlaceholders(Config.getheaderValue(), player, null),
                    applyPlaceholders(Config.getfooterValue(), player, null));
        }
    }

    /* get Displayname, if not exists, simplay use name, concat prefix or suffix */
    private static String genDisplayname(Player player, int sum) {
        LuckPerms lp = TabModifier.getInstance().getLuckPerms();
        /* get prefix and suffix */
        User user = loadUser(player);
        if (user == null) return null;  //user is not online
        Group group = lp.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return null;
        ContextManager cm = lp.getContextManager();
        CachedMetaData usermeta = user.getCachedData().getMetaData(cm.getStaticQueryOptions());
        CachedMetaData groupmeta = group.getCachedData().getMetaData(cm.getStaticQueryOptions());
        String prefix = (usermeta.getPrefix() != null) ? usermeta.getPrefix() : groupmeta.getPrefix();
        String suffix = (usermeta.getSuffix() != null) ? usermeta.getSuffix() : groupmeta.getSuffix();
        String displayname = TextSerializers.FORMATTING_CODE.serialize(player.getDisplayNameData().displayName().get());
        if (prefix == null) prefix = Config.getInitPrefix();
        if (suffix == null) suffix = Config.getInitSuffix();
        if (displayname == null) displayname = player.getName();
        switch (sum) {
            case 111:
                return prefix + displayname + suffix;
            case 110:
                return prefix + player.getName() + suffix;
            case 101:
                return prefix + displayname;
            case 100:
                return prefix + player.getName();
            case 11:
                return displayname + suffix;
            case 10:
                return player.getName() + suffix;
            case 1:
                return displayname;
            case 0:
                return player.getName();
        }
        return player.getName();
    }

    /* load user instance, if player is not online, return null */
    private static User loadUser(Player player) {
        LuckPerms lp = TabModifier.getInstance().getLuckPerms();
        if (!player.isOnline()) return null;
        else return lp.getUserManager().getUser(player.getUniqueId());
    }

    private static Text applyPlaceholders(String str, Player player, Player players) {
        str = str.replaceAll("\\\\n", "\n");
        str = str.replaceAll("\\n", "\n");
        Text text = TextSerializers.FORMATTING_CODE.deserialize(str);
        Optional<PlaceholderService> placeholders = TabModifier.getInstance().getPlaceholders();
        if (placeholders.isPresent()) {
            return placeholders.get().replacePlaceholders(text, player, players);
        }
        else {
            return text;
        }
    }
}
