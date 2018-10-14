package com.example.rafae.tip_calculator;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
        implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    //declare your variables for the widgets
    private EditText editTextBillAmount;
    private TextView textViewBillAmount, textViewPercent, textViewTipAmount, textViewTotalAmount, textViewEachAmount;
    private SeekBar seekBar;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button calculateButton;


    //declare the variables for the calculations
    private double billAmount = 0.0;
    private double percent = 0.15;
    private int listvalue = 1;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat =
            NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add Listeners to Widgets
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);//uncomment this line
        editTextBillAmount.addTextChangedListener((TextWatcher) this);//uncomment this line

        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);

        textViewPercent = (TextView)findViewById(R.id.textView_Percent);
        textViewPercent.setText(percentFormat.format(percent));

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(15);
        seekBar.setOnSeekBarChangeListener(this);

        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.spinnerValues,
                R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        textViewTipAmount = (TextView)findViewById(R.id.textView_TipAmount);
        textViewTotalAmount = (TextView)findViewById(R.id.textView_TotalAmount);
        textViewEachAmount = (TextView)findViewById(R.id.textView_EachAmount);

        calculateButton = (Button)findViewById(R.id.calcButton);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_button:
                String textmsg = "The bill is "+ billAmount+ ". The tip is "+ textViewTipAmount.getText()+
                        ". The total is"+ textViewTotalAmount.getText()+". Let's split the bill "+ textViewEachAmount.getText() + " each.";

                String mimeType = "text/plain";

                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle("Share this text with: ")
                        .setText(textmsg)
                        .startChooser();

                return true;
            case R.id.help_button:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Help");
                alertDialog.setMessage("The drop down list represents the number of people you want to split the bill with.");
                alertDialog.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /*
    Note:   int i, int i1, and int i2
            represent start, before, count respectively
            The charSequence is converted to a String and parsed to a double for you
     */

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);

        try{
            //charSequence is converted to a String and parsed to a double for you
            billAmount = Double.parseDouble(charSequence.toString()) / 100;
            //    Log.d("MainActivity", "Bill Amount = "+billAmount);
        } catch (NumberFormatException e){
            billAmount = 0;
            //   Log.d("MainActivity", "Bill Amount = "+billAmount);
        }

        //setText on the textView
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        //perform tip and total calculation and update UI by calling calculate
    }

    @Override
    public void afterTextChanged(Editable editable) {
//        Log.d("MainActivity", "afterTextChanged: "+ editable);
        if (editable.toString().equals("0")){
//            Log.d("MainActivity", "editable: " + editable);
            editable.delete(0,1);
//            Log.d("MainActivity", "editable: " + editable);
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress * 0.01; //calculate percent based on seeker value

/*        Log.d("MainActivity", "percent: " + percent);
        Log.d("MainActivity", "progress: " + progress);*/
        textViewPercent.setText(percentFormat.format(percent));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        Log.d("MainActivity", "percent: "+percent);
    }

    public void buttonClicked(View view) {
        calculate();
    }


    private void calculate() {
//        Log.d("MainActivity", "inside calculate method");

        if (textViewBillAmount.getText() == ""){
            // Log.d("MainActivity", "empty bill amount");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Missing Bill Info");
            alertDialog.setMessage("Please enter your bill where it says Enter Amount.");
            alertDialog.show();
        } else{
            // Check Radio Option
            int radioId = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton)findViewById(radioId);

            double tip = 0.0;
            double total = 0.0;
            double each = 0.0;

            if (radioButton == (RadioButton)findViewById(R.id.radioButton1)){
                //No option selected

                tip = billAmount * percent;
                total = tip + billAmount;
            } else if (radioButton == (RadioButton)findViewById(R.id.radioButton2)){
                //Tip only option selected

                tip = billAmount * percent;
                tip = Math.ceil(tip);
                total = tip + billAmount;
            } else{
                //Total only option selected
                tip = billAmount * percent;
                total =  tip + billAmount;
                total = Math.ceil(total);
            }

            each = total / listvalue;

            // display tip, total,and each formatted as currency
            // user currencyFormat instead of percentFormat to set the textViewTipAmount
            textViewTipAmount.setText(currencyFormat.format(tip));
            // use the tip example to do the same for the Total
            textViewTotalAmount.setText(currencyFormat.format(total));
            textViewEachAmount.setText(currencyFormat.format(each));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString()+ " was selected.";
        Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
        listvalue = position + 1;
        // Log.d("MainActivity", "onItemSelected: "+ listvalue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
