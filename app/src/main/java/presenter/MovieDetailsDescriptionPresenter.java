package presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import model.MovieTile;
import tv.cloudwalker.launcher.R;

/**
 * Created by cognoscis on 8/1/18.
 */

public class MovieDetailsDescriptionPresenter extends Presenter {

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_details, parent, false);
        return new MovieDetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        MovieTile movie = (MovieTile) item;
        MovieDetailsViewHolder holder = (MovieDetailsViewHolder) viewHolder;
        holder.bind(movie);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
