package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cognoscis on 9/1/18.
 */

public class PaletteColors implements Parcelable {
    public static final Creator<PaletteColors> CREATOR = new Creator<PaletteColors>() {
        @Override
        public PaletteColors createFromParcel(Parcel source) {
            return new PaletteColors(source);
        }

        @Override
        public PaletteColors[] newArray(int size) {
            return new PaletteColors[size];
        }
    };
    private int toolbarBackgroundColor;
    private int statusBarColor;
    private int textColor;
    private int titleColor;

    public PaletteColors() {
    }

    protected PaletteColors(Parcel in) {
        this.toolbarBackgroundColor = in.readInt();
        this.statusBarColor = in.readInt();
        this.textColor = in.readInt();
        this.titleColor = in.readInt();
    }

    public int getToolbarBackgroundColor() {
        return toolbarBackgroundColor;
    }

    public PaletteColors setToolbarBackgroundColor(int toolbarBackgroundColor) {
        this.toolbarBackgroundColor = toolbarBackgroundColor;
        return this;
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public PaletteColors setStatusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public PaletteColors setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public PaletteColors setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.toolbarBackgroundColor);
        dest.writeInt(this.statusBarColor);
        dest.writeInt(this.textColor);
        dest.writeInt(this.titleColor);
    }
}
