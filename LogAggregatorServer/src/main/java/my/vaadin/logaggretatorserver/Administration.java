package my.vaadin.logaggretatorserver;

import java.util.ArrayList;
import java.util.HashMap;

public class Administration {

    public User user;
    public Application application;
    public Company company;
    public UserGroup user_group;
    public ObjectCollections object_collections;

    private final ServerDataBase database_connection = new ServerDataBase();
    
    public Administration(UserGroups user_groups) {
        
        this.user = new User(user_groups);
        this.application = new Application(user_groups);
        this.company = new Company(user_groups);
        this.user_group = new UserGroup(user_groups);
        this.object_collections = new ObjectCollections(user_groups);
    }
    
    public Administration() {
        this(new UserGroups());
    }
    
    //Administration.User object start.
    public class User {
        
        public Settings settings;
        private final UserGroups user_groups;
        
        public User(UserGroups user_groups) {
            
            this.settings = new Settings();
            this.user_groups = user_groups;
        }
        
        //User settings start.
        public class Settings {
            
            public final int FIRST_NAME_MAX_LENGTH = 100;
            public final int LAST_NAME_MAX_LENGTH = 100;
            public final int EMAIL_MAX_LENGTH = 100;
            public final int USERNAME_MAX_LENGTH = 20;
            public final int PASSWORD_MAX_LENGTH = 200;
            
            public String COMPANY_ERROR_MESSAGE = null;
            public String USER_GROUP_ERROR_MESSAGE = null;
            public String FIRST_NAME_ERROR_MESSAGE = null;
            public String LAST_NAME_ERROR_MESSAGE = null;
            public String EMAIL_ERROR_MESSAGE = null;
            public String USERNAME_ERROR_MESSAGE = null;
            public String PASSWORD_ERROR_MESSAGE = null;
            
            public void clear_error_messages() {
                if (this.FIRST_NAME_ERROR_MESSAGE != null) this.FIRST_NAME_ERROR_MESSAGE = null;
                if (this.LAST_NAME_ERROR_MESSAGE != null) this.LAST_NAME_ERROR_MESSAGE = null;
                if (this.EMAIL_ERROR_MESSAGE != null) this.EMAIL_ERROR_MESSAGE = null;
                if (this.USERNAME_ERROR_MESSAGE != null) this.USERNAME_ERROR_MESSAGE = null;
                if (this.PASSWORD_ERROR_MESSAGE != null) this.PASSWORD_ERROR_MESSAGE = null;
                if (this.COMPANY_ERROR_MESSAGE != null) this.COMPANY_ERROR_MESSAGE = null;
                if (this.USER_GROUP_ERROR_MESSAGE != null) this.USER_GROUP_ERROR_MESSAGE = null;
            }
        }
        //User settings end.
        
        //Create user method start.
        public boolean create(String first_name, String last_name, String email, String username, String password, String company_id, String user_group_id) {
            if (this.user_groups.manage_users) {
                boolean proceed = true;
                database_connection.connect();
                
                if (first_name == null || first_name.length() < 1) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else if (first_name.length() > this.settings.FIRST_NAME_MAX_LENGTH) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "Your first name can't be longer than " + this.settings.FIRST_NAME_MAX_LENGTH + " characters!";
                    if (proceed) proceed = false;
                }

                if (last_name == null || last_name.length() < 1) {
                    this.settings.LAST_NAME_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else if (last_name.length() > this.settings.LAST_NAME_MAX_LENGTH) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "Your last name can't be longer than " + this.settings.LAST_NAME_MAX_LENGTH + " characters!";
                    if (proceed) proceed = false;
                }

                if (email == null || email.length() < 1) {
                    this.settings.EMAIL_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else if (email.length() > this.settings.EMAIL_MAX_LENGTH) {
                    this.settings.EMAIL_ERROR_MESSAGE = "Your email can't be longer than " + this.settings.EMAIL_MAX_LENGTH + " characters!";
                    if (proceed) proceed = false;
                }

                if (username == null || username.length() < 1) {
                    this.settings.USERNAME_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else if (username.length() > this.settings.USERNAME_MAX_LENGTH) {
                    this.settings.USERNAME_ERROR_MESSAGE = "Your username can't be longer than " + this.settings.USERNAME_MAX_LENGTH + " characters!";
                    if (proceed) proceed = false;
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("username", username);

                    String[][] select = database_connection.select(columnQuery, "user", whereQuery);

                    if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                        this.settings.USERNAME_ERROR_MESSAGE = "This username is taken.";
                        if (proceed) proceed = false;
                    }
                }

                if (password == null || password.length() < 1) {
                    this.settings.PASSWORD_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else if (password.length() > this.settings.PASSWORD_MAX_LENGTH) {
                    this.settings.PASSWORD_ERROR_MESSAGE = "Your password can't be longer than " + this.settings.PASSWORD_MAX_LENGTH + " characters!";
                    if (proceed) proceed = false;
                }

                if (company_id == null || company_id.length() < 1) {
                    this.settings.COMPANY_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("id", company_id);

                    String[][] select = database_connection.select(columnQuery, "company", whereQuery);

                    if (select == null || select.length == 0) {
                        this.settings.COMPANY_ERROR_MESSAGE = "This company doesn't exist.";
                        if (proceed) proceed = false;
                    }
                }

                if (user_group_id == null || user_group_id.length() < 1) {
                    this.settings.USER_GROUP_ERROR_MESSAGE = "This field is required!";
                    if (proceed) proceed = false;
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("id", user_group_id);

                    String[][] select = database_connection.select(columnQuery, "user_groups", whereQuery);

                    if (select == null || select.length == 0) {
                        this.settings.USER_GROUP_ERROR_MESSAGE = "This user group doesn't exist.";
                        if (proceed) proceed = false;
                    }
                }

                if (proceed) {
                    String[] columnQuery = new String[] {
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
                                DataManaging.hashString(password),
                                company_id,
                                user_group_id
                        }
                    };

                    database_connection.insert(columnQuery, values, "user");
                    database_connection.close();
                    
                    //Return true if user was successfully created.
                    return true;
                }

                database_connection.close();
            }
            //Return false if some error was thrown.
            return false;
        }
        //Create user method end.
        
        //Edit user method start.
        public boolean edit(CurrentUser user_information, String first_name, String last_name, String email, String username, String password, String company_id, String user_group_id) {
            if (this.user_groups.manage_users) {
                ArrayList<String> columns = new ArrayList<String>();
                ArrayList<Object> values = new ArrayList<Object>();
                database_connection.connect();
                
                if (first_name == null || first_name.length() < 1) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "This field is required!";
                } else if (first_name.length() > this.settings.FIRST_NAME_MAX_LENGTH) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "Your first name can't be longer than " + this.settings.FIRST_NAME_MAX_LENGTH + " characters!";
                } else if (!first_name.equals(user_information.first_name)) {
                    columns.add("first_name");
                    values.add(first_name);
                }

                if (last_name == null || last_name.length() < 1) {
                    this.settings.LAST_NAME_ERROR_MESSAGE = "This field is required!";
                } else if (last_name.length() > this.settings.LAST_NAME_MAX_LENGTH) {
                    this.settings.FIRST_NAME_ERROR_MESSAGE = "Your last name can't be longer than " + this.settings.LAST_NAME_MAX_LENGTH + " characters!";
                } else if (!last_name.equals(user_information.last_name)) {
                    columns.add("last_name");
                    values.add(last_name);
                }

                if (email == null || email.length() < 1) {
                    this.settings.EMAIL_ERROR_MESSAGE = "This field is required!";
                } else if (email.length() > this.settings.EMAIL_MAX_LENGTH) {
                    this.settings.EMAIL_ERROR_MESSAGE = "Your email can't be longer than " + this.settings.EMAIL_MAX_LENGTH + " characters!";
                } else if (!email.equals(user_information.email)) {
                    columns.add("email");
                    values.add(email);
                }

                if (username == null || username.length() < 1) {
                    this.settings.USERNAME_ERROR_MESSAGE = "This field is required!";
                } else if (username.length() > this.settings.USERNAME_MAX_LENGTH) {
                    this.settings.USERNAME_ERROR_MESSAGE = "Your username can't be longer than " + this.settings.USERNAME_MAX_LENGTH + " characters!";
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("username", username);

                    String[][] select = database_connection.select(columnQuery, "user", whereQuery);

                    if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                        this.settings.USERNAME_ERROR_MESSAGE = "This username is taken.";
                    } else {
                        columns.add("username");
                        values.add(username);
                    }
                }

                if (password == null || password.length() < 1) {
                    this.settings.PASSWORD_ERROR_MESSAGE = "This field is required!";
                } else if (password.length() > this.settings.PASSWORD_MAX_LENGTH) {
                    this.settings.PASSWORD_ERROR_MESSAGE = "Your password can't be longer than " + this.settings.PASSWORD_MAX_LENGTH + " characters!";
                } else {
                    columns.add("password");
                    values.add(password);
                }

                if (company_id == null || company_id.length() < 1) {
                    this.settings.COMPANY_ERROR_MESSAGE = "This field is required!";
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("id", company_id);

                    String[][] select = database_connection.select(columnQuery, "company", whereQuery);

                    if (select == null || select.length == 0) {
                        this.settings.COMPANY_ERROR_MESSAGE = "This company doesn't exist.";
                    } else if (!company_id.equals(user_information.company.id)) {
                        columns.add("company_id");
                        values.add(company_id);
                    }
                }

                if (user_group_id == null || user_group_id.length() < 1) {
                    this.settings.USER_GROUP_ERROR_MESSAGE = "This field is required!";
                } else {
                    String[] columnQuery = { "id" };

                    HashMap whereQuery = new HashMap();
                    whereQuery.put("id", user_group_id);

                    String[][] select = database_connection.select(columnQuery, "user_groups", whereQuery);

                    if (select == null || select.length == 0) {
                        this.settings.USER_GROUP_ERROR_MESSAGE = "This user group doesn't exist.";
                    } else if (!user_group_id.equals(user_information.user_group.id)) {
                        columns.add("user_group_id");
                        values.add(user_group_id);
                    }
                }

                if (!columns.isEmpty() && !values.isEmpty()) {

                    database_connection.update((String[]) columns.toArray(), values.toArray(), "user");
                    database_connection.close();
                    
                    //Return true if user was updated.
                    return true;
                }

                database_connection.close();
            }
            //Return false if some error was thrown.
            return false;
        }
        //Edit user method end.
    }
    //Administration.User object end.
    
    //Administration.Application object start.
    public class Application {
        
        private final UserGroups user_groups;
        
        public Application(UserGroups user_groups) {
            
            this.user_groups = user_groups;
        }
        
    }
    //Administration.Application object end.
    
    //Administration.Company object start.
    public class Company {
        
        private final UserGroups user_groups;
        
        public Company(UserGroups user_groups) {
            
            this.user_groups = user_groups;
        }
    }
    //Administration.Company object end.
    
    //Administration.UserGroup object start.
    public class UserGroup {
        
        private final UserGroups user_groups;
        
        public UserGroup(UserGroups user_groups) {
            
            this.user_groups = user_groups;
        }
    }
    //Administration.UserGroup object end.
    
    //Administration.Collections object start.
    public class ObjectCollections {
        
        private final UserGroups user_groups;
        
        public ObjectCollections(UserGroups user_groups) {
            
            this.user_groups = user_groups;
        }
        
        //CurrentUser collection begin.
        public CurrentUser[] users() {
            if (this.user_groups.manage_users) {
                
            }
            return null;
        }
        //CurrentUser collection end.
        
        //CompanyRow collection begin.
        public CompanyRow[] companies() {
            if (this.user_groups.manage_companies) {
                
                String[] columnQuery = {
                        "id",
                        "name",
                        "website",
                        "details"
                };

                database_connection.connect();
                String[][] select = database_connection.select(columnQuery, "company");
                database_connection.close();
                
                CompanyRow[] results = new CompanyRow[select.length];
                
                if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                    for (int i = 0; i < select.length; i++) {
                        results[i] = new CompanyRow(select[i][0], select[i][1], select[i][2], select[i][3]);
                    }
                    return results;
                }
            }
            return null;
        }
        //CompanyRow collection end.
        
        //UserGroups collection begin.
        public UserGroups[] user_groups() {
            if (this.user_groups.manage_groups) {
                
                String[] columnQuery = {
                        "id",
                        "name",
                        "view_logs",
                        "manage_applications",
                        "manage_users",
                        "manage_companies",
                        "manage_groups"
                };

                database_connection.connect();
                String[][] select = database_connection.select(columnQuery, "user_groups");
                database_connection.close();

                UserGroups[] results = new UserGroups[select.length];
                
                if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                    for (int i = 0; i < select.length; i++) {
                        results[i] = new UserGroups(select[i][0], select[i][1], select[i][2], select[i][3], select[i][4], select[i][5], select[i][6]);
                    }
                    return results;
                }
            }
            return null;
        }
        //UserGroups collection end.
        
        //ApplicationRow collection begin.
        public ApplicationRow[] applications() {
            if (this.user_groups.manage_applications) {
                
            }
            return null;
        }
        //ApplicationRow collection end.
    }
    //Administration.Collections object end.
}