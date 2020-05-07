package com.athishWorks.policeapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class FragmentPage1 extends Fragment {

    private EditText cName, cFatherName, cDOB, cNationality, cAddress;

    private FragmentPage1Listener listener;

    public interface FragmentPage1Listener {
        void sendPage1(String cName, String cFatherName, String cDOB, String cNationality, String cAddress);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page1, container, false);

        declarations(view);
        Button saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sendPage1(cName.getText().toString(),
                        cFatherName.getText().toString(),
                        cDOB.getText().toString(),
                        cNationality.getText().toString(),
                        cAddress.getText().toString());
            }
        });

        return view;
    }

    private void declarations(View view) {
        cName = view.findViewById(R.id.complainantName);
        cFatherName = view.findViewById(R.id.complainantFatherName);
        cDOB = view.findViewById(R.id.complainantDOB);
        cNationality = view.findViewById(R.id.complainantNationality);
        cAddress = view.findViewById(R.id.complainantAddress);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentPage2.FragmentPage2Listener) {
            listener = (FragmentPage1Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must be implement FragmentPage1");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
