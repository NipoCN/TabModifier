package xin.omen.tabmodifier;

import xin.omen.tabmodifier.commands.*;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.config.ConfigDir;
import java.nio.file.Path;
import java.nio.file.Files;
import xin.omen.tabmodifier.config.Config;

import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.command.spec.*;
import org.spongepowered.api.text.*;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.GroupData;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import java.util.Optional;

@Plugin(id="tabmodifier", name="Tab Modifier", version="1.0.0-SNAPSHOT", authors="NipoCN")
public class TabModifier {
	
	@Inject
	private Game game;
	
	@Inject
	private Logger logger;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path ConfigDir;
	
	private static TabModifier instance;
	
	public static TabModifier getInstance(){
		if (instance == null){
			instance = new TabModifier();
		}
		return instance;
	}
	
	@Listener
	public void onServerStart(GameAboutToStartServerEvent event){
		instance = this;
		if (!Files.exists(ConfigDir)){
			try {
				Files.createDirectories(ConfigDir);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Config.getInstance().loadConfig();
		CommandSpec MainCommand = CommandSpec.builder()
				.description(Text.of("show version and author of plugin"))
				.executor(new CommandMain())
				.child(CommandSpec.builder()
					.description(Text.of("reload plugin"))
					.executor(new CommandReload())
					.build(), "reload"
				)
				.build();
		game.getCommandManager().register(this, MainCommand, "tabmodifier", "tab");
		logger.info("---------------------------------------------------------");
		logger.info("Thank you for using TabModifier made by NipoCN");
		logger.info("---------------------------------------------------------");
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event){
		this.refresh();
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Disconnect event){
		this.refresh();
	}
	
	public Path getConfigDir(){
		return ConfigDir;
	}
	
	public void refresh(){
		Optional<ProviderRegistration<LuckPermsApi>> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class);
		for (Player player : Sponge.getServer().getOnlinePlayers()){
			if (provider.isPresent()){
				LuckPermsApi api = provider.get().getProvider();
				User user = api.getUser(player.getUniqueId());
				Optional<Contexts> context = api.getContextForUser(user);
				UserData userdata = user.getCachedData();
				MetaData metadata = userdata.getMetaData(context.get());
				String prefix = metadata.getPrefix();
				String suffix = metadata.getSuffix();
				if (prefix == null){
					Contexts contexts = Contexts.global();
					Group group = api.getGroup(user.getPrimaryGroup());
					GroupData groupdata = group.getCachedData();
					MetaData groupmeta = groupdata.getMetaData(contexts);
					prefix = groupmeta.getPrefix();
				}
				if (suffix == null){
					Contexts contexts = Contexts.global();
					Group group = api.getGroup(user.getPrimaryGroup());
					GroupData groupdata = group.getCachedData();
					MetaData groupmeta = groupdata.getMetaData(contexts);
					prefix = groupmeta.getSuffix();
				}
				if (prefix == null) prefix = Config.getInstance().getprefix();
				if (suffix == null) suffix = Config.getInstance().getsuffix();
				String combinedtext = prefix+player.getName()+suffix;
				Text update = TextSerializers.FORMATTING_CODE.deserialize(combinedtext);
				player.getTabList().getEntry(player.getUniqueId()).get().setDisplayName(update);
			}
			else continue;
		}
	}
}
