package me.daniel.dsb.mods;

import me.daniel.dsb.BotData;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * <b>WARNING: This mod can load arbitrary code.     
 * DO NOT- I repeat- DO NOT load mods you don't   
 * trust! Risks include downloading arbitrary     
 * files to your computer, leaking your bot token,
 * ratting your computer, and more. Damage        
 * caused by this mod is not my responsibility.</b>
**/
public final class LoadMod extends Mod {

	//Makes it easier to load my own mods
	public static final String COREPREFIX = "~";
	
	/*
	 * Load mod: Uses reflection to load a mod
	 * Paths are case sensitive because this is
	 * a hardcore bot for hardcore lusers only!1
	 * Just kidding, I'm just too lazy to make my 
	 * own class loader lol
	 * 
	 * Warning: This mod is not protected. If it's
	 * unloaded, there is no way to get it back
	 * without restarting the bot (or using another
	 * mod that duplicates this mod??). "So why not
	 * protect it?"... where's the fun in that???
	 * 
	 */
	public LoadMod() {
		super("!l<p>", new String[] { "l" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(user.getIdLong() != BotData.getOwnerId() || args.length < 1) {
			channel.sendMessage("?").queue();
			return;
		}
		
		String path = args[0];
		if(path.startsWith(COREPREFIX)) {
			path = "me.daniel.dsb.mods." + path.substring(COREPREFIX.length());
		}
		
		/*
		 * Meta note to readers: This class was written after I
		 * decided to change the design of this bot. Prior to
		 * that decision, the bot sent ":ok_hand:" when some
		 * mods were successful, and "?" otherwise.
		 * The decision was to only send "?" on errors, which
		 * still conveys the correct meaning: If you see "?",
		 * something's wrong. Otherwise, it's all good!
		 * The unfortunate side effect is that code prior to that
		 * decision is designed in a way that makes it easy to have
		 * success messages, with "?" messages thrown around in error
		 * branches. The difference here is that there is only one
		 * path errors can follow, which leads to a single "?" at the
		 * end of this try block. Hopefully that makes sense, and I hope
		 * when I read this comment in several years when I'm feeling
		 * nostalgic, I won't be like "wtf? lol". :-)
		 * 2nd August 2018
		 * 
		 * Also this isn't safe at all, don't load mods you don't trust
		 * Trust = you've read the source code of said mod and compiled
		 * it yourself after verifying it's safe.
		 */
		try {
			Class<?> clazz = Class.forName(path);
			if(Mod.class.isAssignableFrom(clazz)) {
				Mod m = (Mod)clazz.getDeclaredConstructor().newInstance();
				if(Mod.registerMod(m)) return;
			}
		} catch (Exception e) {}
		channel.sendMessage("?").queue();
	}
	
}
