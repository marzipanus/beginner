package com.udemy.whatmedo;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = getActivity().getApplicationContext();
        EditText editText = new EditText(context);
        editText.setId(R.id.edit_note);
        editText.setHint("Вводить текст тут");
        editText.setTextColor(Color.BLACK);
        return editText;
    }
}