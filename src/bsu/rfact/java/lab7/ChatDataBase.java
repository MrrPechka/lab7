package bsu.rfact.java.lab7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ChatDataBase {
    private ArrayList<User> users = new ArrayList<>(10);

    public ChatDataBase() {
        openData();
    }

    private void openData() {
        try {
            File file = new File("src/resources/data.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String name = line;
                line = reader.readLine();
                String address = line;
                users.add(new User(name, address));
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ChatDataBase plusUser(String name, String address) {
        User user = new User(name, address);
        users.add(user);
        return this;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
