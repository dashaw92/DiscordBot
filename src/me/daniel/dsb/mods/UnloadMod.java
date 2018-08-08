package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class UnloadMod extends Mod {

	/*
	 * Unload mod: This mod unloads a currently 
	 * enabled mod. An unloaded mod can be reloaded 
	 * by using LoadMod.
	 */
	public UnloadMod() {
		super("!un<name>", new String[] { "un" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId() || args.length < 1) {
			channel.sendMessage("?").queue();
			return;
		}
		
		String mod = args[0].trim();
		for(Mod m : Mod.mods) {
			if(m.name().equalsIgnoreCase(mod)) {
				if(m.getClass().equals(QuitMod.class) || m.getClass().equals(HelpMod.class)) {
					channel.sendMessage("?").queue();
					return;
				}
				Mod.mods.remove(m);
				break;
			}
		}
	}
	
}
