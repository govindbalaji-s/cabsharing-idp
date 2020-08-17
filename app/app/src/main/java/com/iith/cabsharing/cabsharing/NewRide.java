package com.iith.cabsharing.cabsharing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewRide extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private GoogleSignInClient mGoogleSignInClient;
    String from,to, starttime, waittill;
    Toast toastDate;
    private Date now;
    protected int syear, smonth, sday, wyear, wmonth, wday, shr, smin, whr, wmin;
    private Toolbar myToolbar;
    protected void updateStart(){
        ((EditText)findViewById(R.id.dateStart)).setText(syear+"-"+to2digits(smonth)+"-"+to2digits(sday));
        ((EditText)findViewById(R.id.timeStart)).setText(to2digits(shr)+":"+to2digits(smin));
    }
    protected void updateWait(){
        ((EditText)findViewById(R.id.dateWait)).setText(wyear+"-"+to2digits(wmonth)+"-"+to2digits(wday));
        ((EditText)findViewById(R.id.timeWait)).setText(to2digits(whr)+":"+to2digits(wmin));
    } @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actLogout:
                signOut();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b)
            return;
        switch (view.getId()) {
            case R.id.dateStart:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        syear = y; smonth = m+1; sday = d;
                        updateStart();
                    }
                }, syear, smonth-1, sday).show();
                break;
            case R.id.dateWait:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        wyear = y; wmonth = m+1; wday = d;
                        updateWait();
                    }
                }, wyear, wmonth-1, wday).show();
                break;
            case R.id.timeStart:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        shr = i; smin = i1;
                        updateStart();
                    }
                }, shr, smin, false).show();
                break;
            case R.id.timeWait:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        whr = i; wmin = i1;
                        updateWait();
                    }
                }, whr, wmin, false).show();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ride);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
       // getActionBar().setDisplayHomeAsUpEnabled(true);

        // setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            signOut();
            return;
        }
        Intent home = getIntent();
        from = home.getStringExtra("from");
        to = home.getStringExtra("to");
        starttime = home.getStringExtra("starttime");
        waittill = home.getStringExtra("waittill");
        ((EditText)findViewById(R.id.inpFrom)).setText(from);
        ((EditText)findViewById(R.id.inpTo)).setText(to);
        ((EditText)findViewById(R.id.dateStart)).setText(starttime.substring(0, 10));
        ((EditText)findViewById(R.id.timeStart)).setText(starttime.substring(11));
        ((EditText)findViewById(R.id.dateWait)).setText(waittill.substring(0, 10));
        ((EditText)findViewById(R.id.timeWait)).setText(waittill.substring(11));
//        findViewById(R.id.btnLogout).setOnClickListener(this);
        //Set time
        final Calendar c = Calendar.getInstance();
        syear = wyear = c.get(Calendar.YEAR);
        smonth = wmonth = c.get(Calendar.MONTH)+1;
        sday = wday = c.get(Calendar.DAY_OF_MONTH);
        shr = whr = c.get(Calendar.HOUR_OF_DAY);
        smin = wmin = c.get(Calendar.MINUTE);

        //Set pickers
        findViewById(R.id.dateStart).setOnClickListener(this);
        findViewById(R.id.dateWait).setOnClickListener(this);
        findViewById(R.id.timeStart).setOnClickListener(this);
        findViewById(R.id.timeWait).setOnClickListener(this);

        findViewById(R.id.btnSearch).setOnClickListener(this);

        toastDate = Toast.makeText(this, "Invalid Date/Time Format!", Toast.LENGTH_SHORT);

    }
    @Override
    protected void onStart(){
        super.onStart();
        now = new Date();
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        //Log.w("GSI", "Logged out");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        //Log.w("boring", "something clicked");
        switch (view.getId()) {
            /*case R.id.btnLogout:
                signOut();
                break;*/
            case R.id.dateStart:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        syear = y; smonth = m+1; sday = d;
                        updateStart();
                    }
                }, syear, smonth-1, sday).show();
                break;
            case R.id.dateWait:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        wyear = y; wmonth = m+1; wday = d;
                        updateWait();
                    }
                }, wyear, wmonth-1, wday).show();
                break;
            case R.id.timeStart:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        shr = i; smin = i1;
                        updateStart();
                    }
                }, shr, smin, false).show();
                break;
            case R.id.timeWait:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        whr = i; wmin = i1;
                        updateWait();
                    }
                }, whr, wmin, false).show();
                break;
            case R.id.btnSearch:
                //Validate inputs
                starttime = ((EditText)findViewById(R.id.dateStart)).getText().toString()
                        +" "+((EditText)findViewById(R.id.timeStart)).getText().toString()+":00";
                waittill = ((EditText)findViewById(R.id.dateWait)).getText().toString()
                        +" "+((EditText)findViewById(R.id.timeWait)).getText().toString()+":00";
                String rollno = GoogleSignIn.getLastSignedInAccount(this).getEmail();
                rollno = rollno.substring(0, rollno.length() - 11);

                //Date now = new Date();
                //First check if they are Valid Date Times
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    sdf.setLenient(false);
                    Date startDT = sdf.parse(starttime), waitDT = sdf.parse(waittill);
                    //now = sdf.parse(now.toString());
                    if(startDT.before(now) || waitDT.before(now) || startDT.after(waitDT)){
                        Toast.makeText(this, "The app doesn't support time travel yet.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }catch(ParseException pe){
                    toastDate.show();
                    return;
                }

                try {//TODO change
                    JSONObject request = new JSONObject();
                    request.put("from", ((EditText) findViewById(R.id.inpFrom)).getText());
                    request.put("to", ((EditText) findViewById(R.id.inpTo)).getText());
                    request.put("rollno", rollno);
                    request.put("starttime", starttime);
                    request.put("waittill", waittill);
                    request.put("remarks", ((EditText)findViewById(R.id.inpRemarks)).getText());

                    //Log.v("Before insert req", request.toString());
                    MySingleton.getInstance(this).addToRequestQueue(
                            new JsonObjectRequest(Request.Method.POST, getString(R.string.url_insert), request, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Log.v("response insert", response.toString());

                                    try {
                                        if (response.getString("result").compareTo("SUCCESS") == 0){
                                            Toast.makeText(NewRide.this, "Added ride successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(NewRide.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (JSONException jsex){
                                        Toast.makeText(NewRide.this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                                        jsex.printStackTrace();
                                    }
                                    Intent intent = new Intent(NewRide.this, Enrolled.class);
                                    startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Log.e("volley insert", error.getMessage());
                                }
                            }));
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(NewRide.this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    String to2digits(int x){
        if(x < 10)
            return "0"+x;
        return ""+x;
    }
}
