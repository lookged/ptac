package com.example.kanda.ptacproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kanda.ptacproject.R;

/**
 * Created by Kanda on 9/4/2016.
 */
public class AddRequest extends Activity {
    private Button btnAdd;
    private EditText inputEmailFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_to_addfriend);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        inputEmailFriend = (EditText) findViewById(R.id.inputEmailFriend);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmailFriend.getText().toString().trim();

            }

        });
    }


}

