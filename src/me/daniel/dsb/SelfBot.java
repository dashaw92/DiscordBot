package me.daniel.dsb;

import java.util.Arrays;

import javax.security.auth.login.LoginException;

import me.daniel.dsb.mods.Mod;
import me.daniel.dsb.music.Music;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/*
 * Hello! This is my "self" bot for Discord. It actually
 * is intended to run as a bot user because self bots are
 * against the ToS currently.
 * 
 * Notice: This bot is not intended to be user friendly!
 * I designed this in a way that makes sense to me, and
 * if my design doesn't suit you, R.I.P. you! :)
 * 
 * Usage breakdown: (ListMod) <PREFIX>m[a|i<m>]
 * Ignoring the prefix, this means the following:
 * The command `m` takes the following OPTIONAL
 * arguments: `a` and `i`.
 * 
 * The `a` argument takes no arguments, so to run it:
 * <PREFIX>m a
 * Which would show the list of all mods loaded with the
 * author of each mod as well.
 * 
 * The `i` argument takes one required argument: `m`
 * The `m` argument is a mod name, case-insensitive.
 * <PREFIX>m i listmod
 * Which will show information about ListMod.
 * 
 * TL;DR on usage information:
 * !: This command is only operable by the bot owner.
 * <PREFIX>: The character all commands are prefixed by
 * [ and ]: Optional arguments
 * |: Argument delimiter in usage. Not used in commands.
 * < and >: Required arguments
 * 
 * This mod always keeps the HelpMod loaded, so if you
 * are confused on what you can do, run `?` or `h`.
 */
public final class SelfBot implements EventListener {
	
	private static Music music;
	public static JDA jda;
	
	public static void main(String[] args) {
		System.out.println("Loading bot data...");
		BotData.init(); //Load up bot data
		System.out.println("Registering mods...");
		Mod.initMods(); //Register all mods
		System.out.println("Starting bot.");
		
		jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(BotData.TOKEN)
					.addEventListener(new SelfBot())
					.build();
		} catch(LoginException ex) {
			System.err.println("Encountered a LoginException: ");
			ex.printStackTrace();
			return;
		}
		
		while(jda.getStatus() != Status.SHUTDOWN && jda.getStatus() != Status.SHUTTING_DOWN) 
		{
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
		}
		
		getMusic().disconnect();
		System.out.println("Bot shutting down. Saving data...");
		if(!BotData.save()) {
			System.err.println("An error occurred while saving data on shutdown.");
		}
		System.out.println("Good bye.");
	}
	
	public static Music getMusic() {
		return music;
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof ReadyEvent) {
			music = new Music(event.getJDA().getGuildById(498239080518385705L));
			event.getJDA().getPresence().setGame(Game.watching("https://discord.gg/nh9W2SB"));
			return;
		}
		
		if(event instanceof GuildVoiceLeaveEvent) {
			GuildVoiceLeaveEvent ev = (GuildVoiceLeaveEvent)event;
			if(ev.getChannelLeft().getIdLong() == getMusic().chan_id) {
				if(ev.getChannelLeft().getMembers().size() < 2) {
					getMusic().pause();
				}
			}
			return;
		}
		
		if(event instanceof GuildVoiceJoinEvent) {
			GuildVoiceJoinEvent ev = (GuildVoiceJoinEvent)event;
			if(ev.getChannelJoined().getIdLong() == getMusic().chan_id) {
				getMusic().resume();
			}
			return;
		}
		
		if(event instanceof MessageReceivedEvent) {
			MessageReceivedEvent ev = (MessageReceivedEvent)event;
			String msg = ev.getMessage().getContentDisplay();
			
			//Ignore bots and non-authorized users.
			if(ev.getAuthor().isBot() || ev.getAuthor().getIdLong() != BotData.getOwnerId() && !BotData.isAuthorized(ev.getAuthor().getIdLong())) {
				return;
			}
			
			//Bot has received a command.
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
				if(BotData.DEBUG) {
					String format = String.format("[DEBUG] %s#%s ran command %s with args [%s]", ev.getAuthor().getName(), ev.getAuthor().getDiscriminator(), mod.name(), String.join(" ", args));
					System.out.println(format);
				}
				mod.run(args, ev.getAuthor(), ev.getChannel());

			}
		}
	}
}
