package com.nidaappdev.performancemeasurement.customObjects;

public class Achievement {

    private String name;

    private String description;
    
    private String requirements;

    private int iconResId;

    private boolean achieved;

    public Achievement(String name, String description, String requirements, int iconResId, boolean achieved) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.iconResId = iconResId;
        this.achieved = achieved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String toString() {
        return "\nName: " + getName() + "\nDescription: " + getDescription() + "\nRequirements: " + getRequirements() + "\nIcon Res: " + getIconResId();
    }
}
