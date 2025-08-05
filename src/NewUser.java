public class NewUser extends User {
    private String oldPassword;

    public NewUser(String oldPassword, String username, String password, String status) {
        super(username, password, status);
        this.oldPassword = oldPassword;

    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}