package com.waracle.androidtest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public class CakeBrowserFragment extends Fragment {
    private static final String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";

    private static final String TAG = CakeBrowserFragment.class.getSimpleName();

    private ListView mListView;
    private MyAdapter mAdapter;

    public CakeBrowserFragment() { /**/ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.cakelist);

        // Create and set the list adapter.
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        //loadData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadData()  {
        new LoadDataAsyncTask().execute(JSON_URL);
    }

    private  class LoadDataAsyncTask extends AsyncTask<String, Void, Void> {

        private String[] getCakeDataFromJson(String cakeJsonString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TITLE = "title";
            final String DESCRIPTION = "desc";
            final String IMAGE_LINK = "image";

            JSONObject forecastJson = new JSONObject(cakeJsonString);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected Void doInBackground(String... urls) {
//            String jsonUrlString = urls[0];
//
//            URL url = null;
//            try {
//                url = new URL(jsonUrlString);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            HttpURLConnection urlConnection = null;
//            try {
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                // Can you think of a way to improve the performance of loading data
//                // using HTTP headers???
//
//                // Also, Do you trust any utils thrown your way????
//
//                byte[] bytes = StreamUtils.readUnknownFully(in);
//
//                // Read in charset of HTTP content.
//                String charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));
//
//                // Convert byte array to appropriate encoded string.
//                String jsonText = new String(bytes, charset);
//
//                // Read string as JSON.
//                return new JSONArray(jsonText);
//            } catch (IOException | JSONException e) {
//                Log.e(TAG, e.getMessage());
//            } finally {
//                urlConnection.disconnect();
//            }
//            return null;

            if (urls.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonUrlString = null;
            String cakeJsonString = null;

            try {
                jsonUrlString = urls[0];

                URL url = new URL(jsonUrlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                cakeJsonString = buffer.toString();

                Log.v(TAG, "Result string: " + cakeJsonString);
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getCakeDataFromJson(cakeJsonString);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the data.
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
//            if (result != null) {
//                mForecastAdapter.clear();
//                for(String dayForecastStr : result) {
//                    mForecastAdapter.add(dayForecastStr);
//                }
//                // New data is back from the server.  Hooray!
//            }

            if(result != null) {

                mAdapter.setItems(result);

            }

      }


    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(",");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "UTF-8";
    }

    private class MyAdapter extends BaseAdapter {

        // Can you think of a better way to represent these items???
        private JSONArray mItems;
        private ImageLoader mImageLoader;

        public MyAdapter() {
            this(new JSONArray());
        }

        public MyAdapter(JSONArray items) {
            mItems = items;
            mImageLoader = new ImageLoader();
        }

        @Override
        public int getCount() {
            return mItems.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return mItems.getJSONObject(position);
            } catch (JSONException e) {
                Log.e("", e.getMessage());
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View root = inflater.inflate(R.layout.list_item_layout, parent, false);
            if (root != null) {
                TextView title = (TextView) root.findViewById(R.id.title);
                TextView desc = (TextView) root.findViewById(R.id.description);
                ImageView image = (ImageView) root.findViewById(R.id.image);
                try {
                    JSONObject object = (JSONObject) getItem(position);
                    title.setText(object.getString("title"));
                    desc.setText(object.getString("desc"));
                    mImageLoader.load(object.getString("image"), image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return root;
        }

        public void setItems(JSONArray items) {
            mItems = items;
        }
    }
}
