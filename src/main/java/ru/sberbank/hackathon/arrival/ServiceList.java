package ru.sberbank.hackathon.arrival;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ServiceList {
    ArrayList<Service> list = new ArrayList<>();
}
