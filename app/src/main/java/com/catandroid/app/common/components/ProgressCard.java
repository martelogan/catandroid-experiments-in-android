package com.catandroid.app.common.components;

import com.catandroid.app.R;

/**
 * Created by logan on 2017-03-05.
 */

public class ProgressCard extends InventoryItem {
    /**
     * Get the string resource for a card type
     *
     * @param card
     *            the card type
     * @return the string resource
     */
    public static int getCardStringResource(ProgressCardType card) {
        switch (card) {
            //TODO: implement progress cards
            default:
                return R.string.empty_string;
        }
    }

    public enum ProgressCardType {
        TEST, TEST2
    }
}
