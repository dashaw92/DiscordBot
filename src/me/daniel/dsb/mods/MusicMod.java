package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import me.daniel.dsb.SelfBot;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class MusicMod extends Mod {

	public MusicMod() {
		super("!μ<s|n|p|i>", new String[] { "μ" }, "Dan");
	}

	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId() || args.length < 1) {
			channel.sendMessage("?").queue();
			return;
		}

		switch(args[0].toLowerCase().charAt(0)) {
		case 's':
			SelfBot.getMusic().shuffle();
			break;
		case 'n':
			SelfBot.getMusic().skip();
			break;
		case 'p':
			SelfBot.getMusic().togglePause();
			break;
		case 'i':
			channel.sendMessage(SelfBot.getMusic().nowPlaying()).queue();
			break;
		}
	}

}
