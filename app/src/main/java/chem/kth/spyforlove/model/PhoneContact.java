package chem.kth.spyforlove.model;

import java.util.List;

public class PhoneContact {
    private String name;
    private List<String> phones;

    public PhoneContact() {
    }

    public PhoneContact(String name, List<String> phones) {
        this.name = name;
        this.phones = phones;
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
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "PhoneContact{" +
                "name='" + name + '\'' +
                ", phones=" + phones +
                '}';
    }
}
