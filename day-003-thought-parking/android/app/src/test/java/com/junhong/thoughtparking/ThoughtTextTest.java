package com.junhong.thoughtparking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ThoughtTextTest {
    @Test
    public void normalizeTrimsAndCollapsesWhitespace() {
        assertEquals("퇴근길에 우유 사기", ThoughtText.normalize("  퇴근길에   우유 사기  "));
    }

    @Test
    public void normalizeHandlesNull() {
        assertEquals("", ThoughtText.normalize(null));
    }

    @Test
    public void normalizeLimitsLength() {
        String longText = "가".repeat(ThoughtText.MAX_LENGTH + 10);
        assertEquals(ThoughtText.MAX_LENGTH, ThoughtText.normalize(longText).length());
    }
}
