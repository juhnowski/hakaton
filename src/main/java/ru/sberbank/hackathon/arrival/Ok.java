package ru.sberbank.hackathon.arrival;

public class Ok {
    public static String TEMPLATE= "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Title</title>\n" +
            "    <link href=\"https://fonts.googleapis.com/css?family=Russo+One\" rel=\"stylesheet\">\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: 'Russo One', sans-serif;\n" +
            "            font-size: medium;" +
            "            color:#ffffff;" +
            "            background-color: #bdc9d1;" +
            "            background: url(arrival.png) no-repeat center center fixed;\n" +
            "            -webkit-background-size: cover;\n" +
            "            -moz-background-size: cover;\n" +
            "            -o-background-size: cover;\n" +
            "            background-size: cover;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<a href=\"%s\">На главную</a><br>" +
            "<h1 align=\"center\"> %s </h1><br>" +
            "<a align=\"center\"> %s </a>" +
            "</body>\n" +
            "</html>";

    public static String getPage(String status, String msg){
        return String.format(TEMPLATE,"/index.html",status, msg);
    }

    public static String getErrorPage(String url, String status, String msg){
        return String.format(TEMPLATE,url,status, msg);
    }
}
