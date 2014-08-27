/**
 * Copyright URX 2014
 **/
package com.urx.demo.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.urx.android.AndroidClient;
import com.urx.core.ClientConfig;
import com.urx.core.ResponseHandler;
import com.urx.core.search.SearchResult;
import com.urx.core.search.SearchResults;
import com.urx.core.search.query.Query;

import static com.urx.android.resolve.AndroidResolver.installedApps;
import static com.urx.core.search.query.ActionType.Listen;
import static com.urx.core.search.query.QueryBuilder.action;
import static com.urx.core.search.query.QueryBuilder.phrase;
import static com.urx.core.search.query.QueryBuilder.term;


public class MainActivity extends ActionBarActivity {

    // Set up a client configuration object with your API key, and then
    // build an API client to use for querying and resolving deeplinks.
    // Note that these instances can be shared, as they are thread-safe.
    static final ClientConfig config = new ClientConfig("INSERT-API-KEY-HERE");
    static final AndroidClient client = new AndroidClient(config);

    ViewGroup container;
    ImageView image;
    TextView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting up the UI elements
        setContentView(R.layout.activity_main);
        container = (ViewGroup) findViewById(R.id.container);
        image = (ImageView) findViewById(R.id.image);
        button = (TextView) findViewById(R.id.button);

        // Execute our search functionality
        executeSearch();
    }

    protected void executeSearch() {
        // Build the query: "ellie goulding" AND lights AND action:ListenAction
        final Query q = phrase("ellie goulding").and(term("lights")).and(action(Listen));

        // Execute the query
        client.query(q, new ResponseHandler<SearchResults>() {
            @Override
            public void onSuccess(SearchResults results) {
                // Update our views using the first search result
                SearchResult firstResult = results.getResults().get(0);
                updateViews(firstResult, client);
            }
        });
    }

    protected void updateViews(final SearchResult thing, final AndroidClient client) {
        // Load the image for the result. Here we are using Square's
        // Picasso library (http://square.github.io/picasso/)
        Picasso.with(MainActivity.this)
                .load(thing.getImage())
                .into(image, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        // Don't show the container until the image is loaded
                        container.setVisibility(View.VISIBLE);
                    }
                });

        // Set the button text
        String buttonText = thing.getPotentialAction().getDescription();
        button.setText(buttonText);

        // Resolve deeplink on click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resolve to the deeplink only if the app is installed,
                // otherwise fallback to the web URL
                client.resolve(thing, installedApps(MainActivity.this));
            }
        });
    }

}
