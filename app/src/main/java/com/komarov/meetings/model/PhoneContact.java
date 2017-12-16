package com.komarov.meetings.model;

import com.komarov.meetings.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilia on 16.12.2017.
 */

public class PhoneContact {
    private String name;
    private List<String> phones = new ArrayList<>();

    public PhoneContact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        /*this.phones = phones.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        ;*/
        this.phones = phones;
    }

    @Override
    public String toString() {
        if (Utils.isEmpty(name)) return null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Имя: ").append(name);
        if (phones != null) {
            phones.forEach(phone -> {
                stringBuilder.append("\n Телефон: ").append(phone);
            });
        }
        return stringBuilder.toString();
    }
}
