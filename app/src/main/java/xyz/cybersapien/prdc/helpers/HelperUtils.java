package xyz.cybersapien.prdc.helpers;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import xyz.cybersapien.prdc.BuildConfig;

/**
 * Created by cybersapien on 12/29/2016.
 * The following contains Helper Methods for Conversion of Units for radiations
 */

public class HelperUtils {

    private static final String GOOGLE_ELEVATION_BASE_URL = "https://maps.googleapis.com/maps/api/elevation/json";
    private static final String GOOGLE_GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public static double getCosmicRaysRads(Double altitude){
        if (altitude == null)
            return Double.NaN;
        else if (altitude < 150)
            return 265;
        else if (altitude < 305)
            return 275;
        else if (altitude < 610)
            return 295;
        else if (altitude < 1220)
            return 350;
        else if (altitude < 1828)
            return 460;
        else if (altitude < 2438)
            return 630;
        else if (altitude < 3408)
            return 905;
        else
            return 1070;
    }

    public static double getAltitude(Location location) throws Exception {

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        Uri altitudeQuery = Uri.parse(GOOGLE_ELEVATION_BASE_URL)
                .buildUpon()
                .appendQueryParameter("locations", latitude + "," + longitude)
                .appendQueryParameter("key", BuildConfig.GOOGLE_API_KEY)
                .build();

        URL altitudeQueryURL = new URL(altitudeQuery.toString());

        String result = makeHttpRequest(altitudeQueryURL);

        return parseAltitudeFromJSON(result);
    }

    private static double parseAltitudeFromJSON(String jsonResponse) throws JSONException {
        double altitude = 0;

        if (jsonResponse.isEmpty())
            return altitude;

        JSONObject root = new JSONObject(jsonResponse);
        JSONArray results = root.getJSONArray("results");
        altitude = results.getJSONObject(0).getDouble("elevation");

        return altitude;
    }

    public static String getAddressFromLocation(Location location){
        String result = "Error";
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        Uri locationUri = Uri.parse(GOOGLE_GEOCODE_BASE_URL)
                .buildUpon()
                .appendQueryParameter("latlng", latitude + "," + longitude)
                .appendQueryParameter("key", BuildConfig.GOOGLE_API_KEY)
                .build();

        try {
            URL addressQueryURL = new URL(locationUri.toString());
            String jsonResponse = makeHttpRequest(addressQueryURL);
            result = parseAddressFromJSON(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String parseAddressFromJSON(String jsonResponse) throws JSONException {
        String result = "Error";

        if (jsonResponse.trim().isEmpty())
            return result;

        JSONObject root = new JSONObject(jsonResponse);
        JSONArray resultsArray = root.getJSONArray("results");

        // Worst case scenario, there are no logs for the address.
        // we just return empty string
        if (resultsArray.length() <= 0)
            return result;

        // Now this part is a little tricky,
        // since there can be a lot of results for a single location,
        // The address shown can be wrong.
        // This is just a UI problem, since the altitude doesn't change
        // so much with different address or locality
        JSONObject firstObject = resultsArray.getJSONObject(0);
        result = firstObject.getString("formatted_address");
        return result;
    }



    public static Location getLocationFromAddress(String address) {
        Location loc = null;

        Uri locationUri = Uri.parse(GOOGLE_GEOCODE_BASE_URL)
                .buildUpon()
                .appendQueryParameter("address", address)
                .appendQueryParameter("key", BuildConfig.GOOGLE_API_KEY)
                .build();

        try {
            URL locationUrl = new URL(locationUri.toString());
            String jsonResponse = makeHttpRequest(locationUrl);
            loc = parseLocationFromJSON(jsonResponse);
        } catch (Exception e){
            e.printStackTrace();
        }
        return loc;
    }

    private static Location parseLocationFromJSON(String jsonResponse) throws JSONException {

        JSONObject root = new JSONObject(jsonResponse);
        JSONArray resultArray = root.getJSONArray("results");
        JSONObject geometry = resultArray.getJSONObject(0).getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");

        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("lng");

        Location loc = new Location("GOOGLE GEO");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("HttpConnection: " , "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e("HttpConnection: ", "makeHttpRequest: Problem retrieving the Altitude from JSON result.");
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null){
            InputStreamReader streamReader = new InputStreamReader( inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
