package me.daniel.dsb.mods;

import java.time.Instant;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class DeltaMod extends Mod {

	/*
	 * Delta mod: Prints the time since the command was last run in a server in ms.
	 */
	
	public DeltaMod() {
		super("δ[r<id>|id]", new String[] { "δ" }, "Dan");
	}

	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		long guildid = ((TextChannel)channel).getIdLong();
		
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("r")) {
				if(args.length > 2) {
					guildid = Long.parseLong(args[1]);
				}
				
				if(BotData.DELTAS.containsKey(guildid)) {
					BotData.DELTAS.remove(guildid);
				} else {
					channel.sendMessage("?").queue();
				}
				return;
			} else {
				guildid = Long.parseLong(args[0]);
			}
		}
		
		BotData.DELTAS.putIfAbsent(guildid, Instant.now().toEpochMilli());
		long last = BotData.DELTAS.get(guildid);
		long delta = Instant.now().toEpochMilli() - last;
		channel.sendMessage(guildid + " Δ**" + delta + "**ms (`" + delta/1000 + "`s)").queue();
		
		BotData.DELTAS.put(guildid, Instant.now().toEpochMilli());
	}
	
}
