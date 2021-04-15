package br.com.mix;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import android.media.MediaMetadataRetriever;

public class CarregaListagem {
	
	private String diretorioMusical;
	
    public ArrayList<PropriedadesMusica> listaArquivos(){ 
    	File diretorio = new File(diretorioMusical);
    	MediaMetadataRetriever meta = new MediaMetadataRetriever();
    	File[] diretorioMusical = diretorio.listFiles();
    	
    	ArrayList<PropriedadesMusica> propriedades = new ArrayList<PropriedadesMusica>();
    	
    	for (File arquivo : diretorioMusical) {
    		PropriedadesMusica pm = new PropriedadesMusica();
    		
			if(arquivo.getName().endsWith(".mp3")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".mp3");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".wav")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".wav");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".wave")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".wave");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".wma")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".wma");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".aac")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".aac");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".aiff")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".aiff");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".pcm")){
				meta.setDataSource(arquivo.getAbsolutePath());
				String duracao = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				String titulo = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null ? meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) : arquivo.getName();
				pm.setNome(arquivo.getName());
				pm.setFormato(".pcm");
				pm.setTitulo(titulo);
				pm.setDuracao(covertMile(duracao));
				pm.setDuracaoMile(duracao);
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
			if(arquivo.getName().endsWith(".csd")){
				pm.setNome(arquivo.getName());
				pm.setFormato(".csd");
				pm.setTitulo("Formato Csound");
				pm.setDuracao("IND");
				pm.setDuracaoMile("0");
				pm.setEndereco(arquivo.getAbsolutePath());
				propriedades.add(pm);
			}
		}
    	return propriedades;
    }
	
	public void setDiretorioCS(String diretorioMusical) {
		this.diretorioMusical = diretorioMusical;
	}
	
	private String covertMile(String temp){
		long durationInMs = Long.parseLong(temp);
		double durationInMin = ((double)durationInMs/1000.0)/60.0;
		durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue(); 
		return durationInMin+"";
	}
}