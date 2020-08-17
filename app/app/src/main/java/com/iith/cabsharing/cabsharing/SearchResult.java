package com.iith.cabsharing.cabsharing;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

public class SearchResult extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private Toolbar myToolbar;
    private final LinearLayout.LayoutParams mpwc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private final LinearLayout.LayoutParams mpmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    private  final LinearLayout.LayoutParams wcwc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private final LinearLayout.LayoutParams wcmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
    HashMap<Integer, Integer> idToSno = new HashMap<>();
    String from,to, starttime, waittill;
    JSONArray response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            signOut();
            return;
        }
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
    protected void onStart(){
        super.onStart();
        Intent home = getIntent();
        from = home.getStringExtra("from");
        to = home.getStringExtra("to");
        starttime = home.getStringExtra("starttime");
        waittill = home.getStringExtra("waittill");
        try {
            response = new JSONArray(home.getStringExtra("json"));
        }catch (JSONException jsonex){jsonex.printStackTrace();
            Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
        }
        //The result is ready
        i_am_sleepy(response);
    }
    private void i_am_sleepy(JSONArray response){
        LinearLayout llv = findViewById(R.id.llvert);
        llv.removeAllViews();
        if(response.length() == 0){
            TextView txtNone = new TextView(this);
            txtNone.setLayoutParams(wcwc);
            txtNone.setText("No rides found! Create new!");
            llv.addView(txtNone);
        }
        else {
            Space sp;

            HorizontalScrollView scr = new HorizontalScrollView(this);
            scr.setLayoutParams(mpwc);
            scr.setFillViewport(true);
            scr.setScrollBarSize(px(5));
            ((LinearLayout) findViewById(R.id.tlv)).removeAllViews();
            ((LinearLayout) findViewById(R.id.tlv)).addView(scr);

            LinearLayout headrow = new LinearLayout(this);
            headrow.setLayoutParams(mpmp);
            headrow.setOrientation(LinearLayout.HORIZONTAL);
            scr.addView(headrow);

            TextView headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(40), LinearLayout.LayoutParams.MATCH_PARENT));
            headtexts.setText("From");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(40), LinearLayout.LayoutParams.MATCH_PARENT));
            headtexts.setText("To");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(120), LinearLayout.LayoutParams.MATCH_PARENT));
            headtexts.setText("People");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(215), LinearLayout.LayoutParams.MATCH_PARENT));
            headtexts.setText("Leaving Time");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(100), LinearLayout.LayoutParams.MATCH_PARENT));
            headtexts.setText("Remarks");
            headtexts.setTypeface(null, Typeface.BOLD);
            headrow.addView(headtexts);

            sp = new Space(this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
            headrow.addView(sp);

            headtexts = new TextView(this);
            headtexts.setLayoutParams(new LinearLayout.LayoutParams(px(30), LinearLayout.LayoutParams.MATCH_PARENT));
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
                    LinearLayout.LayoutParams svlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                    tfrom.setLayoutParams(new LinearLayout.LayoutParams(px(40), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tfrom.setText(from);
                    rowlay.addView(tfrom);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tto = new TextView(this);
                    tto.setLayoutParams(new LinearLayout.LayoutParams(px(40), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tto.setText(to);
                    rowlay.addView(tto);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    ScrollView scroll_people = new ScrollView(this);
                    scroll_people.setLayoutParams(new LinearLayout.LayoutParams(px(120), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.25f*/));

                    TextView tpeople = new TextView(this);
                    tpeople.setLayoutParams(mpmp);
                    String tpeople_value = people.getJSONObject(0).getString("rollno");
                    for (int j = 1; j < people.length(); ++j) {
                        tpeople_value = tpeople_value + ",\n" + people.getJSONObject(j).getString("rollno");
                    }
                    tpeople.setText(tpeople_value);
                    scroll_people.addView(tpeople);
                    rowlay.addView(scroll_people);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tstarttime = new TextView(this);
                    tstarttime.setLayoutParams(new LinearLayout.LayoutParams(px(100), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    tstarttime.setText(starttime);
                    rowlay.addView(tstarttime);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView twaittill = new TextView(this);
                    twaittill.setLayoutParams(new LinearLayout.LayoutParams(px(100), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    twaittill.setText(waittill);
                    rowlay.addView(twaittill);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    TextView tremarks = new TextView(this);
                    tremarks.setLayoutParams(new LinearLayout.LayoutParams(px(100), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.25f*/));
                    tremarks.setText(remarks);
                    rowlay.addView(tremarks);

                    sp = new Space(this);
                    sp.setLayoutParams(new LinearLayout.LayoutParams(px(15), LinearLayout.LayoutParams.MATCH_PARENT));
                    rowlay.addView(sp);

                    Button btnDlt = new Button(this);
                    btnDlt.setLayoutParams(new LinearLayout.LayoutParams(px(30), LinearLayout.LayoutParams.MATCH_PARENT/*, 0.15f*/));
                    btnDlt.setText("+");
                    btnDlt.setTextColor(Color.rgb(0, 102, 0));
                    int btnDltId = ViewCompat.generateViewId();
                    btnDlt.setId(btnDltId);
                    idToSno.put(btnDltId, sno);
                    btnDlt.setOnClickListener(this);
                    boolean joinable = true;
                    String rollno = GoogleSignIn.getLastSignedInAccount(this).getEmail();
                    rollno = rollno.substring(0, rollno.length() - 11);
                    for (int k = 0; k < people.length(); k++)
                        if (people.getJSONObject(k).getString("rollno").compareTo(rollno) == 0) {
                            joinable = false;
                            break;
                        }

                    if(!joinable) {
                        btnDlt.setEnabled(false);
                        btnDlt.setVisibility(View.INVISIBLE);
                    }
                    rowlay.addView(btnDlt);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();

            }
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
    public void onClick(View view) {
        //Log.w("boring", "something clicked");
        switch (view.getId()) {
            /*case R.id.btnLogout:
                signOut();
                break;*/
            case R.id.btnSearch:
                Intent intent = new Intent(this, NewRide.class);
                intent.putExtra("from", from);
                intent.putExtra("to", to);
                intent.putExtra("starttime", starttime);
                intent.putExtra("waittill", waittill);
                startActivity(intent);
                break;
            default:
                int sno = idToSno.get(view.getId());
                String rollno = GoogleSignIn.getLastSignedInAccount(this).getEmail();
                rollno = rollno.substring(0, rollno.length() - 11);
                JSONObject request = new JSONObject();
                try{
                    request.put("sno", sno);
                    request.put("rollno", rollno);
                    request.put("starttime", starttime.substring(0, starttime.length()-3));
                    request.put("waittill", waittill.substring(0, waittill.length() - 3));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //Log.w("Before join", request.toString());
                MySingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST,
                        getString(R.string.url_join), request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                      //  updateEnrolled();
                        //Log.w("join", response.toString());
                        try {
                            if (response.getString("result").compareTo("SUCCESS") == 0){
                                Toast.makeText(SearchResult.this, "Joined ride successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SearchResult.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException jsex){
                            Toast.makeText(SearchResult.this, "JSONException occured. Please report the bug!", Toast.LENGTH_SHORT).show();
                            jsex.printStackTrace();
                        }

                        Intent intent = new Intent(SearchResult.this, Enrolled.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // updateEnrolled();
                        //Log.e("join", error.getMessage());
                    }
                }));
                break;
                //TODO
        }
    }
    private int px(int dp){
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (float)dp,
                getResources().getDisplayMetrics()
        ));
    }
}
