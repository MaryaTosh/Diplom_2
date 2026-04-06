package API.POJO;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(){}
    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
