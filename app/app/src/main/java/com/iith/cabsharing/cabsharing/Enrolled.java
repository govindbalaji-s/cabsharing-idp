package com.iith.cabsharing.cabsharing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
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

import java.util.HashMap;

public class Enrolled extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONArray>, Response.ErrorListener{
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private JSONArray requestArray;
    private final LayoutParams mpwc = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private final LayoutParams mpmp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private  final LayoutParams wcwc = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final LayoutParams wcmp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    HashMap<Integer, Integer> idToSno = new HashMap<>();
    private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);        //getActionBar().setDisplayHomeAsUpEnabled(false);
       // setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       // findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        ((TextView)findViewById(R.id.txtEnrolled)).setTypeface(null, Typeface.BOLD);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //ScrollView rides = new ScrollView();
        //Fetch all the rides
        if(account == null){
            signOut();
            return;
        }
        JSONObject request = new JSONObject();
        String rollno = account.getEmail();
        rollno = rollno.substring(0, rollno.length() - 11);
        Log.w("check", "Rollno from email" + rollno);
        try {
            // TODO security request.put("idtoken", account.getIdToken());
            request.put("rollno", rollno);
            requestArray = new JSONArray("["+request.toString()+"]");
        }catch (JSONException jsonex){
            jsonex.printStackTrace();
            Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
        }
        Log.w("Before searchbyuser", requestArray.toString());

    }
    @Override
    protected void onStart(){
        super.onStart();
        updateEnrolled();
    }
    void updateEnrolled(){
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST, getString(R.string.url_enrolled), requestArray, this, this);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

        LinearLayout llv = findViewById(R.id.llvert);
        llv.removeAllViews();
        TextView txtNone = new TextView(this);
        txtNone.setLayoutParams(wcwc);
        txtNone.setText("Loading ... ");
        llv.addView(txtNone);
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
    public void onClick(View v){
        Log.w("boring", "something clicked");
        switch (v.getId()) {
            /*case R.id.btnLogout:
                signOut();
                break;*/
            case R.id.btnSearch:
                Intent intent = new Intent(this, search.class);
                startActivity(intent);
                break;
            default:
                int sno = idToSno.get(v.getId());
                String rollno = GoogleSignIn.getLastSignedInAccount(this).getEmail();
                rollno = rollno.substring(0, rollno.length() - 11);
                JSONObject request = new JSONObject();
                try{
                    request.put("sno", sno);
                    request.put("rollno", rollno);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                }
                Log.w("Before leave", request.toString());
                MySingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST,
                        getString(R.string.url_leave), request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("leave", response.toString());
                        try {
                            if (response.getString("result").compareTo("SUCCESS") == 0){
                                Toast.makeText(Enrolled.this, "Left ride successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Enrolled.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException jsex){
                            Toast.makeText(Enrolled.this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                            jsex.printStackTrace();
                        }
                        updateEnrolled();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Enrolled.this, "Network Error",  Toast.LENGTH_SHORT).show();
                        updateEnrolled();
                        //Log.e("leave", error.getMessage());
                    }
                }));
                break;


        }
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
    public void onResponse(JSONArray response) {
       // response.toJSONArray()
        //Log.w("enrolled response", response.toString());
        LinearLayout llv = findViewById(R.id.llvert);
        llv.removeAllViews();
        if(response.length() == 0){
            TextView txtNone = new TextView(this);
            txtNone.setLayoutParams(wcwc);
            txtNone.setText("No registered rides.");
            llv.addView(txtNone);
        }
        else{
            Space sp;

            HorizontalScrollView scr = new HorizontalScrollView(this);
            scr.setLayoutParams(mpwc);
            scr.setFillViewport(true);
            scr.setScrollBarSize(px(5));
            ((LinearLayout)findViewById(R.id.tlv)).removeAllViews();
            ((LinearLayout)findViewById(R.id.tlv)).addView(scr);

            LinearLayout headrow = new LinearLayout(this);
            headrow.setLayoutParams(mpmp);
            headrow.setOrientation(LinearLayout.HORIZONTAL);
            scr.addView(headrow);

            TextView headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(40), LayoutParams.MATCH_PARENT));
            headtexts.setText("From");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(40), LayoutParams.MATCH_PARENT));
            headtexts.setText("To");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(120), LayoutParams.MATCH_PARENT));
            headtexts.setText("People");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(215), LayoutParams.MATCH_PARENT));
            headtexts.setText("Leaving Time");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(100), LayoutParams.MATCH_PARENT));
            headtexts.setText("Remarks");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LayoutParams(px(30), LayoutParams.MATCH_PARENT));
            headtexts.setText(" ");
            headrow.addView(headtexts);

            try {
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject row = response.getJSONObject(i);
                    Integer sno = row.getInt("sno");
                    String from = row.getString("from");
                    String to = row.getString("to");
                    JSONArray people = row.getJSONArray("people");
                    String starttime = row.getString("starttime");
                    String waittill = row.getString("waittill");
                    String remarks = row.getString("remarks");
                    //
                    HorizScrView sv = new HorizScrView(this, scr);
                    LayoutParams svlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    svlp.topMargin = px(10);
                    sv.setLayoutParams(svlp);
                    sv.setFillViewport(true);
                    llv.addView(sv);
                    //
                    LinearLayout rowlay = new LinearLayout(this);
                    rowlay.setLayoutParams(mpmp);
                  //  rowlay.setLayoutParams(mpwc);
                    rowlay.setOrientation(LinearLayout.HORIZONTAL);
                    rowlay.setWeightSum(1.3f);
                    sv.addView(rowlay);
                    //llv.addView(rowlay);
                    //
                    TextView tfrom = new TextView(this);
                    tfrom.setLayoutParams(new LayoutParams(px(40), LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tfrom.setText(from);
                    rowlay.addView(tfrom);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tto = new TextView(this);
                    tto.setLayoutParams(new LayoutParams(px(40), LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tto.setText(to);
                    rowlay.addView(tto);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    ScrollView scroll_people = new ScrollView(this);
                    scroll_people.setLayoutParams(new LayoutParams(px(120), LayoutParams.MATCH_PARENT/*, 0.25f*/));

                    TextView tpeople = new TextView(this);
                    tpeople.setLayoutParams(mpmp);
                    String tpeople_value = people.getJSONObject(0).getString("rollno");
                    for(int j = 1; j < people.length(); ++j){
                        tpeople_value = tpeople_value + ",\n"+people.getJSONObject(j).getString("rollno");
                    }
                    tpeople.setText(tpeople_value);
                    scroll_people.addView(tpeople);
                    rowlay.addView(scroll_people);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tstarttime = new TextView(this);
                    tstarttime.setLayoutParams(new LayoutParams(px(100), LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tstarttime.setText(starttime);
                    rowlay.addView(tstarttime);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView twaittill = new TextView(this);
                    twaittill.setLayoutParams(new LayoutParams(px(100), LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    twaittill.setText(waittill);
                    rowlay.addView(twaittill);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tremarks = new TextView(this);
                    tremarks.setLayoutParams(new LayoutParams(px(100), LayoutParams.MATCH_PARENT/*, 0.25f*/));
                    tremarks.setText(remarks);
                    rowlay.addView(tremarks);

                    sp = new Space(this);
                    sp.setLayoutParams(new LayoutParams(px(15), LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    Button btnDlt = new Button(this);
                    btnDlt.setLayoutParams(new LayoutParams(px(30), LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    btnDlt.setText("-");
                    btnDlt.setTextColor(Color.RED);
                    int btnDltId = ViewCompat.generateViewId();
                    btnDlt.setId(btnDltId);
                    idToSno.put(btnDltId, sno);
                    btnDlt.setOnClickListener(this);
                    rowlay.addView(btnDlt);

                }
            }catch(JSONException e){
                Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    private int px(int dp){
       return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
               (float)dp,
                getResources().getDisplayMetrics()
        ));
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        //Log.e("Volley", "At enrolled, " + error.getMessage());
        LinearLayout llv = findViewById(R.id.llvert);
        llv.removeAllViews();
        TextView txtNone = new TextView(this);
        txtNone.setLayoutParams(wcwc);
        txtNone.setText("Failed to fetch content! ");
        llv.addView(txtNone);

    }
}
