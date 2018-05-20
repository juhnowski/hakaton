package ru.sberbank.hackathon.arrival;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static spark.Spark.*;

public class Main {
    public static int PORT = 8080;
    public static String ZIP_PATH = "E:\\src";

    public static AuthList authList = new AuthList();
    public static TeamList teamList = new TeamList();
    public static ServiceList serviceList = new ServiceList();
    public static JuryList juryList = new JuryList();

    public static void main(String[] args) {
        port(PORT);
        staticFiles.location("/public");
        init();
        get("/all_srv", (req, res) ->{
            StringBuilder sb = new StringBuilder();
            sb.append("<table width=\"100%\">");
            sb.append("<tr><th>id</th><th>Назначение</th><th>Метод</th><th>URL</th><th>Body</th><th>Примечание</th></tr>");
            System.out.println("s="+Service.class.getName());
            for(Service item : serviceList.list){

                sb.append("<tr>").append("<td>").append(item.id).append("</td>");
                sb.append("<td>").append(item.purpose).append("</td>");
                sb.append("<td>").append(item.method).append("</td>");
                sb.append("<td>").append(item.url).append("</td>");
                sb.append("<td>").append(item.body).append("</td>");
                sb.append("<td>").append(item.description).append("</td>");

                sb.append("</tr>");
            };
            sb.append("<table>");

            return Ok.getPage("СПИСОК СЕРВИСОВ",sb.toString());
        });

        get("/all_jury", (req, res) ->{
            StringBuilder sb = new StringBuilder();

                sb.append("<td><table width=\"100%\" border=\"1\"><tr><th>ФИО</th><th>Роль</th><th>Телефон</th></tr>");
                for(Person p: juryList.list) {
                    sb.append("<tr><td>").append(p.fio).append("</td>");
                    sb.append("<td>").append(p.role).append("</td>");
                    sb.append("<td>").append(p.phone).append("</td></tr>");
                }
                sb.append("</table></td></tr>");

            return Ok.getPage("ЧЛЕНЫ ЖЮРИ",sb.toString());
        });

        get("/all_cmd", (req, res) ->{
            StringBuilder sb = new StringBuilder();
            sb.append("<table width=\"100%\" border=\"1\">");
            sb.append("<tr><th>Login</th><th>Наименование</th><th>Участники</th></tr>");

            for(Team team : teamList.list){

                sb.append("<tr>").append("<td>").append(team.login).append("</td>");
                sb.append("<td>").append(team.name).append("</td>");
                sb.append("<td><table width=\"100%\" border=\"1\"><tr><th>ФИО</th><th>Роль</th><th>Телефон</th></tr>");
                for(Person p: team.persons) {
                    sb.append("<tr><td>").append(p.fio).append("</td>");
                    sb.append("<td>").append(p.role).append("</td>");
                    sb.append("<td>").append(p.phone).append("</td></tr>");
                }
                sb.append("</table></td></tr>");
            };
            sb.append("<table>");

            return Ok.getPage("СПИСОК КОМАНД",sb.toString());
        });

        post ("/reg_cmd", (req,res)->{
            String login = req.queryParams("login");
            System.out.println("fio_1="+req.queryParams("fio_1"));
            System.out.println("login=" + login);
            String password = req.queryParams("password");
            System.out.println("password=" + password);
            if((login==null)&&(password==null)){
                return Ok.getErrorPage("/reg_cmd.html","ERROR","Не заданы login, пароль");
            }

            if(checkExist(req)){
                return Ok.getErrorPage("/reg_cmd.html","ERROR","Такая команда уже зарегистрирована");
            }

            String name = req.queryParams("name");

            if (name==null){
                return Ok.getErrorPage("/reg_cmd.html","ERROR","Не задано имя команды");
            }

            Team team = new Team();
            team.name = name;
            team.password = password;
            team.login = login;


            for (int i=1; i < 4; i++){
                String fio = req.queryParams("fio_"+i);
                String role = req.queryParams("role_"+i);
                String cell = req.queryParams("cell_"+i);
                if((fio!=null)&&(role!=null)&&(cell != null)&&(fio.length()>0)){
                    Person person = new Person();
                    person.fio = fio;
                    person.role = role;
                    person.phone = cell;
                    team.persons.add(person);
                } else {
                    System.out.println(i+": fio="+fio+" role="+role+" cell="+cell);
                }
            }


            teamList.list.add(team);
            authList.list.put(login,password);
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(new File("team.json"), teamList);
                mapper.writeValue(new File("auth.json"), authList);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return Ok.getErrorPage("/reg_cmd.html","ERROR","Ошибка сохранения данных");
            }

            return Ok.getPage("OK","Команда " + name + " успешно зарегистрирована");
        });

        post("/upload", (req,res)->{
            File uploadDir = new File(ZIP_PATH);
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("File has been uploaded: "+tempFile.getFileName());

            return Ok.getPage("OK","Код загружен");
        });

        post("/reg_srv", (req,res)->{

            if (doAuth(req)){
                return Ok.getErrorPage("/reg_srv.html","ERROR","Ошибки авторизации");
            }

            Service service = new Service();
            service.login = req.queryParams("login");
            service.password = req.queryParams("password");
            service.purpose = req.queryParams("purpose");
            service.method = req.queryParams("method");
            service.url = req.queryParams("url");
            service.body = req.queryParams("body");
            service.description = req.queryParams("description");
            System.out.println("service="+service);
            int id = service.hashCode();
            service.id = id;
            serviceList.list.add(service);

            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(new File("service.json"), serviceList);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return Ok.getErrorPage("/reg_srv.html","ERROR", ioe.getMessage());
            }
            return Ok.getPage("OK", "Сервис с id="+id+" зарегистрирован");
        });

        post("/del_srv", (req,res)->{
            if(doAuth(req)){
                return Ok.getErrorPage("/del_srv.html","ERROR","Ошибки авторизации");
            }

            String strId = req.queryParams("id");
            System.out.println("id="+strId);
            try {
                int id = Integer.parseInt(strId);
                serviceList.list.remove(id);
            } catch (Exception e){
                return Ok.getErrorPage("/del_srv.html","ERROR", e.getMessage());
            }
            return Ok.getPage("OK","Сервис id="+strId + " успешно удален.") ;
        });
    }

    private static void init(){
        ObjectMapper mapper = new ObjectMapper();

        try {
            juryList = mapper.readValue(new File("jury.json"), JuryList.class);
            authList = mapper.readValue(new File("auth.json"), AuthList.class);
            teamList = mapper.readValue(new File("team.json"), TeamList.class);
            serviceList = mapper.readValue(new File("service.json"), ServiceList.class);

        } catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    private static boolean doAuth(Request req){
        String login = req.queryParams("login");
        String password = req.queryParams("password");
        if ((login!=null)&&(password!=null)&&(login.length()>0)&&(password.length()>0)){
            String pwd = authList.list.get(login);
            if ((pwd!=null)&&(pwd.equals(password))){
                return false;
            }
        }
        return true;
    }

    private static boolean checkExist(Request req){
        String login = req.queryParams("login");

        if ((login!=null)&&(login.length()>0)){
            String pwd = authList.list.get(login);
            if ((pwd!=null)){
                return true;
            }
        }
        return false;
    }
}