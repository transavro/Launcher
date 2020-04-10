package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cognoscis on 6/1/18.
 */

public class MovieResponse {

    @SerializedName("rowCount")
    private int rowCount;
    @SerializedName("rows")
    private ArrayList<MovieRow> rows;

    public MovieResponse(int rowCount, ArrayList<MovieRow> rows) {
        this.rowCount = rowCount;
        this.rows = rows;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public ArrayList<MovieRow> getRows() {
        return rows;
    }

    public void setRows(ArrayList<MovieRow> rows) {
        this.rows = rows;
    }
}
