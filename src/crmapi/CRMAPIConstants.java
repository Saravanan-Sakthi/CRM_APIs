package crmapi;

public class CRMAPIConstants {
    public enum Method{
        GET, PUT, POST
    }
    public enum Modules{
        ACCOUNTS("https://crm.localzoho.com/crm/v2/Accounts"),
        CONTACTS("https://crm.localzoho.com/crm/v2/Contacts"),
        DEALS("https://crm.localzoho.com/crm/v2/Deals"),
        ACCESS_TOKEN("https://accounts.localzoho.com/oauth/v2/token",
                "1000.bfa68791c3b444901eff6e8490a66e49.9ebccc20f94bb6c47841967eb45fa988",
                "1000.222283DUP8AHPQMLDTACA7N0MLELDC",
                "346eeaa3a2c22bc4c1c6b04174d9d434fb51acaf44");

        long id = -1;
        String path;
        String criteria;

        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }

        public String getCriteria() {
            return criteria;
        }

        String refreshToken;
        String clientId;
        String clientSecret;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPath(){
            return path;
        }

        Modules(String path) {
            this.path = path;
        }

        Modules(String path, String refreshToken, String clientId, String clientSecret) {
            this.refreshToken = refreshToken;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.path = path+"?refresh_token="+this.refreshToken+"&client_id="+this.clientId+
                    "&client_secret="+this.clientSecret+"&grant_type=refresh_token";
        }
    }
}
