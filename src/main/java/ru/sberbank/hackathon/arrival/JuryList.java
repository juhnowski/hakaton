package ru.sberbank.hackathon.arrival;

import lombok.Data;

import java.util.ArrayList;

@Data
public class JuryList {
    ArrayList<Person> list = new ArrayList<>();
}
