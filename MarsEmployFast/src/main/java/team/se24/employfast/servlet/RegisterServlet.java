package team.se24.employfast.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import team.se24.employfast.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class RegisterServlet extends HttpServlet {
    private static MarsEmployFast employSystem = null;
    private static ObjectMapper mapper = null;

    //Parse a candidate's basic information map from request and check if such candidate has existed
    private static boolean checkUserDuplicate(HttpServletRequest req) {
        Map<String, String> dcMap = new HashMap<>();
        for (String key : Candidate.basicKeys) {
            dcMap.put(key, req.getParameter(key));
        }
        if (employSystem == null)
            employSystem = MarsEmployFast.getInstance();
        return employSystem.getCandidateInfoMap(dcMap) != null;
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        if (mapper == null)
            mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        String reqType = req.getParameter("type");
        if(reqType == null) {
            System.out.println("request error");
            writer.write("request content error");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (employSystem == null)
            employSystem = MarsEmployFast.getInstance();

        switch(reqType) {
            //return the essential const selection information
            case "GetInfo":
                Map<String, String[]> map = new HashMap<>();
                map.put("countries", ConstData.countryNames);
                map.put("allergies", ConstData.allergies);
                map.put("foodPreferences", ConstData.foodPreferences);
                map.put("computerSkills", ConstData.computerSkillLevels);

                writer.write(mapper.writeValueAsString(map));
                break;
                //check if the submitted user information is duplicated
            case "DuplicateCheck":
                Map<String, Boolean> retMap = new HashMap<>();

                retMap.put("exist", checkUserDuplicate(req));

                writer.write(mapper.writeValueAsString(retMap));
                break;
                //check if the submitted username has existed
            case "UsernameCheck":
                String username = req.getParameter("username");

                Map<String, String> userCheckMap = new HashMap<>();

                try {
                    userCheckMap.put("exist", Boolean.toString(employSystem.getUser(username) != null));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                userCheckMap.put("salt", Salt.getStaticSaltVal());
                writer.write(mapper.writeValueAsString(userCheckMap));
                break;
                //sign up (add records to the file)
            case "SignUp":
                Map<String, Boolean> signUpMap = new HashMap<>();
                Map<String, String> userMap = null;
                try {
                    userMap = User.parseRequestUser(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userMap != null) {
                    switch (userMap.get(User.usertypeKey)) {
                        case "candidate":
                            Map<String, String> candidateMap = null;
                            try {
                                candidateMap = Candidate.parseRequestCandidateInfo(req);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            User u = null;
                            try {
                                u = employSystem.addUser(userMap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (u != null) {
                                candidateMap.put(User.userIdKey, Long.toString(u.getUserId()));
                                boolean added = employSystem.addCandidate(candidateMap) != null;

                                signUpMap.put("state", added);
                            }

                            break;
                    }
                }
                writer.write(mapper.writeValueAsString(signUpMap));

                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
