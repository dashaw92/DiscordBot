package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class DataMod extends Mod {

	/*
	 * Data mod: Displays bot data.
	 */
	
	public DataMod() {
		super("db", new String[] { "db" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		channel.sendMessage("```" + BotData.serializeData() + "```").queue();
	}
	
}
