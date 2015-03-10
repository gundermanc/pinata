package com.pinata.service.objectmodel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.pinata.service.datatier.SQLConnection;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

/**
 * Object Model Utility functions.
 * @author Christian Gunderman
 */
public class OMUtil {

    /**
     * Checks to make sure SQLConnection is non-null.
     * @throws ApiException If value is null.
     */
    public static void sqlCheck(SQLConnection value) throws ApiException {
      if (value == null) {
            throw new ApiException(ApiStatus.NO_SQL);
        }
    }
    
    /**
     * Checks object for null value.
     * @throws ApiException with MALFORMED_REQUEST if value == null.
     */
    public static void nullCheck(Object value) throws ApiException {
        if (value == null) {
            throw new ApiException(ApiStatus.MALFORMED_REQUEST);
        }
    }

    /**
     * Checks to make sure current User is resource owner or an
     * admin.
     * @throws ApiException With ACCESS_DENIED if not resource owner
     * or admin.
     */
    public static void adminOrOwnerCheck(User current,
                                         User resourceOwner)
        throws ApiException {

        if (!current.equals(resourceOwner) &&
            !current.isRole("ROLE_ADMIN")) {
            throw new ApiException(ApiStatus.ACCESS_DENIED);
        }
    }

    /**
     * Gets sha256 hash of a string. Useful for password storage.
     * @throws ApiException With UNKNOWN_ERROR if SHA-256 isn't a supported
     * algorithm on this platform.
     * @param data The string to hash.
     * @return The hashed string.
     */
    public static String sha256(String data) throws ApiException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new ApiException(ApiStatus.UNKNOWN_ERROR, ex);
        }
    }

    /**
     * Converts an arry of bytes to a String of hex characters.
     * @param bytes Bytes to convert.
     * @return A hex string.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
    }
}
