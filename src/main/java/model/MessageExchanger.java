package model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageExchanger {

    private MessageExchanger() {
    }

    public static void send(ObjectOutputStream out, Object obj) throws IOException {
        out.writeObject(new ObjectMapper().writeValueAsString(obj));
        out.flush();
    }

    public static <T> T receive(ObjectInputStream in, Class<T> clazz) {
        T obj = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            obj = mapper.readValue((String) in.readObject(), clazz);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
