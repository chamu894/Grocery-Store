package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Product;
import entity.Quality;
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

@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        //main code
        //get category list from DB
        Criteria criteria1 = session.createCriteria(Category.class);
        List<Category> categoryList = criteria1.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));

        //get color list from DB
        Criteria criteria2 = session.createCriteria(Quality.class);
        List<Quality> qualityList = criteria2.list();
        jsonObject.add("qualityList", gson.toJsonTree(qualityList));

        //get product list from DB
        Criteria criteria3 = session.createCriteria(Product.class);

        //Get latest product
        criteria3.addOrder(Order.desc("id"));
        jsonObject.addProperty("allProductCount", criteria3.list().size());

        //set product range
        criteria3.setFirstResult(0);
        criteria3.setMaxResults(6);

        List<Product> productList = criteria3.list();

        //remove user from product
        for (Product product : productList) {
            product.setUser(null);
        }

        jsonObject.add("productList", gson.toJsonTree(productList));
        jsonObject.addProperty("success", true);
        //main code

        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

    }

}
