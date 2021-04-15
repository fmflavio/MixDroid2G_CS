package br.com.mix;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Principal extends Activity {     
	private String arquivoSelecionado = "";
	private int posicao;
	private String armazenamentoPadrao = Environment.getExternalStorageDirectory().toString();
	private String pastaPadraoMusicas = "/MixDroidSongs/";
	private String pastaPadraoGravacao = "/MixDroidRecords/";
	private String diretorioMusical = armazenamentoPadrao+pastaPadraoMusicas;
	private String diretorioGravacao = armazenamentoPadrao+pastaPadraoGravacao;
	private String chavePastaMusicas = "pastaMusicas";
	private String chavePastaGravacao = "pastaGravacao";
	private String chaveFormato = "formatoGravacao";
	private String chaveMIC = "estadoMirofone";
	private int quantidadeLinhas = 1;
	private static final int MP3 = 0, WAV = 1;
	private int formatoPosicao = WAV;
	private static final String[] FORMATOSMUSICAIS = new String[]{"MP3", "WAV"};
	private String GravaçãoMilesegundos = "";
	
	private ListView listPrincipal = null;
	private CarregaListagem carregaListagem = new CarregaListagem();
	private MusicasAdapter adapter = null;

	private View viewClicado = null;
	private LinearLayout painel = null;
	private TextView informaGravacao = null;
	private Button gravaButton = null;
	private Button ouvirGravaButton = null;
	private ToggleButton botaoMic = null;
	private Chronometer cronometro = null;
	private View viewTutorial = null;
	private Resources res = null;
	
	//relacionado as preferencias do usuário
	private SharedPreferences preferencia;
	private AlertDialog alertatutorial = null;
	
	private GerenciaSom gerenciaSom = new GerenciaSom();
	
	private GravaAudioMP3 gravaAudioMP3 = new GravaAudioMP3(armazenamentoPadrao, pastaPadraoGravacao);
    private GravaAudioWAV gravaAudioWAV = new GravaAudioWAV(armazenamentoPadrao, pastaPadraoGravacao);
    private GravaXML gravaXML = new GravaXML(armazenamentoPadrao, pastaPadraoGravacao);
	
    private final Handler handler = new Handler();
    private static final int UPDATE_FREQUENCY = 100;
    private final Runnable updatePositionRunnable = new Runnable() {
	    public void run() {
	    	updatePosition();
	    }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		//priorizar o som
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		//mantem a tela sempre ativa
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//responsavel por carregar as strings de textos internacionalizadas
		res = getResources();
		//Verifica a existencia de pasta no cartão externo
		if(!criarDiretorioSeNaoExistir(diretorioMusical, diretorioGravacao)){
			Toast.makeText(this, res.getString(R.string.erro_4), Toast.LENGTH_LONG).show();
		}
		//inicia e geri a listagem
		init1();
		//inicia e geri o painnel com botoes e informações
		init2();
		//atualiza as preferencias
		preferencia = getSharedPreferences("config", 0);
		preferencias();
		//abastece o list com as musicas da pasta pre-selecionada
		populaMusicas();
		//exibe o tutorial ao iniciar a aplicação
		//exibeTutorial();
	}
	//********************************* fim do oncreate*************************************************
	
    private void init1(){    	
    	//Inicializa o listview
    	listPrincipal = (ListView) findViewById(R.id.listViewPrincipal);	
    	//Adapter customizado
    	carregaListagem.setDiretorioCS(diretorioMusical);
    	adapter = new MusicasAdapter(getApplicationContext(), carregaListagem.listaArquivos());
    	listPrincipal.setAdapter(adapter);
    	//Evento de clique do item da lista
    	listPrincipal.setOnItemClickListener(onItem);

		//permite o longo click
    	listPrincipal.setLongClickable(true);
		//executa ações para o longo click
    	listPrincipal.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View longview, int pos, long id) {
				TextView ender =  (TextView)longview.findViewById(R.id.endereco);
				arquivoSelecionado = ender.getText()+"";
				chamaMenuLongoClick(arquivoSelecionado, longview);
				return true;
			}
		});
    }
    private void init2(){ 
		//esconde o painel de gravação
		painel = (LinearLayout)findViewById(R.id.painel_gravacao);

		//texto de informação
		informaGravacao = (TextView) findViewById(R.id.informa_gravacao);
		informaGravacao.setText(res.getString(R.string.gravacao_modo)+" "+FORMATOSMUSICAIS[formatoPosicao]); /////////////////////////////////////
		
		//cronometro em segundos
		cronometro = (Chronometer) findViewById(R.id.chronometer);
		cronometro.setBase(SystemClock.elapsedRealtime());
		cronometro.setText(" 00:00 ");
		//obtem a gravação em milesegundos
		cronometro.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				 long myElapsedMillis = SystemClock.elapsedRealtime() - cronometro.getBase();
			      GravaçãoMilesegundos = myElapsedMillis+"";
			}
		});
		//botões de controle de gravação
		gravaButton = (Button)findViewById(R.id.bt_gravar);
		ouvirGravaButton = (Button)findViewById(R.id.bt_ouvir_gravacao);
		botaoMic = (ToggleButton)findViewById(R.id.tb_microfone_princ);
		
		//eventos para os botões		
		botaoMic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!botaoMic.isChecked()){
					informaGravacao.setText(res.getString(R.string.gravacao_modo_xml));
					Toast.makeText(getApplicationContext(), res.getString(R.string.gravacao_microfone_desativado), Toast.LENGTH_SHORT).show();
				} else {
					informaGravacao.setText(res.getString(R.string.gravacao_modo_mixagem)+" "+FORMATOSMUSICAIS[formatoPosicao]); /////////////////////////////
					Toast.makeText(getApplicationContext(), res.getString(R.string.gravacao_microfone_ativada), Toast.LENGTH_SHORT).show();
				}				
			}
		});
		
		gravaButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(botaoMic.isChecked()){ //se o botão do microfone estiver ativado
					if(formatoPosicao == MP3){
						if(!gravaAudioMP3.isGravando()){
							gravaAudioMP3.startRec();
							ouvirGravaButton.setVisibility(Button.INVISIBLE);
							botaoMic.setVisibility(Button.INVISIBLE);
							gravaButton.setText(res.getString(R.string.bt_parar));
							painel.setBackgroundColor(Color.GREEN);
							informaGravacao.setText(res.getString(R.string.gravacao_gravando)+" "+FORMATOSMUSICAIS[formatoPosicao]);
							cronometro.setBase(SystemClock.elapsedRealtime());
							cronometro.start();
						} else {
							gravaAudioMP3.stop();
							ouvirGravaButton.setVisibility(Button.VISIBLE);
							botaoMic.setVisibility(Button.VISIBLE);
							gravaButton.setText(res.getString(R.string.bt_gravar));
							painel.setBackgroundColor(Color.WHITE);
							informaGravacao.setText(res.getString(R.string.gravacao_gravando)+" "+FORMATOSMUSICAIS[formatoPosicao]+" "+res.getString(R.string.salva)); ////////////////////////////////
							cronometro.stop();
							cronometro.setText(res.getString(R.string.tempo_cronometro)+cronometro.getText());//////////////////////////////////////////////
						}
					}
					if(formatoPosicao == WAV){
						if(!gravaAudioWAV.isRecording()){
							gravaAudioWAV.startRecording();
							ouvirGravaButton.setVisibility(Button.INVISIBLE);
							botaoMic.setVisibility(Button.INVISIBLE);
							gravaButton.setText(res.getString(R.string.bt_parar));
							painel.setBackgroundColor(Color.GREEN);
							informaGravacao.setText(res.getString(R.string.gravacao_gravando)+" "+FORMATOSMUSICAIS[formatoPosicao]);
							cronometro.setBase(SystemClock.elapsedRealtime());
							cronometro.start();
							
						} else {
							gravaAudioWAV.stopRecording();
							ouvirGravaButton.setVisibility(Button.VISIBLE);
							botaoMic.setVisibility(Button.VISIBLE);
							gravaButton.setText(res.getString(R.string.bt_gravar));
							painel.setBackgroundColor(Color.WHITE);
							informaGravacao.setText(res.getString(R.string.gravacao_gravando)+" "+FORMATOSMUSICAIS[formatoPosicao]+" "+res.getString(R.string.salva));
							cronometro.stop();
							cronometro.setText(res.getString(R.string.tempo_cronometro)+cronometro.getText());
						}
					}
				} else { //se o microfone não tiver ativado
					if(!gravaXML.isGravando()){
						gravaXML.startGravacaoXML(gerenciaSom);
						cronometro.setBase(SystemClock.elapsedRealtime());
						cronometro.start();
						ouvirGravaButton.setVisibility(Button.INVISIBLE);
						botaoMic.setVisibility(Button.INVISIBLE);
						gravaButton.setText(res.getString(R.string.bt_parar));
						painel.setBackgroundColor(Color.GREEN);
						informaGravacao.setText(res.getString(R.string.gravacao_gravando_xml));
					} else {
						gravaXML.stopGravacaoXML(GravaçãoMilesegundos, cronometro.getText()+"");
						cronometro.stop();
						cronometro.setText(res.getString(R.string.tempo_cronometro)+cronometro.getText());
						ouvirGravaButton.setVisibility(Button.VISIBLE);
						botaoMic.setVisibility(Button.VISIBLE);
						gravaButton.setText(res.getString(R.string.bt_gravar));
						painel.setBackgroundColor(Color.WHITE);
						informaGravacao.setText(res.getString(R.string.gravacao_salva_xml));
					}			
				}
			}
		});
		ouvirGravaButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(botaoMic.isChecked()){ //se o botão do microfone estiver ativado
					if(formatoPosicao == MP3){
						if(ouvirGravaButton.getText().equals(res.getString(R.string.bt_ouvir_gravacao))){
							gravaAudioMP3.tocar();
							updatePosition();
							ouvirGravaButton.setText(res.getString(R.string.bt_parar));
						} else {
							gravaAudioMP3.stopPlay();
							ouvirGravaButton.setText(res.getString(R.string.bt_ouvir_gravacao));
						}
					}
					if(formatoPosicao == WAV){
						if(ouvirGravaButton.getText().equals(res.getString(R.string.bt_ouvir_gravacao))){
							gravaAudioWAV.tocar();
							updatePosition();
							ouvirGravaButton.setText(res.getString(R.string.bt_parar));
						} else {
							gravaAudioWAV.stopPlay();
							ouvirGravaButton.setText(res.getString(R.string.bt_ouvir_gravacao));
						}
					}
				} else {
					gravaXML.tocarXML();
				}
			}
		});
    }
    
  //resposta a 1 clique curto na lista musical
    private OnItemClickListener onItem = new OnItemClickListener() {
    	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {		
    		//obtem a quantidade de linhas no momento do click
    		quantidadeLinhas = arg0.getCount(); 
    		viewClicado = arg1;
    		//captura o endereço do arquivo
    		TextView ender =  (TextView)viewClicado.findViewById(R.id.endereco);
    		arquivoSelecionado = ender.getText()+"";
    		posicao = position;
    		//captura o formto
    		TextView formato =  (TextView)viewClicado.findViewById(R.id.formato); 
    		//decide que tipo de ação fará para musica
    		//cria  a musica, reinicia ou pausa a musica
    		if((formato.getText()+"").equals(".csd")){
    			gerenciaSom.startPlayCS(arquivoSelecionado);
    		} else {
    			gerenciaSom.startPlay(arquivoSelecionado);
    		}
    		//atualiza o cursor
    		updatePosition();
    		//altera o layout dos botoes e fundo
    		alteraLayoutList();
    		//Verifica se a lista possui algum item marcado na posiçao atual
    		if(!adapter.containsItem(posicao)){
    			adapter.addSelectedItem(posicao); // Marca o item
    			adapter.addSelectedItemNome(arquivoSelecionado); // Marca o item
    			adapter.addSelectedItemProgresso(0); // Marca o item
    		} else {
    			adapter.removeSelectedItem(posicao); // Desmarca o item
    			adapter.removeSelectedItemNome(arquivoSelecionado); // Desmarca o item
    			adapter.removeSelectedItemProgresso(posicao); // Desmarca o item
    		}    			
    		
    		//Notifica o adaptador para atualização das posições marcadas
    		adapter.notifyDataSetChanged();
    	};
    };
	
	//Criase os menus do botão físico
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.pararTudo:
			gerenciaSom.paraTodasMusicas(quantidadeLinhas);
			populaMusicas();
			Toast.makeText(this, res.getString(R.string.todas_musicas_paradas), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.config:
			startActivity(new Intent(getBaseContext(), Config.class));
			gerenciaSom.paraTodasMusicas(quantidadeLinhas);
			populaMusicas();
			return true;
		case R.id.tutorial:
			exibeTutorial();
			return true;
		case R.id.sobre:
			AlertDialog.Builder alerta = new AlertDialog.Builder(this);  
			//mensagem sobre o sistema
			alerta.setMessage(res.getString(R.string.txtSobre));
			alerta.setNeutralButton(res.getString(R.string.bt_retornar), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { ; }});
			alerta.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//cria uma pasta no cartão externo caso não haja
	public static boolean criarDiretorioSeNaoExistir(String enderecoMusicas, String enderecoGravacao) {
		boolean resposta = true;
		File pasta1 = new File(enderecoMusicas);
		if (!pasta1.exists()) {
			if (!pasta1.mkdirs()) {
				resposta = false;
			}
		}
		File pasta2 = new File(enderecoGravacao);
		if (!pasta2.exists()) {
			if (!pasta2.mkdirs()) {
				resposta = false;
			}
		}
		return resposta;
	}
	
	//Controe a listagem de musicas
	public void populaMusicas(){	
		carregaListagem.setDiretorioCS(diretorioMusical);
		adapter = new MusicasAdapter(this, carregaListagem.listaArquivos());
		adapter.limpaItemListas();
    	listPrincipal.setAdapter(adapter);
    	if(carregaListagem.listaArquivos().size()<=0){ Toast.makeText(getApplicationContext(), res.getString(R.string.msg_pasta_vazia), Toast.LENGTH_LONG).show(); }
	}

	//chama menu com longo click
	public void chamaMenuLongoClick(final String arquivo, final View view){
		//exibe mensagem de alerta
		AlertDialog.Builder alerta = new AlertDialog.Builder(this);  

		alerta.setMessage(res.getString(R.string.msg_qual_acao));
		//define os tipos de botões
		alerta.setNegativeButton(res.getString(R.string.bt_parar), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				gerenciaSom.stopPlay(arquivo);
				view.setBackgroundColor(Color.TRANSPARENT);
			}
		});
/*		alerta.setNeutralButton(res.getString(R.string.editar), new DialogInterface.OnClickListener() { //conteudo oculto para futuros trabalhos
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				TextView form =  (TextView)view.findViewById(R.id.formato);
				Intent intent = new Intent(getBaseContext(), Edicao.class);
				intent.putExtra("ENDERECO", arquivo);
				intent.putExtra("FORMATO", form.getText());
				startActivity(intent);
				populaMusicas();
			}
		});	*/	
		alerta.setPositiveButton(res.getString(R.string.apagar), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				if(apagarArquivo(arquivo)){
					Toast.makeText(getApplication(), res.getString(R.string.msg_arquivo_excluido), Toast.LENGTH_SHORT).show();
					populaMusicas();
				} else {
					Toast.makeText(getApplication(), res.getString(R.string.msg_arquivo_nao_excluido), Toast.LENGTH_SHORT).show();
				}
			}
		});
		alerta.setIcon(R.drawable.icon_config);
		//Exibe a mensagem de alerta
		alerta.show();
	}
	//apaga fisicamente arquivo selecionado
	private boolean apagarArquivo(String endereco){
		File file = new File(endereco);
		return file.delete();
	}

	private void alteraLayoutList(){
		if(gerenciaSom.isTocando(arquivoSelecionado) == gerenciaSom.TOCANDO){
			//verifica se esta gravando em XML, caso sim insere a ação no mesmo
			if(!botaoMic.isChecked() && gravaButton.getText().equals(res.getString(R.string.bt_parar))){
				gravaXML.inserirMusicaXML(arquivoSelecionado, true, GravaçãoMilesegundos);
			}
		} else {
			if(gerenciaSom.isTocando(arquivoSelecionado) == gerenciaSom.PAUSADO){
				//verifica se esta gravando em XML, caso sim insere a ação no mesmo
				if(!botaoMic.isChecked() && gravaButton.getText().equals(res.getString(R.string.bt_parar))){
					gravaXML.inserirMusicaXML(arquivoSelecionado, false, GravaçãoMilesegundos);
				}
				//altera o botão
			} else {
				//verifica se esta gravando em XML, caso sim insere a ação no mesmo
				if(!botaoMic.isChecked() && gravaButton.getText().equals(res.getString(R.string.bt_parar))){
					gravaXML.inserirMusicaXML(arquivoSelecionado, false, GravaçãoMilesegundos);
				}
			}
		}
	}

	//para as musicas caso seja fechado
	@Override
	protected void onDestroy() {
		super.onDestroy();
		gerenciaSom.paraTodasMusicas(quantidadeLinhas);  
	}

	@Override
	public void onPause() {
		super.onPause();
		gerenciaSom.paraTodasMusicas(quantidadeLinhas);
		populaMusicas();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populaMusicas();
		preferencias();
		if(!botaoMic.isChecked()){
			informaGravacao.setText(res.getString(R.string.gravacao_modo_xml));
		} else {
			informaGravacao.setText(res.getString(R.string.gravacao_modo_mixagem)+" "+FORMATOSMUSICAIS[formatoPosicao]);
		}
	}	
	
	private void exibeTutorial() {
			LayoutInflater li = getLayoutInflater(); 
			//infla o layout na view 
			viewTutorial = li.inflate(R.layout.tutorial, null);
			//define para o botão do layout um clickListener 
			viewTutorial.findViewById(R.id.btFechar).setOnClickListener(new View.OnClickListener() { 
				public void onClick(View arg0) { 
					//desfaz o alerta. 
					alertatutorial.dismiss(); 
				} 
			}); 
			AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			builder.setTitle(res.getString(R.string.msg_tutorial_basico)); 
			builder.setView(viewTutorial); 
			alertatutorial = builder.create(); 
			alertatutorial.show();
	}
	
	private void preferencias(){
		//muda conforme as preferencias
		if(preferencia.contains(chavePastaMusicas)){
			diretorioMusical = armazenamentoPadrao+preferencia.getString(chavePastaMusicas, "");
			if(!diretorioMusical.contains(armazenamentoPadrao)){
				diretorioMusical = armazenamentoPadrao+diretorioMusical;
			}
		} 
		if(preferencia.contains(chavePastaGravacao)){
			diretorioGravacao = armazenamentoPadrao+preferencia.getString(chavePastaGravacao, "");
			if(!diretorioGravacao.contains(armazenamentoPadrao)){
				diretorioGravacao = armazenamentoPadrao+diretorioGravacao;
			}
		}
		if(preferencia.contains(chaveFormato)){
			formatoPosicao = Integer.parseInt(preferencia.getString(chaveFormato, ""));
		} 
		if(preferencia.contains(chaveMIC)){
			if(preferencia.getString(chaveMIC, "").equals("true")){
				botaoMic.setChecked(true);
			} else {
				botaoMic.setChecked(false);
			}
		} 
	}
	private void updatePosition(){
		handler.removeCallbacks(updatePositionRunnable); 
		handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
		//gerencia a barra de progresso e o termino  da musica
		if(adapter.getItem().size() > 0){
			for(int i = 0; i < adapter.getItem().size(); i++){
				//alcansou o fim da musica
		        if(gerenciaSom.isTocando(adapter.getItemNomes().get(i)) != gerenciaSom.TOCANDO){
					adapter.removeSelectedItem(adapter.getItem().get(i));
					adapter.removeSelectedItemNome(adapter.getItemNomes().get(i));
		        } else {
		        	adapter.setProgresso(i, gerenciaSom.getProgresso(adapter.getItemNomes().get(i)));
		        }
		        adapter.notifyDataSetChanged();
			}
		} else {
			adapter.limpaItemListas();
			adapter.notifyDataSetChanged();
		}
		
        if(formatoPosicao == WAV && ouvirGravaButton.getText().equals(res.getString(R.string.bt_parar))){
        	if(!gravaAudioWAV.isPlaying()){
    			ouvirGravaButton.setText(res.getString(R.string.bt_ouvir_gravacao));
            }
        }
        if(formatoPosicao == MP3 && ouvirGravaButton.getText().equals(res.getString(R.string.bt_parar))){
        	if(!gravaAudioMP3.isPlaying()){
    			ouvirGravaButton.setText(res.getString(R.string.bt_ouvir_gravacao));
            }
        }
	 }
}