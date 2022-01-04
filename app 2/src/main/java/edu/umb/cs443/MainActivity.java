package edu.umb.cs443;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentActivity;


import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.umb.cs443.ListFragment;
import edu.umb.cs443.SearchFragment;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

	public final static String DEBUG_TAG="edu.umb.cs443.MYMSG";


    private String url = "https://periodic-table-api.herokuapp.com/";
    private JSONArray JsonArray;
    private JSONObject JsonObject;
    private JSONObject JsonObject1;
    private ListView myListview;
    private ArrayAdapter adapter;
    private String[] s;
    private EditText myEditText;

    private TextView textView;
    private GridView mygridview;

    private BottomNavigationView bottomNavigationView;
    private ListFragment listFragment = new ListFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private WelComeFragment welComeFragment = new WelComeFragment();


    private boolean back = false;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
                listview();
                return true;
            case R.id.navigation_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                return true;
        }
        return false;
    }


    //view for the list periodic table
    public void listview(){
        count = 0;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Thread(new DownloadThread(url)).start();
            //myListview.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadThread implements Runnable{
        private String url;
        DownloadThread(String stringUrl){this.url=stringUrl;}
        @Override
        public void run() {
            try {
                JsonArray = downloadUrl(url);
                myListview = (ListView) findViewById(R.id.text_list);
                if(JsonArray!=null){
                    s = new String[118];

                    for (int i = 0; i < 118; i++){
                        JSONObject element = JsonArray.getJSONObject(i);
                        String elementString = element.getString("symbol");
                        s[i] = elementString;
                    }
                }
                //adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, s);
                threadHandler.sendEmptyMessage(0);

            } catch (IOException e) {
                e.printStackTrace();
            }  catch (JSONException e){
                e.printStackTrace();
            }
        }

        private JSONArray downloadUrl(String myurl) throws IOException{
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setReadTimeout(10000 /* milliseconds */);
                //conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                JSONArray json = new JSONArray(responseStrBuilder.toString());
                return json;

            }catch(Exception e) {
            }finally {
                if (is != null) {
                    is.close();
                }
            }

            return null;
        }
    }
    public Handler threadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            try{
                List<String> s_list = new ArrayList<String>(Arrays.asList(s));
                adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_element, s_list);
                myListview.setAdapter(adapter);
                myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        back = true;
                        new Thread(new DownloadElementThread(i)).start();
                    }
                });
            }catch (Exception e){
                Log.i(DEBUG_TAG, "returned elementnull");
            }
        }
    };

    //onItemclick of element:
    private class DownloadElementThread implements Runnable{
        private int atomicNumber;
        DownloadElementThread(int i){this.atomicNumber=i;}
        @Override
        public void run() {
            try {
                String u = url+"atomicSymbol/"+s[atomicNumber];
                JsonObject1 = downloadUrll(u);
                ElementthreadHandler.sendEmptyMessage(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private JSONObject downloadUrll(String myurl) throws IOException{
            InputStream is = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setReadTimeout(10000 /* milliseconds */);
                //conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject json = new JSONObject(responseStrBuilder.toString());
                return json;

            }catch(Exception e) {
            }finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }
    }
    public Handler ElementthreadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            try{
                String[] ss = new String[30];
                int i = 0;
                String name = "Name";
                ss[i] = name;i+=2;
                String atomicMass = "Atomic Mass";
                ss[i] = atomicMass;i+=2;
                String groupBlock = "Group Block";
                ss[i] = groupBlock;i+=2;
                String atomicNumber = "Atomic Number";
                ss[i] = atomicNumber;i+=2;
                String atomicRadius = "Atomic Radius";
                ss[i] = atomicRadius;i+=2;
                String standardState = "Standard State";
                ss[i] = standardState;i+=2;
                String boilingPoint = "Boiling Point";
                ss[i] = boilingPoint;i+=2;
                String bondingType = "Bonding Type";
                ss[i] = bondingType;i+=2;
                String meltingPoint = "Melting Point";
                ss[i] = meltingPoint;i+=2;
                String density = "Density";
                ss[i] = density;i+=2;
                String electronAffinity = "Electron Affinity";
                ss[i] = electronAffinity;i+=2;
                String electronegativity = "Electrone Gativity";
                ss[i] = electronegativity;i+=2;
                String electronicConfiguration = "Electronic Configuration";
                ss[i] = electronicConfiguration;i+=2;
                String ionRadius = "Ion Radius";
                ss[i] = ionRadius;i+=2;
                String ionizationEnergy = "Ionization Energy";
                ss[i] = ionizationEnergy;i = 1;

                ss[i] = JsonObject1.getString("name");i+=2;
                ss[i] = JsonObject1.getString("atomicMass");i+=2;
                ss[i] = JsonObject1.getString("groupBlock");i+=2;
                ss[i] = JsonObject1.getString("atomicNumber");i+=2;
                ss[i] = JsonObject1.getString("atomicRadius");i+=2;
                ss[i] = JsonObject1.getString("standardState");i+=2;
                ss[i] = JsonObject1.getString("boilingPoint");i+=2;
                ss[i] = JsonObject1.getString("bondingType");i+=2;
                ss[i] = JsonObject1.getString("meltingPoint");i+=2;
                ss[i] = JsonObject1.getString("density");i+=2;
                ss[i] = JsonObject1.getString("electronAffinity");i+=2;
                ss[i] = JsonObject1.getString("electronegativity");i+=2;
                ss[i] = JsonObject1.getString("electronicConfiguration");i+=2;
                ss[i] = JsonObject1.getString("ionRadius");i+=2;
                ss[i] = JsonObject1.getString("ionizationEnergy");

                setContentView(R.layout.element_detail);
                textView = (TextView) findViewById(R.id.element_name);
                mygridview = (GridView) findViewById(R.id.simpleGridView);

                String elementname = JsonObject1.getString("symbol");
                textView.setText(elementname);
                List<String> s_list = new ArrayList<String>(Arrays.asList(ss));
                ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this, R.layout.element_grid_text, s_list);
                mygridview.setAdapter(adapter);

            }catch (Exception e){
                Log.i(DEBUG_TAG, "json error");
            }
        }
    };

    public void doresearch(View view){
        count = 0;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        myEditText = (EditText) findViewById(R.id.element_editText);
        String x = myEditText.getText().toString();
        if (networkInfo != null && networkInfo.isConnected()) {
            back = true;
            new Thread(new ResearchDownloadThread(x)).start();
        } else {
            Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT).show();
        }
    }

    private class ResearchDownloadThread implements Runnable{
        private String input;
        ResearchDownloadThread(String x){this.input=x;}
        @Override
        public void run() {
            try {
                String atomicNumberSearch = url+"atomicNumber/"+input;
                String atomicNameSearch = url+"atomicName/"+input;
                String atomicSymbolSearch = url+"atomicSymbol/"+input;
                String[] listSearch = {atomicNumberSearch,atomicNameSearch,atomicSymbolSearch};
                for (int i = 0; i<3;i++){
                    JsonObject = downloadUrl(listSearch[i]);
                    if (!JsonObject.has("message")){
                        myEditText.setHint("Enter Atomic Number/Sysmbol/Name");
                        textView = (TextView) findViewById(R.id.research_result);
                        mygridview = (GridView) findViewById(R.id.SearchGridView);
                        SearchElementthreadHandler.sendEmptyMessage(0);
                        break;
                    } else if(i==2){
                        Log.i(DEBUG_TAG, "No in three ways");
                        textView = (TextView) findViewById(R.id.research_result);
                        mygridview = (GridView) findViewById(R.id.SearchGridView);
                        textView.setHint("Retry");
                        ErrorthreadHandler.sendEmptyMessage(0);
                        Toast.makeText(getApplicationContext(), "No Result", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                //adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private JSONObject downloadUrl(String myurl) throws IOException{
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setReadTimeout(10000 /* milliseconds */);
                //conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                JSONObject json = new JSONObject(responseStrBuilder.toString());
                return json;

            }catch(Exception e) {
            }finally {
                if (is != null) {
                    is.close();
                }
            }

            return null;
        }
    }
    @Override
    public void onBackPressed(){
        if (back){
            Intent returnBtn = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(returnBtn);
            back = false;
        } else {
            count++;
            Toast.makeText(getApplicationContext(), "Press again to quit the app", Toast.LENGTH_SHORT).show();
            if (count == 2){
                super.onBackPressed();
            }
        }
    }
    public Handler SearchElementthreadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            try{
                String[] ss = new String[30];
                int i = 0;
                String name = "Name";
                ss[i] = name;i+=2;
                String atomicMass = "Atomic Mass";
                ss[i] = atomicMass;i+=2;
                String symbol = "Symbol";
                String groupBlock = "Group Block";
                ss[i] = groupBlock;i+=2;
                String atomicNumber = "Atomic Number";
                ss[i] = atomicNumber;i+=2;
                String atomicRadius = "Atomic Radius";
                ss[i] = atomicRadius;i+=2;
                String standardState = "Standard State";
                ss[i] = standardState;i+=2;
                String boilingPoint = "Boiling Point";
                ss[i] = boilingPoint;i+=2;
                String bondingType = "Bonding Type";
                ss[i] = bondingType;i+=2;
                String meltingPoint = "Melting Point";
                ss[i] = meltingPoint;i+=2;
                String density = "Density";
                ss[i] = density;i+=2;
                String electronAffinity = "Electron Affinity";
                ss[i] = electronAffinity;i+=2;
                String electronegativity = "Electrone Gativity";
                ss[i] = electronegativity;i+=2;
                String electronicConfiguration = "Electronic Configuration";
                ss[i] = electronicConfiguration;i+=2;
                String ionRadius = "Ion Radius";
                ss[i] = ionRadius;i+=2;
                String ionizationEnergy = "Ionization Energy";
                ss[i] = ionizationEnergy;i = 1;

                ss[i] = JsonObject.getString("name");i+=2;
                ss[i] = JsonObject.getString("atomicMass");i+=2;
                ss[i] = JsonObject.getString("groupBlock");i+=2;
                ss[i] = JsonObject.getString("atomicNumber");i+=2;
                ss[i] = JsonObject.getString("atomicRadius");i+=2;
                ss[i] = JsonObject.getString("standardState");i+=2;
                ss[i] = JsonObject.getString("boilingPoint");i+=2;
                ss[i] = JsonObject.getString("bondingType");i+=2;
                ss[i] = JsonObject.getString("meltingPoint");i+=2;
                ss[i] = JsonObject.getString("density");i+=2;
                ss[i] = JsonObject.getString("electronAffinity");i+=2;
                ss[i] = JsonObject.getString("electronegativity");i+=2;
                ss[i] = JsonObject.getString("electronicConfiguration");i+=2;
                ss[i] = JsonObject.getString("ionRadius");i+=2;
                ss[i] = JsonObject.getString("ionizationEnergy");

                String elementname = JsonObject.getString("symbol");
                textView.setText(elementname);
                List<String> s_list = new ArrayList<String>(Arrays.asList(ss));
                ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this, R.layout.element_grid_text, s_list);
                mygridview.setAdapter(adapter);

            }catch (Exception e){
                Log.i(DEBUG_TAG, "returned elementnull");
            }
        }
    };
    public Handler ErrorthreadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            try{
                textView.setText("Please enter correct input");
                String[] ss = new String[30];
                for (int i = 0; i<30; i++){
                    ss[i]="";
                }
                List<String> s_list = new ArrayList<String>(Arrays.asList(ss));
                ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this, R.layout.element_grid_text, s_list);
                mygridview.setAdapter(adapter);
            }catch (Exception e){
                Log.i(DEBUG_TAG, "returned elementnull");
            }
        }
    };

}
