package com.alphawallet.app.repository;

import android.net.Uri;

/**
 * Repository class to handle NBCEX integration
 */
public class NbcexRepository {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "www.nbcex.com";
    private static final String TRADE_CENTER_PATH = "TradeCenter";
    private static final String DEFAULT_TRADING_PAIR = "nbcusdt";

    /**
     * Get the URI for NBCEX trading
     *
     * @return The NBCEX trading URI
     */
    public static String getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TRADE_CENTER_PATH)
                .appendPath(DEFAULT_TRADING_PAIR);

        return builder.build().toString();
    }
}
