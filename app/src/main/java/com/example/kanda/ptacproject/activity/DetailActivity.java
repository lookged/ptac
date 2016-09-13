package com.example.kanda.ptacproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.example.kanda.ptacproject.R;

/**
 * Created by NamPeung on 12-Sep-16.
 */
public class DetailActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Button btnBack = (Button) findViewById(R.id.btnBack);

    }
}
