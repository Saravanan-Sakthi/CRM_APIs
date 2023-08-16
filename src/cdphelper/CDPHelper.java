package cdphelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class CDPHelper {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("/home/local/ZOHOCORP/saravanan-12380/saro/saro_zoho/cdpcodenotes/migration/cdpmigration0323/json.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            int count = 0;
            while((line = reader.readLine()) != null) {
                line= line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
                line = line.replace("\\\"","\"");
                try {

                    JSONObject json = new JSONObject(line);

                    System.out.println(json);
                } catch (JSONException e) {
                    //System.out.println(line);
                    line = line.replace("\\\"","\"");
                    try{
                        //System.out.println("retry");
                        JSONObject json = new JSONObject(line);
                        System.out.println(json);
                    } catch (JSONException ex) {
                        //System.out.println("fail");
                    }

                    count++;
                }
                //System.out.println(json);
               // System.out.println(count);
                line = "";
            }
            System.out.println(count);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
