package com.cmput301w20t23.newber.helpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class draws a route on the provided Google map.
 *
 * @author Ayushi Patel
 */
public class RouteDrawer {
    final private GoogleMap googleMap;
    private String apiKey;
    private Polyline polyline;

    /**
     * Instantiates a new RouteDrawer.
     * @param googleMap the Google map
     * @param apiKey    the Directions API key
     */
    public RouteDrawer(GoogleMap googleMap, String apiKey) {
        this.googleMap = googleMap;
        this.apiKey = apiKey;
    }

    /**
     * Starts fetcher and parser tasks.
     * @param origin    the start location
     * @param dest      the end location
     */
    public void drawRoute(LatLng origin, LatLng dest) {
        String url = getUrl(origin, dest);

        FetchUrlTask fetchUrlTask = new FetchUrlTask(new Callback<String>() {
            @Override
            public void myResponseCallback(String jsonData) {
                ParseJsonTask parseJsonTask = new ParseJsonTask(new Callback<List<LatLng>>() {
                    @Override
                    public void myResponseCallback(List<LatLng> route) {
                        if (polyline != null) {
                            polyline.remove();
                        }

                        // Drawing polyline in the Google Map
                        polyline = googleMap.addPolyline(new PolylineOptions()
                            .addAll(route)
                            .width(6)
                            .color(Color.BLUE));
                    }
                });

                // Invokes the thread for parsing the JSON data
                parseJsonTask.execute(jsonData);
            }
        });

        // Start downloading JSON data from Directions API
        fetchUrlTask.execute(url);
    }

    /**
     * This class fetches data from the provided URL.
     */
    public static class FetchUrlTask extends AsyncTask<String, Void, String> {
        private Callback<String> callback;

        /**
         * Instantiates a new FetchUrlTask.
         *
         * @param callback  the callback to fire when task is done
         */
        FetchUrlTask(Callback<String> callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = null;

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("FetchUrlTask", data);
            } catch (Exception e) {
                Log.d("FetchUrlTask", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            callback.myResponseCallback(result);
        }

        /**
         * Downloads data from the URL.
         * @param strUrl    the URL string
         * @return the downloaded data
         * @throws IOException
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("downloadUrl", e.toString());
            } finally {
                assert iStream != null;
                iStream.close();
                urlConnection.disconnect();
            }

            return data;
        }
    }

    /**
     * This class parses the JSON data.
     */
    private static class ParseJsonTask extends AsyncTask<String, Integer, List<LatLng>> {
        private Callback<List<LatLng>> callback;

        /**
         * Instantiates a new ParseJsonTask.
         *
         * @param callback  the callback to fire
         */
        ParseJsonTask(Callback<List<LatLng>> callback) {
            this.callback = callback;
        }

        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            List<LatLng> route = null;

            try {
                JSONObject jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);

                route = parseData(jObject);
                Log.d("ParserTask", route.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }

            return route;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);
            callback.myResponseCallback(result);
        }

        /**
         * Parses the JSON data to produce a list of points.
         *
         * @param jObject   the JSON data
         * @return a list of points along the route
         */
        private List<LatLng> parseData(JSONObject jObject) {
            List<LatLng> route = new ArrayList<>();

            try {
                JSONObject jRoute = (JSONObject) jObject.getJSONArray("routes").get(0);
                JSONObject jOverviewPolyline = (JSONObject) jRoute.get("overview_polyline");
                String points = jOverviewPolyline.getString("points");
                route = PolyUtil.decode(points);
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }

            return route;
        }
    }

    /**
     * Builds the URL string
     * @param origin    the start location
     * @param dest      the end location
     * @return
     */
    private String getUrl(LatLng origin, LatLng dest) {
        String url = "https://maps.googleapis.com/maps/api/directions/";

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String str_key = "key=" + apiKey;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + str_key;

        // Output format
        String output = "json";

        // Building the url to the web service
        url = url + output + "?" + parameters;

        return url;
    }
}