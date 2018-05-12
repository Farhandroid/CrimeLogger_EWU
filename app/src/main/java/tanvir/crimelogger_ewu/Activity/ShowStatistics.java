package tanvir.crimelogger_ewu.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
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

import ru.whalemare.sheetmenu.SheetMenu;
import tanvir.crimelogger_ewu.MOdelClass.PieChartDataMC;
import tanvir.crimelogger_ewu.MOdelClass.UserPostMC;
import tanvir.crimelogger_ewu.R;

public class ShowStatistics extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private ArrayList<UserPostMC> allUserPostMCSData;
    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<UserPostMC> copyUserPostMCForDateAndTimeSort;
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
    private int timeFromInt, timeToInt;
    private String dateFrom, dateTo;
    Context context;
    TextView timeRangeTV, dateRangeTV;
    String timeCopy;
    private boolean isItsortByDateAndTime=false,isAlreadyInSortDate=false;;

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
        emptyRelativeLayout = findViewById(R.id.emptyRelativeLayoutForShowStatistics);
        ///searchedMonthTV = findViewById(R.id.sortedMonth);
        Toolbar toolbar = findViewById(R.id.toolbarInShowPieChart);
        setSupportActionBar(toolbar);
        searchView.setSuggestions(getResources().getStringArray(R.array.place_arrays));
        relativeLayout = findViewById(R.id.searchRelativeLayout);
        relativeLayout.setVisibility(View.VISIBLE);
        userPostMCS = new ArrayList<>();
        context = ShowStatistics.this;
        copyUserPostMCForDateAndTimeSort=new ArrayList<>();
        timeRangeTV = findViewById(R.id.timeRangeTV);
//        dateRangeTV = findViewById(R.id.dateRangeTV);
        retrivesharedpreference();
    }

    public void retrivesharedpreference() {
        SharedPreferences prefs = getSharedPreferences("PostData", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("PostData", null);

        if (jsonText != null) {
            UserPostMC[] text = gson.fromJson(jsonText, UserPostMC[].class);
            allUserPostMCSData = new ArrayList<>(Arrays.asList(text));
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedCrimePlace = query;
                ///searchedMonthTV.setText("Select Month");

                if (query.length() > 0) {
                    retrieveCrimeType(query);
                    timeRangeTV.setText("");
//                    setPieChart();
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

    public void retrieveCrimeType(String query) {
        TextView textView = findViewById(R.id.PlaceTV);
        textView.setText(query);
        crimeType.clear();
        pieCharts.clear();
        String place = query.toLowerCase();
        for (int i = 0; i < allUserPostMCSData.size(); i++) {
            String crimePlace = allUserPostMCSData.get(i).getCrimePlace().toLowerCase();
            if (crimePlace.contains(place) || place.contains(crimePlace)) {
                userPostMCS.add(allUserPostMCSData.get(i));
                crimeType.add(allUserPostMCSData.get(i).getCrimeType());
                crimeDate.add(allUserPostMCSData.get(i).getCrimeDate());
            }
        }
        setEmptyRelativeLayoutVisibility();
    }

    public void setEmptyRelativeLayoutVisibility() {
        Log.d("setEmptyRelativeLayout", "setEmptyRelativeLayoutVisibility");
        seperatedCrimeType.clear();
        Log.d("setEmptyRLSize",Integer.toString(crimeType.size()));
        if (crimeType.size() > 0) {
            emptyRelativeLayout.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            pieCharts.clear();
            if (isItsortByDateAndTime && isAlreadyInSortDate==false)
            {
                isAlreadyInSortDate=false;
                showDateRange();
            }

            else
                seperateCrimeType();
        } else {
            emptyRelativeLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            TastyToast.makeText(getApplicationContext(), "Sorry , no data found  !", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void seperateCrimeType() {
        String currentCrimeType;
        int initialPosition;
        Log.d("seperateCrimeTypeSize",Integer.toString(crimeType.size()));
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
       /* for (int i = 0; i < seperatedCrimeType.size(); i++) {
            Toast.makeText(this, "crimeType :  "+Integer.toString(i)+" "+seperatedCrimeType.get(i), Toast.LENGTH_SHORT).show();
        }*/
        countCrimeType();
    }

    public void countCrimeType() {
        Log.d("countCrimeType", "countCrimeType");
        String crimeType;
        int length;
        int count;
        pieCharts.clear();
        Log.d("seperatedCrimeTypeSize",Integer.toString(seperatedCrimeType.size()));

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
            if (seperatedCrimeType.size() == 0) {
                setPieChart();
                break;
            }
        }

    }

    public void setPieChart() {

        Log.d("pieChartsSize", Integer.toString(pieCharts.size()));
        Log.d("crimeTypeSize", Integer.toString(crimeType.size()));
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
        } else startMainActivity();

    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "ShowStatisticsActivity");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }


    public void showDateRange() {
        isAlreadyInSortDate=true;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) ShowStatistics.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.setMaxDate(now);
        dpd.show(ShowStatistics.this.getFragmentManager(), "Datepickerdialog");
    }

    public void showTimeRange() {
        isAlreadyInSortDate=false;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) ShowStatistics.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.show(ShowStatistics.this.getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " To  " + dayOfMonthEnd + "/" + (monthOfYearEnd + 1) + "/" + yearEnd;

        if (isItsortByDateAndTime)
        {
            Log.d("timeCopyInDate",timeCopy);
            Log.d("sortByDateAndTimeDate","isItsortByDateAndTime");
            timeRangeTV.setText("");
            timeRangeTV.setText(timeCopy+" and \n"+date);
        }
        else
            timeRangeTV.setText(date);

        dateFrom = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTo = dayOfMonthEnd + "/" + (monthOfYearEnd + 1) + "/" + yearEnd;
        if (isItsortByDateAndTime)
        {
            sortByDate(copyUserPostMCForDateAndTimeSort);
            isItsortByDateAndTime=false;
        }
        else
          sortByDate();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String timeFrom, timeTo;
        String hourOfDayFormat;

        if (hourOfDay == 0) {
            hourOfDay += 12;
            hourOfDayFormat = "AM";
        } else if (hourOfDay == 12) {
            hourOfDayFormat = "PM";

        } else if (hourOfDay > 12) {
            hourOfDay -= 12;
            hourOfDayFormat = "PM";

        } else {
            hourOfDayFormat = "AM";
        }

        String hourOfDayEndFormat;
        if (hourOfDayEnd == 0) {
            hourOfDayEnd += 12;
            hourOfDayEndFormat = "AM";
        } else if (hourOfDayEnd == 12) {
            hourOfDayEndFormat = "PM";
        } else if (hourOfDayEnd > 12) {
            hourOfDayEnd -= 12;
            hourOfDayEndFormat = "PM";
        } else {
            hourOfDayEndFormat = "AM";
        }
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;
        String time = hourString + ":" + minuteString + ":" + hourOfDayFormat + " To " + hourStringEnd + ":" + minuteStringEnd + ":" + hourOfDayEndFormat;
        timeCopy=time;
        Log.d("timeCopyInTime",timeCopy);
        timeRangeTV.setText(time);
        timeFrom = hourString + ":" + minuteString + " " + hourOfDayFormat;
        timeTo = hourStringEnd + ":" + minuteStringEnd + " " + hourOfDayEndFormat;
        timeFromInt = getTimeInInteger(timeFrom);
        timeToInt = getTimeInInteger(timeTo);

        sortByTime();
    }

    public int getTimeInInteger(String time) {
        if (!time.contains("Unkn"))
        {
            String timeCopy = time;
            time = time.substring(0, time.length() - 2);
            time = time.replace(":", "");
            if (timeCopy.contains("PM")) {
                String timeSub = time.substring(0, 2);
                timeSub = timeSub.trim();
                int i1 = Integer.parseInt(timeSub);
                i1 += 12;
                time = Integer.toString(i1) + time.substring(2);

            }
            time = time.trim();
            time = time.replaceAll(" ", "");
            int timeInt = Integer.parseInt(time);
            return timeInt;
        }
        else
            return 0;

    }

    public void sortByDate() {
        ///Toast.makeText(activity, "SortByDate", Toast.LENGTH_SHORT).show();
        crimeType.clear();
        for (int i = 0; i < userPostMCS.size(); i++) {
            String date = userPostMCS.get(i).getCrimeDate();

            if (!date.contains("Unkn"))
            {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


                try {
                    Date dateFromDate = format.parse(dateFrom);
                    Date dateToDate = format.parse(dateTo);
                    Date date2 = format.parse(date);

                    if (date2.compareTo(dateFromDate) > 0 && date2.compareTo(dateToDate) < 0) {
                        crimeType.add(userPostMCS.get(i).getCrimeType());
                        //Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                    } else if (date2.compareTo(dateFromDate) < 0 && date2.compareTo(dateToDate) > 0) {
                        crimeType.add(userPostMCS.get(i).getCrimeType());
                        //Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();

                    } else if (date2.compareTo(dateFromDate) == 0 || date2.compareTo(dateToDate) == 0) {
                        crimeType.add(userPostMCS.get(i).getCrimeType());
                        ///Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("sortByDateExcption", e.toString());
                    ///Toast.makeText(context, "ParseException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
            }

        setEmptyRelativeLayoutVisibility();
    }

    public void sortByDate(ArrayList<UserPostMC> userPostMCS ) {
        ///Toast.makeText(activity, "SortByDate", Toast.LENGTH_SHORT).show();
        crimeType.clear();
        for (int i = 0; i < userPostMCS.size(); i++) {
            String date = userPostMCS.get(i).getCrimeDate();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


            try {
                Date dateFromDate = format.parse(dateFrom);
                Date dateToDate = format.parse(dateTo);
                Date date2 = format.parse(date);

                if (date2.compareTo(dateFromDate) > 0 && date2.compareTo(dateToDate) < 0) {
                    crimeType.add(userPostMCS.get(i).getCrimeType());
                    //Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                } else if (date2.compareTo(dateFromDate) < 0 && date2.compareTo(dateToDate) > 0) {
                    crimeType.add(userPostMCS.get(i).getCrimeType());
                    //Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();

                } else if (date2.compareTo(dateFromDate) == 0 || date2.compareTo(dateToDate) == 0) {
                    crimeType.add(userPostMCS.get(i).getCrimeType());
                    ///Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("sortByDateExcption", e.toString());
                Toast.makeText(context, "ParseException : " + e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
        setEmptyRelativeLayoutVisibility();
    }

    public void sortByTime() {

        boolean isItPMtoAM = false;
        boolean isTimeFromBig = false;
        Log.d("timeFromInt", "From : " + Integer.toString(timeFromInt) + "to : " + Integer.toString(timeToInt));
        if ((timeFromInt >= 1200 && timeFromInt <= 2459) && (timeToInt >= 100 && timeToInt <= 1159)) {
            isItPMtoAM = true;
            Log.d("isItPMtoAM", "isItPMtoAM");
        }
        if (timeFromInt > timeToInt) {
            Log.d("isTimeFromBig", "isTimeFromBig");
            isTimeFromBig = true;

        }
        String time;
        int timeInt;
        ArrayList<UserPostMC> copyUserPostMCS=new ArrayList<>();

        crimeType.clear();
        for (int i = 0; i < userPostMCS.size(); i++) {
            time = userPostMCS.get(i).getCrimeTime();
            timeInt = getTimeInInteger(time);
            if (isItPMtoAM) {
                Log.d("Enter", "AM to PM");
                if ((timeInt >= 100 && timeInt <= timeFromInt) || (timeInt >= timeToInt && timeInt <= 2459)) {
                    crimeType.add(userPostMCS.get(i).getCrimeType());
                    copyUserPostMCS.add(userPostMCS.get(i));
                    Log.d("namePlaceAMPm  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                    ///Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                }

            } else if (isTimeFromBig) {
                if ((timeInt >= 100 && timeInt <= timeToInt) || (timeInt >= timeFromInt && timeInt <= 2459)) {
                    crimeType.add(userPostMCS.get(i).getCrimeType());
                    copyUserPostMCS.add(userPostMCS.get(i));
                    Log.d("namePlaceAMPm  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                    ///Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();

                }

            } else if (timeInt == timeFromInt || timeInt == timeToInt) {
                crimeType.add(userPostMCS.get(i).getCrimeType());
                copyUserPostMCS.add(userPostMCS.get(i));
                Log.d("namePlaceNormal  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
               /// Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
            } else if ((timeInt > timeFromInt && timeInt < timeToInt)) {
                crimeType.add(userPostMCS.get(i).getCrimeType());
                copyUserPostMCS.add(userPostMCS.get(i));
                Log.d("namePlaceNormal  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                ///Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
            }
            ///Toast.makeText(context,"time : "+time, Toast.LENGTH_SHORT).show();
        }
        copyUserPostMCForDateAndTimeSort=copyUserPostMCS;
        setEmptyRelativeLayoutVisibility();
    }


    public void showCrimeSortSheetMenu(View view) {
        SheetMenu.with(this).setTitle("Select Option").setMenu(R.menu.crime_type_sort_menu).setAutoCancel(true).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sortByTime) {
                    isItsortByDateAndTime=false;
                        showTimeRange();
                } else if (item.getItemId() == R.id.sortByDate) {
                    isItsortByDateAndTime=false;
                        showDateRange();
                } else if (item.getItemId() == R.id.sortByDateAndTime) {
                    isItsortByDateAndTime=true;
                    showTimeRange();
                    ///setLogginnInformation();
                    ///TastyToast.makeText(getApplicationContext(), "Logged out successfull", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
                return false;
            }
        }).show();
    }
}
