package team.se24.employfast.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import team.se24.employfast.service.MarsEmployFast;
import team.se24.employfast.service.Salt;
import team.se24.employfast.service.UserVerify;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {
    private static MarsEmployFast employSystem = new MarsEmployFast();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        String reqType = req.getParameter("type");
        if(reqType == null) {
            System.out.println("request error");
            writer.write("request content error");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        switch(reqType) {
            case "salt": {
                HttpSession saltSession = req.getSession(true);

                Salt loginSalt = new Salt();
                saltSession.setAttribute("dynamicSalt", loginSalt.getDynamicSaltVal());
                saltSession.setMaxInactiveInterval(60);
                System.out.println(saltSession.getId());
                writer.write(mapper.writeValueAsString(loginSalt));
                break;
            }
            case "verify": {
                HttpSession saltSession = req.getSession();
                String inputUser = req.getParameter("username");
                String inputPasswd = req.getParameter("passwd");

                String dSalt = null;
                if ((dSalt = (String)saltSession.getAttribute("dynamicSalt")) != null ) {

                }
            }

        }
        writer.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
