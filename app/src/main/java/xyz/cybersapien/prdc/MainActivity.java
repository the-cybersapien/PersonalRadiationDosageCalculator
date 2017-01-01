package xyz.cybersapien.prdc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    public static String LOCATIONFRAGMENT = "LOCATIONFRAGMENT";

    private Double totalRads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the Content view and the Toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the total Radiations to zero
        totalRads = 0d;

        // Check that the activity contains the fragment Container
        // and that the activity isn't being restored from a previous state
        // else there might be overlapping fragments
        if (findViewById(R.id.fragment_container) != null && savedInstanceState == null) {

            //Create a new Fragment to be placed
            LocationFragment firstFragment = new LocationFragment();

            // Add the fragment to the container
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, firstFragment, LOCATIONFRAGMENT)
                    .commit();
        }

        showInternetErrorDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Public method to let other activities to update the total radiation tally in the Calculator.
     * @param rads the amount of radiation to be added
     */
    public void addRads(Double rads) {
        this.totalRads += rads;
        updateRads();
    }

    /**
     * Private method to update the total radiation in the UI
     */
    private void updateRads() {
        TextView total = (TextView) findViewById(R.id.totalRadsTextView);
        total.setText(String.format("%.1f", totalRads));
    }

    public void showInternetErrorDialog(){
        AlertDialog.Builder netAlertBuilder = new AlertDialog.Builder(this);

        netAlertBuilder.setMessage("Error! Please Check Internet Connection!");
        netAlertBuilder.setTitle("No! Internet");
        netAlertBuilder.setNegativeButton("Exit Application", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        netAlertBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        netAlertBuilder.create().show();
    }
}
