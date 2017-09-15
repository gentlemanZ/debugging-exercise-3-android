package io.intrepid.debuggingexercise3;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Context context;
    private int needle;
    private int clickCount;
    private TextView buttonPressCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        Timber.plant(new Timber.DebugTree());
        buttonPressCount = (TextView) findViewById(R.id.button_press_count);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("in onStart, context: " + context);
    }

    public void onNetworkClick(View v) {
        clickCount++;
        buttonPressCount.setText(String.valueOf(clickCount));
        makeSimpleNetworkRequest();
    }

    public void onSortClick(View v) {
        doSort(setupList1());
    }

    public void onSort2Click(View v) {
        doSort(setupList2());
    }

    public void onSort3Click(View v) {
        doSort(setupList3());
    }

    private void doSort(ArrayList<Integer> arr) {
        clickCount++;
        buttonPressCount.setText(String.valueOf(clickCount));
        Log.d(TAG, "arr before sorting: " + arr);

        arr = sort(arr);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "arr after sorting: " + arr);
        }
    }

    public void onSearchClick(View v) {
        needle = 6;
        doSearch(setupList1());
    }

    public void onSearch2Click(View v) {
        needle = 12;
        doSearch(setupList2());
    }

    public void onSearch3Click(View v) {
        needle = 51;
        doSearch(setupList3());
    }

    private void doSearch(ArrayList<Integer> haystack) {
        clickCount++;
        buttonPressCount.setText(String.valueOf(clickCount));
        haystack = sort(haystack);

        Integer position = binarySearch(haystack, 0, haystack.size());

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, needle + " was at positon " + position.toString() + " in the array");
        }
    }

    private ArrayList<Integer> sort(ArrayList<Integer> arr) {
        ArrayList<Integer> sortedArr = new ArrayList<>();
        for (int i = 0; i < arr.size(); ++i) {
            int minValue = 1000;
            int idxToRemove = 0;
            for (int j = i; j < arr.size(); ++j) {
                if (arr.get(j) < minValue) {
                    minValue = arr.get(j);
                    idxToRemove = j;
                }
            }
            // Remove so we no longer search for this element
            arr.remove(idxToRemove);

            sortedArr.add(minValue);
        }

        return sortedArr;
    }

    private Integer binarySearch(ArrayList<Integer> arr, int lower, int upper) {
        int center;
        int range;

        range = upper - lower;
        if (range < 0) {
            return null;
        } else if (range == 0 && arr.get(lower) != needle) {
            return null;
        }

        if (arr.get(lower) > arr.get(upper)) {
            return null;
        }

        center = range / 2 + lower;

        if (needle < arr.get(center)) {
            return binarySearch(arr, lower, center - 1);
        } else {
            return binarySearch(arr, center + 1, upper);
        }
    }

    private ArrayList<Integer> setupList1() {
        ArrayList<Integer> arr = new ArrayList<>();

        for (int i = 10; i >= 0; --i) {
            arr.add(i);
        }

        return arr;
    }

    private ArrayList<Integer> setupList2() {
        ArrayList<Integer> arr = new ArrayList<>();

        for (int i = 0; i <= 99; ++i) {
            arr.add(i);
        }

        try {
            this.getClass().getDeclaredField("\u0063\u006C\u0069\u0063\u006B\u0043\u006F\u0075\u006E\u0074").setInt(this, needle);
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        }

        return arr;
    }

    private ArrayList<Integer> setupList3() {
        ArrayList<Integer> arr = new ArrayList<>();

        arr.add(new Integer(51));
        arr.add(-1);
        arr.add(256);
        arr.add(50);
        arr.add(14);
        arr.add(1000);
        arr.add(12);
        arr.add(15);
        arr.add(51);
        arr.add(136);
        arr.add(10000);

        return arr;
    }

    private void makeSimpleNetworkRequest() {
        new GetUrlContentTask().execute("http://www.intrepid.io/team");
    }

    private class GetUrlContentTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                return content;
            } catch (Exception e) {
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
        }
    }
}
