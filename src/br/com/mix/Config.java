package br.com.mix;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Config extends Activity{
	private static String armazenamentoPadrao = Environment.getExternalStorageDirectory().toString();
	private static final String chave = "config";
	private static final String chavePastaMusicas = "pastaMusicas";
	private static final String chavePastaGravacao = "pastaGravacao";
	private static final String chaveFormato = "formatoGravacao";
	private static final String chaveMIC = "estadoMirofone";
	private static String[] FORMATOS = null;
	private static final int MP3 = 0, WAV = 1;
	private int formatoPosicao = WAV;
	private Resources res = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);

		final SharedPreferences preferencia = getSharedPreferences(chave, 0);
		
		//responsavel por carregar as strings de textos internacionalizadas
		res = getResources();
		FORMATOS = new String[]{res.getString(R.string.formato_mp3), res.getString(R.string.formato_WAV)};
		
		//referente as caixas de textos
		final TextView editTextEnderecoMusicas = (TextView)findViewById(R.id.editTextEnderecoMusicas);
		final TextView editTextEnderecoGravacao = (TextView)findViewById(R.id.editTextEnderecoGravacao);
		
		//referente ao spiner
		final Spinner spinner = (Spinner) findViewById(R.id.spinner_formato_gravacao);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FORMATOS);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		    	formatoPosicao = position;
		    }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		//referente botao do microfone
		final ToggleButton botaoMic = (ToggleButton)findViewById(R.id.tb_microfone_conf);
		//trabalhando com as preferencias
		if(preferencia.contains(chavePastaMusicas)){
			editTextEnderecoMusicas.setText(preferencia.getString(chavePastaMusicas, ""));
		}
		if(preferencia.contains(chavePastaGravacao)){
			editTextEnderecoGravacao.setText(preferencia.getString(chavePastaGravacao, ""));
		}
		if(preferencia.contains(chaveFormato)){
			spinner.setSelection(Integer.parseInt(preferencia.getString(chaveFormato, "")));
		}
		if(preferencia.contains(chaveMIC)){
			if(preferencia.getString(chaveMIC, "").equals("true")){
				botaoMic.setChecked(true);
			} else {
				botaoMic.setChecked(false);
			}
		}
		
		findViewById(R.id.buttonSalvarConfig).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder mensagem = new Builder(Config.this);
				mensagem.setMessage(res.getString(R.string.deseja_salvar));
				mensagem.setPositiveButton(res.getString(R.string.sim), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = preferencia.edit();
						editor.putString(chavePastaMusicas, editTextEnderecoMusicas.getText().toString());
						editor.putString(chavePastaGravacao, editTextEnderecoGravacao.getText().toString());
						if(formatoPosicao != WAV && formatoPosicao != MP3){
							formatoPosicao = WAV;
						}
						editor.putString(chaveFormato, formatoPosicao+"");
						if(botaoMic.isChecked()){
							editor.putString(chaveMIC, "true");
						} else {
							editor.putString(chaveMIC, "false");
						}
						if(testaPasta(editTextEnderecoMusicas.getText().toString()) && testaPasta(editTextEnderecoGravacao.getText().toString()) &&
								criarDiretorioSeNaoExistir(editTextEnderecoMusicas.getText().toString(), editTextEnderecoGravacao.getText().toString())){
							editor.commit();
							Toast.makeText(getBaseContext(), res.getString(R.string.salvo_com_sucesso), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getBaseContext(), res.getString(R.string.erro_1), Toast.LENGTH_LONG).show();
						}
						finish();
					}
				});
				mensagem.setNegativeButton(res.getString(R.string.nao), null);
				mensagem.show();
			}
		});
	}
	
	private boolean testaPasta(String pasta){
		if(pasta.charAt(pasta.length()-1) != '/' || pasta.charAt(0) != '/'){
			return false;
		} else{
			return true;
		}
	}
	//cria uma pasta no cartão externo caso não haja
	public static boolean criarDiretorioSeNaoExistir(String enderecoMusicas, String enderecoGravacao) {
		boolean resposta = true;
		File pasta1 = new File(armazenamentoPadrao+enderecoMusicas);
		if (!pasta1.exists()) {
			if (!pasta1.mkdirs()) {
				resposta = false;
			}
		}
		File pasta2 = new File(armazenamentoPadrao+enderecoGravacao);
		if (!pasta2.exists()) {
			if (!pasta2.mkdirs()) {
				resposta = false;
			}
		}
		return resposta;
	}
}