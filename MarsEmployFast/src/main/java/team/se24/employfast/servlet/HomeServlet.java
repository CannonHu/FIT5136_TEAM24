package team.se24.employfast.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import team.se24.employfast.service.MarsEmployFast;
import team.se24.employfast.service.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HomeServlet extends HttpServlet {
    private static MarsEmployFast employSystem = null;
    private static ObjectMapper mapper = null;

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
            //Check if the connection has login and return the user type and username
            case "UserInfo":
                HttpSession userSession = req.getSession(true);

                User user = null;
                if ((user = (User)userSession.getAttribute("user")) != null) {
                    Map<String, Object> retMap = new HashMap<>();
                    retMap.put(User.usertypeKey, user.getUsertype());
                    retMap.put(User.usernameKey, user.getUsername());
                    retMap.put("state", true);
                    writer.write(mapper.writeValueAsString(retMap));
                } else {
                    Map<String, Object> retMap = new HashMap<>();
                    retMap.put("state", false);
                    writer.write(mapper.writeValueAsString(retMap));
                }
                break;
                //user logout, session invalidate
            case "Logout":
                userSession = req.getSession(true);

                if ((user = (User)userSession.getAttribute("user")) != null) {
                    Map<String, Boolean> retMap = new HashMap<>();
                    retMap.put("state", true);
                    writer.write(mapper.writeValueAsString(retMap));
                }
                userSession.invalidate();
                break;
        }
    }
}
