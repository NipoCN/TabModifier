package xin.omen.tabmodifier.utils;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.inject.Inject;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.GroupData;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import xin.omen.tabmodifier.config.Config;

import org.slf4j.Logger;

public class Utilities {
	
	@Inject
	private Logger logger;
	
	private static Utilities instance;
	public static Utilities getInstance(){
		if (instance == null){
			instance = new Utilities();
		}
		return instance;
	}
	
	public void updateAllPlayers(){
		for (Player player : Sponge.getServer().getOnlinePlayers()){
			updateTargetPlayer(player);
			player.getTabList().setHeaderAndFooter(
					TextSerializers.FORMATTING_CODE.deserialize(Config.getInstance().getheader()), 
					TextSerializers.FORMATTING_CODE.deserialize(Config.getInstance().getfooter())
					);
		}
	}
	
	public void updateTargetPlayer(Player player){
		Optional<ProviderRegistration<LuckPermsApi>> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class);
		if (provider.isPresent()){
			//get luckperms api instance
			LuckPermsApi api = provider.get().getProvider();
			String prefix, suffix;
			//update whole tablist
			for (Player players : Sponge.getServer().getOnlinePlayers()){
				//get prefix & suffix
				User user = api.getUser(players.getUniqueId());
				prefix = getuniqueprefix(user, api.getContextsForPlayer(players));
				if (prefix == null){
					prefix = getgroupprefix(api.getGroup(user.getPrimaryGroup().toString()));
					if (prefix == null){
						//use default prefix
						prefix = Config.getInstance().getprefix();
					}
				}
				//deal with suffix
				suffix = getuniquesuffix(user, api.getContextsForPlayer(players));
				if (suffix == null){
					suffix = getgroupsuffix(api.getGroup(user.getPrimaryGroup().toString()));
					if (suffix == null){
						//use default prefix
						suffix = Config.getInstance().getsuffix();
					}
				}
				//refresh tablist
				//player.getTabList().getEntry(player.getUniqueId()).get().setDisplayName();
				if (Config.getInstance().getdisableprefix()){
					if (Config.getInstance().getdisablesuffix()){
						player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
								TextSerializers.FORMATTING_CODE.deserialize(players.getName())
								);
					}
					else{
						//name + suffix
						player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
								TextSerializers.FORMATTING_CODE.deserialize(players.getName()+suffix)
								);
					}
				}
				else{
					if (Config.getInstance().getdisablesuffix()){
						//prefix + name
						player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
								TextSerializers.FORMATTING_CODE.deserialize(prefix+players.getName())
								);
					}
					else{
						//prefix + name + suffix
						player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
								TextSerializers.FORMATTING_CODE.deserialize(prefix+players.getName()+suffix)
								);
					}
				}
			}
		}
		else{
			logger.error("LuckPerms not found");
		}
	}
	
	public String getuniqueprefix(User user, Contexts contexts){
		UserData userdata = user.getCachedData();
		MetaData metadata = userdata.getMetaData(contexts);
	    return metadata.getPrefix();
	}
	
	public String getuniquesuffix(User user, Contexts contexts){
		UserData userdata = user.getCachedData();
		MetaData metadata = userdata.getMetaData(contexts);
	    return metadata.getSuffix();
	}
	
	public String getgroupprefix(Group group){
		GroupData groupdata = group.getCachedData();
		MetaData metadata = groupdata.getMetaData(Contexts.global());
		return metadata.getPrefix();
	}
	
	public String getgroupsuffix(Group group){
		GroupData groupdata = group.getCachedData();
		MetaData metadata = groupdata.getMetaData(Contexts.global());
		return metadata.getSuffix();
	}
}