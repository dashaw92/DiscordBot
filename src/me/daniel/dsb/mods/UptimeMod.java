package me.daniel.dsb.mods;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class UptimeMod extends Mod {

	private final long start;
	/*
	 * Tracks the uptime of the bot. It actually records when this mod loads, but meh.
	 */
	public UptimeMod() {
		super("u", new String[] { "u" }, "Dan");
		this.start = Instant.now().toEpochMilli();
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		String time = "";
		long seconds = ChronoUnit.SECONDS.between(Instant.ofEpochMilli(start), Instant.now());
		time = seconds + "s";
		if(seconds > 86_400) {
			time = ((seconds / 3600) / 24) + "d";
		} else if(seconds > 3600) {
			time = ((seconds / 60) / 60) + "h";
		} else if(seconds > 60) {
			time = (seconds / 60) + "m";
		}
		channel.sendMessage("U" + time).queue();
	}
	
}
