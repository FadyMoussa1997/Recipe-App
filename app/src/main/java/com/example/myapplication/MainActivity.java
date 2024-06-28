package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);

        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(mealList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mealAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    performApiCall(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter search term!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performApiCall(String searchTerm) {
        String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" +searchTerm;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mealList.clear(); // this is just to clear the previous list
                            JSONArray mealsArray = response.getJSONArray("meals");
                            for (int i = 0; i < mealsArray.length(); i++) {
                                JSONObject mealObject = mealsArray.getJSONObject(i);
                                String mealName = mealObject.getString("strMeal");
                                String category = mealObject.getString("strCategory");
                                String instructions = mealObject.getString("strInstructions");
                                String mealTags = mealObject.getString("strTags");

                                String mealImageURL = mealObject.getString("strMealThumb");

                                Meal meal = new Meal(mealName, category, instructions, mealImageURL, mealTags);
                                mealList.add(meal);
                            }
                            mealAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this, "Error while fetching the data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private static class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

        private List<Meal> mealList;


        public MealAdapter(List<Meal> mealList) {
            this.mealList = mealList;
        }

        @NonNull
        @Override
        public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
            return new MealViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
            Meal meal = mealList.get(position);
            holder.bind(meal);
        }


        @Override
        public int getItemCount() {
            return mealList.size();
        }

        public static class MealViewHolder extends RecyclerView.ViewHolder {

            private ImageView mealImageView;
            private TextView categoryTextView;
            private TextView tagsTextView;
            private Button mealNameButton;

            public MealViewHolder(@NonNull View itemView) {
                super(itemView);

                mealImageView = itemView.findViewById(R.id.mealImageView);
                categoryTextView = itemView.findViewById(R.id.categoryTextView);
                tagsTextView = itemView.findViewById(R.id.tagsTextView);
                mealNameButton = itemView.findViewById(R.id.mealNameButton);
            }

            public void bind(Meal meal) {

                Picasso.get().load(meal.getMealImageURL()).into(mealImageView);

                categoryTextView.setText("Category: " + meal.getCategory());
                tagsTextView.setText("Tags: " + meal.getMealTags());
                mealNameButton.setText(meal.getMealName());
            }
        }

    }

    private static class Meal {
        private String mealName;
        private String category;
        private String instructions;
        private String mealImageURL;

        private String mealTags;

        public Meal(String mealName, String category, String instructions, String mealImageURL, String mealTags) {
            this.mealName = mealName;
            this.category = category;
            this.instructions = instructions;
            this.mealImageURL = mealImageURL;
            this.mealTags = mealTags;
        }

        public String getMealName() {
            return mealName;
        }

        public String getCategory() {
            return category;
        }

        public String getInstructions() {
            return instructions;
        }

        public String getMealImageURL() {
            return mealImageURL;
        }

        public String getMealTags() {
            return mealTags;
        }
    }
}