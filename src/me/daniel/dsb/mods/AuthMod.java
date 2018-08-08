package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class AuthMod extends Mod {

	/**
	 * Auth mod: Auth or deauthorize a user from using this bot.
	 */
	public AuthMod() {
		super("!a<id|r<id>>", new String[] { "a" }, "Dan");
	}
	
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId() || args.length < 1) {
			channel.sendMessage("?").queue();
			return;
		}
		
		//Deauthorize a user.
		if(args[0].toLowerCase().charAt(0) == 'r') {
			if(args.length < 2) {
				channel.sendMessage("?").queue();
				return;
			}
			long userauth = Long.parseLong(args[1]);
			BotData.deauthUser(userauth);
		} 
		//Authorize a user
		else {
			long userauth = Long.parseLong(args[0]);
			BotData.authUser(userauth);
		}
		return;
	}
	
}
