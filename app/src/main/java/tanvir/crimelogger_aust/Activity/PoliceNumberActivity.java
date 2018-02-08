package tanvir.crimelogger_aust.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Collections;

import tanvir.crimelogger_aust.HelperClass.RecyclerAdapterToShowPolicePhoneNumber;
import tanvir.crimelogger_aust.R;

public class PoliceNumberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private MaterialSearchView searchView;

    private ArrayList<String> police_numberAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_number);

        recyclerView = findViewById(R.id.recyclerViewToShowPN);

        searchView=findViewById(R.id.search_view_in_PoilcePhoneNumber);


        Toolbar toolbar = findViewById(R.id.toolbarInPoilcePhoneNumber);
        setSupportActionBar(toolbar);


        police_numberAL = new ArrayList<String>();
        Resources res = getResources();
        Collections.addAll(police_numberAL, res.getStringArray(R.array.police_number));



        updateRecyclerView(police_numberAL);




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
                    //retrieveCrimeType(query);
                    ///setPieChart();
                }
                else
                    TastyToast.makeText(getApplicationContext(), "Please Enter Place Name", TastyToast.LENGTH_SHORT, TastyToast.WARNING);



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<String> police_numberAlCopy=new ArrayList<>();


                for (int i=0;i<police_numberAL.size();i++)
                {
                    String data = police_numberAL.get(i);

                    if (data.toLowerCase().contains(newText.toLowerCase()))
                    {
                        police_numberAlCopy.add(data);
                    }
                }

                updateRecyclerView(police_numberAlCopy);


                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {



            }

            @Override
            public void onSearchViewClosed() {

                updateRecyclerView(police_numberAL);

            }
        });

        return true;
    }

    public void updateRecyclerView(ArrayList<String> police_numberAL )
    {
        adapter = new RecyclerAdapterToShowPolicePhoneNumber(this, police_numberAL);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
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
        myIntent.putExtra("cameFromWhichActivity","PoliceNumberActivity");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }
}
