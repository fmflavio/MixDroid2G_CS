package br.com.mix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicial extends Activity{

	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inicial);
		
		//cria botão e adiciona ação a ele
		button = (Button) findViewById(R.id.bt_avancar);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				chamaPlayer();
			}
		});
	}
	//destroi a aplicação
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
		System.exit(0);
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	//responsavel por chamar a nova janela
	private void chamaPlayer(){
		Intent intent = new Intent(this, Principal.class);
		startActivity(intent);
	}  
}
