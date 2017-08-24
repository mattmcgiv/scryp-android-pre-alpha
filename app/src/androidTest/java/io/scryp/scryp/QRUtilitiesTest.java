package io.scryp.scryp;

import org.junit.Before;
import org.junit.Test;

import static io.scryp.scryp.QRUtilities.isQRFormattedCorrectly;
import static org.junit.Assert.*;

/**
 * Created by Matt on 8/24/2017.
 */
public class QRUtilitiesTest {
    ConfirmTransactionActivity activity;
    String wellFormedQRCode = "{\n" +
            "   \"deal\": {\n" +
            "       \"id\": \"123xyz\",\n" +
            "       \"total\": \"4.50\",\n" +
            "       \"usd_amount\": \"2.50\",\n" +
            "       \"scryp_amount\": \"2.00\",\n" +
            "       \"items\": {\n" +
            "           \"item_0\": {\n" +
            "               \"name\": \"16oz. Latte\"\n" +
            "           }\n" +
            "       }\n" +
            "   },\n" +
            "   \"recipient\": {\n" +
            "       \"id\": \"456zyx\",\n" +
            "       \"name\": \"Local Coffee Co.\"\n" +
            "   }\n" +
            "}";
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void isQRFormattedCorrectly_WellFormedInput() throws Exception {
        boolean result = isQRFormattedCorrectly(wellFormedQRCode);
        assertTrue(result);
    }

    @Test
    public void isQRFormattedCorrectly_MalformedInput() throws Exception {
        boolean result = isQRFormattedCorrectly("{\"foo\":\"bar\"}");
        assertFalse(result);
    }
}