package me.daniel.dsb.music;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class Music {

	private MusicManager mgr;
	private AudioManager audio;
	public long chan_id;
	
	public Music(Guild guild) {
		VoiceChannel vc = guild.getVoiceChannelsByName("play", true).get(0);
		chan_id = vc.getIdLong();
		audio = guild.getAudioManager();
		
		DefaultAudioPlayerManager manager = new DefaultAudioPlayerManager();
		manager.setPlayerCleanupThreshold(Long.MAX_VALUE);
		manager.registerSourceManager(new LocalAudioSourceManager());
		
		mgr = new MusicManager(manager);
		
		
		audio.setSendingHandler(mgr.sendHandler);
		audio.openAudioConnection(vc);
		if(vc.getMembers().size() < 2) pause();
	}
	
	public void shuffle() {
		mgr.scheduler.shuffle();
	}
	
	public void pause() {
		mgr.scheduler.pause();
	}
	
	public void resume() {
		mgr.scheduler.resume();
	}
	
	public void togglePause() {
		mgr.scheduler.player.setPaused(!mgr.scheduler.player.isPaused());
	}
	
	public void skip() {
		mgr.scheduler.nextTrack();
	}
	
	public String nowPlaying() {
		return mgr.scheduler.currentTitle();
	}
	
	public boolean searchAndPlay(String search) {
		return mgr.scheduler.searchAndPlay(search);
	}
	
	public void disconnect() {
		pause();
		try {
			audio.closeAudioConnection();
		} catch(Exception ignored) {}
		audio.setSendingHandler(null);
	}
}
