package org.elis.depeat.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.elis.depeat.R;
import org.elis.depeat.datamodels.Restaurant;
import org.elis.depeat.ui.adapters.RestaturantAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView restaurantRV;
    RecyclerView.LayoutManager layoutManager;
    RestaturantAdapter adapter;
    ArrayList<Restaurant> arrayList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    private static final String SharedPrefs = "org.elis.depeat.general_prefs";
    private static final String VIEW_MODE = "VIEW_MODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantRV = findViewById(R.id.places_rv);

        layoutManager = getLayoutManager(getSavedLayoutManager());
        adapter = new RestaturantAdapter(this);
        adapter.setGridMode(getSavedLayoutManager());

        restaurantRV.setLayoutManager(layoutManager);
        restaurantRV.setAdapter(adapter);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://5ba19290ee710f0014dd764c.mockapi.io/api/v1/restaurant";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, // HTTP request method
                url, // Server link
                new Response.Listener<String>() {  // Listener for successful response
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,response);
                        //Start parsing
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            JSONArray restaurantJsonArray = responseJson.getJSONArray("data");
                            for (int i = 0; i< restaurantJsonArray.length(); i++){
                                Restaurant restaurant = new Restaurant(restaurantJsonArray.getJSONObject(i));
                                arrayList.add(restaurant);
                            }
                            adapter.setData(arrayList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() { // Listener for error response
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.getMessage());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    private ArrayList<Restaurant> getData(){
        arrayList = new ArrayList<>();
        arrayList.add(new Restaurant("Burger","via le mani dal naso",2.00f));
        arrayList.add(new Restaurant("Burger","via le mani dal naso",2.00f));
        arrayList.add(new Restaurant("Burger","via le mani dal naso",2.00f));
        arrayList.add(new Restaurant("Burger","via le mani dal naso",2.00f));


        return arrayList;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.view_mode).setIcon(getSavedLayoutManager()?R.drawable.baseline_grid_on_white_24:R.drawable.baseline_list_white_24);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.view_mode){
            setLayoutManager();
            item.setIcon(adapter.isGridMode()?R.drawable.baseline_grid_on_white_24 : R.drawable.baseline_list_white_24);
            return true;
        }
        if(item.getItemId() == R.id.login_menu){

            startActivity(new Intent(this,LoginActivity.class));
             return true;
        }else if(item.getItemId() == R.id.checkout_menu){
            startActivity(new Intent(this,CheckoutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setLayoutManager(){
        layoutManager = getLayoutManager(!adapter.isGridMode());
        adapter.setGridMode(!adapter.isGridMode());
        restaurantRV.setLayoutManager(layoutManager);
        restaurantRV.setAdapter(adapter);
        saveLayoutManager(adapter.isGridMode());
    }




    private void saveLayoutManager(boolean isGridLayout){
        sharedPreferences = getSharedPreferences(SharedPrefs,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(VIEW_MODE,isGridLayout);
        editor.apply();
    }

    private RecyclerView.LayoutManager getLayoutManager(boolean isGridLayout){
        return isGridLayout? new GridLayoutManager(this,2) : new LinearLayoutManager(this);
    }

    private boolean getSavedLayoutManager(){
        sharedPreferences = getSharedPreferences(SharedPrefs,MODE_PRIVATE);
        return sharedPreferences.getBoolean(VIEW_MODE,false);

    }
}
