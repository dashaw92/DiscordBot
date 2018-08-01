package me.daniel.dsb;

import java.util.Arrays;

import javax.security.auth.login.LoginException;

import me.daniel.dsb.mods.Mod;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public final class SelfBot implements EventListener {
	
	public static void main(String[] args) {
		System.out.println("Loading bot data...");
		BotData.init(); //Load up bot data
		System.out.println("Registering mods...");
		Mod.initMods(); //Register all mods
		System.out.println("Starting bot.");
		
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(BotData.TOKEN)
					.addEventListener(new SelfBot())
					.buildBlocking();
		} catch(LoginException | InterruptedException ex) {
			System.err.println("Encountered a LoginException: ");
			ex.printStackTrace();
			return;
		}
		
		while(jda.getStatus() == Status.CONNECTED) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
		}
		
		System.out.println("Bot shutting down. Saving data...");
		if(!BotData.save()) {
			System.err.println("An error occurred while saving data on shutdown.");
		}
		System.out.println("Good bye.");
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof MessageReceivedEvent) {
			MessageReceivedEvent ev = (MessageReceivedEvent)event;
			String msg = ev.getMessage().getContentDisplay();
			
			//Ignore bots and non-authorized users.
			if(ev.getAuthor().isBot() || ev.getAuthor().getIdLong() != BotData.getOwnerId() && !BotData.isAuthorized(ev.getAuthor().getIdLong())) {
				return;
			}
			
			//Bot has recieved a command.
			if(msg.startsWith(BotData.CMD_CHAR)) {
				//Set up the parameters for the mod.
				String[] args = ev.getMessage().getContentDisplay().split(" ");
				String cmd = args[0].substring(BotData.CMD_CHAR.length());
				if(args.length < 1) {
					args = new String[] { "" };
				} else {
					args = Arrays.copyOfRange(args, 1, args.length);
				}
				
				Mod mod = Mod.getMod(cmd).orElse(null);
				if(mod == null) return;
				mod.run(args, ev.getAuthor(), ev.getChannel());

			}
		}
	}
}
