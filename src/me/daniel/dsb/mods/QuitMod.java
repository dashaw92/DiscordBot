package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class QuitMod extends Mod {

	/*
	 * Quit command. Shuts the bot off.
	 */
	
	protected QuitMod() {
		super("!q", new String[] { "q" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId()) {
			channel.sendMessage("?").queue();
			return;
		}

		channel.getJDA().shutdown();
		try {
			Thread.sleep(30);
		} catch(InterruptedException e) {}
		System.exit(0);
	}

}
