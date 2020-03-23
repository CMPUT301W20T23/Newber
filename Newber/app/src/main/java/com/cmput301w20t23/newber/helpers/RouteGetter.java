package com.cmput301w20t23.newber.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.cmput301w20t23.newber.models.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Singleton class for getting route details between two points.
 *
 * @author Ayushi Patel
 */
public class RouteGetter {
    /**
     * Starts fetcher and parser tasks.
     *
     * @param origin    the start location
     * @param dest      the end location
     */
    public static void getRoute(LatLng origin, LatLng dest, String apiKey,
                                final Callback<Route> callback) {
        String url = getUrl(origin, dest, apiKey);

        FetchUrlTask fetchUrlTask = new FetchUrlTask(new Callback<String>() {
            @Override
            public void myResponseCallback(String result) {
                ParseJsonTask parseJsonTask = new ParseJsonTask(new Callback<Route>() {
                    @Override
                    public void myResponseCallback(Route result) {
                        callback.myResponseCallback(result);
                    }
                });

                // Invokes the thread for parsing the JSON data
                parseJsonTask.execute(result);
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
            String data = "";

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
         * @throws IOException thrown if stream not closed
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
    private static class ParseJsonTask extends AsyncTask<String, Integer, Route> {
        private Callback<Route> callback;

        /**
         * Instantiates a new ParseJsonTask.
         *
         * @param callback  the callback to fire
         */
        ParseJsonTask(Callback<Route> callback) {
            this.callback = callback;
        }

        @Override
        protected Route doInBackground(String... jsonData) {
            Route route = null;

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
        protected void onPostExecute(Route result) {
            super.onPostExecute(result);
            callback.myResponseCallback(result);
        }

        /**
         * Parses the JSON data to produce a list of points.
         *
         * @param jObject   the JSON data
         * @return a list of points along the route
         */
        private Route parseData(JSONObject jObject) {
            Route route = null;

            try {
                JSONObject jRoute = (JSONObject) jObject.getJSONArray("routes").get(0);

                JSONObject jOverviewPolyline = (JSONObject) jRoute.get("overview_polyline");
                String points = jOverviewPolyline.getString("points");
                List<LatLng> routePoints = PolyUtil.decode(points);

                JSONObject jLeg = (JSONObject) jRoute.getJSONArray("legs").get(0);
                JSONObject jDistance = (JSONObject) jLeg.get("distance");
                double routeDistance = jDistance.getDouble("value");

                route = new Route(routePoints, routeDistance);
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
     * @return the URL string
     */
    private static String getUrl(LatLng origin, LatLng dest, String apiKey) {
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