package me.nipo.tabmodifier;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.*;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;

import org.slf4j.Logger;
import com.google.inject.Inject;
import me.nipo.tabmodifier.commands.CommandRefresh;
import me.nipo.tabmodifier.commands.ExecutorRefresh;
import me.nipo.tabmodifier.commands.ExecutorReload;
import me.nipo.tabmodifier.commands.ExecutorSetFooter;
import me.nipo.tabmodifier.commands.ExecutorSetHeader;
import me.nipo.tabmodifier.config.Config;
import me.nipo.tabmodifier.listener.GroupListener;
import me.nipo.tabmodifier.listener.UserListener;
import me.lucko.luckperms.api.LuckPermsApi;

import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "tabmodifier", name = "Tab Modifier", version = "1.4.0", authors = "NipoCN", 
dependencies = {@Dependency(id = "luckperms", optional = false), @Dependency(id = "placeholderapi", optional = false)})
public class tabmodifier {
	@Inject
	Logger logger;
	
	@Inject
	Game game;
	
	private static tabmodifier instance;
	private static LuckPermsApi api;
	
	private static PlaceholderService phservice;
	
	public static tabmodifier getInstance(){
		return instance;
	}
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File configFile;
	
	
	@Listener
	public void onServerStart(GameAboutToStartServerEvent event){
		instance = this;
		api = Sponge.getServiceManager().getRegistration(LuckPermsApi.class).get().getProvider();
		phservice = Sponge.getServiceManager().getRegistration(PlaceholderService.class).get().getProvider();
		Config.buildConfig(configLoader, configFile);
		CommandSpec commands = CommandSpec.builder()
								.child(
							     CommandSpec.builder()
							    .description(Text.of("reload config"))
							    .permission("tabmodifier.reload")
							    .executor(
								    new ExecutorReload()
							 	)
							    .build(), "reload"
							    )
								  .child(
								   CommandSpec.builder()
								   .description(Text.of("refresh tablist"))
								   .permission("tabmodifier.refresh")
								   .executor(
									   new ExecutorRefresh()
									)
								   .build(), "refresh"
								   )
								  .child(CommandSpec.builder()
										  .description(Text.of("set header value and save it to config file"))
										  .permission("tabmodifier.setheader")
										  .executor(new ExecutorSetHeader())
										  .arguments(
												  GenericArguments.remainingJoinedStrings(Text.of("message"))
												  )
										  .build()
										  , "setheader")
								  .child(CommandSpec.builder()
										  .description(Text.of("set footer value and save it to config file"))
										  .permission("tabmodifier.setfooter")
										  .executor(new ExecutorSetFooter())
										  .arguments(
												  GenericArguments.remainingJoinedStrings(Text.of("message"))
												  )
										  .build()
										  , "setfooter")
								  .build();
		game.getCommandManager().register(instance, commands, "tabmodifier", "tab");
		new UserListener(instance, api);
		new GroupListener(api);
		Task.builder().execute(new Runnable(){
			public void run(){
				CommandRefresh.updatahdAndft();
			}
		}).intervalTicks((long)(Config.getHFUpdateinterval()*20)).submit(instance);
		Task.builder().execute(new Runnable(){
			public void run(){
				CommandRefresh.updateAllTab(toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
			}
		}).intervalTicks((long)(Config.getNameUpdateInterval()*20)).submit(instance);
		logger.info("************************************");
		logger.info("* Thank you for using Tab Modifier *");
		logger.info("************************************");
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent event, @Root Player player){
		refreshtablist(player);
	}
	
	public void refreshtablist(Player player){
		int Delay = Config.getDelay();
		if (Delay > 0){
			Task.builder().execute(new Runnable(){
				public void run(){
					CommandRefresh.refreshOthers(player, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
					CommandRefresh.refreshSelf(player, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
				}
			}).delayTicks((long)Delay).submit(instance);
		}
		else{
			CommandRefresh.refreshOthers(player, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
			CommandRefresh.refreshSelf(player, toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
		}
	}
	
	private int toInteger(boolean boolvalue){
		return (boolvalue == true) ? 1 : 0;
	}
	
	public LuckPermsApi getLuckPerms(){
		return api;
	}
	
	public PlaceholderService getPlaceHolder(){
		return phservice;
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader(){
		return configLoader;
	}
}
