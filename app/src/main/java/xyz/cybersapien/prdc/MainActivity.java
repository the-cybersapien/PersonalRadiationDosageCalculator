package xyz.cybersapien.prdc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import xyz.cybersapien.prdc.helpers.HelperUtils;

/**
 * The App uses the values location based exposure from an Article by
 * the International Journal of Radiation research, present here:
 * http://www.ijrr.com/article-1-738-en.pdf
 *
 * Medical and LifeStyle based exposure values are from the following websites and articles:
 * https://www.nrc.gov/about-nrc/radiation/around-us/calculator.html
 * https://www.epa.gov/radiation/calculate-your-radiation-dose#self
 *
 * http://www.nrc.gov/reading-rm/basic-ref/students/for-educators/average-dose-worksheet.pdf
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    public static String LOCATION_FRAGMENT = "LOCATION_FRAGMENT";
    public static final String LIFESTYLE_FRAGMENT = "LIFESTYLE_FRAGMENT";

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
        updateRads();

        // Check that the activity contains the fragment Container
        // and that the activity isn't being restored from a previous state
        // else there might be overlapping fragments
        if (findViewById(R.id.fragment_container) != null && savedInstanceState == null) {

            //Create a new Fragment to be placed
            LocationFragment firstFragment = new LocationFragment();

            // Add the fragment to the container
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, firstFragment, LOCATION_FRAGMENT)
                    .commit();
        }
        if (!HelperUtils.isInternetConnected(this)){
            showInternetErrorDialog();
        }
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

        if (id == R.id.action_convertor) {
            startActivity(new Intent(MainActivity.this, ConvertorActivity.class));
            return true;
        } else if (id == R.id.action_settings){
            LifeStyleFragment lf = new LifeStyleFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, lf, LIFESTYLE_FRAGMENT)
                    .addToBackStack(LIFESTYLE_FRAGMENT)
                    .commit();
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
        total.setText(String.format("%.2f", totalRads));
    }

    public void showInternetErrorDialog(){

        AlertDialog.Builder netAlertBuilder = new AlertDialog.Builder(this);

        netAlertBuilder
                .setMessage("Error! Please Check Internet Connection!")
                .setTitle("No! Internet")
                .setNegativeButton("Exit Application", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create();
        netAlertBuilder.show();
    }
}
