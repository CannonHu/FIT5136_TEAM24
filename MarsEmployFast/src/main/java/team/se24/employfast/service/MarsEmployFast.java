package team.se24.employfast.service;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MarsEmployFast {
    private static MarsEmployFast INSTANCE = null;
    private UsersManage userManage = null;
    private CandidatesManage candidatesManage = null;
    private MissionManage missionManage = null;

    private int flushCnt = 0;

    //Single Instance
    public static MarsEmployFast getInstance() {
        if (INSTANCE == null)
            synchronized(MarsEmployFast.class) {
                if (INSTANCE == null)
                    INSTANCE = new MarsEmployFast();
            }
        return INSTANCE;
    }

    /*Refresh the data stored in files in order of a cycle. It is designed for the scheduled task. It will flush one lnid of data at a time*/
    public synchronized void flush() {
        int index = flushCnt % 3;
        switch(index) {
            case 0:
                userManage.flush();
                break;
            case 1:
                candidatesManage.flush();
                break;
            case 2:
                missionManage.flush();
                break;
        }
        flushCnt++;
    }

    /*refresh all the files*/
    public void flushAll() {
        userManage.flush();
        candidatesManage.flush();
        missionManage.flush();
    }

    /*Get the whole map record of a candidate's personal information based on a given map which contains the required keys and values
    * @params Map<String, String> infoMap: contains the required values. The searched result must contain the keys and values the the provided map.
    *                                     It is typically provided when a user fill in the basic information of a candidate during register.
    * @return Map<String, String> the matching record in the file
    * */
    public Map<String, String> getCandidateInfoMap(Map<String, String> infoMap) {
        return candidatesManage.getCandidateInfoMap(infoMap);
    }

    /*Get the User by username
     * @Params String username: the username of the target user
     * @return the matching user with the given username. If not exists return null*/
    public User getUser(String username) throws Exception {
        return userManage.getUserByUsername(username);
    }

    /*Verify the user login.
    * @Params String username: the given username
    *         String hashed: the given hashed password. It is finally hashed with the static and dynamic salt.
    *         String dynamixSalt: the corresponding dynamic salt for hash. The hashed password stored in the file is only hashed with the static salt. To complete the verify process, the dynamic salt is essential
    * @return Map<String, Object> there will be two kinds of (key, value).
    *       1. ("state", Integer), represents the authentication result of the login, the integer is the state code.
    *       2. ("user", User), returns the login user object when it is verified successfully*/
    public Map<String, Object> Login(String username, String hashed, String dynamicSalt) throws Exception {
        Map<String, Object> loginMap = new HashMap<>();
        User user = getUser(username);
        if (user == null) {
            loginMap.put("state", Integer.toString(UsersManage.usernameNotExist));
        } else if (!hashed.equals(Salt.sha256Hash(user.getPassword() + dynamicSalt))) {
            loginMap.put("state", Integer.toString(UsersManage.wrongPassword));
        } else {
            loginMap.put("state", Integer.toString(UsersManage.login));
            loginMap.put("user", user);
        }

        return loginMap;
    }

    /*Add a new user to the user db. A proper userId will be generated automatically.
     * @Params Map<String, String> userMap: a map with username and hashed password (The key should be correctly set). Typically dome from the submit ajax from front-end
     * @return the cooresponding User object. If the given map is invalid, it will return null*/
    public User addUser(Map<String, String> userMap) throws Exception {
        return userManage.addUser(userMap);
    }

    /*Add the given map of candidate to the csv file.
     * @params Map<String, String> candidateMap : a complete map of candidate containing all essential keys and values
     * @return Candidate a converted Candidate object from the given map. If the map is invalid, it will return null and the record is not added*/
    public Candidate addCandidate(Map<String, String> candMap) {
        return candidatesManage.addCandidate(candMap);
    }

    /* Get the mission record by name
     * @params String missionName: provide the target mission record's missionName
     * @return the record stored in the csv file*/
    public Mission getMission(String missionName) {
        Map<String, String> missionMap = missionManage.getMissionMap(missionName);
        Mission m = null;
        try {
            m = Mission.parseMap(missionMap);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return m;
    }

    /*Add a mission record to the csv file
     * @params Mission m : a Mission object. It will convert to a proper map record to add to the file.
     * @return if the given object is properly set it will return the given mission, otherwise will return null*/
    public Mission addMission(Mission m) {
        Mission newMission = null;
        try {
            newMission = missionManage.addMission(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMission;
    }

    private MarsEmployFast() {
        try {
            userManage = UsersManage.getInstance();
            candidatesManage = CandidatesManage.getInstance();
            missionManage = MissionManage.getInstance();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
