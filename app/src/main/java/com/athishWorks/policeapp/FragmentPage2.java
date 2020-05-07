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

public class FragmentPage2 extends Fragment {

    private EditText complaint;
    private FragmentPage2Listener listener;

    public interface FragmentPage2Listener {
        void sendPage2(String complaint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page2, container, false);

        complaint = view.findViewById(R.id.complainant);
        Button saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sendPage2(complaint.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentPage2Listener) {
            listener = (FragmentPage2Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must be implement FragmentPage2");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
