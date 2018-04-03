package com.layoutxml.sabs;

import java.util.HashSet;
import java.util.Set;

public class Global {
    public static boolean BlockPort53 = true;
    public static boolean BlockPortAll = false;
    public static int BlockedUniqueUrls = 0;
    public static Set<String> domainsToExport = new HashSet<>();
    public static int RecentActivityDays = 1;
}