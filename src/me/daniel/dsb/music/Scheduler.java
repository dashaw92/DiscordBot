package me.daniel.dsb.music;

import java.awt.Color;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import me.daniel.dsb.SelfBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
		}
	}
	
	public void nextTrack() {
		if(queue.peek() == null) {
			original.forEach(track -> queue.add(track.makeClone()));
			shuffle();
		}
		
		player.startTrack(queue.poll(), false);
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
			
			embed.setImage("attachment://cover.jpg")
			 .setDescription("");
			embed.setColor(Color.PINK);
			embed.addField("Title", title, true);
			embed.addField("Author", author, true);
	
			msg.setEmbed(embed.build());
			chan.sendFile(cover, "cover.jpg", msg.build()).queue();
		}
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
}
