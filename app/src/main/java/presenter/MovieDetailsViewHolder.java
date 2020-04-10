package presenter;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import model.MovieTile;
import tv.cloudwalker.launcher.R;

/**
 * Created by cognoscis on 9/1/18.
 */

public class MovieDetailsViewHolder extends Presenter.ViewHolder {

    private TextView movieTitleTV, movieYearTV, movieOverview, mRuntimeTV, mTileSource, mDirectorTv, tileCasts;
    private RatingBar ratingBar;
    private LinearLayout mGenresLayout;

    private View itemView;

    public MovieDetailsViewHolder(View view) {
        super(view);
        Typeface tfConReg = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        Typeface tfReg = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/Roboto-Regular.ttf");

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        movieTitleTV = (TextView) view.findViewById(R.id.movie_title);
        movieTitleTV.setTypeface(tfConReg);

        tileCasts = (TextView) view.findViewById(R.id.casts);
        tileCasts.setTypeface(tfConReg);

        movieYearTV = (TextView) view.findViewById(R.id.movie_year);
        movieYearTV.setTypeface(tfReg);

        movieOverview = (TextView) view.findViewById(R.id.overview);
        movieOverview.setTypeface(tfReg);

        mRuntimeTV = (TextView) view.findViewById(R.id.runtime);
        mRuntimeTV.setTypeface(tfReg);

        mTileSource = (TextView) view.findViewById(R.id.tile_source);
        mTileSource.setTypeface(tfReg);

        mDirectorTv = (TextView) view.findViewById(R.id.director_tv);
        mDirectorTv.setTypeface(tfConReg);

        mGenresLayout = (LinearLayout) view.findViewById(R.id.genres);
        itemView = view;
    }

    private String getSeperatedValuesWithHeader(String seperator, String header, ArrayList<String> list) {
        String values = "";
        for (String value : list) {
            if (value.length() > 0) {
                values += value + seperator;
            } else {
                return "";
            }
        }
        values = values.replaceAll("[" + seperator + "] $", "");
        if (header.length() > 0) {
            return (header + " : " + values);
        } else {
            return values;
        }
    }


    private static SpannableStringBuilder getSpannableStringDescription(MovieTile movie) {
        SpannableStringBuilder spannableStringDescription = new SpannableStringBuilder();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = movie.getSynopsis();
        iterator.setText(source);
        int start = iterator.first();
        int maxLength = 0;
        for (int end = iterator.next(), lineCount = 1;
             end != BreakIterator.DONE && lineCount <= 4;
             start = end, end = iterator.next(), lineCount++) {
            maxLength += source.substring(start, end).length();
            if (maxLength > 375) {
                continue;
            }
            spannableStringDescription.append(source.substring(start, end));
        }
        return spannableStringDescription;
    }


    public void bind(MovieTile movie) {
        if (movie != null && movie.getTitle() != null) {
            mRuntimeTV.setText(movie.getRuntime());

            if (movie.getSource().length() > 0) {
                mTileSource.setText(movie.getSource());
            }
            tileCasts.setText(movie.getCast().toString());
            movieTitleTV.setText(movie.getTitle());
            movieYearTV.setText(movie.getYear());
            movieOverview.setText(movie.getSynopsis());
            movieOverview.setText(getSpannableStringDescription(movie));
            mGenresLayout.removeAllViews();
            ratingBar.setRating((movie.getRating() / 2));

            if (movie.getDirector() != null) {
                mDirectorTv.setText(getSeperatedValuesWithHeader(", ", "Directors", movie.getDirector()));
            }

            if (movie.getCast() != null) {
                tileCasts.setText(getSeperatedValuesWithHeader(", ", "Cast", movie.getCast()));
            }

            int _16dp = (int) itemView.getResources().getDimension(R.dimen.full_padding);
            int _8dp = (int) itemView.getResources().getDimension(R.dimen.half_padding);
            float corner = itemView.getResources().getDimension(R.dimen.genre_corner);

            // Adds each genre to the genre layout


            for (String g : movie.getGenre()) {
                TextView tv = new TextView(itemView.getContext());
                tv.setText(g);
                tv.setTextColor(itemView.getContext().getResources().getColor(R.color.pure_white));
                tv.setTextSize(16);
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(corner);
                shape.setColor(itemView.getContext().getResources().getColor(R.color.tageback_color));
                tv.setPadding(_8dp, _8dp, _8dp, _8dp);
                tv.setBackground(shape);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, _16dp, 0);
                tv.setLayoutParams(params);

                mGenresLayout.addView(tv);
            }
        }

    }
}
