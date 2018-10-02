package com.example.android.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Query The Guardian API and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link article}s
        List<Article> article = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link Article}s
        return article;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
                Log.e(LOG_TAG, "Problem converting JSON response into String");
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Article> extractFeatureFromJson(String articleJSON) {

        Log.e(LOG_TAG, "Problem extracting from JSON...");

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of items (or articles).
            JSONObject responseJSONObject = baseJsonResponse.getJSONObject("response");

            JSONArray responseJSONArray = responseJSONObject.getJSONArray("results");

            // For each article in the responseJSONArray, create an {@link Article} object
            for (int i = 0; i < responseJSONArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = responseJSONArray.optJSONObject(i);

                // Extract the value for the key called "title"
                // If no title value exists then a 'not found' will indicate so.
                String title;
                if (currentArticle.has("webTitle")) {
                    title = currentArticle.optString("webTitle");
                } else {
                    title = ("R.string.title_unavailable");
                }
                // Extract the value for the key called "sectionName"
                // If no Section Name value exists then a 'not found' will indicate so.
                String section;
                if (currentArticle.has("sectionName")) {
                    section = currentArticle.optString("sectionName");
                } else {
                    section = ("R.string.section_unavailable");
                }
                // Extract the value for the key called "webPublicationDate"
                // If no Date value exists then a 'unavailable' will indicate so.
                String date;
                if (currentArticle.has("webPublicationDate")) {
                    date = currentArticle.optString("webPublicationDate");
                } else {
                    date = ("R.string.date_unavailable");
                }

                String author = null;
                JSONArray tagArray = currentArticle.optJSONArray("tags");
                for (int j = 0; j < tagArray.length(); j++) {

                    JSONObject currentAuthor = tagArray.optJSONObject(j);
                    author = currentAuthor.getString("webTitle");
                }

                String url = currentArticle.optString("webUrl");

                // Create a new {@link Article} object with the section, title and date variables
                // from the JSON response.
                Article article = new Article(section, title, author, date, url);

                // Add the new {@link article} to the list of articles.
                articles.add(article);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        // Return the list of books
        return articles;
    }
}
