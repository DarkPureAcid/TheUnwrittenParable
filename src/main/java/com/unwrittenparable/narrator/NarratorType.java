package com.unwrittenparable.narrator;

public enum NarratorType {
    STANLEY("Stanley", "The everyman, confused by your choices"),
    HELPFUL("The Helpful Narrator", "Tries to guide you, often frustrated"),
    SARCASTIC("The Sarcastic Narrator", "Dry wit and passive-aggressive commentary"),
    DARK("The Dark Narrator", "Ominous, cryptic, unsettling"),
    OPTIMISTIC("The Optimistic Narrator", "Relentlessly positive, even in failure"),
    NARCISSISTIC("The Narcissistic Narrator", "Makes everything about themselves");

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