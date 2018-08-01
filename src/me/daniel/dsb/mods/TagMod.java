package me.daniel.dsb.mods;

import java.util.Arrays;

import me.daniel.dsb.BotData;
import me.daniel.dsb.BotData.Tag;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public final class TagMod extends Mod {

	/*
	 * Tag command. Allows saving content and recalling said content by id.
	 */
	
	protected TagMod() {
		super("t<a<title,content..>|!r<id>|c|id>", new String[] { "t" }, "Dan");
	}
	
	@Override
	public void run(String[] args, User user, MessageChannel channel) {
		if(args.length < 1) {
			channel.sendMessage("?").queue();
			return;
		}
		
		//Argument is the tag ID, look up and send the tag with that ID.
		if(Character.isDigit(args[0].charAt(0))) {
			int id = Integer.parseInt(args[0]);
			if(!BotData.tags.containsKey(id)) {
				channel.sendMessage("?").queue();
				return;
			}
			channel.sendMessage(BotData.tags.get(id).toString()).queue();
			return;
		}
		
		//Argument is 'c', send the current tag IDs.
		if(args[0].toLowerCase().charAt(0) == 'c') {
			if(BotData.tags.isEmpty()) {
				//No tags exist.
				channel.sendMessage("n").queue();
				return;
			}
			String ids = Arrays.toString(BotData.tags.keySet().toArray());
			channel.sendMessage(ids).queue();
			return;
		}
		
		//(a)dd and (r)emove commands.
		if(args.length < 2) {
			channel.sendMessage("?").queue();
			return;
		}
		
		//Remove tag command.
		if(args[0].toLowerCase().charAt(0) == 'r') {
			if(user.getIdLong() != BotData.getOwnerId()) {
				channel.sendMessage("?").queue();
				return;
			}

			int id = Integer.parseInt(args[1]);
			BotData.tags.remove(id);
			channel.sendMessage(":ok_hand:").queue();
			return;
		}
		
		//Add tag command
		if(args[0].toLowerCase().charAt(0) == 'a') {
			if(args.length < 3) {
				channel.sendMessage("?").queue();
				return;
			}
			
			String title = args[1];
			String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
			int nextid = 0;
			if(!BotData.tags.isEmpty()) {
				nextid = BotData.tags.lastKey() + 1;
			}
			String author = String.format("%s#%s", user.getName(), user.getDiscriminator());
			BotData.tags.put(nextid, new Tag(title, author, content));
			channel.sendMessage(":ok_hand:").queue();
			return;
		}
		
		//Who knows what they're trying to do. Whatever, it's not part of the tag mod.
		channel.sendMessage("?").queue();
	}

}
