package ru.sberbank.hackathon.arrival;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static spark.Spark.*;

public class Main {
    public static int PORT = 80;
    public static String ZIP_PATH = "D:\\src";

    public static void main(String[] args) {
        port(PORT);
        staticFiles.location("/public");

        get("/all", (req, res) ->"{}");
        post("/upload", (req,res)->{
            File uploadDir = new File(ZIP_PATH);
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("File has been uploaded: "+tempFile.getFileName());
            System.out.println("name="+req.queryParams("name"));
            return "Ok";
        });

        post("/reg_srv", (req,res)->{
            System.out.println("name="+req.queryParams("name"));
            System.out.println("method="+req.queryParams("method"));
            System.out.println("url=" + req.queryParams("url"));
            System.out.println(req.queryParams("body"));
            System.out.println(req.queryParams("description"));
            return "OK";
        });

        post("/del_srv", (req,res)->{
            System.out.println("login="+req.queryParams("login"));
            System.out.println("password="+req.queryParams("password"));
            System.out.println("id="+req.queryParams("id"));
            return "OK";
        });

        /*<form action="/del_srv" method="POST">
    <h1>Добавить сервис</h1>
    Login:<input type="text" id="login" name="login"><br>
    Пароль:<input type="password" id="password" name="password"><br>
    Сервис id:<input type="text" id="id" name="id"><br>
    <input type="submit" value="Удалить">
*/
    }
}
