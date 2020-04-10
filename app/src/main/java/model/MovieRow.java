package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cognoscis on 6/1/18.
 */

public class MovieRow {

    @SerializedName("rowIndex")
    private int rowIndex;
    @SerializedName("rowHeader")
    private String rowHeader;
    @SerializedName("rowLayout")
    private String rowLayout;
    @SerializedName("rowItems")
    private ArrayList<MovieTile> rowItems;

    public MovieRow(int rowIndex, String rowHeader, String rowLayout, ArrayList<MovieTile> rowItems) {
        this.rowIndex = rowIndex;
        this.rowHeader = rowHeader;
        this.rowItems = rowItems;
        this.rowLayout = rowLayout;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getRowHeader() {
        return rowHeader;
    }

    public void setRowHeader(String rowHeader) {
        this.rowHeader = rowHeader;
    }

    public ArrayList<MovieTile> getRowItems() {
        return rowItems;
    }

    public void setRowItems(ArrayList<MovieTile> rowItems) {
        this.rowItems = rowItems;
    }

    public String getRowLayout() {
        return rowLayout;
    }

    public void setRowLayout(String rowLayout) {
        this.rowLayout = rowLayout;
    }
}
