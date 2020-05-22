package team.se24.employfast.service;

import java.net.URISyntaxException;
import java.util.HashMap;

public class MarsEmployFast {
    private UserVerify userVerify;

    public String Login(String username, String passwd, String dynamicSalt) {
        return null;
    }

    public MarsEmployFast() {
        String csvDbs[] = {"admin.csv", "coordinate.csv", "candidate.csv"};
        HashMap<String, String> csvDbMap = new HashMap<>();
        csvDbMap.put("admin", "admin.csv");
        csvDbMap.put("coordinate", "coordinate.csv");
        csvDbMap.put("candidate", "candidate.csv");
        try {
            userVerify = new UserVerify(csvDbMap);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MarsEmplyFast init ERROR");
        }
    }
}
