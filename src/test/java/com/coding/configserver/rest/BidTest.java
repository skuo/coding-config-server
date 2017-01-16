package com.coding.configserver.rest;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

public class BidTest {
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HHmmss";
    private static final DateTimeFormatter utcFmt = ISODateTimeFormat.dateTime().withZoneUTC();
    
    @Test
    public void testDateFormatUtils() {
        DateTime dt = new DateTime(2013, 4, 4, 10, 51, 25);
        Timestamp ts = new Timestamp(dt.getMillis());
        assertEquals("2013-04-04 105125",DateFormatUtils.format(ts.getTime(), TIMESTAMP_FORMAT));
        assertEquals("2013-04-04T17:51:25.000Z", utcFmt.print(dt));
        assertEquals("2013-04-04T17:51:25.000Z", utcFmt.print(dt.getMillis()));
    }
}
