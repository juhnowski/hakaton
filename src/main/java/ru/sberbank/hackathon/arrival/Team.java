package ru.sberbank.hackathon.arrival;

import lombok.Data;

import java.util.ArrayList;
@Data
public class Team {
    String login;
    String name;
    String password;
    ArrayList<Person> persons = new ArrayList();
}
