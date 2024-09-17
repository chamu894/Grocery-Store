package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Entity(name="user")
@WebServlet(name = "X", urlPatterns = {"/X"})
public class X extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        @Id
        
        
    }
    }



}
