package presenter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import model.MovieTile;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;

public class CardPresenter extends Presenter {
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private int lw, lh, pw,ph ,sw, sh;


    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sDefaultBackgroundColor = ((CloudwalkerApplication) parent.getContext().getApplicationContext()).getColor("title_non_focus");
        sSelectedBackgroundColor = ((CloudwalkerApplication) parent.getContext().getApplicationContext()).getColor("title_focus");
        ImageCardView cardView =
                new ImageCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        super.setSelected(selected);
                    }
                };

        BaseCardView.LayoutParams layoutParams = (BaseCardView.LayoutParams) cardView.findViewById(R.id.info_field).getLayoutParams();
        layoutParams.height = 10;
        cardView.findViewById(R.id.info_field).setLayoutParams(layoutParams);

        cardView.setFocusable(true);
        cardView.setCardType(ImageCardView.CARD_REGION_VISIBLE_ACTIVATED);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        loadDimens(parent.getContext());
        return new ViewHolder(cardView);
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
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        MovieTile movie = (MovieTile) item;
        if (movie.getPoster() != null) {
            setLayoutOfTile(movie, cardView.getContext(), cardView, cardView.getMainImageView());
        }
    }


    private void setLayoutOfTile(MovieTile movie, Context context, ImageCardView imageCardView, ImageView imageView) {
        if (movie != null && movie.getRowLayout() != null) {
            switch (movie.getRowLayout()) {

                case "portrait": {
                    if (movie.getTileWidth() != null && !movie.getTileWidth().isEmpty() && movie.getTileHeight() != null && !movie.getTileHeight().isEmpty()) {
                        pw = dpToPx(context, Integer.parseInt(movie.getTileWidth()));
                        ph = dpToPx(context, Integer.parseInt(movie.getTileHeight()));
                    }
                    Glide.with(context)
                            .load(movie.getPortrait())
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .override(pw, ph)
                            .error(R.drawable.movie)
                            .skipMemoryCache(true)
                            .into(imageView);

                    imageCardView.setMainImageDimensions(pw,ph);
                }
                break;

                case "square": {
                    if (movie.getTileWidth() != null && !movie.getTileWidth().isEmpty() && movie.getTileHeight() != null && !movie.getTileHeight().isEmpty()) {
                        sw = dpToPx(context, Integer.parseInt(movie.getTileWidth()));
                        sh = dpToPx(context, Integer.parseInt(movie.getTileHeight()));
                    }
                    Glide.with(context)
                            .load(movie.getPortrait())
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .override(sw, sh)
                            .error(R.drawable.movie)
                            .skipMemoryCache(true)
                            .into(imageView);

                    imageCardView.setMainImageDimensions(sw, sh);
                }
                break;

                case "landscape": {
                    if (movie.getTileWidth() != null && !movie.getTileWidth().isEmpty() && movie.getTileHeight() != null && !movie.getTileHeight().isEmpty()) {
                        lw = dpToPx(context, Integer.parseInt(movie.getTileWidth()));
                        lh = dpToPx(context, Integer.parseInt(movie.getTileHeight()));
                    }
                    Glide.with(context)
                            .load(movie.getPoster())
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .override(lw, lh)
                            .error(R.drawable.movie)
                            .skipMemoryCache(true)
                            .into(imageView);

                    imageCardView.setMainImageDimensions(lw, lh);
                }
                break;
            }
        } else {
            Glide.with(context)
                    .load(R.drawable.movie)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(true)
                    .into(imageView);

            imageCardView.setMainImageDimensions(lw, lh);
        }
    }

    private int dpToPx(Context ctx, int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
