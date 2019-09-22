package com.officialakbarali.fabiz.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CommonInformation {
    private static int PHONE_NUMBER_LENGTH = 6;
    private static int DECIMAL_LENGTH = 3;

    public static String TruncateDecimal(String value) {
        return new BigDecimal(value)
                .setScale(DECIMAL_LENGTH, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static int GET_PHONE_NUMBER_LENGTH() {
        return PHONE_NUMBER_LENGTH;
    }

    public static int GET_DECIMAL_LENGTH() {
        return DECIMAL_LENGTH;
    }

    public static void SET_PHONE_NUMBER_LENGTH(int UP_PHONE_NUMBER_LENGTH) {
        PHONE_NUMBER_LENGTH = UP_PHONE_NUMBER_LENGTH;
    }

    public static void SET_DECIMAL_LENGTH(int UP_DECIMAL_LENGTH) {
        DECIMAL_LENGTH = UP_DECIMAL_LENGTH;
    }


}


