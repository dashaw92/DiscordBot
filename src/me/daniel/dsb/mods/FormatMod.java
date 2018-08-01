package me.daniel.dsb.mods;

import java.util.Arrays;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class FormatMod extends Mod {

	/*
	 * Format command.
	 * a[o|x]: Prints argument, character by character, as a decimal/octal/hexa number corresponding to the ASCII value
	 * l: Prints argument as all lowercase
	 * u: Prints argument as all uppercase
	 */
	
	protected FormatMod() {
		super("f<a[o|x],<msg>|l<msg>|u<msg>>", new String[] { "f" }, "Dan");
		
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(args.length < 2) {
			channel.sendMessage("?").queue();
			return;
		}
		
		switch(args[0].toLowerCase().charAt(0)) {
		case 'u': //Uppercase
			channel.sendMessage("" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toUpperCase()).queue();
			return;
		case 'l': //Lowercase
			channel.sendMessage("" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase()).queue();
			return;
		case 'a': //ASCII
			int mode = 0;
			if(args[0].length() > 1) {
				if(args[0].toLowerCase().charAt(1) == 'x') mode = 1; //Hexadecimal mode
				if(args[0].toLowerCase().charAt(1) == 'o') mode = 2; //Octal mode
			}
			
			String flat = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			String chars = "";
			for(char c : flat.toCharArray()) {
				if(mode == 1) {
					//hex
					chars += String.format("%X ", (int)c);
				} else if(mode == 2) {
					//octal
					chars += String.format("%o ", (int)c);
				} else {
					//decimal
					chars += (int)c + " ";
				}
			}
			channel.sendMessage("" + chars).queue();
			return;
		default:
			channel.sendMessage("?").queue();
			return;
		}
		
	}

}
