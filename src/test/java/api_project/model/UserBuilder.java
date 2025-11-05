package api_project.model;

public class UserBuilder {
    private long id = 1;
    private String username = "defaultUser";
    private String firstName = "John";
    private String lastName = "Doe";
    private String email = "john.doe@example.com";
    private String password = "password123";
    private String phone = "1234567890";
    private long userStatus = 1;
    public UserBuilder id(long id){
        this.id=id;
        return this;
    }
    public UserBuilder username(String username){
        this.username=username;
        return this;
    }
    public UserBuilder firstName(String firstName){
        this.firstName=firstName;
        return this;
    }
    public UserBuilder lastName(String lastName){
        this.lastName=lastName;
        return this;
    }
    public UserBuilder email(String email){
        this.email=email;
        return this;
    }
    public UserBuilder password(String password){
        this.password=password;
        return this;
    }
    public UserBuilder phone(String phone){
        this.phone=phone;
        return this;
    }
    public UserBuilder userStatus(long userStatus){
        this.userStatus=userStatus;
        return this;
    }
    public User build(){
        return new User(id,username,firstName,lastName,email,password,phone,userStatus);
    }
}
