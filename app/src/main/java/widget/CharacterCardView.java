package widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.BaseCardView;
import model.SettingsModel;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;

public class CharacterCardView extends BaseCardView {

    public CharacterCardView(Context context) throws Exception {
        super(context, null, 0);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.character_card, this);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.findViewById(R.id.container).setBackground(((CloudwalkerApplication)context.getApplicationContext()).getDrawable("character_focuser"));
    }

    public void updateUi(Object card) throws Exception {
        TextView primaryText = findViewById(R.id.primary_text);
        final ImageView imageView = findViewById(R.id.main_image);

        if(card instanceof SettingsModel)
        {
            SettingsModel sm = (SettingsModel) card;
            primaryText.setText(sm.getSettingsName());
            if (! sm.getSettings_icon_resId().isEmpty()) {

                Bitmap bitmap = drawableToBitmap(((CloudwalkerApplication) getContext().getApplicationContext()).getDrawable(sm.getSettings_icon_resId()));
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
                drawable.setAntiAlias(true);
                String radius = ((CloudwalkerApplication)getContext().getApplicationContext()).getString("settings_orb_radius");
                double image_factor = 1;
                if(((CloudwalkerApplication)getContext().getApplicationContext()).getTvInfo().getBoard().contains("ATM30")){
                    image_factor = 1.5;
                }
                double radiusTemp = Float.parseFloat(radius) / image_factor;
                drawable.setCornerRadius((float) (Math.max(bitmap.getWidth(), bitmap.getHeight()) / radiusTemp));
                imageView.setImageDrawable(drawable);
            }
        }
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
