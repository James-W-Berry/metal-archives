package com.android.metal_archives;

import android.content.SearchRecentSuggestionsProvider;


public class BandSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.android.metal_archives.BandSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public BandSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}