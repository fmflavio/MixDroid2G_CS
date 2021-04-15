package br.com.mix;

import java.util.Timer;
import java.util.TimerTask;

public class Agendamento {
	private Timer timer;
    private String arquivo;
    private int milesegundosMusica;
    private GerenciaSom gerenciaSom = null;

    public Agendamento(GerenciaSom gerenciaSom, String arquivo, int milesegundosMusica) {
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
    	this.arquivo = arquivo;
    	this.milesegundosMusica = milesegundosMusica;
    	this.gerenciaSom = gerenciaSom;
  	
        timer = new Timer();
        timer.schedule(new ExecutandoTarefa(), milesegundosMusica);
    }
    class ExecutandoTarefa extends TimerTask {
        public void run() {
        	gerenciaSom.startPlay(arquivo, milesegundosMusica);
            timer.cancel(); //Terminate the timer thread
        }
    }
}
