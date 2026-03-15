package com.kiemhiep.core.cache;

/**
 * Key naming: kiemhiep:&lt;domain&gt;:&lt;id&gt;
 */
public final class CacheKeys {

    private static final String PREFIX = "kiemhiep";

    public static String key(String domain, String id) {
        return PREFIX + ":" + domain + ":" + id;
    }

    public static String playerByUuid(String uuid) {
        return key("player", uuid);
    }

    public static String playerById(long id) {
        return key("player", "id:" + id);
    }

    public static String cultivationByPlayer(long playerId) {
        return key("cultivation", String.valueOf(playerId));
    }

    public static String cultivationById(long id) {
        return key("cultivation", "id:" + id);
    }

    // Wallet / Economy cache keys
    public static String walletById(long id) {
        return key("wallet", "id:" + id);
    }

    public static String walletByPlayerAndCurrency(long playerId, String currency) {
        return key("wallet", playerId + ":" + currency);
    }

    public static String walletsByPlayer(long playerId) {
        return key("wallets", String.valueOf(playerId));
    }

    private CacheKeys() {}
}
