package me.daniel.dsb.mods;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class HelpMod extends Mod {
	/*
	 * Displays all currently loaded mods' help info (intended to be terse)
	 */
	private String cache = "";
	
	public HelpMod() {
		super("?|h", new String[] { "?", "h" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(cache.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(Mod m : Mod.mods) {
				sb.append(m.getHelp());
				sb.append(" ");
			}
			cache = sb.toString().trim();
		}
		channel.sendMessage(cache).queue();
	}
	
}
