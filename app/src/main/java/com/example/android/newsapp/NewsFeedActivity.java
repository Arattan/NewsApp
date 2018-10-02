package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {


    public static final String LOG_TAG = NewsFeedActivity.class.getName();
    private static final String NEWSFEED_REQUEST_URL = "https://content.guardianapis.com/football/arsenal?order-by=newest&show-tags=contributor&api-key=19bca5f7-ee10-4afc-a20f-5bdb2389dbc0";
    private static final int ARTICLE_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        final ListView newsFeedListView = findViewById(R.id.list);

        mAdapter = new ArticleAdapter(NewsFeedActivity.this, new ArrayList<Article>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsFeedListView.setAdapter(mAdapter);

        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = mAdapter.getItem(position);
                Uri currentArticleUri = Uri.parse(currentArticle.getUrl());
                Intent articleIntent = new Intent (Intent.ACTION_VIEW, currentArticleUri);
                startActivity(articleIntent);
            }
        });

        // Set empty view
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsFeedListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManager to check state of connectivity
        // after onClick is called
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        Log.i(LOG_TAG, "TEST: ConnectivityManager called after NewsFeed called..");

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
        @Override
        public Loader<List<Article>> onCreateLoader(int i, @Nullable Bundle bundle) {

            Log.e(LOG_TAG, "TEST: onCreateLoader() called ...");

            // Create a new loader for the given URL
            return new ArticleLoader(this, NEWSFEED_REQUEST_URL);
        }

        @Override
        public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

            Log.i(LOG_TAG, "TEST: onLoadFinished() called...");
            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No articles found currently."
            mEmptyStateTextView.setText(R.string.no_articles);

            // Clear the adapter of previous article data
            mAdapter.clear();

            // If there is a valid list of {@link article}s, then add them to the adapter's
            // dataset. This will trigger the ListView to update.
            if (articles != null && !articles.isEmpty()) {
                mAdapter.addAll(articles);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Article>> loader) {
            Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");
            // Loader reset, so we can clear out our existing data.
            mAdapter.clear();
        }

    }

