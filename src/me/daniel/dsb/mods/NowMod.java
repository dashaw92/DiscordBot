package me.daniel.dsb.mods;

import java.time.Instant;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class NowMod extends Mod {

	/*
	 * Now command: Displays the current epoch time in ms
	 */
	
	public NowMod() {
		super("n", new String[] { "n" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		channel.sendMessage("" + Instant.now().toEpochMilli()).queue();
	}
	
}
