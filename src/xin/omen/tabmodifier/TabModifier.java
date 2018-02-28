package xin.omen.tabmodifier;

import xin.omen.tabmodifier.commands.*;
import xin.omen.tabmodifier.utils.Utilities;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.config.ConfigDir;
import java.nio.file.Path;
import java.nio.file.Files;
import xin.omen.tabmodifier.config.Config;

import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.command.spec.*;
import org.spongepowered.api.text.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

@Plugin(id="tabmodifier", name="Tab Modifier", version="1.2.0-SNAPSHOT", authors="NipoCN", dependencies = {@Dependency(id = "luckperms", optional = false)})
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
	public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player){
		this.refresh();
	}
	
	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event, @Root Player player){
		this.refresh();
	}
	
	public Path getConfigDir(){
		return ConfigDir;
	}
	
	public void refresh(){
		//create task
		Sponge.getScheduler().createTaskBuilder().execute(() -> {Utilities.getInstance().updateAllPlayers();}).delayTicks(20).submit(TabModifier.getInstance());
	}
}
