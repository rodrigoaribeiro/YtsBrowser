package com.digao.ytsbrowser.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.digao.ytsbrowser.R;
import com.digao.ytsbrowser.Utils.CfGlobal;

public class ActConfig extends AppCompatActivity {
    final Context context = this;
    CfGlobal config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config);
        this.setTitle("Configurações");


        config = CfGlobal.getInstance(context);// getApplicationContext());
        final TextView limite = findViewById(R.id.idLimit);
        limite.setText(getResources().getString(R.string.limitpag) + " " + config.getLIMIT());
        limite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Informe o Limite por página");

                final NumberPicker input = new NumberPicker(context);

                builder.setView(input);
                input.setMinValue(1);
                input.setMaxValue(99);
                input.setValue(config.getLIMIT());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setLIMIT(input.getValue());
                        limite.setText(getResources().getString(R.string.limitpag) + " " + config.getLIMIT());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });
        //limite.setText(String.valueOf(config.getLIMIT()));
        //Spinner spQlty = (Spinner) findViewById(R.id.spinQlty);
        Spinner spordem = findViewById(R.id.spinSortBy);
        RadioGroup rQlty = findViewById(R.id.idGrQlty);

        rQlty.clearCheck();
        int vIndex = 0;

        if (config.getQuality().isEmpty()) {
            vIndex = 0;

        } else {
            vIndex = java.util.Arrays.asList(getResources().getStringArray(R.array.arrayQlty)).indexOf(config.getQuality());

        }
        rQlty.check(getResources().getIdentifier("rButton" + vIndex, "id", getPackageName()));
        //spQlty.setSelection(vIndex);
        if (config.getSortBy().isEmpty()) {
            spordem.setSelection(0);
        } else {
            spordem.setSelection(java.util.Arrays.asList(getResources().getStringArray(R.array.arrayOrderYts)).indexOf(config.getSortBy()));
        }

        rQlty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                String buttonName = null;
//                RadioButton botao  = (RadioButton) findViewById(checkedId);
                buttonName = findViewById(checkedId).getResources().getResourceName(checkedId);
                int index = Integer.valueOf(buttonName.substring(buttonName.length() - 1));
//                Toast.makeText(getApplicationContext() , buttonName +"\n"+   "indice = " + buttonName.substring( buttonName.length()-1) , Toast.LENGTH_LONG).show();
                if (index == 0) {
                    config.setQUALITY("");
                } else {
                    config.setQUALITY(getResources().getStringArray(R.array.arrayQlty)[index]);
                }

            }

        });
/*
        spQlty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                if (index == 0) {
                    config.setQUALITY("");
                } else {
                    config.setQUALITY(getResources().getStringArray(R.array.arrayQlty)[index]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
/* não habilitar ....
        servidor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                config.setDomain(getResources().getStringArray(R.array.servidores)[index]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
        spordem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();

                config.setSORTBY(getResources().getStringArray(R.array.arrayOrderYts)[index]);
                config.setORDERBY("asc");
                if (index > 0) {
                    config.setORDERBY("desc");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


}
