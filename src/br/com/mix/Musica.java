package br.com.mix;

import android.media.MediaPlayer;

public class Musica {
	//responsavel por executar midias de som
	private String arquivo;
	private boolean tocando = false;
	private int tempoCorrente;
	private MediaPlayer mediaPlayer = new MediaPlayer();

	public Musica(String arquivo) {
		this.setArquivo(arquivo);
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isTocando() {
		return tocando;
	}

	public void setTocando(boolean tocando) {
		this.tocando = tocando;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public int getTempoCorrente() {
		tempoCorrente = mediaPlayer.getCurrentPosition();
		return tempoCorrente;
	}

	public void setTempoCorrente(int tempoCorrente) {
		this.tempoCorrente = tempoCorrente;
	}	
}
