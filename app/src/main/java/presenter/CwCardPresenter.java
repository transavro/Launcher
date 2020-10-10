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


public class CwCardPresenter extends Presenter {

    private int lw, lh, pw,ph ,sw, sh;
    private double image_factor = 1;

    public CwCardPresenter() {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageView v = new ImageView(parent.getContext());
        try {
            v.setBackground(((CloudwalkerApplication)parent.getContext().getApplicationContext()).getDrawable("tile_focuser"));
            loadDimens(parent.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        v.setScaleType(ImageView.ScaleType.FIT_XY);
        v.setPadding(5, 5, 5, 5);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        return new ViewHolder(v);
    }




    private void loadDimens(Context context) throws Exception {
        image_factor = 1;
//        if(((CloudwalkerApplication)context.getApplicationContext()).getTvInfo().getBoard().contains("ATM30")){
//            image_factor = 1.5;
//        }

        lw = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileLandScapeWidth")) / image_factor);
        lh = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileLandScapeHeight")) / image_factor);

        pw = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tilePotraitWidth")) / image_factor);
        ph = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tilePotraitHeight")) / image_factor);

        sw = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileSquareWidth")) / image_factor);
        sh = (int) (dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileSquareHeight")) / image_factor);
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
                try {
                    loadFinal(viewHolder.view.getContext(),
                            imageUrl,
                            (int)(Integer.parseInt(movieTile.getTileWidth())/ image_factor),
                            (int)(Integer.parseInt(movieTile.getTileHeight())/ image_factor),
                            posterImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    setLayoutOfTile(movieTile,viewHolder.view.getContext(), posterImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageView posterImageView = (ImageView) viewHolder.view;
        posterImageView.setImageDrawable(null);
    }

    private void setLayoutOfTile(MovieTile movie, Context context, ImageView imageView) throws Exception {
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

    private void loadFinal(Context context, String url, int width, int height, ImageView targetImageView) throws Exception {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(width, height)
                .error(((CloudwalkerApplication)context.getApplicationContext()).getDrawable("placeholder_logo"))
                .centerCrop()
                .dontAnimate()
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