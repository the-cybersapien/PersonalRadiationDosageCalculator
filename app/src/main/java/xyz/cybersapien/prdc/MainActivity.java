package xyz.cybersapien.prdc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    public static final String LOCATION_FRAGMENT = "LOCATION_FRAGMENT";
    public static final String LIFESTYLE_FRAGMENT = "LIFESTYLE_FRAGMENT";
    public static final String RESULTS_FRAGMENT = "RESULT_FRAGMENT";

    private static final int NUM_PAGES = 3;

    private LocationFragment locationFragment;
    private LifeStyleFragment lifeStyleFragment;
    private ResultsFragment resultsFragment;
    @BindView(R.id.fragment_container) ViewPager mViewPager;

    private PagerAdapter mPagerAdapter;

    @BindView(R.id.rads_explanation) TextView explanationTextView;
    @BindView(R.id.main_button_next) Button nextButton;
    @BindView(R.id.main_button_prev) Button prevButton;

    private Double totalRads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the Content view and the Toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Initialize the total Radiations to zero
        totalRads = 0d;
        updateRads();

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (!HelperUtils.isInternetConnected(this)){
            showInternetErrorDialog();
        }

        locationFragment = new LocationFragment();
        lifeStyleFragment = new LifeStyleFragment();
        resultsFragment = new ResultsFragment();

        updateToLocationFragment();
    }

    @Override
    public void onBackPressed(){
        if (mViewPager.getCurrentItem() == 0){
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
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
        // Handle action bar item clicks here.
        switch (item.getItemId()){
            case R.id.action_convertor:
                startActivity(new Intent(MainActivity.this, ConvertorActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
        total.setText(getString(R.string.total_radiation_display, HelperUtils.getPreferredValue(totalRads, this)));
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

    private ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    updateToLocationFragment();
                    break;
                case 1:
                    updateToLifestyleFragment();
                    break;
                case 2:
                    updateToResultsFragment();
                    break;
            }
            super.onPageSelected(position);
        }
    };

    private void updateToLocationFragment(){
        nextButton.setText("NEXT");
        nextButton.setVisibility(View.VISIBLE);
        explanationTextView.setText("SAMPLE EXPLANATIONS FOR LOCATION");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        prevButton.setVisibility(View.GONE);
    }

    private void updateToLifestyleFragment(){
        explanationTextView.setText("SAMPLE EXPLANATION FOR LIFESTYLE");
        nextButton.setText("NEXT");
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setText("BACK");
        prevButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });
    }

    private void updateToResultsFragment(){
        explanationTextView.setText("");
        nextButton.setVisibility(View.GONE);
        prevButton.setText("BACK");
        prevButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(LOG_TAG, "onActivityResult: " + requestCode + " " + resultCode);
        switch (requestCode){
            case 1000:
                if (resultCode == RESULT_OK)
                    locationFragment.onConnected(null);
                else
                    locationFragment.showLocationErrorDialog();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter{

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return locationFragment;
                case 1:
                    return lifeStyleFragment;
                case 2:
                    return resultsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // HACK!! Do Nothing!!
            // We don't want the Fragments to be destroyed, so, don't do anything!
        }
    }
}
