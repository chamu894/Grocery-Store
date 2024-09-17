package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Orders;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        HttpSession httpSession = request.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        boolean isCurrentAddress = requestJsonObject.get("isCurrentAddress").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (httpSession.getAttribute("user") != null) {

            //user signed in
            //get user from db
            User_DTO user_DTO = (User_DTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isCurrentAddress) {

                //get current address
                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {

                    //current address not found
                    responseJsonObject.addProperty("message", "Current address not found. Please create a new address");

                } else {

                    //current address found
                    //complete
                    Address address = (Address) criteria2.list().get(0);

                    //complete checkout process
                    saveOrders(session, transaction, address, user, responseJsonObject);

                }

            } else {

                //create new address
                if (first_name.isEmpty()) {

                    responseJsonObject.addProperty("message", "Please fill first name");

                } else if (last_name.isEmpty()) {

                    responseJsonObject.addProperty("message", "Please fill last name");

                } else if (!Validations.isInteger(city_id)) {

                    responseJsonObject.addProperty("message", "Invalid city");

                } else {

                    //check city from db
                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {

                        responseJsonObject.addProperty("message", "Invalid city");

                    } else {

                        //city found
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {

                            responseJsonObject.addProperty("message", "Please fill address line 1");

                        } else if (address2.isEmpty()) {

                            responseJsonObject.addProperty("message", "Please fill address line 2");

                        } else if (postal_code.isEmpty()) {

                            responseJsonObject.addProperty("message", "Please fill postal code");

                        } else if (postal_code.length() != 5) {

                            responseJsonObject.addProperty("message", "Invalid postal code");

                        } else if (!Validations.isInteger(postal_code)) {

                            responseJsonObject.addProperty("message", "Invalid postal code");

                        } else if (mobile.isEmpty()) {

                            responseJsonObject.addProperty("message", "Please fill mobile number");

                        } else if (!Validations.isMobileNumberValid(mobile)) {

                            responseJsonObject.addProperty("message", "Invalid mobile number");

                        } else {

                            //create new address
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);

                            session.save(address);

                            //complete checkout process
                            saveOrders(session, transaction, address, user, responseJsonObject);

                        }

                    }

                }

            }

        } else {

            //not signed in
            responseJsonObject.addProperty("message", "User not signed in");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

    private void saveOrders(Session session, Transaction transaction, Address address, User user, JsonObject responseJsonObject) {

        try {

            //create order to db
            Orders orders = new Orders();
            orders.setAddress(address);
            orders.setDate_time(new Date());
            orders.setUser(user);

            int order_id = (int) session.save(orders);

            //get cart items
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //get ordrer_status (1 - paid) from db
            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 5); //5 - payment pending

            //create order item in db
            double amount = 0;
            String items = "";

            for (Cart cartItem : cartList) {

                //calculate amount
                amount += cartItem.getQty() * cartItem.getProduct().getPrice();
                if (address.getCity().getId() == 1) {
                    amount += 350;
                } else {
                    amount += 500;
                }

                //get item details
                items += cartItem.getProduct().getTitle() + "x" + cartItem.getQty();

                //get product
                Product product = cartItem.getProduct();

                Order_Item order_Item = new Order_Item();
                order_Item.setOrders(orders);
                order_Item.setOrder_status(order_Status);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());

                session.save(order_Item);

                //update product qty in db
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //delete cart item in db
                session.delete(cartItem);

            }

            transaction.commit();

            //set payment data
            String merchant_id = "1227652";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String currency = "LKR";
            String merchantSecret = PayHere.generateMD5("MjE4MDA3NjQ0MDMwMDU4MDA1NDI1MzAzNDgyNjMxNjA1Mjk1NDIx");

            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchant_id);

            payhere.addProperty("return_url", "");
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", "VerifyPayments");

            payhere.addProperty("first_name", user.getFname());
            payhere.addProperty("last_name", user.getLname());
            payhere.addProperty("email", user.getEmail());
            payhere.addProperty("phone", "0773720462");
            payhere.addProperty("address", "no23, palliayawattha, pannala");
            payhere.addProperty("city", "Kurunegala");
            payhere.addProperty("country", "Sri-Lanka");
            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", currency);
            payhere.addProperty("amount", formatedAmount);
            payhere.addProperty("sandbox", true);

            //generate md5
            String md5Hash = PayHere.generateMD5(merchant_id + order_id + formatedAmount + currency + merchantSecret);
            payhere.addProperty("hash", md5Hash);

            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "Checkout Completed");

            Gson gson = new Gson();
            responseJsonObject.add("payhereJson", gson.toJsonTree(payhere));

        } catch (Exception e) {
            transaction.rollback();
        }

    }

}
