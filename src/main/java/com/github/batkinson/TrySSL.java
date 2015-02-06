package com.github.batkinson;


import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public class TrySSL {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        if (args.length != 1) {
            System.err.printf("Usage: java %s <secure-url>", TrySSL.class.getName());
            System.exit(1);
        }

        boolean trustAllCerts = true;
        boolean trustAllHostnames = true;

        if (trustAllCerts) {
            // Install a trust manager that trusts all certificates
            TrustManager[] trustAllManager = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        if (trustAllHostnames) {
            // Install a hostname verifier that always succeeds
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }

        // Establish the connection
        URLConnection urlConn = new URL(args[0]).openConnection();

        // Dump the headers to stderr
        Map<String, List<String>> headers = urlConn.getHeaderFields();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            for (String value : header.getValue()) {
                if (header.getKey() == null)
                    System.err.println(value);
                else
                    System.err.printf("%s: %s\n", header.getKey(), value);
            }
        }

        // Dump the contents to stdout
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
