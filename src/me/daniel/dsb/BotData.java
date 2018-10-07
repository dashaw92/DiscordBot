package me.daniel.dsb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import me.daniel.dsb.mods.Mod;

/*
 * BotData contains all the main data about this bot at run time.
 * It manages the config files on start/shutdown.
 * 
 * The current structure of files is as follows:
 * 
 * ../
 * ./
 * 	<bot.jar>
 * 	data/
 * 		cfg.txt
 * 		mods.txt
 * 		delta.txt
 * 		tags.dat
 * 		mods/
 * 
 * data/ is the root folder for all config files for this bot.
 * cfg.txt contains all the main configuration:
 * 		Bot token, authorized user IDs, command prefix,
 * 		owner user ID, and debug mode
 * 
 * mods.txt is a list of mods to disallow from loading.
 * 		Mod names listed here will not be allowed to load.
 * 
 * delta.txt is used by DeltaMod to keep track of deltas in servers.
 * 		Really, that's it lol. I should definitely put this in a
 * 		plugin specific folder (config per plugin :O).
 * 
 * tags.dat is a Java object serialized to a file.
 * 		It contains all the tags.
 * 
 * mods/
 * 		Custom mods will go in here. Not currently implemented.
 * 
 */
public final class BotData {
	
	//The root directory of all data files.
	private static final File ROOT = new File(new File(BotData.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile(), "/data");
	//Main bot settings
	private static final File CONFIG = new File(ROOT.getAbsolutePath() + "/cfg.txt");
	//Delta command data
	private static final File TRACKING = new File(ROOT.getAbsolutePath() + "/delta.txt");
	//Tag database (Serialized instances of tags)
	private static final File TAGDB = new File(ROOT.getAbsolutePath() + "/tags.dat");
	//Mod folder
	private static final File MODROOT = new File(ROOT.getAbsolutePath() + "/mods");
	//Mods to disable
	private static final File MODCFG = new File(ROOT.getAbsolutePath() + "/mods.txt");
	
	//Internal for initializing files. <File, Folder?>
	private static Map<File, Boolean> files = new HashMap<>();
	static {
		files.put(ROOT, true);
		files.put(CONFIG, false);
		files.put(TRACKING, false);
		files.put(TAGDB, false);
		files.put(MODROOT, true);
		files.put(MODCFG, false);
	}
	
	//Main bot data
	private static long OWNER_ID = 136651506098241536L; //The ID of the owner of this bot
	public static String CMD_CHAR = "."; //The character used to prefix commands
	public static boolean DEBUG = false; //Should any debug code run?
	protected static String TOKEN = ""; //The bot token
	private static final String DELIMITER = ":"; //What fields are delimited by in config files
	
	//Extra data
	public static Map<Long, Long> DELTAS = new HashMap<>(); //Deltas for the delta command
	private static List<Long> AUTH = new ArrayList<>(); //Users authorized to use this bot
	public static TreeMap<Integer, Tag> tags = new TreeMap<>(); //Tag database
	
	//Called when the bot is started. Initializes all data from files.
	public static void init() {
		System.out.println("\tBot data root: " + ROOT.getAbsolutePath());
		
		for(File f : files.keySet()) {
			if(f.exists()) continue;
			try {
				if(files.get(f)) f.mkdirs();
				else f.createNewFile();
			} catch(IOException e) {
				System.err.println("\tCould not create a data file.");
			}
		}
		
		try {
			parseConfig();
			parseMods();
			findMods();
			parseTracking();
			readTags();
		} catch(FileNotFoundException e) {
			System.err.println("\tAn error occurred initializing data:");
			e.printStackTrace();
			System.exit(-1);
		}
	}
		
	//Parses the main bot config file
	private static void parseConfig() throws FileNotFoundException {
		int index = 0;
		try(Scanner scanner = new Scanner(CONFIG)) {
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				index++;
				
				String type = line.split(DELIMITER)[0].toLowerCase();
				switch(type) {
				case "token": //Bot token
					TOKEN = line.split(DELIMITER)[1];
					break;
				case "owner": //Owner ID
					OWNER_ID = Long.parseLong(line.split(DELIMITER)[1]);
					break;
				case "auth": //Authorized user
					long id = Long.parseLong(line.split(DELIMITER)[1]);
					AUTH.add(id);
					break;
				case "debug": //Turn debug mode on
					DEBUG = true;
					break;
				case "char": //The string all commands are prefixed by
					CMD_CHAR = line.substring(type.length() + DELIMITER.length());
					break;
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("\tLine " + index + ": Invalid numerical data encountered in main config.");
		}
	}
	
	//Parses the mod config.
	//One entry per line. Every entry means "don't enable this plugin"
	private static void parseMods() throws FileNotFoundException {
		try(Scanner scanner = new Scanner(MODCFG)) {
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Mod.disabled.add(line.toLowerCase().trim());
			}
		}
	}
	
	private static void findMods() throws FileNotFoundException {
		Map<URL, String> mods = new HashMap<>();
		for(File child : MODROOT.listFiles()) {
			String path = "";
			try(JarFile jar = new JarFile(child)) {
				Scanner scanner = new Scanner(jar.getInputStream(jar.getEntry("info")));
				while(scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.startsWith("main: ")) {
						path = line.substring(6);
					}
				}
				if(path.trim().isEmpty()) {
					System.err.println("\tInvalid info in mod jar " + child.getName());
					continue;
				}
				scanner.close();
				mods.put(child.toURI().toURL(), path);
			} catch(IOException e) {
				System.err.println("\tMissing info file in " + child.getName() + ", skipping.");
				continue;
			}
		}
		
		URL[] urls = mods.keySet().toArray(new URL[mods.keySet().size()]);
		ClassLoader loader = URLClassLoader.newInstance(urls, BotData.class.getClassLoader());
		for(String s : mods.values()) {
			try {
				Class<?> clazz = Class.forName(s, true, loader);
				if(Mod.class.isAssignableFrom(clazz)) {
					Mod mod = (Mod)clazz.getDeclaredConstructor().newInstance();
					Mod.external.add(mod);
				}
			} catch (Exception e) {
				System.err.println("\tError loading class in external mod: " + s);
				e.printStackTrace();
				continue;
			}
		}
	}
	
	//Parses the delta command data
	private static void parseTracking() throws FileNotFoundException {
		Scanner scanner = new Scanner(TRACKING);
		int index = 0;
		while(scanner.hasNextLine()) {
			index++;
			String line = scanner.nextLine();
			String id = line.split(DELIMITER)[0];
			String delta = line.split(DELIMITER)[1];
			try {
				DELTAS.put(Long.parseLong(id), Long.parseLong(delta));
			} catch(NumberFormatException e) { 
				System.err.println("\tLine " + index + ": Invalid numerical data encountered in delta config.");
			}
		}
		scanner.close();
	}
	
	//Deserializes tags from the tag database file
	private static void readTags() throws FileNotFoundException {
		try(ObjectInputStream tag_in = new ObjectInputStream(new FileInputStream(TAGDB))) {
			int i = 0;
			Object read = tag_in.readObject();
			if(!(read instanceof ArrayList<?>)) {
				System.err.println("\tCould not deserialize tags from database.");
				return;
			}
			
			ArrayList<?> rawobjs = (ArrayList<?>)read;
			for(Object obj : rawobjs) {
				if(obj instanceof Tag) {
					tags.put(i++, (Tag)obj);
				} else {
					System.err.println("\tEncountered an invalid tag in database, skipping.");
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("\tCould not read tag database.");
			e.printStackTrace();
		}
	}
	
	//Serializes tags to the tag database file
	private static boolean saveTags() {
		try(ObjectOutputStream tag_out = new ObjectOutputStream(new FileOutputStream(TAGDB, false))) {
			ArrayList<Tag> taglist = new ArrayList<>();
			tags.values().forEach(taglist::add);
			tag_out.writeObject(taglist);
		} catch (IOException e) {
			System.err.println("\tCould not write tag database.");
			return false;
		}
		return true;
	}
	
	private static boolean saveTracking() {
		try(FileWriter fw = new FileWriter(TRACKING, false)) {
			for(long id : DELTAS.keySet()) {
				fw.write(id + DELIMITER + DELTAS.get(id) + "\r\n");
			}
		} catch(IOException e) {
			System.err.println("\tFailed to save delta config.");
			return false;
		}
		return true;
	}
	
	private static boolean saveConfig() {
		try(FileWriter fw = new FileWriter(CONFIG, false)) {
			fw.write("token" + DELIMITER + TOKEN + "\r\n");
			fw.write("owner" + DELIMITER + OWNER_ID + "\r\n");
			fw.write("char" + DELIMITER + CMD_CHAR + "\r\n");
			if(DEBUG) fw.write("debug\r\n");
			for(long id : AUTH) {
				fw.write("auth" + DELIMITER + id + "\r\n");
			}
		} catch(IOException e) {
			System.err.println("\tFailed to save main config.");
			return false;
		}
		return true;
	}

	//Public method for the specific save methods
	public static boolean save() {
		return saveTags() & saveTracking() & saveConfig();
	}
	
	public static long getOwnerId() {
		return OWNER_ID;
	}
	
	public static void authUser(long id) {
		if(AUTH.contains(id)) return;
		AUTH.add(id);
	}
	
	public static void deauthUser(long id) {
		if(!AUTH.contains(id)) return;
		AUTH.remove(id);
	}
	
	public static boolean isAuthorized(long id) {
		return AUTH.contains(id);
	}
	
	//Used by DataMod
	public static String serializeData() {
		String authed    = "[" + String.join(", ", AUTH.stream().map(n -> "" + n).collect(Collectors.toList())) + "]";
		String tracking  = "[" + Arrays.toString(DELTAS.entrySet().toArray()) + "]";
		String ids = Arrays.toString(BotData.tags.keySet().toArray());
		if(authed.equals("[]")) authed = "n";
		if(tracking.equals("[[]]")) tracking = "n";
		if(BotData.tags.isEmpty()) ids = "n";
		return String.format("a: %s\r\nδ: %s\r\nt: %s\r\ndg: %s", authed, tracking, ids, DEBUG);
	}
	
	@SuppressWarnings("serial")
	public static final class Tag implements Serializable {
		String author;
		String title;
		String content;
		
		public Tag(String title, String author, String content) {
			this.title = title.length() >= 32? title.substring(0, 32) : title;
			this.author = author;
			this.content = content;
		}
		
		public String toString() {
			return "`" + title + "` - **`" + author + "`**\r\n" + content;
		}
	}
}
