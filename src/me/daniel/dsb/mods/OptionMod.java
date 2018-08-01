package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class OptionMod extends Mod {
	/*
	 * Toggles debug mode or changes the command char used by the bot.
	 */

	public OptionMod() {
		super("!o<c|d>", new String[] { "o" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId()) {
			channel.sendMessage("?").queue();
			return;
		}
		
		switch(args[0].toLowerCase().charAt(0)) {
		case 'd': //Debug mode toggle
			BotData.DEBUG = !BotData.DEBUG;
			channel.sendMessage((BotData.DEBUG == true)? "1" : "0").queue();
			break;
		case 'c': //Command char modification
			if(args.length < 2) {
				channel.sendMessage("?").queue();
				return;
			}
			
			BotData.CMD_CHAR = args[1];
			BotData.save();
			channel.sendMessage(":ok_hand:").queue();
			break;
		default:
			channel.sendMessage("?").queue();
			break;
		}
	}
}
