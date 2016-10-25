package my.vaadin.logaggretatorserver;

public class Administration {

    public User user = null;
    public Application application = null;
    public Company company = null;
    public UserGroup user_group = null;

    private final ServerDataBase database_connection = new ServerDataBase();
    
    public Administration(UserGroups user_group) {
        
        if (user_group.manage_users) this.user = new User();
        if (user_group.manage_applications) this.application = new Application();
        if (user_group.manage_companies) this.company = new Company();
        if (user_group.manage_groups) this.user_group = new UserGroup();
    }
    
    private class User {

    }
    
    private class Application {
        
    }
    
    private class Company {
        
    }
    
    private class UserGroup {
        
    }
}