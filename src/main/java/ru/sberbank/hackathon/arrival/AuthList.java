package ru.sberbank.hackathon.arrival;

import lombok.Data;

import java.util.HashMap;

@Data
public class AuthList {
    public HashMap<String,String> list = new HashMap<>();
}
