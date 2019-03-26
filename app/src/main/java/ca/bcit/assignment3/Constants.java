/*-------------------------------------------------------------------------------------
--	SOURCE FILE: Constants.java - contains all the constants used by this program
--
--	PROGRAM:		FIND_MY_PHONE
--
--	DATE:			March 20, 2019
--
--	REVISIONS:		March 20, 2019
--
--	DESIGNER:		Jason Kim, Dasha Strigoun
--
--	PROGRAMMER:		Jason Kim, Dasha Strigoun
--
--	NOTES:  Main GUI on android
--------------------------------------------------------------------------------------*/
package ca.bcit.assignment3;

/**
 * Created by jmkim on 2019-03-12.
 */

/*-------------------------------------------------------------------------------------
--	CLASS:	        Constants
--
--	DATE:			March 20, 2019
--
--	REVISIONS:		March 20, 2019
--
--	DESIGNER:		Jason Kim
--
--	PROGRAMMER:		Jason Kim
--
--	INTERFACE:		public class Constants
--
--	NOTES:
--	Contains all the constants used in this program
--------------------------------------------------------------------------------------*/
public class Constants {
    public static final String TAG = "Debug";
    public static final String
            ACTION_LOCATION_BROADCAST = BackgroundLocationService.class.getName() + "LocationBroadcast",
            EXTRA_LATITUDE = "extra_latitude",
            EXTRA_LONGITUDE = "extra_longitude";
    public static final String SERVER_CONNECT_ADDRESS = "http://ec2-18-237-90-132.us-west-2.compute.amazonaws.com:4985";

}
