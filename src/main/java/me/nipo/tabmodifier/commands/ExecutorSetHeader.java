package me.nipo.tabmodifier.commands;

import me.nipo.tabmodifier.config.Config;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorSetHeader implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String newText = args.<String>getOne("message").get();
        Config.setHeader(newText);
        Config.saveConfig();
        CommandRefresh.updatahdAndft();
        src.sendMessage(Text.of(TextColors.YELLOW, "header value set, tablist has been refreshed"));
        return CommandResult.success();
    }
}
