package me.nipo.tabmodifier.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.nipo.tabmodifier.config.Config;

public class ExecutorReload implements CommandExecutor{
	@Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Config.reloadConfig();
        CommandRefresh.updateAllTab(toInteger(Config.showPrefix()), toInteger(Config.showSuffix()), toInteger(Config.showDisplayName()));
        src.sendMessage(Text.of(TextColors.YELLOW, "tabmodifier successfully reloaded"));
        return CommandResult.success();
    }
	
	private int toInteger(boolean boolvalue){
		return (boolvalue == true) ? 1 : 0;
	}
}
