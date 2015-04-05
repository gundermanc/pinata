package com.pinata.android;

import android.content.Intent;

import com.pinata.client.*;

/**
 * Android Utility Functions.
 * @author Christian Gunderman
 */
public abstract class Util {

    /**
     * Bundles a user session with the specified intent.
     * This function is useful for transferring a UserSession between Android
     * Activities. Unbundle in the next Activity's onCreate method.
     * No validation is performed. This method assumes that session is valid.
     * @param session The UserSession to bundle with the intent.
     * @param intent The intent to bundle with.
     */
    public static void bundleSessionWithIntent(UserSession session, Intent intent) {
        intent.putExtra(UserSession.INTENT_EXTRA_ID,
                        session.toSessionHeader());
    }

    /**
     * Unbundles a user session from the specified intent.
     * This function is useful for transferring a UserSession between Android
     * Activities. Bundle in the calling activity. Bundled user session is not
     * validated server side until the next server call.
     * @throws ClientException If the bundled session is incorrectly formatted
     * or not present.
     * @param intent The intent to unbundle the UserSession from.
     * @return The bundled UserSession.
     */
    public static UserSession unbundleSessionFromIntent(Intent intent)
        throws ClientException {
        String sessionHeader
            = intent.getStringExtra(UserSession.INTENT_EXTRA_ID);

        if (sessionHeader == null) {
            throw new ClientException(ClientStatus.HTTP_INVALID_SESSION);
        }

        return UserSession.fromSessionHeader(sessionHeader);
    }
}
