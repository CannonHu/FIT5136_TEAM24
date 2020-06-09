package team.se24.employfast.service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class User {
    public static final String userIdKey = "userId";
    public static final String usernameKey = "username";
    public static final String passwdKey = "passwd";
    public static final String usertypeKey = "usertype";
    public static final String[] allKeys = {usernameKey, passwdKey, usertypeKey};

    public static final String[] userType = {"admin", "coordinator", "candidate"};

    private long userId;
    private String username;
    private String password;
    private String usertype;

    public static Map<String, String> parseRequestUser(HttpServletRequest req) throws Exception {
        Map<String, String> userMap = new HashMap<>();
        for (String key : allKeys) {
            String val = req.getParameter(key);
            if (val != null)
                userMap.put(key, val);
            else
                throw new Exception(key + " missed");
        }
        return userMap;
    }

    public static User parseUser(Map<String, String> userMap) throws Exception {
        if (userMap == null)
            return null;

        String uid = userMap.get(userIdKey);
        if (uid == null)
            throw new Exception(userIdKey + " missed");

        String un = userMap.get(usernameKey);
        if (un == null)
            throw new Exception(usernameKey + " missed");

        String pwd = userMap.get(passwdKey);
        if (pwd == null)
            throw new Exception(passwdKey + " missed");

        String type = userMap.get(usertypeKey);
        if (type == null)
            throw new Exception(usertypeKey + " missed");

        boolean typeValid = false;
        for (String t : User.userType) {
            if (t.equals(type)) {
                typeValid = true;
                break;
            }
        }
        if (!typeValid)
            throw new Exception("UserType: " + type + " is INVALID");


        User u = new User();
        u.userId = Long.parseLong(uid);
        u.username = un;
        u.password = pwd;
        u.usertype = type;
        return u;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {}

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
