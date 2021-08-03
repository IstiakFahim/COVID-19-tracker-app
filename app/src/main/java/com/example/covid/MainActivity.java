package com.example.covid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal, mTotal, mActive, mTodayActive, mRecovered, mTodayRecovered, mDeaths, mTodayDeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types = {"cases", "deaths", "recovered", "active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mPiechart;
    private RecyclerView recyclerView;
    com.example.covid.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        countryCodePicker = findViewById(R.id.countrySelect);
        mTodayTotal = findViewById(R.id.todayTotalCase);
        mTotal = findViewById(R.id.totalCase);
        mActive = findViewById(R.id.totalActive);
        mTodayActive = findViewById(R.id.todayTotalActive);
        mRecovered = findViewById(R.id.totalRecovered);
        mTodayRecovered = findViewById(R.id.todayTotalRecoveres);
        mDeaths = findViewById(R.id.totalDeaths);
        mTodayDeaths = findViewById(R.id.todayTotalDeaths);
        mPiechart = findViewById(R.id.pieChart);
        spinner = findViewById(R.id.spinner);
        mfilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.countrylist);
        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });

        fetchdata();

    }

    private void fetchdata() {

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0;i<modelClassList.size();i++){
                    if (modelClassList.get(i).getCountry().equals(country)){

                        mActive.setText((modelClassList.get(i).getActive()));
                        mTodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mDeaths.setText((modelClassList.get(i).getDeaths()));
                        mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mRecovered.setText((modelClassList.get(i).getRecovered()));
                        mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mTotal.setText((modelClassList.get(i).getCases()));


                        int active, total, recovered, deaths;

                        active = Integer.parseInt(modelClassList.get(i).getActive());
                        total = Integer.parseInt(modelClassList.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths = Integer.parseInt(modelClassList.get(i).getDeaths());

                        updategraph(active, total, recovered, deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updategraph(int active, int total, int recovered, int deaths) {


        mPiechart.clearChart();
        mPiechart.addPieSlice(new PieModel("Total", total, Color.parseColor("#FFB701")));
        mPiechart.addPieSlice(new PieModel("Active", active, Color.parseColor("#38ACDD")));
        mPiechart.addPieSlice(new PieModel("Recovered", recovered, Color.parseColor("#FF4CAF50")));
        mPiechart.addPieSlice(new PieModel("Deaths", deaths, Color.parseColor("#f55c47")));

        mPiechart.startAnimation();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        String item = types[position];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}