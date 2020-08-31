package presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import model.MovieTile;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;


public class CwCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        v.setBackground(((CloudwalkerApplication)parent.getContext().getApplicationContext()).getDrawable("focus_on_select_bg"));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final ImageView posterImageView;
        posterImageView =  viewHolder.view.findViewById(R.id.posterImageView);

        if(item instanceof MovieTile) {
            final MovieTile movieTile = (MovieTile) item;
            if(movieTile.getTileWidth() != null && movieTile.getTileHeight() != null && movieTile.getPoster() != null && movieTile.getRowLayout() != null ){
                if(movieTile.getRowLayout().compareToIgnoreCase("landscape")==0)
                {
                    Glide.with(viewHolder.view.getContext())
                            .load(movieTile.getPoster())
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .skipMemoryCache(true)
                            .override(Integer.parseInt(movieTile.getTileWidth()),Integer.parseInt(movieTile.getTileHeight()))
                            .error(R.drawable.movie)
                            .into(posterImageView);

                }else if(movieTile.getRowLayout().compareToIgnoreCase("square")==0 || movieTile.getRowLayout().compareToIgnoreCase("portrait") == 0)
                {
                    Glide.with(viewHolder.view.getContext())
                            .load(movieTile.getPortrait())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .override(Integer.parseInt(movieTile.getTileWidth()),Integer.parseInt(movieTile.getTileHeight()))
                            .error(R.drawable.movie)
                            .into(posterImageView);
                }

                ViewGroup.LayoutParams layoutParams = viewHolder.view.getLayoutParams();
                layoutParams.width =  Integer.parseInt(movieTile.getTileWidth());
                layoutParams.height = Integer.parseInt(movieTile.getTileHeight()) ;
                viewHolder.view.setLayoutParams(layoutParams);

            }else {
                setLayoutOfTile(movieTile,viewHolder.view.getContext(),viewHolder.view, posterImageView);
            }
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageView posterImageView;
        posterImageView = (ImageView) viewHolder.view.findViewById(R.id.posterImageView);
        posterImageView.setImageDrawable(null);
    }

    private void setLayoutOfTile(MovieTile movie, Context context, View view, ImageView imageView)
    {
        if(movie != null && movie.getRowLayout() != null && movie.getTitle() != null)
        {
            switch (movie.getRowLayout())
            {
                case"portrait" :
                {
                    int width = dpToPx(context , context.getResources().getInteger(R.integer.tilePotraitWidth));
                    int height = dpToPx(context , context.getResources().getInteger(R.integer.tilePotraitHeight));
                    loadFinal(context, movie.getPortrait(), width, height, imageView, view);
                }
                break;

                case "square":
                {
                    int width = dpToPx(context , context.getResources().getInteger(R.integer.tileSquareWidth));
                    int height = dpToPx(context , context.getResources().getInteger(R.integer.tileSquareHeight));
                    loadFinal(context, movie.getPortrait(), width, height, imageView, view);
                }
                break;

                case "landscape":
                {
                    int width = dpToPx(context , context.getResources().getInteger(R.integer.tileLandScapeWidth));
                    int height = dpToPx(context , context.getResources().getInteger(R.integer.tileLandScapeHeight));
                    loadFinal(context, movie.getPoster(), width, height, imageView, view);
                }
                break;
            }
        }
        else
        {
            loadFinal(context,
                    "",
                    dpToPx(context , context.getResources().getInteger(R.integer.defaulttileWidth)),
                    dpToPx(context , context.getResources().getInteger(R.integer.deafulttileHeight)),
                    imageView,
                    view);
        }
    }

    private void loadFinal(Context context, String url, int width, int height, ImageView targetImageView, View parentView){
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(width, height)
                .error(R.drawable.movie)
                .skipMemoryCache(true)
                .into(targetImageView);
        ViewGroup.LayoutParams layoutParams = parentView.getLayoutParams();
        layoutParams.width = width  ;
        layoutParams.height =  height;
        parentView.setLayoutParams(layoutParams);
    }

    private int dpToPx(Context ctx , int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
