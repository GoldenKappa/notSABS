package com.layoutxml.sabs.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.layoutxml.sabs.db.entity.BlockUrl;
import com.layoutxml.sabs.db.entity.BlockUrlProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BlockUrlUtils {
    private static final String TAG = BlockUrlUtils.class.getCanonicalName();

    @NonNull
    public static List<BlockUrl> loadBlockUrls(BlockUrlProvider blockUrlProvider) throws IOException {

        // Local host directory for conditional check
        String localhostDir = "/";

        // Create a BufferedReader object outside of the if statements
        BufferedReader bufferedReader;

        // If we are processing a local host file
        if(blockUrlProvider.url.startsWith(localhostDir))
        {
            // Create a new file object
            File localhostFile = new File(blockUrlProvider.url);

            // Create a file input stream of the host file
            FileInputStream fileInputStream = new FileInputStream(localhostFile);

            // Create a buffered reader for the local host file
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        }
        else
        {
            URL urlProviderUrl = new URL(blockUrlProvider.url);
            URLConnection connection = urlProviderUrl.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }


        List<BlockUrl> blockUrls = new ArrayList<>();

        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null) {

            // Let's run some string replacements
            inputLine = inputLine

                    // Remove 'deadzone' - We only want the domain
                    .replace("127.0.0.1", "")
                    .replace("0.0.0.0", "")

                    // Remove whitespace
                    .replaceAll("\\s","")

                    // Remove comments
                    .replaceAll("(#.*)|((\\s)+#.*)","")

                    // Remove WWW, WWW1 etc. prefix
                    .replaceAll("^(www)([0-9]{0,3})?(\\.)","")

                    .trim()
                    .toLowerCase();

            /* Move on to domain validation */

            // If we have a wildcard
            if (inputLine.contains("*")) {

                // Pass it for validation
                boolean validWildcard = BlockUrlPatternsMatch.wildcardValid(inputLine);

                // If it's not a valid wildcard
                if (!validWildcard) {
                    // Debug output
                    Log.d(TAG, "Invalid Wildcard: " + inputLine);
                    // Skip to the next block url
                    continue;
                }

                Log.i(TAG, "Wildcard found: " + inputLine);

                // Otherwise add it to the deny list
                BlockUrl blockUrl = new BlockUrl(inputLine, blockUrlProvider.id);
                blockUrls.add(blockUrl);

            // else if it's a standard domain
            } else {
                // Pass it for validation
                boolean validDomain = BlockUrlPatternsMatch.domainValid(inputLine);
                // If it's not a valid domain
                if (!validDomain) {
                    // Debug output
                    Log.d(TAG, "Invalid Domain: " + inputLine);
                    // Skip to the next block url
                    continue;
                }

                Log.i(TAG, "Domain found: " + inputLine);

                // Otherwise add it to the deny list
                BlockUrl blockUrl = new BlockUrl(inputLine, blockUrlProvider.id);
                blockUrls.add(blockUrl);
            }
        }

        bufferedReader.close();

        return blockUrls;
    }
}
