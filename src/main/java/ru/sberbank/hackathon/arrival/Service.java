package ru.sberbank.hackathon.arrival;

import lombok.Data;

@Data
public class Service {
    long id;
    String login = "";
    String password = "";
    String purpose = "";
    String method = "";
    String url = "";
    String body = "";
    String description = "";
}

