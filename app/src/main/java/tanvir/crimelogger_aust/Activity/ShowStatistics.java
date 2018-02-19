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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sdsmdg.tastytoast.TastyToast;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import tanvir.crimelogger_aust.MOdelClass.PieChartDataMC;
import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

public class ShowStatistics extends AppCompatActivity {

    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<String> crimeType;
    private ArrayList<String> seperatedCrimeType;

    RelativeLayout emptyRelativeLayout;

    TextView searchedMonthTV;


    private PieChart pieChart;

    private ArrayList<PieChartDataMC> pieCharts;

    private MaterialSearchView searchView;
    private RelativeLayout relativeLayout;
    ArrayList<String> crimeDate;
    String searchedMonth = "";
    String searchedCrimePlace = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_statistics);

        crimeType = new ArrayList<>();
        crimeDate = new ArrayList<>();
        seperatedCrimeType = new ArrayList<>();
        ///autoCompleteTextView=findViewById(R.id.placeETSA);
        pieCharts = new ArrayList<>();

        pieChart = findViewById(R.id.pieChart);

        pieChart.setVisibility(View.GONE);

        searchView = findViewById(R.id.search_view_in_pie_chart);

        emptyRelativeLayout=findViewById(R.id.emptyRelativeLayoutForShowStatistics);
        searchedMonthTV = findViewById(R.id.sortedMonth);


        Toolbar toolbar = findViewById(R.id.toolbarInShowPieChart);
        setSupportActionBar(toolbar);

        searchView.setSuggestions(getResources().getStringArray(R.array.place_arrays));

        relativeLayout = findViewById(R.id.searchRelativeLayout);
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


        TextView textView = findViewById(R.id.PlaceTV);
        textView.setText(query);

        seperatedCrimeType.clear();
        crimeType.clear();
        pieCharts.clear();
        String place = query.toLowerCase();

        for (int i = 0; i < userPostMCS.size(); i++) {


            String crimePlace = userPostMCS.get(i).getCrimePlace().toLowerCase();


            if (crimePlace.contains(place) || place.contains(crimePlace)) {

                crimeType.add(userPostMCS.get(i).getCrimeType());
                crimeDate.add(userPostMCS.get(i).getCrimeDate());
            }


        }
        if (crimeType.size() > 0) {

            emptyRelativeLayout.setVisibility(View.GONE);

            pieChart.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            seperateCrimeType();
        } else
        {
            emptyRelativeLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            TastyToast.makeText(getApplicationContext(), "Sorry , no data found  !", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }



    }

    public void seperateCrimeType() {
        String currentCrimeType;
        int initialPosition;
        int commaPosition;

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
        /*for (int i = 0; i < seperatedCrimeType.size(); i++) {
            Toast.makeText(this, "crimeType :  "+Integer.toString(i)+" "+seperatedCrimeType.get(i), Toast.LENGTH_SHORT).show();
        }*/
        countCrimeType();
    }


    public void countCrimeType() {
        String crimeType;
        int length;
        int count;
        pieCharts.clear();

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

            if (length == 1) {
                if (seperatedCrimeType.get(0).contains(crimeType)) {
                    count = count + 1;

                    ////Toast.makeText(this, "Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();
                    seperatedCrimeType.remove(0);

                }
            }

            ///Toast.makeText(this, "total Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();

            PieChartDataMC pieChartDataMC = new PieChartDataMC(crimeType, count);
            pieCharts.add(pieChartDataMC);

            if (seperatedCrimeType.size() == 0)
                break;


        }


    }


    public void setPieChart() {


        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#455A64"));
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (int i = 0; i < pieCharts.size(); i++) {
            PieChartDataMC pieChartDataMC = pieCharts.get(i);

            yValues.add(new PieEntry(pieChartDataMC.getCrimeNumber(), pieChartDataMC.getCrimeType()));
        }

        PieDataSet dataSet = new PieDataSet(yValues, "CrimeType");

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
        } else
            startMainActivity();


    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "ShowStatisticsActivity");
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

                searchedCrimePlace=query;
                searchedMonthTV.setText("Select Month");

                if (query.length() > 0) {
                    retrieveCrimeType(query);
                    setPieChart();
                } else
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

    public void initializeateAndMonthPicker(View view) {



        YearMonthPickerDialog pickerDialog = new YearMonthPickerDialog(this, new YearMonthPickerDialog.OnDateSetListener() {
            @Override
            public void onYearMonthSet(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
                searchedMonth = dateFormat.format(calendar.getTime());

                searchedMonthTV.setText(dateFormat.format(calendar.getTime()));

                //separateCrimeTypeByMonth();
                retrieveCrimeTypeForSorting();
            }
        });

        pickerDialog.show();
    }

   /* public void separateCrimeTypeByMonth()
    {
        int length = searchedMonth.length();
        String searchMonth = searchedMonth.substring(0,length-5);
        String searchYear = searchedMonth.substring(length-5);

        for (int i=0;i<crimeDate.size();i++)
        {
            String date = crimeDate.get(i);

            if (!date.contains("Unknown"))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Date parse = null;
                try {
                    parse = sdf.parse(crimeDate.get(i));
                } catch (ParseException e) {
                    Toast.makeText(this, "parse exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);

                int month = calendar.get(Calendar.MONTH)+1;
                int year = calendar.get(Calendar.YEAR);

                String crimeMonth = getMonthName(month);
                String crimeYear=Integer.toString(year);


                ///Toast.makeText(this, "month : "+Integer.toString(month), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "month by name : "+getMonthName(month), Toast.LENGTH_SHORT).show();
                ///Toast.makeText(this, "year : "+Integer.toString(year), Toast.LENGTH_SHORT).show();
            }

            /*int l = crimeDate.get(i).length();
            String year = crimeDate.get(i).substring(l-5);
            String month = crimeDate.get(i).substring(l-5);*/


    ///}

    ///Toast.makeText(this, "searchMonth : "+searchMonth, Toast.LENGTH_SHORT).show();
    //// Toast.makeText(this, "sarch year : "+searchYear, Toast.LENGTH_SHORT).show();
    ///}

    public String getMonthName(int monthNumber) {

        if (monthNumber == 1) {
            return "January";

        } else if (monthNumber == 2) {
            return "February";

        } else if (monthNumber == 3) {
            return "March";
        } else if (monthNumber == 4) {
            return "April";
        } else if (monthNumber == 5) {
            return "May";
        } else if (monthNumber == 6) {
            return "June";
        } else if (monthNumber == 7) {
            return "July";
        } else if (monthNumber == 8) {
            return "August";
        } else if (monthNumber == 9) {
            return "September";
        } else if (monthNumber == 10) {
            return "October";
        } else if (monthNumber == 11) {
            return "November";
        } else if (monthNumber == 12) {
            return "December";
        }

        return "";

    }

    public void retrieveCrimeTypeForSorting() {




        seperatedCrimeType.clear();
        crimeType.clear();
        pieCharts.clear();


        int length = searchedMonth.length();
        String searchMonth = searchedMonth.substring(0, length - 5).trim();
        String searchYear = searchedMonth.substring(length - 5).trim();


        for (int i = 0; i < userPostMCS.size(); i++) {


            String crimePlace = userPostMCS.get(i).getCrimePlace().toLowerCase();
            String crimeDate = userPostMCS.get(i).getCrimeDate();


            if (!crimeDate.contains("Unknown")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Date parse = null;
                try {
                    parse = sdf.parse(crimeDate);
                } catch (ParseException e) {
                    Toast.makeText(this, "parse exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);

                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);

                String crimeMonth = getMonthName(month).trim();
                String crimeYear = Integer.toString(year).trim();
                searchedCrimePlace=searchedCrimePlace.toLowerCase();

                /*if (crimePlace.contains(searchedCrimePlace) || searchedCrimePlace.contains(crimePlace)) {

                    Toast.makeText(this, "Search month : " + searchMonth + "\nCrimeMonth : " + crimeMonth + "\nSearchYear : " + crimeYear + "\nCrimeYear : " + crimeYear, Toast.LENGTH_LONG).show();

                }*/


               /// Toast.makeText(this, "Search month : " + searchMonth + "\nCrimeMonth : " + crimeMonth + "\nSearchYear : " + crimeYear + "\nCrimeYear : " + crimeYear, Toast.LENGTH_LONG).show();


                if ((crimePlace.contains(searchedCrimePlace) || searchedCrimePlace.contains(crimePlace)) && searchMonth.contains(crimeMonth) && searchYear.contains(crimeYear) ) {

                    crimeType.add(userPostMCS.get(i).getCrimeType());

                   //// Toast.makeText(this, "Search month : " + searchMonth + "\nCrimeMonth : " + crimeMonth + "\nSearchYear : " + crimeYear + "\nCrimeYear : " + crimeYear, Toast.LENGTH_LONG).show();

                }


            }


        }
        if (crimeType.size() > 0) {

            ///pieChart.clear();

            relativeLayout.setVisibility(View.GONE);

            ///Toast.makeText(this, "crimeTypeSize : "+Integer.toString(crimeType.size()), Toast.LENGTH_SHORT).show();
            pieChart.setVisibility(View.VISIBLE);
            emptyRelativeLayout.setVisibility(View.GONE);

            seperateCrimeType();
            setPieChart();
        } else
        {
            emptyRelativeLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            TastyToast.makeText(getApplicationContext(), "Sorry , no data found  !", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }



    }


}
