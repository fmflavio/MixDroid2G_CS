package br.com.mix;

///////////////////////Este codigo será mantido escondido para futuras versões

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class Edicao extends Activity {
	
	private TabHost abas = null;
	private EditText etCS = null;
	private Button bt_salvar = null;
	private Button bt_cancelar1 = null;
	private Button bt_cancelar2 = null;
	private Button bt_tocar1 = null;
	private Button bt_tocar2 = null;
	private String endereco = null;
	private String formato = null;
	private GerenciaSom gerenciaSom = new GerenciaSom();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edicao);
		
		// Recebe endereço do arquivo
	    Intent intent = getIntent();
	    endereco = intent.getStringExtra("ENDERECO");
	    formato = intent.getStringExtra("FORMATO");
	    
		bt_salvar = (Button) findViewById(R.id.salvar2);
		bt_salvar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salvarArquivo(endereco);
            }
        });
		bt_tocar1 = (Button) findViewById(R.id.tocar1);
		bt_tocar1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playAudio(endereco, formato);
            }
        });
		bt_tocar2 = (Button) findViewById(R.id.tocar2);
		bt_tocar2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playAudio(endereco, formato);
            }
        });
		bt_cancelar1 = (Button) findViewById(R.id.cancelar1);
		bt_cancelar1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
		bt_cancelar2 = (Button) findViewById(R.id.cancelar2);
		bt_cancelar2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
		abas = (TabHost) findViewById(R.id.tabhost);
		abas.setup();

		TabSpec descritor = abas.newTabSpec("aba1");
		descritor.setContent(R.id.aba1);
		descritor.setIndicator(getString(R.string.aba_manipulacao));
		abas.addTab(descritor);
		
		descritor = abas.newTabSpec("aba2");
		descritor.setContent(R.id.aba2);
		descritor.setIndicator(getString(R.string.aba_edicao));
		abas.addTab(descritor);
		
		etCS = (EditText) findViewById(R.id.editTextCS);
		if(formato.equals(".csd")){
			etCS.setText(carregaArquivo(endereco));
		} else {
			etCS.setText(R.string.edicao_textual_suportado+formato);
			etCS.setFocusable(false);
		}
	}
	
	//para as musicas caso seja fechado
	@Override
	protected void onDestroy() {
		super.onDestroy();
		gerenciaSom.paraTodasMusicas(1);  
	}

	@Override
	public void onPause() {
		super.onPause();
		gerenciaSom.paraTodasMusicas(1);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		gerenciaSom.paraTodasMusicas(1);
	}
	
	//método responsável por carregar o arquivo .csd
	private String carregaArquivo(String endereco){
		String line;
		String conteudo = "";
		BufferedReader br = null;
		try {
			// Acessa o arquivo.
			br = new BufferedReader(new FileReader(endereco));
			// Faz a leitura, uma linha por vez, até o fim do arquivo,
			while ((line = br.readLine()) != null) {
				conteudo += line;
				conteudo += "\n";// Quebra de linha.
			}
		} catch (Exception e) {
			Log.e("Erros", R.string.erro_2+"", e);
		} finally {
			if (br != null) {
				try {br.close();}
				catch(Exception e){}
			}
		}
		// Retorna o conteúdo do arquivo.
		return conteudo;
	}
	//Método responsavel por sobrescrever os arquivos .csd
	private void salvarArquivo(String endereco){
		File arq;
		FileOutputStream fos;
		byte[] dados;
		try {			         
			arq = new File(endereco);				                  
			dados = etCS.getText().toString().getBytes();		         
			fos = new FileOutputStream(arq);
			fos.write(dados);
			fos.flush();
			fos.close();
			Toast.makeText(getApplication(), R.string.gravacao_concluida, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplication(), R.string.erro_3+""+e, Toast.LENGTH_SHORT).show();
		} 
	}
	//método responsavel por executar os arquivos de  audio csound ou outros
	public void playAudio(String arquivo, String format){
		if(format.equals(".csd")){
			gerenciaSom.startPlayCS(arquivo);
		} else {
			gerenciaSom.startPlay(arquivo);
		}
	}
}