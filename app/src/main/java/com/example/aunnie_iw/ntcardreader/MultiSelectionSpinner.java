package com.example.aunnie_iw.ntcardreader;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Aunnie-IW on 12/6/2560.
 */

public class MultiSelectionSpinner  extends android.support.v7.widget.AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;

    public MultiSelectionSpinner(Context context) {
        super(context);
    }

    public MultiSelectionSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSelectionSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked)
            selected[which] = true;
        else
            selected[which] = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuffer spinnerBuffer = new StringBuffer();
        boolean someUnselected = false;
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i] == true) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                count = count+1;
            } else {
                someUnselected = true;
            }
        }
        String spinnerText;
        if (someUnselected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                //spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
                spinnerText = defaultText + " "+ count + " ภาษา" ;
            else{
                spinnerText = defaultText + " 0 ภาษา" ;
            }
        } else {
            spinnerText = defaultText + " 5 ภาษา";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });

        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, String allText,MultiSpinnerListener listener, boolean[] itemsData) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        int count = 0;
        // all selected by default
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = itemsData[i];
            if (itemsData[i]==true)
                count = count+1;
        }
        // all text on the spinner
        ArrayAdapter<String> adapter;
        if (count!=0) {
            adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, new String[]{allText +" "+ count + " ภาษา"});
        }
        else{
            adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, new String[]{allText});
        }
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);

        void onPostExecute(String s);
    }
}