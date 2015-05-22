package me.calebjones.thejonestheory.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.calebjones.thejonestheory.FeedItem;
import me.calebjones.thejonestheory.R;
import me.calebjones.thejonestheory.loader.MyRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FetchData.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FetchData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FetchData extends Fragment {

    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;

    private Button btnSubmit;
    private final static String URL = "https://public-api.wordpress.com/rest/v1.1/sites/calebjones.me/posts?number=20";
    /*Downloading data from below url*/
    public final String url = "https://public-api.wordpress.com/rest/v1.1/sites/calebjones.me/posts?&number=20";
    private TextView tvPostCount, tvPostTitle, tvPostUrl;
    public static final String TAG = "stats";

    private OnFragmentInteractionListener mListener;

    public static FetchData newInstance(String param1, String param2) {
        FetchData fragment = new FetchData();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FetchData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Activity c = getActivity();

        LayoutInflater lf = getActivity().getLayoutInflater();

        View view = lf.inflate(R.layout.fragment_fetch_data, container, false);
//
//        tvPostCount = (TextView) view.findViewById(R.id.txtPostCount);
//        tvPostTitle = (TextView) view.findViewById(R.id.txtPostTitle);
//        tvPostUrl = (TextView) view.findViewById(R.id.txtPostUrl);

//        /* Allow activity to show indeterminate progressbar */
//        this.getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        /* Initialize recyclerview */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(layoutManager);


//        new FetchDataTask().execute(URL);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncHttpTask().execute(url);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                /* forming th java.net.URL object */
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode ==  200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1; // Successful
                }else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            getActivity().setProgressBarIndeterminateVisibility(false);

            /* Download complete. Lets update UI */
            if (result == 1) {
                adapter = new MyRecyclerAdapter(getActivity(), feedItemList);
                mRecyclerView.setAdapter(adapter);
            } else Log.e(TAG, "Failed to fetch data!");
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");

            /*Initialize array if null*/
            if (null == feedItemList) {
                feedItemList = new ArrayList<FeedItem>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                FeedItem item = new FeedItem();
                item.setTitle(post.optString("title"));
                Integer ImageLength = post.optString("featured_image").length();
                if (ImageLength == 0) {
                    Log.d(TAG, "It should be null!");
                    item.setThumbnail(null);
                } else {
                    item.setThumbnail(post.optString("featured_image"));
                }
                feedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
