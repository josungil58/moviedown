package com.example.android.moviedown;

import android.content.Intent;
import android.os.AsyncTask;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDownFragment extends Fragment {

    ArrayAdapter <String> mMovieAdapter;
    String[] movieID, movieTitle, movieReleaseDate, movieVoteAverage, movieOverView, moviePosterPath;

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

    class FetchMovieTask extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=c20129fdf73b5df3ab44548ad7f73586");

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

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

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
            } catch (JSONException j) {
                Log.e(LOG_TAG, "JSON Error", j);
            }
            return null;
        }

        private String[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            movieId = new String[movieArray.length()];
            movieTitle = new String[movieArray.length()];
            movieReleaseDate = new String[movieArray.length()];
            movieVoteAverage = new String[movieArray.length()];
            movieOverview = new String[movieArray.length()];
            moviePosterPath = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++)
            {
                JSONObject movie = movieArray.getJSONObject(i);
                movieId[i] = movie.getString("id");
                movieTitle[i] = movie.getString("original_title");
                movieReleaseDate[i] = movie.getString("release_date");
                movieVoteAverage[i] = movie.getString("vote_average");
                movieOverview[i] = movie.getString("overview");
                moviePosterPath[i] = movie.getString("poster_path");
            }
            return movieTitle;
        }

        @Override
        protected void onPostExecute(String[] strings)
        {
            super.onPostExecute(strings);
            mMovieAdapter.clear();
            mMovieAdapter.addAll(strings);
        }
    }
}

