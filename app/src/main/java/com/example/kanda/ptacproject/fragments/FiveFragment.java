package com.example.kanda.ptacproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.AddRequest;


public class FiveFragment extends Fragment{
    private static final String TAG = FiveFragment.class.getSimpleName();
    private Button btnAddFriend;
    public FiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_five, container, false);
        btnAddFriend = (Button) rootView.findViewById(R.id.btnAddFriend);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(),
                        AddRequest.class);
                startActivity(i);

            }
        });
        return rootView;
    }

}
