package crmapi.connection;

import crmapi.CRMAPIConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class CRMConnection {

    private static String accessToken = "Bearer 1000.f6178c0cc795af2ba383db742266f41d.3acc8341ec89fffcd583fc03cf40d2ac";

    public CRMConnection(){
        //refreshAccessToken();
        System.out.println(accessToken);
    }

    public void refreshAccessToken(){
        HttpURLConnection urlConnection = (HttpURLConnection) getConnection(CRMAPIConstants.Modules.ACCESS_TOKEN);
        try {
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String line = bufferedReader.readLine();
            JSONObject response = null;
            if(line != null){
                response = new JSONObject(line);
            }
            System.out.println("Refresh Access Token response : "+response);
            this.accessToken = "Bearer "+response.get("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public URLConnection getConnection(CRMAPIConstants.Modules module){
        try {
            String urlPath = module.getPath();
            if(module.getId() != -1){
                urlPath = urlPath+"/"+module.getId();
            }
            if(module.getCriteria() != null && !module.getCriteria().isEmpty()) {
                urlPath+=module.getCriteria();
                module.setCriteria(null);
            }
            System.out.println("URL : "+urlPath);
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("Authorization", accessToken);
            return urlConnection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getRecords(CRMAPIConstants.Modules module, String modifiedSince) {
        HttpURLConnection urlConnection = (HttpURLConnection) getConnection(module);
        if(modifiedSince != null){
            System.out.println(modifiedSince);
            urlConnection.addRequestProperty("If-Modified-Since", modifiedSince);
        }
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            if (urlConnection.getResponseCode() < 200 || urlConnection.getResponseCode() >= 300) {
                inputStream = urlConnection.getErrorStream();
            } else {
                inputStream = urlConnection.getInputStream();
            }
            if(inputStream == null){
                System.out.println("No data available");
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = bufferedReader.readLine();
            JSONObject json = null;
            System.out.println(line);
            if(line != null){
                json = new JSONObject(line);
            }
            return json;
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                inputStream.close();
                bufferedReader.close();
                System.out.println("Connection closed\n");
            } catch (Exception e) {
            }
        }
        return null;
    }

    public JSONObject postRecords(CRMAPIConstants.Modules module, JSONObject params) {
        return connectAndGetResponse(module, params, CRMAPIConstants.Method.POST);
    }

    public JSONObject putRecords(CRMAPIConstants.Modules module, JSONObject params) {
        return connectAndGetResponse(module, params, CRMAPIConstants.Method.PUT);
    }

    public JSONObject connectAndGetResponse(CRMAPIConstants.Modules module, JSONObject params, CRMAPIConstants.Method method) {
        JSONObject response = null;
        HttpURLConnection urlConnection = (HttpURLConnection) getConnection(module);
        JSONArray recordArray = new JSONArray().put(params);
        JSONObject body = new JSONObject().put("data", recordArray);
        String bodyString = body.toString();
        urlConnection.setDoOutput(true);
        try {
            urlConnection.setRequestMethod(method.name());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            outputStream = urlConnection.getOutputStream();
            outputStream.write(bodyString.getBytes());
            outputStream.flush();
            if (urlConnection.getResponseCode() < 200 || urlConnection.getResponseCode() >= 300) {
                inputStream = urlConnection.getErrorStream();
            } else {
                inputStream = urlConnection.getInputStream();
            }
            if (inputStream != null) {
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String eachLine = bufferedReader.readLine();
                while (eachLine != null) {
                    sb.append(eachLine);
                    eachLine = bufferedReader.readLine();
                }
                response  = new JSONObject(sb.toString());
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                outputStream.close();
                inputStream.close();
                bufferedReader.close();
                System.out.println("Connection closed\n");
            } catch (Exception e) {
            }
        }
        return response;
    }
}
