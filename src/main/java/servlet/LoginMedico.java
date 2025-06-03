package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.MedicoJpaController;
import dto.Medico;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.JwtUtil;

@WebServlet(name = "LoginMedico", urlPatterns = {"/login-medico"})
public class LoginMedico extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        try ( PrintWriter out = response.getWriter()) {
            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            String dni = json.get("dni").getAsString();
            String pass = json.get("pass").getAsString();
            MedicoJpaController cjc = new MedicoJpaController();
            Medico cliente = cjc.validar(dni, pass);
            if (cliente != null) {
                String token = JwtUtil.generarToken(cliente.getNombMedi());
                jsonObject.addProperty("result", "ok");
                jsonObject.addProperty("token", token);
            } else {
                jsonObject.addProperty("result", "not");
            }
            out.print(gson.toJson(jsonObject));
            out.flush();

        } catch (Exception e) {
            // Enviar respuesta de error en JSON para que JS pueda manejarla
            try ( PrintWriter out = response.getWriter()) {
                jsonObject.addProperty("result", "error");
                jsonObject.addProperty("message", e.getMessage());
                out.print(gson.toJson(jsonObject));
                out.flush();
            }
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
