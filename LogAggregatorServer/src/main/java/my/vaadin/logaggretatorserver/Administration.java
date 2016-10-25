package my.vaadin.logaggretatorserver;

import java.util.HashMap;

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
    
    //Administration.User object start.
    public class User {
        
        //Create user method start.
        public String create(String first_name, String last_name, String email, String username, String password, String company_id, String user_group_id) {
            if (first_name != null && last_name != null && email != null && username != null && password != null && company_id != null && user_group_id != null) {
                String[] columnQuery = { "id" };

                HashMap whereQuery = new HashMap();
                whereQuery.put("username", username);

                database_connection.connect();
                String[][] select = database_connection.select(columnQuery, "user", whereQuery);

                if (select == null || (select != null && select.length == 0)) {
                    //Username is available.
                    whereQuery.clear();
                    whereQuery.put("id", company_id);

                    select = database_connection.select(columnQuery, "company", whereQuery);

                    if (select != null && select.length == 1 && select[0].length == columnQuery.length) {
                        //Company exists.
                        whereQuery.replace("id", user_group_id);

                        select = database_connection.select(columnQuery, "user_groups", whereQuery);

                        if (select != null && select.length == 1 && select[0].length == columnQuery.length) {
                            //User group exists.
                            columnQuery = new String[] {
                                        "first_name",
                                        "last_name",
                                        "email",
                                        "username",
                                        "password",
                                        "company_id",
                                        "user_group_id"
                            };
                            
                            String[][] values = {
                                {
                                        first_name,
                                        last_name,
                                        email,
                                        username,
                                        password,
                                        company_id,
                                        user_group_id
                                }
                            };
                            
                            database_connection.insert(columnQuery, values, "user");
                            System.out.println(database_connection.error_message);
                            database_connection.close();
                            return "User successfully created.";
                        } else {
                            database_connection.close();
                            return "User group doesn't exist.";
                        }
                    } else {
                        database_connection.close();
                        return "Company doesn't exist.";
                    }
                } else {
                    database_connection.close();
                    return "Username taken.";
                }
            } else {
                return "All requred fields weren't provided.";
            }
        }
        //Create user method end.
    }
    //Administration.User object end.
    
    //Administration.Application object start.
    public class Application {
        
    }
    //Administration.Application object end.
    
    //Administration.Company object start.
    public class Company {
        
    }
    //Administration.Company object end.
    
    //Administration.UserGroup object start.
    public class UserGroup {
        
    }
    //Administration.UserGroup object end.
}