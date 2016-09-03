package com.natalieperna.cupful;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    DatabaseHelper dbHelper = null;

    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    Button buttonDot, buttonBackspace, buttonConvert;
    Button buttonQuarter, buttonThird, buttonHalf;

    Spinner ingredientSpinner, fromSpinner, toSpinner;
    EditText fromEdit, toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up view elements
        button0 = (Button) findViewById(R.id.button_0);
        button1 = (Button) findViewById(R.id.button_1);
        button2 = (Button) findViewById(R.id.button_2);
        button3 = (Button) findViewById(R.id.button_3);
        button4 = (Button) findViewById(R.id.button_4);
        button5 = (Button) findViewById(R.id.button_5);
        button6 = (Button) findViewById(R.id.button_6);
        button7 = (Button) findViewById(R.id.button_7);
        button8 = (Button) findViewById(R.id.button_8);
        button9 = (Button) findViewById(R.id.button_9);

        buttonDot = (Button) findViewById(R.id.button_dot);
        buttonBackspace = (Button) findViewById(R.id.button_back);
        buttonConvert = (Button) findViewById(R.id.convert);

        ingredientSpinner = (Spinner) findViewById(R.id.ingredient);
        fromSpinner = (Spinner) findViewById(R.id.fromUnit);
        toSpinner = (Spinner) findViewById(R.id.toUnit);

        fromEdit = (EditText) findViewById(R.id.fromValue);
        toEdit = (EditText) findViewById(R.id.toValue);

        // Disallow input with keyboard for numerical fields
        fromEdit.setRawInputType(InputType.TYPE_CLASS_TEXT);
        toEdit.setRawInputType(InputType.TYPE_CLASS_TEXT);
        fromEdit.setTextIsSelectable(true);
        toEdit.setTextIsSelectable(true);

        // Units available for conversion
        // TODO Store units in database
        Unit[] units = {
                new Unit("oz", UnitType.WEIGHT, 28.3495),
                new Unit("cups", UnitType.VOLUME, 1),
                new Unit("grams", UnitType.WEIGHT, 1),
                new Unit("tsp", UnitType.VOLUME, 0.0208333)
        };

        // Setup database
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        // Show ingredients in spinner
        List<Ingredient> ingredients = dbHelper.getIngredients();
        ArrayAdapter<Ingredient> ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredients);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientSpinner.setAdapter(ingredientAdapter);

        // Show units in from and to spinners
        ArrayAdapter<Unit> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(unitAdapter);
        toSpinner.setAdapter(unitAdapter);

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ingredient ingredient;
                Unit fromUnit;
                Unit toUnit;
                double fromVal;
                double toVal;

                ingredient = (Ingredient) ingredientSpinner.getSelectedItem();
                fromUnit = (Unit) fromSpinner.getSelectedItem();
                toUnit = (Unit) toSpinner.getSelectedItem();
                fromVal = Double.parseDouble(((EditText) findViewById(R.id.fromValue)).getText().toString());

                double baseVal = fromVal * fromUnit.toBase;
                if (fromUnit.type != toUnit.type) {
                    if (fromUnit.type == UnitType.WEIGHT) {
                        baseVal /= ingredient.getBaseDensity();
                    } else {
                        baseVal *= ingredient.getBaseDensity();
                    }
                }
                toVal = toUnit.fromBase() * baseVal;

                EditText display = (EditText) findViewById(R.id.toValue);
                display.setText(String.format("%f", toVal));
            }
        });

        // Set up number input buttons, dot, and backspace
        View.OnClickListener inputListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertNumber((Button) view);
            }
        };

        button0.setOnClickListener(inputListener);
        button1.setOnClickListener(inputListener);
        button2.setOnClickListener(inputListener);
        button3.setOnClickListener(inputListener);
        button4.setOnClickListener(inputListener);
        button5.setOnClickListener(inputListener);
        button6.setOnClickListener(inputListener);
        button7.setOnClickListener(inputListener);
        button8.setOnClickListener(inputListener);
        button9.setOnClickListener(inputListener);

        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDot();
            }
        });

        buttonBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backspace();
            }
        });
    }

    private void insertNumber(Button button) {
        EditText focused = toEdit.hasFocus() ? toEdit : fromEdit;

        Editable field = focused.getText();
        field.append(button.getText());
    }

    private void insertDot() {
        EditText focused = toEdit.hasFocus() ? toEdit : fromEdit;

        Editable field = focused.getText();
        if (!field.toString().contains("."))
            field.append(".");
    }

    private void backspace() {
        EditText focused = toEdit.hasFocus() ? toEdit : fromEdit;

        Editable field = focused.getText();
        int length = field.length();
        if (length > 0) {
            field.delete(length - 1, length);
        }
    }
}
