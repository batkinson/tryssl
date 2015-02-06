package com.github.batkinson;


import org.apache.commons.cli.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public class TrySSL {

    private static void printHelp(Options opts) {
        HelpFormatter helpFormatter = new HelpFormatter();
        String usage = String.format("java %s [-C] [-H] [-h] <https-url>", TrySSL.class.getName());
        PrintWriter w = new PrintWriter(System.err);
        helpFormatter.printHelp(usage, opts);
        w.flush();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, ParseException {

        Options clOpts = new Options();
        clOpts.addOption("C", "valid-certs", false, "check certificates (off by default)");
        clOpts.addOption("H", "valid-hosts", false, "check hostnames (off by default)");
        clOpts.addOption("h", "help", false, "prints this information");
        CommandLineParser clParser = new GnuParser();

        boolean trustAllCerts = false;
        boolean trustAllHostnames = false;
        try {
            CommandLine cl = clParser.parse(clOpts, args);
            trustAllCerts = !cl.hasOption("C");
            trustAllHostnames = !cl.hasOption("H");
            if (cl.hasOption("h")) {
                printHelp(clOpts);
                return;
            }
        } catch (ParseException pe) {
            System.err.println(pe.getMessage());
            printHelp(clOpts);
            System.exit(1);
        }

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
