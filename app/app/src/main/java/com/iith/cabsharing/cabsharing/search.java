package com.iith.cabsharing.cabsharing;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class search extends AppCompatActivity implements
        View.OnClickListener, View.OnFocusChangeListener,
        Response.Listener<JSONArray>, Response.ErrorListener {
    private GoogleSignInClient mGoogleSignInClient;
    private Toolbar myToolbar;
    private Date now;
    private static int RC_SIGN_IN = 100;
    Toast toastDate;
    protected int syear, smonth, sday, wyear, wmonth, wday, shr, smin, whr, wmin;
    ProgressDialog progSearching;
    protected void updateStart(){
        ((EditText)findViewById(R.id.dateStart)).setText(syear+"-"+to2digits(smonth)+"-"+to2digits(sday));
        ((EditText)findViewById(R.id.timeStart)).setText(to2digits(shr)+":"+to2digits(smin));
    }
    protected void updateWait(){
        ((EditText)findViewById(R.id.dateWait)).setText(wyear+"-"+to2digits(wmonth)+"-"+to2digits(wday));
        ((EditText)findViewById(R.id.timeWait)).setText(to2digits(whr)+":"+to2digits(wmin));
    }
    @Override
    protected void onStart(){
        super.onStart();
         now = new Date();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
       // getActionBar().setDisplayHomeAsUpEnabled(true);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //findViewById(R.id.btnLogout).setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
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
                String starttime = ((EditText)findViewById(R.id.dateStart)).getText().toString()
                        +" "+((EditText)findViewById(R.id.timeStart)).getText().toString()+":00";
                String waittill = ((EditText)findViewById(R.id.dateWait)).getText().toString()
                        +" "+((EditText)findViewById(R.id.timeWait)).getText().toString()+":00";


                //First check if they are Valid Date Times
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    sdf.setLenient(false);
                    Date startDT = sdf.parse(starttime), waitDT = sdf.parse(waittill);
                  //  now = sdf.parse(now.toString());
                    if(startDT.before(now) || waitDT.before(now) || startDT.after(waitDT)){
                        Toast.makeText(this, "The app doesn't support time travel yet.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }catch(ParseException pe){
                    toastDate.show();
                    return;
                }

                try {
                    JSONObject request = new JSONObject();
                    request.put("from", ((EditText) findViewById(R.id.inpFrom)).getText());
                    request.put("to", ((EditText) findViewById(R.id.inpTo)).getText());
                    request.put("starttime", starttime);
                    request.put("waittill", waittill);

                    JSONArray requestArray = new JSONArray("["+request.toString()+"]");
                    //Log.v("Before search req", requestArray.toString());
                    MySingleton.getInstance(this).addToRequestQueue(new JsonArrayRequest(Request.Method.POST, getString(R.string.url_search), requestArray, this, this));
                    progSearching = ProgressDialog.show(this, "", "Loading ...", true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            MySingleton.getInstance(search.this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                                @Override
                                public boolean apply(Request<?> request) {
                                    return true;
                                }
                            });
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    String to2digits(int x){
        if(x < 10)
            return "0"+x;
        return ""+x;
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(progSearching != null)
            progSearching.cancel();
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT);
        //Log.e("Volley Search", error.getMessage());
    }

    @Override
    public void onResponse(JSONArray response) {
        if(progSearching != null)
            progSearching.cancel();
        //Log.w("Volley","search result"+response.toString());
        String starttime = ((EditText)findViewById(R.id.dateStart)).getText().toString()
                +" "+((EditText)findViewById(R.id.timeStart)).getText().toString()+":00";
        String waittill = ((EditText)findViewById(R.id.dateWait)).getText().toString()
                +" "+((EditText)findViewById(R.id.timeWait)).getText().toString()+":00";

        Intent intent = new Intent(this, SearchResult.class);
        intent.putExtra("from", ((EditText) findViewById(R.id.inpFrom)).getText().toString())
                .putExtra("to", ((EditText) findViewById(R.id.inpTo)).getText().toString())
                .putExtra("starttime", starttime)
                .putExtra("waittill", waittill)
                .putExtra("json", response.toString());
        startActivity(intent);

    }
}
