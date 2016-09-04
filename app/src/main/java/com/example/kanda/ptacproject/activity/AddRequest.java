package com.example.kanda.ptacproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmailFriend.getText().toString().trim();

            }

        });
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.go_to_addfriend, container, false);
        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        return rootView;
    }
}

