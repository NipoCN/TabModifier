package me.nipo.tabmodifier.commands;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.*;
import org.spongepowered.api.entity.living.player.tab.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.*;

import me.lucko.luckperms.api.*;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.context.ContextManager;
import me.nipo.tabmodifier.tabmodifier;
import me.nipo.tabmodifier.config.Config;
import me.lucko.luckperms.api.LuckPermsApi;
import me.rojo8399.placeholderapi.PlaceholderService;

public class CommandRefresh {
	
	/*On Player Join Event, Update Other players' tab list, simply insert*/
	public static void refreshOthers(Player player, int prefix, int suffix, int displayname){
		//get luckperms
		PlaceholderService phservice = tabmodifier.getInstance().getPlaceHolder();
		int sum = prefix*100 + suffix*10 + displayname;
		for (Player players : Sponge.getServer().getOnlinePlayers()){
			if (players.equals(player)) continue;
			TabList tablist = players.getTabList();
			Optional<TabListEntry> sysentry = tablist.getEntry(player.getUniqueId());  //minecraft thread added this entry?
			if (sysentry.isPresent()) {
				String strname = genDisplayname(player, sum);
				if (strname == null || strname.isEmpty()) continue;  //player is not online 
				Text name = phservice.replacePlaceholders(TextSerializers.FORMATTING_CODE.deserialize(strname), player, players);
				sysentry.get().setDisplayName(name);
			}
		}
	}
	
	/* On Player Join Event, Update player, get all online players' data */
	public static void refreshSelf(Player player, int prefix, int suffix, int displayname){
		if (player.isOnline()){
			PlaceholderService phservice = tabmodifier.getInstance().getPlaceHolder();
			int sum = prefix*100 + suffix*10 + displayname;
			TabList tablist = player.getTabList();
			for (Player players : Sponge.getServer().getOnlinePlayers()){
				String strname = genDisplayname(players, sum);
				if (strname == null || strname.isEmpty()) continue;  //player is not online, skip
				Text name = phservice.replacePlaceholders(TextSerializers.FORMATTING_CODE.deserialize(strname), players, player);
				Optional<TabListEntry> entry = tablist.getEntry(players.getUniqueId());
				if (entry.isPresent()) entry.get().setDisplayName(name);
			}
			tablist.setHeaderAndFooter(phservice.replacePlaceholders(Config.getheaderValue(), player, null), 
					phservice.replacePlaceholders(Config.getfooterValue(), player, null));
		}
	}
	
	public static void updatahdAndft(){
		PlaceholderService phservice = tabmodifier.getInstance().getPlaceHolder();
		for (Player player : Sponge.getServer().getOnlinePlayers()){
			player.getTabList().setHeaderAndFooter(phservice.replacePlaceholders(Config.getheaderValue(), player, null), 
					phservice.replacePlaceholders(Config.getfooterValue(), player, null));
		}
	}
	
	public static void updateTab4All(ArrayList<Player> groupplayer, int prefix, int suffix, int displayname){
		PlaceholderService phservice = tabmodifier.getInstance().getPlaceHolder();
		int sum = prefix*100 + suffix*10 + displayname;
		for (Player players : Sponge.getServer().getOnlinePlayers()){
			TabList tablist = players.getTabList();
			for (Player groplayer : groupplayer){
				Optional<TabListEntry> entry = tablist.getEntry(groplayer.getUniqueId());
				if (entry.isPresent()){
					String strname = genDisplayname(groplayer, sum);
					if (strname == null || strname.isEmpty()) continue;  //player is not online 
					Text name = phservice.replacePlaceholders(TextSerializers.FORMATTING_CODE.deserialize(strname), groplayer, players);
					entry.get().setDisplayName(name);
				}
			}
		}
	}
	
	public static void updateAllTab(int prefix, int suffix, int displayname){
		PlaceholderService phservice = tabmodifier.getInstance().getPlaceHolder();
		int sum = prefix*100 + suffix*10 + displayname;
		for (Player player : Sponge.getServer().getOnlinePlayers()){
			TabList tablist = player.getTabList();
			for (Player players : Sponge.getServer().getOnlinePlayers()){
				Optional<TabListEntry> entry = tablist.getEntry(players.getUniqueId());
				if (entry.isPresent()){
					String strname = genDisplayname(players, sum);
					if (strname == null || strname.isEmpty()) continue;  //player is not online 
					Text name = phservice.replacePlaceholders(TextSerializers.FORMATTING_CODE.deserialize(strname), players, player);
					entry.get().setDisplayName(name);
				}
			}
			tablist.setHeaderAndFooter(phservice.replacePlaceholders(Config.getheaderValue(), player, null), 
					phservice.replacePlaceholders(Config.getfooterValue(), player, null));
		}
	}
	
	/* get Displayname, if not exists, simplay use name, concat prefix or suffix */
	private static String genDisplayname(Player player, int sum){
		LuckPermsApi api = tabmodifier.getInstance().getLuckPerms();
		/* get prefix and suffix */
		User user = loadUser(player);
		if (user == null) return null;  //user is not online
		Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
		if (group == null) return null;
		ContextManager cm = api.getContextManager();
		MetaData usermeta = user.getCachedData().getMetaData(cm.getStaticContexts());
		MetaData groupmeta = group.getCachedData().getMetaData(cm.getStaticContexts());
		String prefix = (usermeta.getPrefix() != null) ? usermeta.getPrefix() : groupmeta.getPrefix();
		String suffix = (usermeta.getSuffix() != null) ? usermeta.getSuffix() : groupmeta.getSuffix();
		String displayname = TextSerializers.FORMATTING_CODE.serialize(player.getDisplayNameData().displayName().get());
		if (prefix == null) prefix = Config.getInitPrefix();
		if (suffix == null) suffix = Config.getInitSuffix();
		if (displayname == null) displayname = player.getName();
		switch(sum){
		case 111: return prefix + displayname + suffix;
		case 110: return prefix + player.getName() + suffix;
		case 101: return prefix + displayname;
		case 100: return prefix + player.getName();
		case 011: return displayname + suffix;
		case 010: return player.getName() + suffix;
		case 001: return displayname;
		case 000: return player.getName();
		}
		return player.getName();
	}
	
	/* load user instance, if player is not online, return null */
	private static User loadUser(Player player){
		LuckPermsApi api = tabmodifier.getInstance().getLuckPerms();
		if (!player.isOnline()) return null;
		else return api.getUserManager().getUser(player.getUniqueId());
	}
}
