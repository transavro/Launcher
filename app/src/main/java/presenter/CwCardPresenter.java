package presenter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.leanback.widget.Presenter;
import model.MovieTile;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;


public class CwCardPresenter extends Presenter {

    private int lw, lh, pw,ph ,sw, sh;

    public CwCardPresenter(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageView v = new ImageView(parent.getContext());
        v.setBackground(((CloudwalkerApplication)parent.getContext().getApplicationContext()).getDrawable("focus_on_select_bg"));
        v.setPadding(4,4,4,4);
        loadDimens(parent.getContext());
        return new ViewHolder(v);
    }

    private void loadDimens(Context context){
        lw = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileLandScapeWidth"));
        lh = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileLandScapeHeight"));

        pw = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tilePotraitWidth"));
        ph = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tilePotraitHeight"));

        sw = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileSquareWidth"));
        sh = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileSquareHeight"));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final ImageView posterImageView = (ImageView) viewHolder.view;

        if(item instanceof MovieTile) {
            final MovieTile movieTile = (MovieTile) item;
            if(movieTile.getTileWidth() != null && movieTile.getTileHeight() != null && movieTile.getPoster() != null && movieTile.getRowLayout() != null ){
                String imageUrl = "";
                if(movieTile.getRowLayout().compareToIgnoreCase("landscape")==0){
                    imageUrl = movieTile.getPoster();
                }else if(movieTile.getRowLayout().compareToIgnoreCase("square")==0 || movieTile.getRowLayout().compareToIgnoreCase("portrait") == 0){
                    imageUrl  = movieTile.getPortrait();
                }
                loadFinal(viewHolder.view.getContext(),
                        imageUrl,
                        Integer.parseInt(movieTile.getTileWidth()),
                        Integer.parseInt(movieTile.getTileHeight()),
                        posterImageView);
            }else {
                setLayoutOfTile(movieTile,viewHolder.view.getContext(), posterImageView);
            }
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageView posterImageView = (ImageView) viewHolder.view;
        posterImageView.setImageDrawable(null);
    }

    private void setLayoutOfTile(MovieTile movie, Context context, ImageView imageView)
    {
        if(movie != null && movie.getRowLayout() != null && movie.getTitle() != null)
        {
            switch (movie.getRowLayout())
            {
                case"portrait" :
                {
                    loadFinal(context, movie.getPortrait(), pw, ph, imageView);
                }
                break;

                case "square":
                {
                    loadFinal(context, movie.getPortrait(), sw, sh, imageView);
                }
                break;

                case "landscape":
                {
                    loadFinal(context, movie.getPoster(), lw, lh, imageView);
                }
                break;
            }
        }
        else
        {
            loadFinal(context,
                    "",
                    lw,
                    lh,
                    imageView);
        }
    }

    private void loadFinal(Context context, String url, int width, int height, ImageView targetImageView){
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(width, height)
                .error(R.drawable.movie)
                .skipMemoryCache(true)
                .into(targetImageView);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        targetImageView.setLayoutParams(layoutParams);
    }

    private int dpToPx(Context ctx , int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}