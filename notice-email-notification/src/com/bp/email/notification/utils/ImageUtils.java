package com.bp.email.notification.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageUtils {
	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	public byte[] recoverImageFromUrl(String urlText) throws Exception {
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }
        return output.toByteArray();
    }
	public static String encodeImageFromUrl(String urlText) throws Exception {
		
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64 x = new Base64();
        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }
        return x.encodeAsString(output.toByteArray());
    }
	public static byte[] decodeImageFromUrl(String urlText) throws Exception {
		
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64 x = new Base64();
        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }
        return x.decode(x.encodeAsString(output.toByteArray()));
    }
	
}
