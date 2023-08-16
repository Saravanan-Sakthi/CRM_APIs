package crmapi;

import crmapi.connection.CRMConnection;
import org.json.JSONObject;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CRMAPIDriver {

    String abc = "{\"SigninMode\":20,\"Zid\":\"90000148990\",\"ServiceName\":\"ZohoAssist\",\"AdminZuid\":\"90000149235\",\"IP_address\":\"220.209.86.181\",\"UseCase\":2,\"AuditedTime\":1650097243109,\"CRMMarkData\":{\"Stage\":\"Do Not Chase\",\"Do_not_Mail\":true}}";
    public static Scanner scan = new Scanner(System.in);
    private static final CRMConnection crmConnection = new CRMConnection();
    public static void main(String[] args) {
        while (true){
            System.out.print("Select the operation\n1. GET\n2. INSERT\n3. UPDATE\n4. Refresh Access token\n5. Search\n6. Exit\nEnter the option : ");
            try{
                int option = scan.nextInt();
                scan.nextLine();
                if (option == 1) {
                    getRecords();
                } else if (option == 2) {
                    postRecords();
                } else if (option == 3) {
                    putRecords();
                } else if(option == 4){
                    crmConnection.refreshAccessToken();
                } else if(option == 5){
                    searchRecords();
                }else if (option == 6) {
                    System.out.println("Exiting");
                    break;
                } else {
                    System.out.println("Invalid input");
                }
            } catch (InputMismatchException e){
                System.out.println("Enter valid number\n");
                scan.next();
            }
        }
    }

    private static void searchRecords() {
        CRMAPIConstants.Modules module = getModule();
        if(module.equals(CRMAPIConstants.Modules.DEALS)) {
            StringBuilder params = new StringBuilder();
            params.append("/search?fields=Deal_Name,Zoho_Service,ORG_ID,Account_Name&criteria=");
            String zohoService = "1536471000000518261";
            StringBuilder criteria = new StringBuilder();
            criteria.append("((ORG_ID:equals:").append("sp_69827471").append(") and (Zoho_Service:equals:").append(zohoService).append("))");
            params.append(criteria.toString());
            module.setCriteria(params.toString());
            JSONObject result;
            result = crmConnection.getRecords(module, null);
            System.out.println(result);
        }
    }

    private static void putRecords() {
        CRMAPIConstants.Modules module = getModule();
        System.out.print("Enter the "+module+" id : ");
        try{
            long id = scan.nextLong();
            scan.nextLine();
            module.setId(id);
        } catch (InputMismatchException e){
            e.printStackTrace();
        }
        JSONObject params = getJSONObject();
        JSONObject response = crmConnection.putRecords(module, params);
        System.out.println(response+"/n/n");
    }

    private static void postRecords() {
        CRMAPIConstants.Modules module = getModule();
        JSONObject params = getJSONObject();
        JSONObject response = crmConnection.postRecords(module,params);
        System.out.println(response+"\n\n");
    }

    private static void getRecords() {
        CRMAPIConstants.Modules module = getModule();
        System.out.print("Select any one\n1. Get all "+module+"\n2. Get one "+module+"\n3. Exit\nEnter the option : ");
        try{
            int option = scan.nextInt();
            scan.nextLine();
            JSONObject result;
            if(option == 1){
                String modifiedSince = getModifiedSince();
                result = crmConnection.getRecords(module, modifiedSince);
                System.out.println(result+"\n\n");
            } else if (option ==2 ){
                System.out.print("Enter the "+module+" id : ");
                long id = scan.nextLong();
                scan.nextLine();
                module.setId(id);
                result = crmConnection.getRecords(module, null);
                System.out.println(result+"\n\n");
                module.setId(-1l);
            } else if (option == 3){
                System.out.println("Exiting");
            } else {
                System.out.println("Invalid input");
                getRecords();
            }
        }catch (InputMismatchException e){
            System.out.println("Enter valid number");
            scan.next();
            getRecords();
        }
    }

    private static CRMAPIConstants.Modules getModule() {
        System.out.print("Select the module\n1. Accounts\n2. Contacts\n3. Deals\n4. Exit\nEnter the option : ");
        try{
            int option = scan.nextInt();
            scan.nextLine();
            if (option == 1){
                return CRMAPIConstants.Modules.ACCOUNTS;
            } else if (option == 2){
                return CRMAPIConstants.Modules.CONTACTS;
            } else if (option == 3){
                return CRMAPIConstants.Modules.DEALS;
            } else if (option == 4){
                System.out.println("Exiting");
            } else {
                System.out.println("Invalid input");
                return getModule();
            }
        } catch (InputMismatchException e){
            System.out.println("Enter valid number\n");
            scan.next();
            return getModule();
        }
        return null;
    }

    private static JSONObject getJSONObject() {
        System.out.println("Enter keys and values in the json, type \"Exit\" to finish");
        JSONObject params = new JSONObject();
        while (true){
            System.out.print("Enter key : ");
            String key = scan.nextLine();
            if(key.equalsIgnoreCase("exit")){
                System.out.println("Constructed json");
                System.out.println(params);
                break;
            }
            System.out.print("1. Create an entry\n2. Create an inner json\nEnter the option : ");
            try{
                int option = scan.nextInt();
                scan.nextLine();
                if(option == 1){
                    System.out.print("Enter value : ");
                    String value = scan.nextLine();
                    params.put(key, value);
                } else if (option == 2){
                    params.put(key, getJSONObject());
                } else {
                    System.out.println("Invalid input - the last key was not stored");
                }
            } catch (InputMismatchException e){
                System.out.println("Enter a valid number - the last key was not stored\n");
                scan.nextLine();
            }
        }
        return params;
    }

    private static String getModifiedSince() {
        String modifiedSince ;
        while (true){
            System.out.print("\n1. Get all records\n2. Get records modified since...\nSelect any one option : ");
            int option;
            try{
                option = scan.nextInt();
                scan.nextLine();
                if(option == 1){
                    return null;
                } else if (option == 2){
                    modifiedSince = "";
                    System.out.print("Enter the year since (yyyy) : ");
                    modifiedSince=modifiedSince+scan.nextLine()+"-";
                    System.out.print("Enter the month since (mm) : ");
                    modifiedSince = modifiedSince+scan.nextLine()+"-";
                    System.out.print("Enter the day since (dd) : ");
                    modifiedSince = modifiedSince+scan.nextLine()+"T";
                    System.out.print("Enter the time in the 24 hour format \'hh:mm:ss\' :");
                    modifiedSince = modifiedSince + scan.nextLine()+"+05:30";
                    break;
                } else {
                    System.out.println("Please select any one");
                }
            } catch (InputMismatchException e){
                System.out.println("Invalid input");
            }
        }
        return modifiedSince;
    }

}
