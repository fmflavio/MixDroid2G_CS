package br.com.mix;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MusicasAdapter extends BaseAdapter{
	private Context context = null;
    private List<PropriedadesMusica> stateList = null;
    private List<Integer> selectedItems = null;
    private List<String> selectedItemsNome = null;
    private List<Integer> selectedItemsProgresso = null;
    private LayoutInflater inflater = null;
    
	public MusicasAdapter(Context context, List<PropriedadesMusica> statelist) {
        this.context = context;
        this.stateList = statelist;
        this.selectedItems = new ArrayList<Integer>();
        this.selectedItemsNome = new ArrayList<String>();
        this.selectedItemsProgresso = new ArrayList<Integer>();
	}	
	
	//Adiciona uma nova posição na lista
	public void addSelectedItem(int position){
		selectedItems.add(position);
	}
	
	//Remove a posição da lista
	public void removeSelectedItem(int position){
		selectedItems.remove(Integer.valueOf(position));
	}
	
	//Verifica se a lista contém a posição
	public boolean containsItem(int position){
		return selectedItems.contains(position);
	}
	
	//recupera a lista de posições selecionadas
	public List<Integer> getItem(){
		return selectedItems;
	}
	
	//Adiciona uma novo nome na lista
	public void addSelectedItemNome(String nome){
		selectedItemsNome.add(nome);
	}
	
	//Remove o nome da lista
	public void removeSelectedItemNome(String nome){
		selectedItemsNome.remove(nome);
	}
	
	//Verifica se a lista contém o nome
	public boolean containsItemNome(String nome){
		return selectedItemsNome.contains(nome);
	}
	
	//recupera a lista de nomes selecionados
	public List<String> getItemNomes(){
		return selectedItemsNome;
	}
	
	//Adiciona um novo progresso na lista
	public void addSelectedItemProgresso(int position){
		selectedItemsProgresso.add(position);
	}
	
	//Remove o Progresso da lista
	public void removeSelectedItemProgresso(int position){
		selectedItemsProgresso.remove(Integer.valueOf(position));
	}
	
	//Verifica se a lista contém o Progresso
	public boolean containsItemProgresso(int position){
		return selectedItemsProgresso.contains(position);
	}
	
	//recupera a lista de Progressos selecionados
	public List<Integer> getItemProgresso(){
		return selectedItemsProgresso;
	}
	
	//intere novo progresso na posição
	public void setProgresso(int position, int progresso){
		selectedItemsProgresso.set(position, progresso);
	}
	
	//limpa as listas
	public void limpaItemListas(){
        this.selectedItems = new ArrayList<Integer>();
        this.selectedItemsNome = new ArrayList<String>();
        this.selectedItemsProgresso = new ArrayList<Integer>();
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<PropriedadesMusica> getStateList() {
		return stateList;
	}

	public void setStateList(List<PropriedadesMusica> stateList) {
		this.stateList = stateList;
	}
	@Override
	public int getCount() {
		return stateList.size();
	}

	@Override
	public Object getItem(int position) {
		return stateList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// Recupera o estado da posição atual
		PropriedadesMusica propriedadesMusica = stateList.get(position);
		View view = converView;
		Holder holder;
		
		//Infla o layout na primeira visualização
		//Serve para manter a referência da lista na tela
		if(converView == null){
			// Cria uma instância do layout XML para os objetos correspondentes na View
	        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        view = inflater.inflate(R.layout.modelo_linha, parent, false);
	        holder = new Holder();
			holder.title = (TextView)view.findViewById(R.id.title);
			holder.name = (TextView)view.findViewById(R.id.displayname);
			holder.duration = (TextView)view.findViewById(R.id.duration);
			holder.endereco = (TextView)view.findViewById(R.id.endereco);
			holder.formato = (TextView)view.findViewById(R.id.formato);
			holder.playImageButton = (ImageView)view.findViewById(R.id.playListCust);  
			holder.progressBar = (ProgressBar)view.findViewById(R.id.progressBarTempo);
			holder.progressBar.getProgressDrawable().setColorFilter(Color.RED, Mode.MULTIPLY);

			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
				
		holder.title.setText(propriedadesMusica.getTitulo());
		holder.name.setText(propriedadesMusica.getNome());
		holder.duration.setText(propriedadesMusica.getDuracao());
		holder.endereco.setText(propriedadesMusica.getEndereco());
		holder.formato.setText(propriedadesMusica.getFormato());
		holder.progressBar.setMax(Integer.parseInt(propriedadesMusica.getDuracaoMile()));
		
		//Se a lista possui a posição do elemento, marca o item com a cor escolhida
		if(selectedItems.contains(position)){
			view.setBackgroundColor(Color.GREEN);
			holder.playImageButton.setImageResource(android.R.drawable.ic_media_pause);
			holder.progressBar.setProgress(selectedItemsProgresso.get(selectedItems.indexOf(position)));	
		}else{
			view.setBackgroundColor(Color.TRANSPARENT);
			holder.playImageButton.setImageResource(android.R.drawable.ic_media_play);
			holder.progressBar.setProgress(0);
		}
		return view;
	}
	
	private class Holder{
		TextView title;
		TextView name;
		TextView duration;
		TextView endereco;
		TextView formato;
		ImageView playImageButton;
		ProgressBar progressBar;
	}
}
