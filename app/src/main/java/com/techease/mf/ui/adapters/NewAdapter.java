package com.techease.mf.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.techease.mf.R;
import com.techease.mf.ui.fragments.ProductsFragment;
import com.techease.mf.ui.models.NewModel;
import com.techease.mf.utils.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaxhiftaj on 2/27/18.
 */

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.MyViewHolder> {

    ArrayList<NewModel> newArrayList;
    Context context;
   String collection_id;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String user_id ;

    public NewAdapter(Context context, ArrayList<NewModel> newModels) {
        this.context=context;
        this.newArrayList = newModels ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_new, parent, false);

        sharedPreferences = context.getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        user_id = sharedPreferences.getString("user_id", "");

        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NewModel model= newArrayList.get(position);

        holder.noLikes.setText(model.getNoLikes()+ " Likes");
        Glide.with(context).load(model.getImage()).into(holder.item_image);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                collection_id = model.getId();
                if (!user_id.equals("")) {
                    apicall();
                }else {
                    Toast.makeText(context, "Please Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String collection_id = model.getId();

                Intent i = new Intent(context ,ProductsFragment.class);
                i.putExtra("collection_id", collection_id);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {

        return newArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView noLikes;
        ImageView item_image ;
        ImageButton share, like;


        public MyViewHolder(View itemView) {
            super(itemView);
            noLikes =(TextView)itemView.findViewById(R.id.noLikes);
            share=(ImageButton) itemView.findViewById(R.id.share);
            like = (ImageButton) itemView.findViewById(R.id.like);
            item_image = (ImageView) itemView.findViewById(R.id.item_image);

        }


    }




    private void apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://menfashion.techeasesol.com/restapi/userliked"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("true")) {

                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "user_id");
                params.put("collection_id", collection_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}

