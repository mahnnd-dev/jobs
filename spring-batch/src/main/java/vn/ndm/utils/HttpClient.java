package vn.ndm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: ndSenses
 * Date: 6/2/2021
 * Time: 4:34 PM
 * Project: adapter-vasp
 **/
public class HttpClient {
    public static String sendPostSOAP(String request, String urlApi, int timeout) throws IOException {
        String v = "";
        StringBuilder response = new StringBuilder();
        HttpURLConnection httpConn = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        OutputStream out = null;

        try{
            URL url = new URL(urlApi);
            URLConnection connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;
            httpConn.setConnectTimeout(timeout);
            httpConn.setChunkedStreamingMode(0);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Length", String.valueOf(request.length()));
            httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            httpConn.setRequestProperty("SOAPAction", "");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = httpConn.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            out.close();
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);
            String value = null;

            while ((value = in.readLine()) != null) {
                response.append(value);
            }
            in.close();
            v = response.toString();
            return v;

        } finally {
            isDisconnect(httpConn, isr, in, out);
        }
    }
    public static String sendPostJSON(String request, String urlApi, int timeout) throws IOException {
        String v = "";
        StringBuilder response = new StringBuilder();
        HttpURLConnection httpConn = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        OutputStream out = null;

        try{
            URL url = new URL(urlApi);
            URLConnection connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;
            httpConn.setConnectTimeout(timeout);
            httpConn.setChunkedStreamingMode(0);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Length", String.valueOf(request.length()));
            httpConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = httpConn.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            out.close();
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);
            String value = null;

            while ((value = in.readLine()) != null) {
                response.append(value);
            }
            in.close();
            v = response.toString();
            return v;

        } finally {
            isDisconnect(httpConn, isr, in, out);
        }
    }

    private static void isDisconnect(HttpURLConnection httpConn, InputStreamReader isr, BufferedReader in, OutputStream out) {
        try{
            if (httpConn != null)
                httpConn.disconnect();
        } catch (Exception var27) {
            var27.printStackTrace();
        }
        try{
            if (out != null)
                out.close();
        } catch (IOException var26) {
            var26.printStackTrace();
        }
        try{
            if (isr != null)
                isr.close();
        } catch (IOException var25) {
            var25.printStackTrace();
        }
        try{
            if (in != null)
                in.close();
        } catch (IOException var24) {
            var24.printStackTrace();
        }
    }
}
