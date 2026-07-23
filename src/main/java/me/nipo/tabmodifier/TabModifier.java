package me.nipo.tabmodifier;

import com.google.inject.Inject;
import me.nipo.tabmodifier.commands.*;
import me.nipo.tabmodifier.config.Config;
import me.nipo.tabmodifier.listener.GroupListener;
import me.nipo.tabmodifier.listener.UserListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

@Plugin(id = "tabmodifier", name = "Tab Modifier", version = "2.0.1")
public class TabModifier {
    private static TabModifier instance;

    private static Object luckPerms;
    private static Optional<Object> placeholders = Optional.empty();

    @Inject
    Logger logger;

    @Inject
    Game game;

    @Inject
    PluginContainer pluginContainer;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;

    public static TabModifier getInstance() {
        return instance;
    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        instance = this;

        try {
            Class<?> luckPermsProviderClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            Method getMethod = luckPermsProviderClass.getMethod("get");
            luckPerms = getMethod.invoke(null);
            logger.info("LuckPerms found!");
        } catch (ClassNotFoundException e) {
            logger.error("LuckPerms is required but not installed!");
            throw new RuntimeException("LuckPerms dependency missing", e);
        } catch (Exception e) {
            logger.error("Failed to initialize LuckPerms!", e);
            throw new RuntimeException("LuckPerms initialization failed", e);
        }

        try {
            Class<?> placeholderServiceClass = Class.forName("me.rojo8399.placeholderapi.PlaceholderService");
            Optional<ProviderRegistration<?>> registration = Sponge.getServiceManager()
                    .getRegistration(placeholderServiceClass)
                    .map(reg -> (ProviderRegistration<?>) reg);

            if (registration.isPresent()) {
                placeholders = Optional.of(registration.get().getProvider());
                logger.info("PlaceholderAPI found!");
            } else {
                placeholders = Optional.empty();
                logger.warn("PlaceholderAPI not found - placeholders disabled");
            }
        } catch (ClassNotFoundException e) {
            placeholders = Optional.empty();
            logger.warn("PlaceholderAPI not found - placeholders disabled");
        } catch (Exception e) {
            placeholders = Optional.empty();
            logger.warn("PlaceholderAPI not available - placeholders disabled");
        }

        Config.buildConfig(configLoader, configFile);

        CommandSpec commands = CommandSpec.builder()
                .executor(this::parentCommandExecutor)
                .child(
                        CommandSpec.builder()
                                .description(Text.of("reload config"))
                                .permission("me.nipo.tabmodifier.reload")
                                .executor(new ExecutorReload())
                                .build(),
                        "reload"
                )
                .child(
                        CommandSpec.builder()
                                .description(Text.of("refresh tablist"))
                                .permission("me.nipo.tabmodifier.refresh")
                                .executor(new ExecutorRefresh())
                                .build(),
                        "refresh"
                )
                .child(
                        CommandSpec.builder()
                                .description(Text.of("set header value and save it to config file"))
                                .permission("me.nipo.tabmodifier.setheader")
                                .executor(new ExecutorSetHeader())
                                .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
                                .build(),
                        "setheader"
                )
                .child(
                        CommandSpec.builder()
                                .description(Text.of("set footer value and save it to config file"))
                                .permission("me.nipo.tabmodifier.setfooter")
                                .executor(new ExecutorSetFooter())
                                .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
                                .build(),
                        "setfooter"
                )
                .child(
                        CommandSpec.builder()
                                .description(Text.of("get list of subcommands and plugin info"))
                                .permission("me.nipo.tabmodifier.help")
                                .executor(this::parentCommandExecutor)
                                .arguments()
                                .build(),
                        "help"
                )
                .build();

        game.getCommandManager().register(instance, commands, "tabmodifier");

        new UserListener(instance);
        new GroupListener(instance);

        Task.builder()
                .execute(CommandRefresh::updatahdAndft)
                .intervalTicks(Config.getHFUpdateinterval() * 20L)
                .submit(instance);

        Task.builder()
                .execute(() -> CommandRefresh.updateAllTab(
                        toInteger(Config.showPrefix()),
                        toInteger(Config.showSuffix()),
                        toInteger(Config.showDisplayName())
                ))
                .intervalTicks(Config.getNameUpdateInterval() * 20L)
                .submit(instance);

        logger.info("************************************");
        logger.info("* Thank you for using Tab Modifier *");
        logger.info("************************************");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent event, @Root Player player) {
        refreshtablist(player);
    }

    public void refreshtablist(Player player) {
        int Delay = Config.getDelay();
        if (Delay > 0) {
            Task.builder()
                    .execute(() -> {
                        CommandRefresh.refreshOthers(player,
                                toInteger(Config.showPrefix()),
                                toInteger(Config.showSuffix()),
                                toInteger(Config.showDisplayName())
                        );
                        CommandRefresh.refreshSelf(player,
                                toInteger(Config.showPrefix()),
                                toInteger(Config.showSuffix()),
                                toInteger(Config.showDisplayName())
                        );
                    })
                    .delayTicks(Delay)
                    .submit(instance);
        } else {
            CommandRefresh.refreshOthers(player,
                    toInteger(Config.showPrefix()),
                    toInteger(Config.showSuffix()),
                    toInteger(Config.showDisplayName())
            );
            CommandRefresh.refreshSelf(player,
                    toInteger(Config.showPrefix()),
                    toInteger(Config.showSuffix()),
                    toInteger(Config.showDisplayName())
            );
        }
    }

    private int toInteger(boolean boolvalue) {
        return boolvalue ? 1 : 0;
    }

    public <T> T getLuckPerms() {
        return (T) luckPerms;
    }

    public <T> Optional<T> getPlaceholders() {
        return placeholders.map(v -> (T) v);
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
        return configLoader;
    }

    private CommandResult parentCommandExecutor(CommandSource src, CommandContext args) {
        Map<String, String> subCommands = new LinkedHashMap<>();
        subCommands.put("help", "me.nipo.tabmodifier.help");
        subCommands.put("reload", "me.nipo.tabmodifier.reload");
        subCommands.put("refresh", "me.nipo.tabmodifier.refresh");
        subCommands.put("setheader", "me.nipo.tabmodifier.setheader");
        subCommands.put("setfooter", "me.nipo.tabmodifier.setfooter");

        List<String> available = new ArrayList<>();
        for (Map.Entry<String, String> entry : subCommands.entrySet()) {
            if (src.hasPermission(entry.getValue())) {
                available.add(entry.getKey());
            }
        }

        if (available.isEmpty()) {
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&cYou don't have permission to use this command!"));
            return CommandResult.empty();
        } else {
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format(
                            "&7| &b&l%s &3(v%s)\n&7| &bAvailable subcommands:\n&7|   &3",
                            pluginContainer.getName(),
                            pluginContainer.getVersion().get()
                    ) + String.join(", ", available)
            ));
            return CommandResult.success();
        }
    }
}