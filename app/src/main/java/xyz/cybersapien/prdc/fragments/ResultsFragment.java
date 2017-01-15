package xyz.cybersapien.prdc.fragments;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.prdc.*;
import xyz.cybersapien.prdc.helpers.HelperUtils;
import xyz.cybersapien.prdc.helpers.UpdateUI;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment {

    public static final String cbrspnBaseURL = "http://www.cybersapien.xyz/prdc/";


    private UpdateUI uiUpdater;

    private View rootView;

    @BindView(R.id.national_average_view) TextView natAvgTextView;
    @BindView(R.id.annual_radiation_dosage) TextView annualRadiationDosage;

    public ResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_results, container, false);

        ButterKnife.bind(this, rootView);

        uiUpdater = (UpdateUI) getActivity();

        // Initialize and execute the Task to get National average
        getNatAvg nationalAveragetask = new getNatAvg();
        nationalAveragetask.execute();

        return rootView;
    }

    public void updateAnnualRadiationDosage(){
        Double totalRads = uiUpdater.getTotalRads();

        annualRadiationDosage.setText(HelperUtils.getPreferredValue(totalRads, getContext()));
    }

    private class getNatAvg extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            natAvgTextView.setText("Getting National Avg..");
        }

        @Override
        protected String doInBackground(Void... params) {
            Uri queryURI = Uri.parse(cbrspnBaseURL)
                    .buildUpon()
                    .appendPath("get_nat_avg.php")
                    .build();
            boolean isSuccessful = true;

            String totalVal = null;
            try {
                URL queryURL = new URL(queryURI.toString());
                String jsonResponse = HelperUtils.makeHttpRequest(queryURL);

                double nationalAverage = getAverage(jsonResponse);

                totalVal = HelperUtils.getPreferredValue(nationalAverage, getContext());

            } catch (IOException | JSONException e){
                e.printStackTrace();
                isSuccessful = false;
            } finally {
                if (!isSuccessful){
                    Toast.makeText(getActivity(), "Error Getting National Average!", Toast.LENGTH_SHORT).show();
                }
            }
            return totalVal;
        }

        @Override
        protected void onPostExecute(String value) {
            natAvgTextView.setText(value);
        }

        private double getAverage(String jsonResponse) throws JSONException {

            JSONArray root = new JSONArray(jsonResponse);

            JSONObject object = root.getJSONObject(0);

            return object.getDouble("AVG(value)");
        }
    }
}
