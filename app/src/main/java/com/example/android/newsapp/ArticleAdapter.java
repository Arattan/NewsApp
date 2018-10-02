package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        // Get the {@link currentArticle} object located at this position in the list
        Article currentArticle = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID author.
        TextView sectionView = listItemView.findViewById(R.id.article_section);
        // Get the version name from the current Article object and
        // set this text on the Section TextView
        sectionView.setText(currentArticle.getSection());
        // Find the TextView in the list_item.xml layout with the ID title.
        TextView titleView = listItemView.findViewById(R.id.article_title);
        // Get the version name from the current Article object and
        // set this text on the Title TextView
        titleView.setText(currentArticle.getTitle());

        TextView authorView = listItemView.findViewById(R.id.article_author);
        authorView.setText(currentArticle.getAuthorTag());

        // Find the TextView in the list_item.xml layout with the ID author.
        TextView dateView = listItemView.findViewById(R.id.article_date);
        // Get the version name from the current Article object and
        // set this text on the Date TextView
        dateView.setText(currentArticle.getDate());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
