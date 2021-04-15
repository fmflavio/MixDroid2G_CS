package br.com.mix;

import java.io.File;
import android.annotation.SuppressLint;
import com.csounds.CsoundObj;

@SuppressLint("Instantiatable")
public class GerenciaCsound {
	private String endereco;
	private File file;
	private boolean tocando = false;
	private CsoundObj csoundObj = new CsoundObj();
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public CsoundObj getCsoundObj() {
		return csoundObj;
	}

	public void setCsoundObj(CsoundObj csoundObj) {
		this.csoundObj = csoundObj;
	}

	public GerenciaCsound(String endereco){
		this.setEndereco(endereco);
	}
	
	public void setEndereco(String endereco) {
		this.endereco = endereco;
		this.file = new File(endereco);
	}
	
	public String getEndereco() {
		return endereco;	
	}
	
	public boolean isPlay() {
		return !csoundObj.isMuted();
	}
	
	public void startCsound(){
		csoundObj.startCsound(file);
	}
	
	public void pauseCsound(){
		csoundObj.pause();
	}
	
	public void stopCsound(){
		csoundObj.stopCsound();
	}
	
	public boolean isTocando() {
		return tocando;
	}

	public void setTocando(boolean tocando) {
		this.tocando = tocando;
	}
}
