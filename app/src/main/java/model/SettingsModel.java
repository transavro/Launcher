package model;

public class SettingsModel {

    private String settingsName;
    private String action;
    private String settings_icon_resId;



    public SettingsModel(String settingsName, String settings_icon_resId, String action) {
        this.settingsName = settingsName;
        this.action = action;
        this.settings_icon_resId = settings_icon_resId;
    }

    public String getSettingsName() {
        return settingsName;
    }

    public String getAction() {
        return action;
    }

    public String getSettings_icon_resId() {
        return settings_icon_resId;
    }
}
