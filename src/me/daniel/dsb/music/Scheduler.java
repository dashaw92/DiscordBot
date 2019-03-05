package me.daniel.dsb.music;

import java.awt.Color;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import me.daniel.dsb.SelfBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Scheduler extends AudioEventAdapter {

	final AudioPlayer player;
	final Queue<AudioTrack> queue;
	private Queue<AudioTrack> original;
	AudioTrack last;
	
	public Scheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedList<>();
		original = new LinkedList<>();
	}
	
	public void pause() {
		if(player.isPaused()) return;
		player.setPaused(true);
	}
	
	public void resume() {
		if(!player.isPaused()) return;
		player.setPaused(false);
	}
	
	public void queue(AudioTrack track) {
		if(!player.startTrack(track, true)) {
			queue.offer(track);
		} else {
			sendEmbed();
		}
	}
	
	public void nextTrack() {
		if(queue.peek() == null) {
			original.forEach(track -> queue.add(track.makeClone()));
			if(queue.peek().getIdentifier().equals(last.getIdentifier())) {
				queue.poll();
				queue.offer(last.makeClone());
			}
			shuffle();
		}
		
		player.startTrack(queue.poll(), false);
		
		sendEmbed();
	}
	
	public boolean searchAndPlay(String search) {
		final String query = search.toLowerCase();
		
		Optional<AudioTrack> first = original.stream()
										.filter(at -> {
											String title  = at.getInfo().title.toLowerCase();
											String author = at.getInfo().author.toLowerCase();
											return title.contains(query) || author.contains(query);
										})
										.findFirst();
		if(first.isPresent()) {
			player.startTrack(first.get().makeClone(), false);
			sendEmbed();
			return true;
		}
		return false;
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
		this.last = track;
		if(reason.mayStartNext) {
			nextTrack();
		}
	}
	
	public void shuffle() {
		Collections.shuffle((List<?>) queue);
	}
	
	public void setOriginal(Queue<AudioTrack> tracks) {
		original = tracks;
	}
	
	public String currentTitle() {
		if(player.getPlayingTrack() == null) return "Nothing is playing currently.";
		return player.getPlayingTrack().getInfo().title;
	}
	
	public void sendEmbed() {
		if(player.getPlayingTrack() != null) {	
			String path = player.getPlayingTrack().getIdentifier();
			File cover = new File(new File(path).getParentFile().getAbsolutePath(), "/cover.jpg");
			
			String title = player.getPlayingTrack().getInfo().title;
			String author = player.getPlayingTrack().getInfo().author;
			
			MessageChannel chan = SelfBot.jda.getGuildById(498239080518385705L).getTextChannelsByName("now", true).get(0);
			
			if(!cover.exists() || cover.isDirectory() || !cover.canRead()) {
				chan.sendMessage(String.format("Now Playing: `%s` by `%s`", title, author)).queue();
				return;
			}
			
			MessageBuilder msg = new MessageBuilder();
			EmbedBuilder embed = new EmbedBuilder();
			
			embed.setTitle(title);
			embed.setThumbnail("attachment://cover.jpg");
			embed.setColor(generateRandomColor(Color.WHITE));
			embed.addField("🎵☺", author, true);
	
			msg.setEmbed(embed.build());
			Message message = msg.build();
			chan.sendFile(cover, "cover.jpg", message).queue();
		}
	}
	
	//https://stackoverflow.com/a/43235
	public Color generateRandomColor(Color mix) {
	    Random random = new Random();
	    int red = random.nextInt(256);
	    int green = random.nextInt(256);
	    int blue = random.nextInt(256);

	    // mix the color
	    if (mix != null) {
	        red = (red + mix.getRed()) / 2;
	        green = (green + mix.getGreen()) / 2;
	        blue = (blue + mix.getBlue()) / 2;
	    }

	    Color color = new Color(red, green, blue);
	    return color;
	}
}
