package br.com.mix;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.util.Log;

public class GravaAudioMP3 {
	private MediaRecorder mediaRecorder;
	private static File audiofile = null;
	private boolean gravando = false;
	private String armazenamentoPadrao = null;
	private String pastaPadraoGravacao = null;
	private String diretorioGravacao = null;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat data =  new SimpleDateFormat("yyyyMMddHHmmss");
	private String nomeArquivo = "SuaMixagem.mp3";
	private Musica musica;
	
	public GravaAudioMP3(String armazenamentoPadrao, String pastaPadraoGravacao) {
		this.setArmazenamentoPadrao(armazenamentoPadrao);
		this.setPastaPadraoGravacao(pastaPadraoGravacao);
		this.diretorioGravacao = armazenamentoPadrao+pastaPadraoGravacao;
	}

	public void startRec(){
        audiofile = new File(diretorioGravacao, getNameFile());
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(audiofile.getAbsolutePath());
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			setGravando(true);
		} catch (IllegalStateException e) {
			Log.d("", "teste - "+e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("", "teste - "+e);
			e.printStackTrace();
		}	
	}

	public void stop(){
		mediaRecorder.stop();
		mediaRecorder.release();
		setGravando(false);
		processaudiofile();
	}
	
	protected void processaudiofile() {
	   ContentValues values = new ContentValues(3);
	   long current = System.currentTimeMillis();
	   values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
	   values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
	   values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
	   values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());	     
	}

	public boolean isGravando() {
		return gravando;
	}

	public void setGravando(boolean gravando) {
		this.gravando = gravando;
	}
	
	public void tocar(){
		try {
			musica = new Musica(diretorioGravacao+nomeArquivo);
			musica.getMediaPlayer().setDataSource(diretorioGravacao+nomeArquivo);
			musica.getMediaPlayer().prepare();
			musica.getMediaPlayer().start();
			musica.setTocando(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void stopPlay(){
		musica.getMediaPlayer().stop();
	}
	
	public boolean isPlaying(){
		return musica.getMediaPlayer().isPlaying();
	}

	public String getArmazenamentoPadrao() {
		return armazenamentoPadrao;
	}

	public void setArmazenamentoPadrao(String armazenamentoPadrao) {
		this.armazenamentoPadrao = armazenamentoPadrao;
	}

	public String getPastaPadraoGravacao() {
		return pastaPadraoGravacao;
	}

	public void setPastaPadraoGravacao(String pastaPadraoGravacao) {
		this.pastaPadraoGravacao = pastaPadraoGravacao;
	}
	
	private String getNameFile(){
		nomeArquivo = "mix"+data.format(new Date())+".mp3";
		return nomeArquivo;		
	}
}
