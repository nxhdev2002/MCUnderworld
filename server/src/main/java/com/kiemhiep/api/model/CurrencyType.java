package com.kiemhiep.api.model;

/**
 * Currency types for the economy system.
 * <p>
 * Exchange rates:
 * <ul>
 *   <li>1 GOLD = 10000 SPIRIT_STONE</li>
 *   <li>1 SILVER = 100 SPIRIT_STONE</li>
 *   <li>1 SPIRIT_STONE = 1 (base currency)</li>
 * </ul>
 *
 * Currency is stored as signed numbers (long) in database to avoid Minecraft's 64-item stack limit.
 * Optional item conversion is supported for trading via currency items.
 */
public enum CurrencyType {

    /**
     * Gold - highest value currency.
     * Item ID: kiemhiep:currency_gold
     * Exchange: 1 GOLD = 10000 SPIRIT_STONE
     */
    GOLD("gold", "kiemhiep:currency_gold", 10000),

    /**
     * Silver - medium value currency.
     * Item ID: kiemhiep:currency_silver
     * Exchange: 1 SILVER = 100 SPIRIT_STONE
     */
    SILVER("silver", "kiemhiep:currency_silver", 100),

    /**
     * Spirit Stone - base currency.
     * Item ID: kiemhiep:currency_spirit_stone
     * Exchange: 1 SPIRIT_STONE = 1
     */
    SPIRIT_STONE("spirit_stone", "kiemhiep:currency_spirit_stone", 1);

    private final String name;
    private final String itemId;
    private final long exchangeRateToSpiritStone;

    CurrencyType(String name, String itemId, long exchangeRateToSpiritStone) {
        this.name = name;
        this.itemId = itemId;
        this.exchangeRateToSpiritStone = exchangeRateToSpiritStone;
    }

    public String getName() {
        return name;
    }

    public String getItemId() {
        return itemId;
    }

    public long getExchangeRateToSpiritStone() {
        return exchangeRateToSpiritStone;
    }

    /**
     * Convert this currency to spirit stone amount.
     */
    public long toSpiritStone(long amount) {
        return amount * exchangeRateToSpiritStone;
    }

    /**
     * Convert spirit stone amount to this currency.
     */
    public long fromSpiritStone(long spiritStoneAmount) {
        return spiritStoneAmount / exchangeRateToSpiritStone;
    }

    public static CurrencyType byName(String name) {
        for (CurrencyType ct : values()) {
            if (ct.getName().equals(name)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown currency type: " + name);
    }

    public static CurrencyType byItemId(String itemId) {
        for (CurrencyType ct : values()) {
            if (ct.itemId.equals(itemId)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown currency item ID: " + itemId);
    }
}
