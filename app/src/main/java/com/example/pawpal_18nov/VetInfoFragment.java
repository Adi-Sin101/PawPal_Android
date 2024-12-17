package com.example.pawpal_18nov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VetInfoFragment extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<ContactModel> arrContacts = new ArrayList<>();
    private RequestQueue requestQueue;
    private TextView textView;
    private RecyclerContactAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vet_info, container, false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Doctors");


        requestQueue = Volley.newRequestQueue(requireContext());


        RecyclerView recyclerView = view.findViewById(R.id.recyclerContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize TextView
       // textView = view.findViewById(R.id.textView);


        adapter = new RecyclerContactAdapter(requireContext(), arrContacts);
        recyclerView.setAdapter(adapter);


        jsonParse();

        return view;
    }

    private void jsonParse() {
        String url = "https://api.myjson.online/v1/records/1f20d2a6-80a1-443a-8347-d86e70819c5f";  // Change this URL to your actual JSON URL

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the JSON array named "data"
                            JSONArray jsonArray = response.getJSONArray("data");


                            arrContacts.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("name");
                                String phone = jsonObject.getString("phone");
                                String gender = jsonObject.getString("gender");

                                arrContacts.add(new ContactModel(name, phone, gender));
                            }


                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {

                            textView.setText("Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        textView.setText("Error: " + error.getMessage());
                    }
                });


        requestQueue.add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }
}
