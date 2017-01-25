package com.example.android.moviedown;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDownFragment extends Fragment {

    ArrayAdapter <String> mMovieAdapter;

    public MovieDownFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_movies,
                R.id.image_view_movie,
                new ArrayList<String>());

        return inflater.inflate(R.layout.fragment_main, container, false);
        GridView listView = (GridView)rootview.findViewByID(R.id.gridview_movies);

        listView.setAdapter(mMovieAdapter);
        listView.setOnClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapteriView, View view, int i, long l){
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                String movieDown = mMovieAdapter.getItem(i);
                String send = "Overview " + movieOverView[i] + "\n" + "Release Date " + movieReleaseDate[i];
                intent.putExtra(Intent.EXTRA_TEXT, send);
                startActivity(intent);
            }
        });
    }

    public void onStart()
    {
        super.onStart();
        updateMovie();
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr, int page)
                throws JSONException {

            final String TMDB_MOVIE_ID = "id";
            final String TMDB_MOVIE_TITLE = "original_title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_USER_RATING= "vote_average";
            final String TMDB_MOVIE_SYNOPSIS = "overview";
            final String TMDB_MOVIE_POSTER = "poster_path";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_MOVIE_ID);

            String[] resultStrs = new String[page];

            for (int i = 0; i < movieArray.length(); i++){

                String title;
                String date;
                String rate;
                String synopsis;
                String poster;

                JSONObject movieDown = movieArray.getJSONObject(i);

                JSONObject movieObject = movieDown;

                resultStrs[i] =
            }

    }

    @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            int page = 5;

            try {
                final String MOVIEDOWN_BASE_URL =
                        "https//api.themoviedb.org/3/movie/popular";
                final String PAGE = "page";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDOWN_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE, page)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDOWN_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built Uri " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                      return null;
                }

                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie Down String " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if (result != null){
                mMovieAdapter.clear();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    mMovieAdapter.addAll(result);
        }
}

