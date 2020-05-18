package team.se24.employfast.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import team.se24.employfast.service.Salt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {
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
            case "salt":
                System.out.println("Get Salt Request");
                Salt loginSalt = new Salt();
                writer.write(mapper.writeValueAsString(loginSalt));
                break;
            case "verify":
                String user = req.getParameter("username");
                String hashed = req.getParameter("hashed");

                
                break;
        }
        writer.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
