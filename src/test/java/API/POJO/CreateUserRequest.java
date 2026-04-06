package API.POJO;

public class CreateUserRequest {

    private String email;
    private String password;
    private String name;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String email, String password, String name) {
        this.email = email;
        this.name = name;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
    public String getPassword(){
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

