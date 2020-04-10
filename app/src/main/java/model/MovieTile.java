package model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cognoscis on 6/1/18.
 */

public class MovieTile implements Parcelable {

    public static final Creator<MovieTile> CREATOR = new Creator<MovieTile>() {
        @Override
        public MovieTile createFromParcel(Parcel source) {
            return new MovieTile(source);
        }

        @Override
        public MovieTile[] newArray(int size) {
            return new MovieTile[size];
        }
    };
    @SerializedName("tileType")
    private String tileType;
    @SerializedName("tid")
    private String tid;
    @SerializedName("title")
    private String title;
    @SerializedName("poster")
    private String poster;
    @SerializedName("background")
    private String background;
    @SerializedName("portrait")
    private String portrait;
    @SerializedName("rating")
    private float rating;
    @SerializedName("runtime")
    private String runtime;
    @SerializedName("startTime")
    private float startTime;
    @SerializedName("startIndex")
    private String startIndex;
    @SerializedName("target")
    private ArrayList<String> target;
    @SerializedName("type")
    private String type;
    @SerializedName("useAlternate")
    private boolean useAlternate;
    @SerializedName("alternateUrl")
    private String alternateUrl;
    @SerializedName("playstoreUrl")
    private String playstoreUrl;
    @SerializedName("package")
    private String packageName;
    @SerializedName("playStorePackage")
    @Expose
    private String playStorePackage;
    @SerializedName("detailPage")
    private boolean detailPage;
    @SerializedName("source")
    private String source;
    @SerializedName("genre")
    private ArrayList<String> genre;
    @SerializedName("year")
    private String year;
    @SerializedName("director")
    private ArrayList<String> director;
    @SerializedName("cast")
    private ArrayList<String> cast;
    @SerializedName("synopsis")
    private String synopsis;
    private String rowLayout;
    @SerializedName("tileWidth")
    private String tileWidth;
    @SerializedName("tileHeight")
    private String tileHeight;
    @SerializedName("showTileInfo")
    private Boolean showTileInfo;


    public Boolean isShowTileInfo() {
        return showTileInfo;
    }

    public void setShowTileInfo(boolean showTileInfo) {
        this.showTileInfo = showTileInfo;
    }

    private String tileContentText;
    private Drawable tileBadgeIcon;

    public Drawable getTileBadgeIcon() {
        return tileBadgeIcon;
    }

    public void setTileBadgeIcon(Drawable tileBadgeIcon) {
        this.tileBadgeIcon = tileBadgeIcon;
    }

    public String getTileContentText() {
        return tileContentText;
    }

    public void setTileContentText(String tileContentText) {
        this.tileContentText = tileContentText;
    }

    public String getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(String tileWidth) {
        this.tileWidth = tileWidth;
    }

    public String getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(String tileHeight) {
        this.tileHeight = tileHeight;
    }

    public MovieTile() {
    }

    public MovieTile(String tileType, String tid, String title, String poster, String background, String portrait,
                     float rating, String runtime, float startTime, String startIndex,
                     ArrayList<String> target, String type, boolean useAlternate,
                     String alternateUrl, String playstoreUrl, String packageName,
                     boolean detailPage, String source, ArrayList<String> genre, String year,
                     ArrayList<String> director, ArrayList<String> cast, String synopsis) {
        this.tileType = tileType;
        this.tid = tid;
        this.title = title;
        this.poster = poster;
        this.background = background;
        this.rating = rating;
        this.runtime = runtime;
        this.startTime = startTime;
        this.startIndex = startIndex;
        this.target = target;
        this.type = type;
        this.useAlternate = useAlternate;
        this.alternateUrl = alternateUrl;
        this.playstoreUrl = playstoreUrl;
        this.packageName = packageName;
        this.detailPage = detailPage;
        this.source = source;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.cast = cast;
        this.synopsis = synopsis;
        this.portrait = portrait;

    }


    protected MovieTile(Parcel in) {
        this.tileType = in.readString();
        this.tid = in.readString();
        this.title = in.readString();
        this.poster = in.readString();
        this.portrait = in.readString();
        this.background = in.readString();
        this.rating = in.readFloat();
        this.runtime = in.readString();
        this.startTime = in.readFloat();
        this.startIndex = in.readString();
        this.target = in.createStringArrayList();
        this.type = in.readString();
        this.useAlternate = in.readByte() != 0;
        this.alternateUrl = in.readString();
        this.playstoreUrl = in.readString();
        this.packageName = in.readString();
        this.playStorePackage = in.readString();
        this.detailPage = in.readByte() != 0;
        this.source = in.readString();
        this.genre = in.createStringArrayList();
        this.year = in.readString();
        this.director = in.createStringArrayList();
        this.cast = in.createStringArrayList();
        this.synopsis = in.readString();
    }

    public String getRowLayout() {
        return rowLayout;
    }

    public void setRowLayout(String rowLayout) {
        this.rowLayout = rowLayout;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getTileType() {
        return tileType;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public ArrayList<String> getTarget() {
        return target;
    }

    public void setTarget(ArrayList<String> target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUseAlternate() {
        return useAlternate;
    }

    public void setUseAlternate(boolean useAlternate) {
        this.useAlternate = useAlternate;
    }

    public String getAlternateUrl() {
        return alternateUrl;
    }

    public void setAlternateUrl(String alternateUrl) {
        this.alternateUrl = alternateUrl;
    }

    public String getPlaystoreUrl() {
        return playstoreUrl;
    }

    public void setPlaystoreUrl(String playstoreUrl) {
        this.playstoreUrl = playstoreUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPlayStorePackage() {
        return playStorePackage;
    }

    public void setPlayStorePackage(String playStorePackage) {
        this.playStorePackage = playStorePackage;
    }

    public boolean isDetailPage() {
        return detailPage;
    }

    public void setDetailPage(boolean detailPage) {
        this.detailPage = detailPage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ArrayList<String> getDirector() {
        return director;
    }

    public void setDirector(ArrayList<String> director) {
        this.director = director;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tileType);
        dest.writeString(tid);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(portrait);
        dest.writeString(background);
        dest.writeFloat(rating);
        dest.writeString(runtime);
        dest.writeFloat(startTime);
        dest.writeString(startIndex);
        dest.writeStringList(target);
        dest.writeString(type);
        dest.writeByte(this.useAlternate ? (byte) 1 : (byte) 0);
        dest.writeString(alternateUrl);
        dest.writeString(playstoreUrl);
        dest.writeString(packageName);
        dest.writeString(playStorePackage);
        dest.writeByte(this.detailPage ? (byte) 1 : (byte) 0);
        dest.writeString(source);
        dest.writeStringList(genre);
        dest.writeString(year);
        dest.writeStringList(director);
        dest.writeStringList(cast);
        dest.writeString(synopsis);
    }
}
