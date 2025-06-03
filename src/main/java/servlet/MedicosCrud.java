package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.MedicoJpaController;
import dto.Medico;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HashPass;
import util.JwtUtil;

@WebServlet(name = "MedicosCrud", urlPatterns = {"/medicos-table"})
public class MedicosCrud extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        String authHeader = request.getHeader("Authorization");
        try ( PrintWriter out = response.getWriter()) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String token = authHeader.substring(7);
            if (JwtUtil.validarToken(token)) {
                MedicoJpaController cjc = new MedicoJpaController();
                List<Medico> clientes = cjc.findMedicoEntities();
                jsonResponse.addProperty("action", "show");
                jsonResponse.add("data", gson.toJsonTree(clientes));
            } else {
                jsonResponse.addProperty("message", "El token expiró o es invalido");
                jsonResponse.addProperty("action", "update");
            }
            out.print(gson.toJson(jsonResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            String dni = json.get("dni").getAsString();
            String nombre = json.get("nombre").getAsString();
            String appater = json.get("appa").getAsString();
            String apmater = json.get("apma").getAsString();
            String fechaJs = json.get("fecha").getAsString();
            Date fecha = Date.valueOf(fechaJs);
            String logi = json.get("logi").getAsString();
            String pass = json.get("pass").getAsString();
            String password = HashPass.hashPassword(pass);
            MedicoJpaController cjc = new MedicoJpaController();
            try {
                Medico cliente = new Medico(dni, appater, apmater, nombre, fecha, logi, password);
                cjc.create(cliente);
                jsonObject.addProperty("result", "created");
            } catch (Exception e) {
                jsonObject.addProperty("result", "error" + e);
                e.printStackTrace();
            }
            out.print(gson.toJson(jsonObject));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            int cod = json.get("cod").getAsInt();
            String dni = json.get("dni").getAsString();
            String nombre = json.get("nombre").getAsString();
            String appater = json.get("appa").getAsString();
            String apmater = json.get("apma").getAsString();
            String fechaJs = json.get("fecha").getAsString();
            Date fecha = Date.valueOf(fechaJs);
            String logi = json.get("logi").getAsString();
            MedicoJpaController cjc = new MedicoJpaController();
            try {
                Medico medico = cjc.findMedico(cod);
                medico.setNdniMedi(dni);
                medico.setAppaMedi(appater);
                medico.setApmaMedi(apmater);
                medico.setNombMedi(nombre);
                medico.setFechNaciMedi(fecha);
                medico.setLogiMedi(logi);
                cjc.edit(medico);
                jsonObject.addProperty("result", "updated");
            } catch (Exception e) {
                jsonObject.addProperty("result", "error" + e);
                e.printStackTrace();
            }
            out.print(gson.toJson(jsonObject));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            int cod = json.get("codigo").getAsInt();

            MedicoJpaController cjc = new MedicoJpaController();
            try {
                cjc.destroy(cod);
                jsonObject.addProperty("result", "deleted");
            } catch (Exception e) {
                jsonObject.addProperty("result", "error" + e);
                e.printStackTrace();
            }
            out.print(gson.toJson(jsonObject));
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
