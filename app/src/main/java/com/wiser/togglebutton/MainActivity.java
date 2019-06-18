package com.wiser.togglebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wiser.toggle.OnToggleListener;
import com.wiser.toggle.ToggleView;

public class MainActivity extends AppCompatActivity implements OnToggleListener {

	private ToggleView toggleView;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toggleView = findViewById(R.id.toggleView);

		toggleView.setOnToggleListener(this);
	}

	@Override public void toggle(boolean isToggle) {
		System.out.println("--------->>"+(isToggle?"开":"关"));
		if (isToggle) {
			Toast.makeText(this, "开", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "关", Toast.LENGTH_SHORT).show();
		}
	}

	public void open(View view){
		toggleView.openToggle();
	}

	public void close(View view){
		toggleView.closeToggle();
	}
}
