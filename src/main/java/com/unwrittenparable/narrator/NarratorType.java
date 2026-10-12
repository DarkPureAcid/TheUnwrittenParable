package com.unwrittenparable.narrator;

public enum NarratorType {
    STANLEY("Stanley", "The everyman, confused by your choices"),
    HELPFUL("Nigel", "Tries to guide you, often frustrated"),
    SARCASTIC("Malcolm", "Dry wit and passive-aggressive commentary"),
    DARK("Edgar", "Ominous, cryptic, unsettling"),
    OPTIMISTIC("Chester", "Relentlessly positive, even in failure"),
    NARCISSISTIC("Reginald", "Makes everything about themselves");

    private final String displayName;
    private final String description;

    NarratorType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}