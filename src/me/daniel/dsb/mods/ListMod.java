package me.daniel.dsb.mods;

import java.util.stream.Collectors;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class ListMod extends Mod {

	/*
	 * List mod displays all mods currently loaded, or gives information about a mod
	 */
	
	public ListMod() {
		super("m[a|i<m>]", new String[] { "m" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		//Display info about a specific mod
		if(args.length > 1 && args[0].toLowerCase().charAt(0) == 'm') {
			if(args.length < 2) {
				channel.sendMessage("?").queue();
				return;
			}
			
			//Find the mod the user requested, display the aliases used by the mod.
			String mod = args[1];
			for(Mod m : Mod.mods) {
				if(m.name().equalsIgnoreCase(mod)) {
					String msg = String.format("`%s` by `%s`: [%s]", m.name(), m.getAuthor(), String.join(", ", m.getAliases()));
					channel.sendMessage(msg).queue();
					return;
				}
			}
			
			//The mod they requested does not exist.
			channel.sendMessage("?").queue();
			return;
		}
		
		//Display author info?
		final boolean extra = args.length > 0 && args[0].toLowerCase().charAt(0) == 'a';
		//Formats all mods into a single comma-separated string.
		String list = String.join(", ", Mod.mods.stream().map(mod -> {
			String name = mod.name();
			if(!extra) return name;
			String author = mod.getAuthor();
			return String.format("%s(%s)", name, author);
		}).collect(Collectors.toList()));
		channel.sendMessage("```" + list + "```").queue();
	}
}
