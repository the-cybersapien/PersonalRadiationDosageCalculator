package xyz.cybersapien.prdc.helpers;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.ArrayList;

import xyz.cybersapien.prdc.BuildConfig;

/**
 * Created by cybersapien on 12/29/2016.
 * The following contains Helper Methods for Conversion of Units for radiations
 */

public class HelperUtils {

    private static final String GOOGLE_ELEVATION_BASE_URL = "https://maps.googleapis.com/maps/api/elevation/json";
    private static final String GOOGLE_GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public static final String TERRESTRIAL = "terrestrial";
    public static final String INHALATION = "inhalation";
    public static final String COSMOGENIC = "cosmogenic";
    public static final String INGESTION = "ingestion";


    private static Bundle BANGALORE_BUNDLE = new Bundle();
    private static Bundle CHENNAI_BUNDLE = new Bundle();
    private static Bundle DELHI_BUNDLE = new Bundle();
    private static Bundle HYDERABAD_BUNDLE = new Bundle();
    private static Bundle KOLKATTA_BUNDLE = new Bundle();
    private static Bundle MUMBAI_BUNDLE = new Bundle();
    private static Bundle NAGPUR_BUNDLE = new Bundle();
    private static Bundle TRIVANDRUM_BUNDLE = new Bundle();

    static {
        BANGALORE_BUNDLE.putDouble(TERRESTRIAL, 412);
        BANGALORE_BUNDLE.putDouble(INHALATION, 700);
        BANGALORE_BUNDLE.putDouble(COSMOGENIC, 15);
        BANGALORE_BUNDLE.putDouble(INGESTION, 315);

        CHENNAI_BUNDLE.putDouble(TERRESTRIAL, 536);
        CHENNAI_BUNDLE.putDouble(INHALATION, 550);
        CHENNAI_BUNDLE.putDouble(COSMOGENIC, 15);
        CHENNAI_BUNDLE.putDouble(INGESTION, 315);

        DELHI_BUNDLE.putDouble(TERRESTRIAL, 477);
        DELHI_BUNDLE.putDouble(INHALATION, 700);
        DELHI_BUNDLE.putDouble(COSMOGENIC, 15);
        DELHI_BUNDLE.putDouble(INGESTION, 315);

        HYDERABAD_BUNDLE.putDouble(TERRESTRIAL, 875);
        HYDERABAD_BUNDLE.putDouble(INHALATION, 1090);
        HYDERABAD_BUNDLE.putDouble(COSMOGENIC, 15);
        HYDERABAD_BUNDLE.putDouble(INGESTION, 315);

        KOLKATTA_BUNDLE.putDouble(TERRESTRIAL, 568);
        KOLKATTA_BUNDLE.putDouble(INHALATION, 1760);
        KOLKATTA_BUNDLE.putDouble(COSMOGENIC, 15);
        KOLKATTA_BUNDLE.putDouble(INGESTION, 315);

        MUMBAI_BUNDLE.putDouble(TERRESTRIAL, 202);
        MUMBAI_BUNDLE.putDouble(INHALATION, 620);
        MUMBAI_BUNDLE.putDouble(COSMOGENIC, 15);
        MUMBAI_BUNDLE.putDouble(INGESTION, 315);

        NAGPUR_BUNDLE.putDouble(TERRESTRIAL, 317);
        NAGPUR_BUNDLE.putDouble(INHALATION, 1960);
        NAGPUR_BUNDLE.putDouble(COSMOGENIC, 15);
        NAGPUR_BUNDLE.putDouble(INGESTION, 315);

        TRIVANDRUM_BUNDLE.putDouble(TERRESTRIAL, 412);
        TRIVANDRUM_BUNDLE.putDouble(INHALATION, 700);
        TRIVANDRUM_BUNDLE.putDouble(COSMOGENIC, 15);
        TRIVANDRUM_BUNDLE.putDouble(INGESTION, 315);
    }
    public static final double RESIDENT_MATERIAL_RADIATION = 70;

    // Averaged out values from US-EPA website
    public static final double XRAY_SKULL = 20;
    public static final double XRAY_CHEST = 40;
    public static final double XRAY_THORACIC_SPINE = 350;
    public static final double XRAY_LUMBAR_SPINE = 500;
    public static final double XRAY_ABDOMEN = 615;
    public static final double XRAY_PELVIS = 765;
    public static final double XRAY_DENTAL = 4;
    public static final double XRAY_LIMBS = 60;
    public static final double PROCEDURE_IVP = 2500;
    public static final double PROCEDURE_BARIUM_SWALLOW = 1500;
    public static final double PROCEDURE_BARIUM_MEAL = 3000;
    public static final double PROCEDURE_BARIUM_FOLLOW_UP = 3000;
    public static final double PROCEDURE_BARIUM_ENEMA = 7000;
    public static final double PROCEDURE_CT_HEAD = 2000;
    public static final double PROCEDURE_CT_CHEST = 8000;
    public static final double PROCEDURE_CT_ABDOMEN = 10000;
    public static final double PROCEDURE_CT_PELVIS = 10000;
    public static final double PROCEDURE_PTCA = 7500;
    public static final double PROCEDURE_CORONARY = 4600;
    public static final double PROCEDURE_MAMMOGRAM = 130;

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
        if (jsonResponse.isEmpty())
            return null;
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

    public static Location getNearestTabLocation(Location loc1){
        Location nearestLocation = null;
        ArrayList<Location> locations = new ArrayList<>();

        Location BANGALORE_LOCATION = new Location("412");
        BANGALORE_LOCATION.setLongitude(77.5945627);
        BANGALORE_LOCATION.setLatitude(12.9715987);
        BANGALORE_LOCATION.setExtras(BANGALORE_BUNDLE);
        locations.add(BANGALORE_LOCATION);

        Location CHENNAI_LOCATION = new Location("536");
        CHENNAI_LOCATION.setLongitude(80.2707184);
        CHENNAI_LOCATION.setLatitude(13.0826802);
        CHENNAI_LOCATION.setExtras(CHENNAI_BUNDLE);
        locations.add(CHENNAI_LOCATION);

        Location DELHI_LOCATION = new Location("477");
        DELHI_LOCATION.setLongitude(77.1024902);
        DELHI_LOCATION.setLatitude(28.7040592);
        DELHI_LOCATION.setExtras(DELHI_BUNDLE);
        locations.add(DELHI_LOCATION);

        Location HYDERABAD_LOCATION = new Location("875");
        HYDERABAD_LOCATION.setLongitude(78.486671);
        HYDERABAD_LOCATION.setLatitude(17.385044);
        HYDERABAD_LOCATION.setExtras(HYDERABAD_BUNDLE);
        locations.add(HYDERABAD_LOCATION);

        Location KOLKATTA_LOCATION = new Location("568");
        KOLKATTA_LOCATION.setLongitude(88.363895);
        KOLKATTA_LOCATION.setLatitude(22.572646);
        KOLKATTA_LOCATION.setExtras(KOLKATTA_BUNDLE);
        locations.add(KOLKATTA_LOCATION);

        Location MUMBAI_LOCATION = new Location("202");
        MUMBAI_LOCATION.setLongitude(72.8776560);
        MUMBAI_LOCATION.setLatitude(19.0759837);
        MUMBAI_LOCATION.setExtras(MUMBAI_BUNDLE);
        locations.add(MUMBAI_LOCATION);

        Location NAGPUR_LOCATION = new Location("317");
        NAGPUR_LOCATION.setLongitude(21.1458004);
        NAGPUR_LOCATION.setLatitude(79.0881546);
        NAGPUR_LOCATION.setExtras(NAGPUR_BUNDLE);
        locations.add(NAGPUR_LOCATION);

        Location TRIVANDRUM_LOCATION = new Location("412");
        TRIVANDRUM_LOCATION.setLongitude(76.9366376);
        TRIVANDRUM_LOCATION.setLatitude(8.52413910);
        TRIVANDRUM_LOCATION.setExtras(TRIVANDRUM_BUNDLE);
        locations.add(TRIVANDRUM_LOCATION);

        double nearestDistance = Double.MAX_VALUE;


        for (Location e : locations) {
            double distance = loc1.distanceTo(e);
            if (distance < nearestDistance){
                nearestDistance = distance;
                nearestLocation = e;
            }
        }
        return nearestLocation;
    }

    public static boolean isInternetConnected(Context context){
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static Double getRadsByTravel(Double kilometres) {
        return kilometres * 1.609344 / 100;
    }
}