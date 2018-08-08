package me.daniel.dsb.mods;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class Base10Mod extends Mod {

	/*
	 * Base10 mod: Prints input as base 10.
	 */
	
	public Base10Mod() {
		super("10<b<b,n>>", new String[] { "10" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(args.length < 2) {
			channel.sendMessage("?").queue();
			return;
		}
		
		int base;
		try {
			base = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			channel.sendMessage("?").queue();
			return;
		}
		
		String coderaw = args[1];
		int code;
		try {
			code = Integer.parseInt(coderaw, base);
		} catch(NumberFormatException e) {
			channel.sendMessage("?").queue();
			return;
		}
		
		channel.sendMessage("" + code).queue();
	}
}
