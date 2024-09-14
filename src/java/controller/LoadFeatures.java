package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Measure;
import entity.Quality;
import entity.Model;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@WebServlet(name = "LoadFeatures", urlPatterns = {"/LoadFeatures"})
public class LoadFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list();

        Criteria criteria2 = session.createCriteria(Model.class);
        criteria2.addOrder(Order.asc("name"));
        List<Model> modelList = criteria2.list();

        Criteria criteria3 = session.createCriteria(Measure.class);
        criteria3.addOrder(Order.asc("name"));
        List<Measure> measureList = criteria3.list();

        Criteria criteria4 = session.createCriteria(Quality.class);
        criteria4.addOrder(Order.asc("id"));
        List<Quality> qualityList = criteria4.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("modelList", gson.toJsonTree(modelList));
        jsonObject.add("measureList", gson.toJsonTree(measureList));
        jsonObject.add("qualityList", gson.toJsonTree(qualityList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

        session.close();

    }

}
