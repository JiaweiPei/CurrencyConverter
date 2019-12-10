package com.example.currencyconverter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    /*
     * the currency type that the use selected to enter
     */
    private TextView currency;
    /*
     * The EditText that allows the user to enter amounts
     */
    private EditText userInput;
    /*
     * The TextView to display the date and time of the latest update
     */
    private TextView lastUpdate;
    /*
     * The user entered number in double format
     */
    private double input;
    /*
     * The display of after converted amount in USD
     */
    private TextView usd;
    /*
     * The display of after converted amount in EUR
     */
    private TextView eur;
    /*
     * The display of after converted amount in GBP
     */
    private TextView gbp;
    /*
     * The display of after converted amount in CNY
     */
    private TextView cny;
    /*
     * The display of after converted amount in CAD
     */
    private TextView cad;
    /*
     * The display of after converted amount in AUD
     */
    private TextView aud;
    /*
     * the web request queue
     */
    private RequestQueue queue;
    /*
     * The decimal format to keep the amount after convert in two decimal
     */
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup the TextViews
        usd = findViewById(R.id.usdNum);
        eur = findViewById(R.id.eurNum);
        gbp = findViewById(R.id.gbpNum);
        cny = findViewById(R.id.cnyNum);
        cad = findViewById(R.id.cadNum);
        aud = findViewById(R.id.audNum);
        lastUpdate = findViewById(R.id.time);
        currency = findViewById(R.id.Currency);

        //setup the EditText
        userInput = findViewById(R.id.input);

        //setup all the Buttons
        Button USD = findViewById(R.id.USD);
        Button EUR = findViewById(R.id.EUR);
        Button GBP = findViewById(R.id.GBP);
        Button CYN = findViewById(R.id.CNY);
        Button CAD = findViewById(R.id.CAD);
        Button AUD = findViewById(R.id.AUD);
        Button convert = findViewById(R.id.convertButton);

        //The currency that is clicked will become the currency type that the user are intend to enter
        USD.setOnClickListener(unused -> replaceWith(USD.getText().toString()));
        EUR.setOnClickListener(unused -> replaceWith(EUR.getText().toString()));
        GBP.setOnClickListener(unused -> replaceWith(GBP.getText().toString()));
        CYN.setOnClickListener(unused -> replaceWith(CYN.getText().toString()));
        CAD.setOnClickListener(unused -> replaceWith(CAD.getText().toString()));
        AUD.setOnClickListener(unused -> replaceWith(AUD.getText().toString()));


        queue = Volley.newRequestQueue(this);

        //Once the convert Button is clicked, the Web Request will be send and the amounts after conversion will be display
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If no number is putted by the user, display notice message
                if (userInput.getText().toString().trim().length() <= 0) {
                    Snackbar.make(view, "Please put a valid number", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                //If a valid number is putted, send Web Request
                if (userInput.getText().toString().trim().length() > 0) {
                    String userPut = userInput.getText().toString();
                    input = Double.parseDouble(userPut);
                    System.out.println("userPut: " + userPut);
                    connect();
                }
            }
        });
    }

    /*
     * connect to WebApi
     */
    public void connect() {
        String link = "https://api.exchangerate-api.com/v4/latest/USD";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, link, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Get JSONObject of rates
                                JSONObject array = response.getJSONObject("rates");

                                //Get individual Rates from the Object
                                String USDRate = array.getString("USD");
                                String EURRate = array.getString("EUR");
                                String GBPRate = array.getString("GBP");
                                String CNYRate = array.getString("CNY");
                                String CADRate = array.getString("CAD");
                                String AUDRate = array.getString("AUD");

                                //Get date and time from Web
                                String date = response.getString("date");
                                String time = response.getString("time_last_updated");

                                //Use helper function to combined and display the update time
                                setTime(date, time);

                                //Use helper function to display the currency amounts
                                update(usd, USDRate, array.getString(currency.getText().toString()));
                                update(eur, EURRate, array.getString(currency.getText().toString()));
                                update(gbp, GBPRate, array.getString(currency.getText().toString()));
                                update(cny, CNYRate, array.getString(currency.getText().toString()));
                                update(cad, CADRate, array.getString(currency.getText().toString()));
                                update(aud, AUDRate, array.getString(currency.getText().toString()));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
            }
        });
            queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Replace the title with whichever currency that is selected by the user
     */
    public void replaceWith(String input) {
        currency.setText(input);
    }

    /*
     * Update a particular currency
     * @param currency the Textview that needs to be update
     * @param rate the currency rate of this currency
     * @param titleRate the currency rate of the entered type currency
     */
    public void update(TextView currency, String rate, String titleRate) {
        double rateD = Double.parseDouble(rate);
        double titleRateD = Double.parseDouble(titleRate);
        String toUpdate = df2.format((input / titleRateD) * rateD);
        currency.setText(String.valueOf(toUpdate));
        System.out.println("updated");
    }

    /*
     * Combine the time components and display them when called
     */
    public void setTime(String date, String time) {
        String toSet = "Last update: " + date + "  " + time;
        lastUpdate.setText(toSet);
    }
}

