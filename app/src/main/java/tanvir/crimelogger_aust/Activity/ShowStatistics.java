package tanvir.crimelogger_aust.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Arrays;

import tanvir.crimelogger_aust.MOdelClass.PieChartDataMC;
import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

public class ShowStatistics extends AppCompatActivity {

    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<String> crimeType;
    private ArrayList<String> seperatedCrimeType;


    private PieChart pieChart;

    private ArrayList<PieChartDataMC> pieCharts;

    private MaterialSearchView searchView;
    private RelativeLayout relativeLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_statistics);

        crimeType = new ArrayList<>();
        seperatedCrimeType = new ArrayList<>();
        ///autoCompleteTextView=findViewById(R.id.placeETSA);
        pieCharts=new ArrayList<>();

        pieChart = findViewById(R.id.pieChart);

        pieChart.setVisibility(View.GONE);

        searchView=findViewById(R.id.search_view_in_pie_chart);

        Toolbar toolbar = findViewById(R.id.toolbarInShowPieChart);
        setSupportActionBar(toolbar);

        searchView.setSuggestions(getResources().getStringArray(R.array.place_arrays));

        relativeLayout=findViewById(R.id.searchRelativeLayout);
        relativeLayout.setVisibility(View.VISIBLE);


        retrivesharedpreference();
    }

    public void retrivesharedpreference() {


        SharedPreferences prefs = getSharedPreferences("PostData", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("PostData", null);

        if (jsonText != null) {
            UserPostMC[] text = gson.fromJson(jsonText, UserPostMC[].class);
            userPostMCS = new ArrayList<>(Arrays.asList(text));


        }

    }

    public void retrieveCrimeType(String query) {



        seperatedCrimeType.clear();
        crimeType.clear();
        pieCharts.clear();
        String place=query.toLowerCase();

        for (int i = 0; i < userPostMCS.size(); i++) {



            String crimePlace = userPostMCS.get(i).getCrimePlace().toLowerCase();


            if (crimePlace.contains(place))
            {

                crimeType.add(userPostMCS.get(i).getCrimeType());
            }



        }
        if (crimeType.size()>0)
        {


            pieChart.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            seperateCrimeType();
        }
        else
            TastyToast.makeText(getApplicationContext(), "Sorry , no data found  !", TastyToast.LENGTH_LONG, TastyToast.ERROR);





    }

    public void seperateCrimeType() {
        String currentCrimeType;
        int initialPosition ;
        int commaPosition ;

        for (int i = 0; i < crimeType.size(); i++) {
            currentCrimeType = crimeType.get(i);
            initialPosition = 0;

            for (int j = 0; j < currentCrimeType.length(); j++) {
                commaPosition = currentCrimeType.indexOf(',', initialPosition);


                if (commaPosition != -1) {

                    seperatedCrimeType.add(currentCrimeType.substring(initialPosition, commaPosition));
                    initialPosition = commaPosition + 1;
                }

                if (commaPosition == -1 && initialPosition < (currentCrimeType.length() - 1)) {

                    seperatedCrimeType.add(currentCrimeType.substring(initialPosition));
                    break;
                }


            }
        }

        showSeparetedCrimeType();
    }

    public void showSeparetedCrimeType() {
        for (int i = 0; i < seperatedCrimeType.size(); i++) {
            ///Toast.makeText(this, "crimeType :  "+Integer.toString(i)+" "+seperatedCrimeType.get(i), Toast.LENGTH_SHORT).show();
        }
        countCrimeType();
    }


    public void countCrimeType() {
        String crimeType ;
        int length ;
        int count ;

        for (; ; ) {
            crimeType = seperatedCrimeType.get(0);
            seperatedCrimeType.remove(0);
            length = seperatedCrimeType.size();
            count = 1;

            for (int j = 0; j < length; j++) {


                    if (seperatedCrimeType.get(j).contains(crimeType)) {
                        count = count + 1;

                        ////Toast.makeText(this, "Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();
                        seperatedCrimeType.remove(j);
                        length = seperatedCrimeType.size();

                    }
            }

            if (length==1)
            {
                if (seperatedCrimeType.get(0).contains(crimeType)) {
                    count = count + 1;

                    ////Toast.makeText(this, "Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();
                    seperatedCrimeType.remove(0);

                }
            }

            ///Toast.makeText(this, "total Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();

            PieChartDataMC pieChartDataMC = new PieChartDataMC(crimeType,count);
            pieCharts.add(pieChartDataMC);

            if (seperatedCrimeType.size() == 0)
                break;


        }


    }



    public void setPieChart()
    {


        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#455A64"));
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (int i=0;i<pieCharts.size();i++)
        {
            PieChartDataMC pieChartDataMC=pieCharts.get(i);

            yValues.add(new PieEntry(pieChartDataMC.getCrimeNumber(),pieChartDataMC.getCrimeType()));
        }

        PieDataSet dataSet = new PieDataSet(yValues,"CrimeType");

        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(3f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);


        pieChart.animateY(1000);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    public void onBackPressed() {
        super.onBackPressed();

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        else
            startMainActivity();


    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity","ShowStatisticsActivity");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length()>0)
                {
                    retrieveCrimeType(query);
                    setPieChart();
                }
                else
                    TastyToast.makeText(getApplicationContext(), "Please Enter Place Name", TastyToast.LENGTH_SHORT, TastyToast.WARNING);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {


            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        return true;
    }

}
