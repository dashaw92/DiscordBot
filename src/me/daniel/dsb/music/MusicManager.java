package me.daniel.dsb.music;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.daniel.dsb.BotData;
import me.daniel.dsb.SelfBot;

public class MusicManager {

	private final AudioPlayerManager rawMgr;
	public final AudioPlayer player;
	public final Scheduler scheduler;
	public final AudioPlayerSendHandler sendHandler;
	
	public MusicManager(AudioPlayerManager manager) {
		rawMgr = manager;
		player = manager.createPlayer();
		scheduler = new Scheduler(player);
		sendHandler = new AudioPlayerSendHandler(player);
		player.addListener(scheduler);
		
		init();
	}
	
	private void init() {
		File music_path = new File(new File(BotData.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile(), "/music");
		if(!music_path.exists()) {
			System.out.println("Missing music directory.");
			music_path.mkdirs();
		}
		
		Queue<AudioTrack> original = new LinkedList<>();
		
		List<File> children = null;
		try {
			children = Files.walk(music_path.toPath()).map(Path::toFile).collect(Collectors.toList());
		} catch(IOException e) {
			System.err.println("Could not walk music directory:");
			e.printStackTrace();
			SelfBot.getMusic().disconnect();
			return;
		}
		
		Collections.shuffle(children);
		
		for(File file : children) {
			if(file.getName().endsWith(".flac")) {
				rawMgr.loadItemOrdered(rawMgr, file.getAbsolutePath(), new AudioLoadResultHandler() {
					
					@Override
					public void trackLoaded(AudioTrack track) {
						original.add(track);
						scheduler.queue(track);
					}
					
					@Override
					public void playlistLoaded(AudioPlaylist arg0) {}
					
					@Override
					public void noMatches() {}
					
					@Override
					public void loadFailed(FriendlyException arg0) {}
				});
			}
		}
		
		scheduler.setOriginal(original);
	}
	
}
