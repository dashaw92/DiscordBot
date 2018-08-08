package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class SaveMod extends Mod {

	/*
	 * Save mod: Saves bot data.
	 */
	
	public SaveMod() {
		super("!s", new String[] { "s" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId()) {
			channel.sendMessage("?").queue();
			return;
		}
		
		if(!BotData.save()) channel.sendMessage("?");
	}
	
}
