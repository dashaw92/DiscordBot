package me.daniel.dsb.mods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

//Base class for Mods to extend.
public abstract class Mod {

	//Mods that shouldn't load.
	public static List<String> disabled = new ArrayList<>();
	
	public static final List<Mod> external = new ArrayList<>();
	protected static List<Mod> mods = new ArrayList<>();

	//Load all default mods (called on start)
	public static void initMods() {
		//All mods. New mods should be added here.
		Class<?>[] MOD_LIST = new Class<?>[] { AuthMod.class, Base10Mod.class, DataMod.class,
											   DeltaMod.class, FormatMod.class, HelpMod.class,
											   ListMod.class, LoadMod.class, OptionMod.class, 
											   QuitMod.class, SaveMod.class, TagMod.class, 
											   UnloadMod.class, UptimeMod.class
										 	 };

		for (Class<?> c : MOD_LIST) {
			//Check if `c` is a subclass of Mod
			if(Mod.class.isAssignableFrom(c)) {
				Mod mod = null;
				try {
					mod = (Mod) c.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					System.err.println("\tFailed to load mod: " + c.getSimpleName());
					continue;
				}
				
				registerMod(mod);
			}
		}
		
		external.forEach(Mod::registerMod);
	}

	//Called when a mod is loaded at start.
	//This method runs several checks to ensure the mod will work.
	public static boolean registerMod(Mod mod) {
		//Check if the mod should even load
		if(disabled.contains(mod.name().toLowerCase())) {
			if(mod.getClass().equals(HelpMod.class) || mod.getClass().equals(QuitMod.class)) {
				System.err.println("\tHelpMod and QuitMod cannot be disabled.");
			} else return false;
		}
		
		//Does the mod even have aliases?
		//True if aliases is null, aliases has 0 elements, or all the aliases are empty.
		if(mod.aliases == null || mod.aliases.length < 1 || Arrays.asList(mod.aliases).stream().filter(a -> a.trim().isEmpty()).count() == mod.aliases.length) {
			System.err.println("\tCould not register mod " + mod.name() + " because it has no aliases.");
			return false;
		}
		
		for(Mod m : mods) {
			//Check if any of the loaded mods conflicts with the new one (aliases)
			//This iterates through the already loaded mod's aliases, and filters
			//on the mod requested to be loaded's aliases. If the alias from the
			//new mod conflicts, error will be true.
			Stream<String> stream = Arrays.asList(m.getAliases()).stream().filter(alias -> Arrays.asList(mod.getAliases()).contains(alias));
			String conflicts = String.join(", ", stream.collect(Collectors.toList()));
			
			if(!conflicts.isEmpty()) {
				System.err.println("\tCould not register mod " + mod.name() + " due to conflicting aliases already in use: " + conflicts);
				return false;
			}
			
			if(m.name().equalsIgnoreCase(mod.name())) {
				System.err.println("\tCould not register mod " + mod.name() + " because the name is already in use by another mod.");
				return false;
			}
		}
		
		mods.add(mod);
		System.out.println("\tMod loaded: " + mod.name());
		return true;
	}

	//Look up a mod by an alias
	public static Optional<Mod> getMod(String cmd) {
		for (Mod mod : mods) {
			for(String s : mod.getAliases()) {
				if(s.equalsIgnoreCase(cmd)) return Optional.of(mod);
			}
		}
		return Optional.empty();
	}
	
	public static int modsLoaded() {
		return mods.size();
	}
	
	//Instance methods for a child of Mod
	
	private String author; //Who wrote the mod
	private String help; //This is used by HelpMod.
	private String[] aliases; // The alias is what is checked in user input: CMD_CHAR<command> [args..]

	public Mod(String help, String[] aliases, String... author) {
		if(author.length < 0) { 
			this.author = "N/A"; 
		} else {
			this.author = String.join(", ", author).replace("`", "").replace("*", "").replace("_", "").replace("~", "").trim();
		}
		this.help = help;
		for(int i = 0; i < aliases.length; i++) {
			aliases[i] = aliases[i].toLowerCase();
		}
		this.aliases = aliases;
	}

	public abstract void run(String[] args, User user, MessageChannel channel);

	public final String getAuthor() {
		return author;
	}
	
	public final String getHelp() {
		return help;
	}

	public final String[] getAliases() {
		return Arrays.copyOf(aliases, aliases.length);
	}
	
	public final String name() {
		return getClass().getSimpleName();
	}
}
