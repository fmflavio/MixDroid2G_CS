package br.com.mix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class GravaXML {
	
	private File xmlFile = null;
	private FileOutputStream fileos = null;
	private XmlSerializer serializer = null;
	private boolean gravando = false;
	private int id = 1;
	private String diretorioGravacao = null;
	private String nomeArquivo = "SuaXMLMixagem.xml";
	private GerenciaSom gerenciaSom = null;
	private ArrayList<Musica> musica = new ArrayList<Musica>();
	
	public GravaXML(String armazenamentoPadrao, String pastaPadraoGravacao){
		this.diretorioGravacao = armazenamentoPadrao+pastaPadraoGravacao;
		xmlFile = new File(diretorioGravacao+nomeArquivo);
	}
	//responsavel por iniciar a gravação do xml
	public void startGravacaoXML(GerenciaSom gerenciaSom){
		this.gerenciaSom = gerenciaSom;
		this.musica = gerenciaSom.getMusicas();
		gravando = true;
		criaXML();
	}
	//abre a string do xml
	private void criaXML(){
        try {
			xmlFile.createNewFile();
			
	        fileos = new FileOutputStream(xmlFile);

	        serializer = Xml.newSerializer();
			serializer.setOutput(fileos, "UTF-8");
	        serializer.startDocument(null, Boolean.valueOf(true));
	        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);	
	        serializer.startTag(null, "lista");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < musica.size(); i++) {
			if(musica.get(i).isTocando()){			
		        try{
		        	serializer.startTag(null, "composicao");
		        	serializer.attribute(null, "ID", (id++)+"");
			        serializer.startTag(null, "titulo");
			        serializer.text(musica.get(i).getArquivo());
			        serializer.endTag(null, "titulo");
			        serializer.startTag(null, "tocando");
			        serializer.text("true");
			        serializer.endTag(null, "tocando");
			        serializer.startTag(null, "tempomusica");
			        serializer.text(musica.get(i).getTempoCorrente()+"");
			        serializer.endTag(null, "tempomusica");
			        serializer.startTag(null, "tempogravacao");
			        serializer.text((i-1)+"");
			        serializer.endTag(null, "tempogravacao");
			        serializer.endTag(null, "composicao");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		gravando = true;
	}
	//adiciona iterações no xml
	public void inserirMusicaXML(String arquivoSelecionado, boolean estaTocando, String tempGravMiliSeg){
		if(!gravando){
			criaXML();
		} else {
			if(estaTocando){
		        try {
		        	serializer.startTag(null, "composicao");
		        	serializer.attribute(null, "ID", (id++)+"");
					serializer.startTag(null, "titulo");
			        serializer.text(arquivoSelecionado);
			        serializer.endTag(null, "titulo");
			        serializer.startTag(null, "tocando");
			        serializer.text("true");
			        serializer.endTag(null, "tocando");
			        serializer.startTag(null, "tempomusica");
			        serializer.text("0");
			        serializer.endTag(null, "tempomusica");
			        serializer.startTag(null, "tempogravacao");
			        serializer.text(tempGravMiliSeg);
			        serializer.endTag(null, "tempogravacao");
			        serializer.endTag(null, "composicao");
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else {
		        try {
		        	serializer.startTag(null, "composicao");
		        	serializer.attribute(null, "ID", (id++)+"");
					serializer.startTag(null, "titulo");
			        serializer.text(arquivoSelecionado);
			        serializer.endTag(null, "titulo");
			        serializer.startTag(null, "tocando");
			        serializer.text("false");
			        serializer.endTag(null, "tocando");
			        serializer.startTag(null, "tempomusica");
			        serializer.text(gerenciaSom.getProgresso(arquivoSelecionado)+"");
			        serializer.endTag(null, "tempomusica");
			        serializer.startTag(null, "tempogravacao");
			        serializer.text(tempGravMiliSeg);
			        serializer.endTag(null, "tempogravacao");
			        serializer.endTag(null, "composicao");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	//finaliza a string do xml
	public void stopGravacaoXML(String tempGravMiliSeg, String tempGravSegundos){
		try{
			for (int i = 0; i < musica.size(); i++) {
				if(musica.get(i).isTocando()){
					serializer.startTag(null, "composicao");
					serializer.attribute(null, "ID", (id++)+"");
			        serializer.startTag(null, "titulo");
			        serializer.text(musica.get(i).getArquivo());
			        serializer.endTag(null, "titulo");
			        serializer.startTag(null, "tocando");
			        serializer.text("false");
			        serializer.endTag(null, "tocando");
			        serializer.startTag(null, "tempomusica");
			        serializer.text(musica.get(i).getTempoCorrente()+"");
			        serializer.endTag(null, "tempomusica");
			        serializer.startTag(null, "tempogravacao");
			        serializer.text(tempGravMiliSeg);
			        serializer.endTag(null, "tempogravacao");
			        serializer.endTag(null, "composicao");
				}
			}
			serializer.endTag(null,"lista");
			
	        serializer.endDocument();
	        serializer.flush();
	        fileos.close();
        }catch(Exception e) {
            Log.e("Exception","Exception occured in wroting");
        }
		gravando = false;
	}
	
	//inicia a execução do xml
	public void tocarXML(){
		@SuppressWarnings("unused")
		String stTitulo = "", stTocando = "", stTempomusica  = "", stTempogravacao = "";
		int atraso = 0;
		gerenciaSom = new GerenciaSom();
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("composicao");
						
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node fstNode = nodeLst.item(i);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					
					NodeList titulo = fstElmnt.getElementsByTagName("titulo");
					Element tituloElement = (Element) titulo.item(0);
					NodeList tituloNode = tituloElement.getChildNodes();
					stTitulo = ((Node) tituloNode.item(0)).getNodeValue();
					
					NodeList tocando = fstElmnt.getElementsByTagName("tocando");
					Element tocandoElement = (Element) tocando.item(0);
					NodeList tocandoNode = tocandoElement.getChildNodes();
					stTocando = ((Node) tocandoNode.item(0)).getNodeValue();
					
					NodeList tempomusica = fstElmnt.getElementsByTagName("tempomusica");
					Element tempomusicaElement = (Element) tempomusica.item(0);
					NodeList tempomusicaNode = tempomusicaElement.getChildNodes();
					stTempomusica = ((Node) tempomusicaNode.item(0)).getNodeValue();
					
					NodeList tempogravacao = fstElmnt.getElementsByTagName("tempogravacao");
					Element tempogravacaoElement = (Element) tempogravacao.item(0);
					NodeList tempogravacaoNode = tempogravacaoElement.getChildNodes();
					stTempogravacao = ((Node) tempogravacaoNode.item(0)).getNodeValue();

					new Agendamento(gerenciaSom, stTitulo, (Integer.parseInt(stTempomusica)+atraso));
					
					if(Integer.parseInt(stTempomusica) == 0 || Integer.parseInt(stTempomusica) == atraso){
						atraso = atraso + 0;
					}					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isGravando() {
		return gravando;
	}

	public void setGravando(boolean gravando) {
		this.gravando = gravando;
	}
}