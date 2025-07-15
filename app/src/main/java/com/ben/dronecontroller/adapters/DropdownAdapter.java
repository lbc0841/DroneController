package com.ben.dronecontroller.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ben.dronecontroller.R;
import com.ben.dronecontroller.data_classes.DropdownModel;

import java.util.ArrayList;
import java.util.List;

public class DropdownAdapter extends ArrayAdapter<String> {
    private final Context adapterContext;
    private final ArrayList<String> textList;
    private final ArrayList<Integer> iconList;


    public DropdownAdapter(@NonNull Context context, @NonNull ArrayList<String> textArray, ArrayList<Integer> iconArray) {
        super(context, R.layout.mode_dropdown, textArray);
        adapterContext =context;
        textList = textArray;
        iconList = iconArray;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.mode_dropdown_item, null);

        TextView text = row.findViewById(R.id.modeText);
        ImageView img = row.findViewById(R.id.modeIcon);

        text.setText(textList.get(position));
        img.setImageResource(iconList.get(position));

        return row;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.mode_dropdown, null);

        TextView text = row.findViewById(R.id.modeText);
        ImageView img = row.findViewById(R.id.modeIcon);

        text.setText(textList.get(position));
        img.setImageResource(iconList.get(position));

        return row;

    }
}
