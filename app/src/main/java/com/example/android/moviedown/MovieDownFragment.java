package com.example.android.moviedown;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
        GridView listView = (GridView)rootview.findViewByID(R.id.gridview_movies);

        mMovieAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_movies,
                R.id.image_view_movie,
                new ArrayList<String>());

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

    public class FetchMovieTask extends AsyncTask<void, void, void>{

        //getweatherdatafromjson에 해당하는 것을 coding해야 함함

    }
}
