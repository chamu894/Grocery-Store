package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Category;
import entity.Measure;
import entity.Model;
import entity.Product;
import entity.Quality;
import entity.Product_Status;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ProductListing", urlPatterns = {"/ProductListing"})
public class ProductListing extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new Gson();

        String categoryId = request.getParameter("categoryId");
        String modelId = request.getParameter("modelId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String qualityId = request.getParameter("qualityId");
        String measureId = request.getParameter("measureId");
        String price = request.getParameter("price");
        String quantity = request.getParameter("quantity");

        Part image1 = request.getPart("image1");

        Session session = HibernateUtil.getSessionFactory().openSession();

        if (!Validations.isInteger(categoryId)) {
            response_DTO.setContent("Invalid Category");

        } else if (!Validations.isInteger(modelId)) {
            response_DTO.setContent("Invalid Model");

        } else if (!Validations.isInteger(qualityId)) {
            response_DTO.setContent("Invalid Quality");

        } else if (!Validations.isInteger(measureId)) {
            response_DTO.setContent("Invalid Measure");

        } else if (title.isEmpty()) {
            response_DTO.setContent("Please fill Title");

        } else if (description.isEmpty()) {
            response_DTO.setContent("Please fill Description");

        } else if (price.isEmpty()) {
            response_DTO.setContent("Please fill Price");

        } else if (!Validations.isDouble(price)) {
            response_DTO.setContent("Invalid price");

        } else if (Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Price must be greater than 0");

        } else if (quantity.isEmpty()) {
            response_DTO.setContent("Invalid Quantity");

        } else if (!Validations.isInteger(quantity)) {
            response_DTO.setContent("Invalid Quantity");

        } else if (Integer.parseInt(quantity) <= 0) {
            response_DTO.setContent("Quantity must be greater than 0");

        } else if (image1.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image1");

        } else {

            Category category = (Category) session.get(Category.class, Integer.parseInt(categoryId));

            if (category == null) {
                response_DTO.setContent("Please select a valid Category");

            } else {

                Model model = (Model) session.get(Model.class, Integer.parseInt(modelId));

                if (model == null) {
                    response_DTO.setContent("Please select a valid Model");

                } else {

                    if (model.getCategory().getId() != category.getId()) {
                        response_DTO.setContent("Please select a valid Model");

                    } else {

                        Quality quality = (Quality) session.get(Quality.class, Integer.parseInt(qualityId));

                        if (quality == null) {
                            response_DTO.setContent("Please select a valid Quality");

                        } else {

                            Measure measure = (Measure) session.get(Measure.class, Integer.parseInt(measureId));

                            if (measure == null) {
                                response_DTO.setContent("Please select a valid Measure");

                            } else {
                                //to do Insert
                                Product product = new Product();
                                product.setMeasure(measure);
                                product.setDate_time(new Date());
                                product.setDescription(description);
                                // product.setId(); auto increment
                                product.setModel(model);
                                product.setPrice(Double.parseDouble(price));

                                //get active status
                                Product_Status product_Status = (Product_Status) session.load(Product_Status.class, 1);
                                product.setProduct_Status(product_Status);

                                product.setQty(Integer.parseInt(quantity));
                                product.setQuality(quality);
                                product.setTitle(title);

                                //get user
                                User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
                                Criteria criteria = session.createCriteria(User.class);
                                criteria.add(Restrictions.eq("email", user_DTO.getEmail()));
                                User user = (User) criteria.uniqueResult();
                                product.setUser(user);

                                int pid = (int) session.save(product);
                                session.beginTransaction().commit();

                                String applicationPath = request.getServletContext().getRealPath("");
                                String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                                File folder = new File(newApplicationPath + File.separator + "product-images" + File.separator + pid);
                                folder.mkdir();

                                File file1 = new File(folder, "image1.png");
                                InputStream inputStream = image1.getInputStream();
                                Files.copy(inputStream, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                response_DTO.setSuccess(true);
                                response_DTO.setContent("Product Added Successfully!");

                            }

                        }

                    }

                }

            }

        }

        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }

}
