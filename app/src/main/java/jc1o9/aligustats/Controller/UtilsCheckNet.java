package com.example.jose.aligustats.Controller;

import java.net.InetAddress;

/**
 * Util class for checking if the aligulac website is down or up
 * used throughout the app when perfoming API requests during Async tasks
 *
 * @author Jose
 */
public class UtilsCheckNet {

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName(Constants.URL_ALIGULAC_WEBSITE); //Check if user can connect to aligulac

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }
}