package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Model;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //get request data
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //search all products
        Criteria criteria1 = session.createCriteria(Product.class);

        //add category filter
        if (requestJsonObject.has("category_name")) {

            //category selected
            String category_name = requestJsonObject.get("category_name").getAsString();

            //get category from db
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("name", category_name));
            Category category = (Category) criteria2.uniqueResult();

            //filter models by category from db
            Criteria criteria3 = session.createCriteria(Model.class);
            criteria3.add(Restrictions.eq("category", category));
            List<Model> modelList = criteria3.list();

            //filter products by model list from db
            criteria1.add(Restrictions.in("model", modelList));

        }

        //add condition filter
        if (requestJsonObject.has("quality_name")) {

            //condition selected
            String quality_name = requestJsonObject.get("quality_name").getAsString();

            //get condition from db
            Criteria criteria4 = session.createCriteria(Quality.class);
            criteria4.add(Restrictions.eq("name", quality_name));
            Quality qualityList = (Quality) criteria4.uniqueResult();

            //filter products by condition from db
            criteria1.add(Restrictions.eq("quality", qualityList));

        }


        //filter products by price from db
        Double price_range_start = requestJsonObject.get("price_range_start").getAsDouble();
        Double price_range_end = requestJsonObject.get("price_range_end").getAsDouble();

        criteria1.add(Restrictions.ge("price", price_range_start));
        criteria1.add(Restrictions.le("price", price_range_end));

        //filter products by sorting from db
        String sort_text = requestJsonObject.get("sort_text").getAsString();
        
        if (sort_text.equals("Sort by Latest")) {
            
            criteria1.addOrder(Order.desc("id"));
            
        } else if (sort_text.equals("Sort by Oldest")) {
            
            criteria1.addOrder(Order.asc("id"));
            
        } else if (sort_text.equals("Sort by Name")) {
            
            criteria1.addOrder(Order.asc("title"));
            
        } else if (sort_text.equals("Sort by Price")) {
            
            criteria1.addOrder(Order.asc("price"));
            
        }
        
        //get all product count
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());

        //set product range
        int firstResult = requestJsonObject.get("firstResult").getAsInt();
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(3);
        
        //get product list
        List<Product> productList = criteria1.list();
        
        //remove users from product list
        for (Product product : productList) {
            product.setUser(null);
        }
        
        responseJsonObject.add("productList", gson.toJsonTree(productList));
        responseJsonObject.addProperty("success", true);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

}
