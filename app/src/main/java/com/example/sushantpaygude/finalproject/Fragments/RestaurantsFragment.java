package com.example.sushantpaygude.finalproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sushantpaygude.finalproject.Adapters.RestaurantRecyclerViewAdapter;
import com.example.sushantpaygude.finalproject.POJOs.Yelp.YelpRestaurantResponse.Business;
import com.example.sushantpaygude.finalproject.POJOs.Yelp.YelpRestaurantResponse.YelpResponse;
import com.example.sushantpaygude.finalproject.R;
import com.example.sushantpaygude.finalproject.Utils.Utilities;
import com.example.sushantpaygude.finalproject.Utils.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class RestaurantsFragment extends Fragment {

    private RequestQueue requestQueue;
    private RecyclerView restaurantsrecyclerView;
    private RestaurantRecyclerViewAdapter restaurantRecyclerViewAdapter;
    private ArrayList<Business> yelpResponseArrayList = new ArrayList<>();

    public RestaurantsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restaurantsrecyclerView = view.findViewById(R.id.restaurantRecyclerView);
        restaurantsrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(restaurantsrecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        restaurantsrecyclerView.addItemDecoration(dividerItemDecoration);


        restaurantRecyclerViewAdapter = new RestaurantRecyclerViewAdapter(yelpResponseArrayList, getActivity());

        restaurantsrecyclerView.setAdapter(restaurantRecyclerViewAdapter);
        getRestaurants();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void getRestaurants() {



        String url = String.format(Utilities.YELP_GET_RESTAURANTS_BY_LOCATION, "39.255613", "-76.710975");  //TODO Resolve Bug when Lat/Long has 00 in the end

        //String url = "https://api.yelp.com/v3/businesses/search?latitude=39.260700&longitude=-76.699453&radius=10000";
        Log.e("URL", ":" + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                YelpResponse yelpResponse = new Gson().fromJson(response.toString(), YelpResponse.class);

                for (Business business : yelpResponse.getBusinesses()) {

                    yelpResponseArrayList.add(business);
                }
                restaurantRecyclerViewAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e("Error", ":" + jsonError);
                }

            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Utilities.getYELPTokenParams(getContext());
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


}
