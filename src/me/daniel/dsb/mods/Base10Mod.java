package me.daniel.dsb.mods;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class Base10Mod extends Mod {

	/*
	 * Base10 mod: Prints octal/hexadecimal input as decimal 
	 */
	
	public Base10Mod() {
		super("10<x<n>|o<n>>", new String[] { "10" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(args.length < 2) {
			channel.sendMessage("?").queue();
			return;
		}
		
		int base;
		switch(args[0].toLowerCase().charAt(0)) {
		case 'o':
			base = 8;
			break;
		case 'x':
			base = 16;
			break;
		default:
			channel.sendMessage("?").queue();
			return;
		}
		String coderaw = args[1];
		int code = Integer.parseInt(coderaw, base);
		channel.sendMessage("" + code).queue();
	}
}
