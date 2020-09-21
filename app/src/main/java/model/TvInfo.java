package model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TvInfo {

    @SerializedName("emac")
    @Expose
    private String emac;
    @SerializedName("wmac")
    @Expose
    private String wmac;
    @SerializedName("panel")
    @Expose
    private String panel;
    @SerializedName("board")
    @Expose
    private String board;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("cota")
    @Expose
    private String cota;
    @SerializedName("fota")
    @Expose
    private String fota;



    @SerializedName("brand")
    @Expose
    private String brand;

    public String getEmac() {
        return emac;
    }

    public void setEmac(String emac) {
        this.emac = emac;
    }

    public String getWmac() {
        return wmac;
    }

    public void setWmac(String wmac) {
        this.wmac = wmac;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCota() {
        return cota;
    }

    public void setCota(String cota) {
        this.cota = cota;
    }

    public String getFota() {
        return fota;
    }

    public void setFota(String fota) {
        this.fota = fota;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

}