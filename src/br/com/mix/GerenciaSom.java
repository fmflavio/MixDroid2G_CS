package br.com.mix;

import java.io.IOException;
import java.util.ArrayList;

public class GerenciaSom {
	//responsavel por executar midias de som
	private ArrayList<Musica> musica = new ArrayList<Musica>();
	private ArrayList<Musica> executando = new ArrayList<Musica>();
	private ArrayList<GerenciaCsound> csounds = new ArrayList<GerenciaCsound>();
	//private GerenciaCsound gCsound = new GerenciaCsound();

	final int TOCANDO = 1, PAUSADO = 2, PARADO = 3;
	
	//iniciar a Musica
	public boolean startPlay(String arquivo) {
		//atribui o tempo inicial da musica em 0 zero milesegundos
		return startPlay(arquivo, 0);
	}
	//iniciar a Musica com delay
	public boolean startPlay(String arquivo, int tempoInicio) {
		//se o array estiver vazio  criará a musica
		if(musica.isEmpty()){
			criaMusica(arquivo, tempoInicio);
			return true;
		} else {
			//para se algo estiver sendo tocado naquela posição
			for (int i = 0; i < musica.size(); i++) {
				//se encontrar o arquivo
				if(musica.get(i).getArquivo().equals(arquivo)){
					//se a musica chegar ao final ela será reiniciada
					if(musica.get(i).getMediaPlayer().getCurrentPosition() >= musica.get(i).getMediaPlayer().getDuration()){
						stopPlay(arquivo);
					}
					//se o arquivo estiver em execução no momento, o pausará e manterá estatos de ativo
					if(musica.get(i).getMediaPlayer().isPlaying() || musica.get(i).getMediaPlayer().isLooping()){
						musica.get(i).getMediaPlayer().pause();
						musica.get(i).setTocando(true);    
						return true;
					} else { //se o arquivo não estiver em execução no momento
						//se o arquivo encontrado não estiver tocando, mais já foi aberto, tocará novamente
						if(musica.get(i).isTocando()){
							musica.get(i).getMediaPlayer().start();
							return true;
						} else { //caso o arquivo tenha sido parado definitivamente, será aberto novamente
							try {
								musica.get(i).getMediaPlayer().setDataSource(arquivo);
								musica.get(i).getMediaPlayer().prepare();
								musica.get(i).getMediaPlayer().start();
								musica.get(i).setTocando(true); 	
								return true;
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//se o arquivo não estiver em execução no momento, porém pausado
						musica.get(i).getMediaPlayer().start();
						musica.get(i).setTocando(true); 
						return true;
					}
				} 
			}  
			//caso o array não esteja vazio, mas não exista nenhum arquivo parecido.
			criaMusica(arquivo);
			return true;
		}
	}
	
	public boolean startPlayCS(String arquivo){
		if(csounds.isEmpty()){
			criaMusicaCS(arquivo);
			return true;
		} else {
			//para se algo estiver sendo tocado naquela posição
			for (int i = 0; i < csounds.size(); i++) {
				//se encontrar o arquivo
				if(csounds.get(i).getEndereco().equals(arquivo)){
					//se o arquivo estiver em execução no momento, o pausará e manterá estatos de ativo
					if(csounds.get(i).isPlay()){
						csounds.get(i).stopCsound();
						csounds.get(i).setCsoundObj(null);
						csounds.get(i).setTocando(false); 
						csounds.remove(i);
						return true;
					}
				}
			}
			//caso o array não esteja vazio, mas não exista nenhum arquivo parecido.
			criaMusicaCS(arquivo);
			return true;
		}
	}
	
	//cria um novo arquivo de audio utilizando o endereço da musica
	public void criaMusica(String arquivo){
		//cria a musica com inicio em 0 milesegundos
		criaMusica(arquivo, 0);
	}
	public void criaMusica(String arquivo, int tempoInicio){
		//caso o arquivo não tenha sido encontrado
		//cria um som e adciona a lista de midias sonoras
		musica.add(new Musica(arquivo));
		//trata exeções do arquivo de audio e toca a Musica 
		try {
			musica.get(musica.size()-1).getMediaPlayer().setDataSource(arquivo);
			musica.get(musica.size()-1).getMediaPlayer().prepare();
			musica.get(musica.size()-1).getMediaPlayer().seekTo(tempoInicio);
			musica.get(musica.size()-1).getMediaPlayer().start();
			musica.get(musica.size()-1).setTocando(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void criaMusicaCS(String arquivo){
		csounds.add(new GerenciaCsound(arquivo));
		csounds.get(csounds.size()-1).startCsound();
		csounds.get(csounds.size()-1).setTocando(true);	
	}
	//Parar a Musica (uma musica)
	public void stopPlay(String arquivo) {
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).getArquivo().equals(arquivo)){
				musica.get(i).getMediaPlayer().stop();
				musica.get(i).getMediaPlayer().reset();
				musica.get(i).setMediaPlayer(null);
				musica.get(i).setTocando(false);
				musica.remove(i);
			}
		}
	}
	//Parar a Musica  Csound (uma musica Csound)
	public void stopPlayCS(String arquivo) {
		for (int i = 0; i < csounds.size(); i++) {
			if(csounds.get(i).getEndereco().equals(arquivo)){
				csounds.get(i).stopCsound();
				csounds.get(i).setCsoundObj(null);
				csounds.get(i).setTocando(false);
				csounds.remove(i);
			}
		}
	}
	//Parar todas as Musica
	public void paraTodasMusicas(int quantidadeLinhas) {
		for (int j = 0; j < quantidadeLinhas; j++) {
			//todas midias comuns
			for (int i = 0; i < musica.size(); i++) {
				musica.get(i).getMediaPlayer().stop();
				musica.get(i).getMediaPlayer().reset();
				musica.get(i).setMediaPlayer(null);
				musica.get(i).setTocando(false);
				musica.remove(i);
			}
			//todos arquivos csound
			for (int i = 0; i < csounds.size(); i++) {
				csounds.get(i).stopCsound();
				csounds.get(i).setCsoundObj(null);
				csounds.get(i).setTocando(false);
				csounds.remove(i);
			}
		}
	}
	//remove uma musica
	public void removeMusica(String arquivo){
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).getArquivo().equals(arquivo)){
				musica.get(i).getMediaPlayer().stop();
				musica.get(i).getMediaPlayer().reset();
				musica.get(i).setMediaPlayer(null);
				musica.get(i).setTocando(false);
				musica.remove(i);
			}
		}
	}
	public int isTocando(String arquivo){
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).getArquivo().equals(arquivo)){
				if(musica.get(i).isTocando() && musica.get(i).getMediaPlayer().isPlaying()){
					return TOCANDO;
				}  else {
					if(musica.get(i).isTocando()){
						return PAUSADO;
					} else {
						return PARADO;
					}
				}    			
			}
		}
		return PARADO;
	}
	public int getProgresso(String arquivo){
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).getArquivo().equals(arquivo)){
				return musica.get(i).getTempoCorrente();
			}
		}    	
		return -1;
	}
	public int getTempoTotalMusica(String arquivo) {
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).getArquivo().equals(arquivo)){
				return musica.get(i).getMediaPlayer().getDuration();
			}
		}    	
		return -1;
	}
	
	public ArrayList<Musica> getMusicas() {
		return musica;
	}
	
	public ArrayList<Musica> getMusicasTocando() {
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).isTocando() && musica.get(i).getMediaPlayer().isPlaying()){
				executando.add(musica.get(i));
			}   			
		}
		return executando;
	}
}