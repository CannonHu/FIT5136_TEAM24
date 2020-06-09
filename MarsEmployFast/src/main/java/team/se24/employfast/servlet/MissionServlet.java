package team.se24.employfast.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import team.se24.employfast.service.ConstData;
import team.se24.employfast.service.MarsEmployFast;
import team.se24.employfast.service.Mission;
import team.se24.employfast.service.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MissionServlet extends HttpServlet {
    private static MarsEmployFast employSystem = null;
    private static ObjectMapper mapper = null;
/*
    private void missionSearchUniqueByAttrs(String params) {
        if (mapper == null)
            mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(params);
            JsonNode bynode = rootNode.get("by");
            JsonNode valnode = rootNode.get("values");

            if (bynode.isArray()) {
                for (JsonNode jn : bynode) {
                    for(String s : Mission.recordKeys) {
                        if (s.equals(jn.asText())) {

                        }
                    }
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
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

        switch(reqType) {
            case "GetInfo":
                HttpSession userSession = req.getSession(true);
                Map<String, Object> infoMap = new HashMap<>();
                User user = null;
                if ((user = (User)userSession.getAttribute("user")) != null
                        && !user.getUsertype().equals(User.userType[2])) {
                    infoMap.put("state", true);
                    infoMap.put("countries", ConstData.countryNames);
                    infoMap.put("statusList", Mission.missionAllStatus);
                    infoMap.put("cargoForList", Mission.cargoForList);
                    infoMap.put("computerSkills", ConstData.computerSkillLevels);
                } else {
                    infoMap.put("state", false);
                }

                writer.write(mapper.writeValueAsString(infoMap));
                break;
            case "CreateMission":

                String missionInfo = req.getParameter("mission");
                if (missionInfo != null) {
                    Map<String, Boolean> retMap = new HashMap<>();
                    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                    mapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));
                    Mission m = mapper.readValue(missionInfo, Mission.class);
                    if (m != null) {
                        userSession = req.getSession();
                        user = null;
                        if ((user = (User)userSession.getAttribute("user")) != null
                                && !user.getUsertype().equals(User.userType[2])) {

                            if (employSystem == null)
                                employSystem = MarsEmployFast.getInstance();

                            m.setMissionId(user.getUserId());
                            m = employSystem.addMission(m);
                            if (m != null)
                                retMap.put("state", true);
                            else
                                retMap.put("state", false);
                        }

                        writer.write(mapper.writeValueAsString(retMap));
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                break;
            case "MissionNameExist": {
                String mName = req.getParameter("name");
                if (mName != null) {
                    if (employSystem == null)
                        employSystem = MarsEmployFast.getInstance();

                    Map<String, Boolean> retMap = new HashMap<>();
                    if (employSystem.getMission(mName) != null) {
                        retMap.put("exist", true);
                    } else {
                        retMap.put("exist", false);
                    }
                    writer.write(mapper.writeValueAsString(retMap));
                }
            }

                break;
            case "SearchByName": {
                String mName = req.getParameter("name");
                if (mName != null) {
                    if (employSystem == null)
                        employSystem = MarsEmployFast.getInstance();

                    Mission m = employSystem.getMission(mName);
                    if (m != null) {
                        writer.write(mapper.writeValueAsString(m));
                    }
                }
            }
                break;
        }
    }
}
