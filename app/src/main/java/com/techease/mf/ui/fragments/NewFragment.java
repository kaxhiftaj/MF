package com.techease.mf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.mf.R;
import com.techease.mf.ui.activities.Profile;
import com.techease.mf.ui.adapters.NewAdapter;
import com.techease.mf.ui.models.NewModel;
import com.techease.mf.utils.AlertsUtils;
import com.techease.mf.utils.Configuration;
import com.techease.mf.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NewFragment extends Fragment {

    android.support.v7.app.AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String email, user_id;
    ArrayList<NewModel> new_model_list = new ArrayList<>();
    NewAdapter new_adapter;
    private boolean _hasLoadedOnce = false;
    Unbinder unbinder;

    @BindView(R.id.rv_new)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new, container, false);
        unbinder = ButterKnife.bind(this, v);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        email = sharedPreferences.getString("email", "");
        user_id = sharedPreferences.getString("user_id", "");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new_adapter = new NewAdapter(getActivity(), new_model_list);
        recyclerView.setAdapter(new_adapter);
        if (InternetUtils.isNetworkConnected(getActivity())) {
            apicall();
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();


        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        customActionBar();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new_model_list.clear();
        new_adapter.notifyDataSetChanged();
        apicall();
        if (alertDialog == null) {
            alertDialog = AlertsUtils.createProgressDialog(getActivity());
        }
        alertDialog.show();
        new_adapter.notifyDataSetChanged();

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.isVisible()) {
            if (menuVisible) {
                new_model_list.clear();
                new_adapter.notifyDataSetChanged();
                if (alertDialog == null)
                    alertDialog = AlertsUtils.createProgressDialog(getActivity());
                alertDialog.show();
                apicall();
            }
        }
    }

    private void apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://menfashion.techeasesol.com/restapi/collection"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("true")) {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArr = jsonObject.getJSONArray("collection");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject temp = jsonArr.getJSONObject(i);

                            NewModel model = new NewModel();
                            String id = temp.getString("id");
                            String name = temp.getString("name");
                            String image = temp.getString("image");
                            String like = temp.getString("likes");
                            String facebook = temp.getString("facebook");
                            String liked = temp.getString("liked");
                            model.setId(id);
                            model.setName(name);
                            model.setImage(image);
                            model.setFacebok(facebook);
                            model.setNoLikes(like);
                            model.setLiked(liked);
                            new_model_list.add(model);


                        }
                        new_adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                } else {

                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (alertDialog != null)
                    alertDialog.dismiss();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    public void customActionBar() {
        android.support.v7.app.ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.custom_main_actionbar, null);
        ImageView ivMF = mCustomView.findViewById(R.id.iv_mf);
        ImageButton profile = (ImageButton) mCustomView.findViewById(R.id.profile);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });

    }

}
