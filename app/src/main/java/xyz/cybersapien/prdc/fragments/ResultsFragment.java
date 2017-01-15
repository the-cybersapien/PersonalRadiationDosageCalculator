package xyz.cybersapien.prdc.fragments;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.cybersapien.prdc.*;
import xyz.cybersapien.prdc.helpers.HelperUtils;
import xyz.cybersapien.prdc.helpers.UpdateUI;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment {

    private static final String LOG_TAG = ResultsFragment.class.getSimpleName();

    public static final String cbrspnBaseURL = "http://www.cybersapien.xyz/prdc/";


    private UpdateUI uiUpdater;

    private View rootView;

    private boolean dataSent;

    @BindView(R.id.national_average_view) TextView natAvgTextView;
    @BindView(R.id.annual_radiation_dosage) TextView annualRadiationDosage;
    @BindView(R.id.name_edit_text) EditText nameEditText;
    @BindView(R.id.submit_data) Button submitButton;

    public ResultsFragment() {
        // Required empty public constructor
        dataSent = false;
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

    @OnClick(R.id.submit_data)
    public void sendData(){
        if (!dataSent){
            SubmitData dataTask = new SubmitData();
            String nameText = nameEditText.getText().toString().trim();
            String valueText = String.valueOf(uiUpdater.getTotalRads());
            dataTask.execute(nameText, valueText);
        }
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

    private class SubmitData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String name = "";
            String value = "";

            if (params.length == 0){
                return null;
            }
            if (!params[0].isEmpty()){
                name = params[0];
            }

            if (!params[1].isEmpty()){
                value = params[1];
            } else {
                return null;
            }

            Uri sendURI = Uri.parse(cbrspnBaseURL)
                    .buildUpon()
                    .appendPath("insert_calculated_value.php")
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("value", value)
                    .build();

            String response = "";
            try {
                response = makePOSTHttpRequest(new URL(sendURI.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            boolean error = true;
            if (!s.isEmpty() && s.equals("success")){
                error = false;
            }
            if (error){
                Toast.makeText(getActivity(), "Error Sending Data!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Data Sent Successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        private String makePOSTHttpRequest(URL url) throws IOException{
            String response = "";

            if (url == null)
                return null;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try{

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    response = HelperUtils.readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "makePOSTHttpRequest: Error Sending data");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (inputStream != null)
                    inputStream.close();

            }

            return response;
        }
    }
}