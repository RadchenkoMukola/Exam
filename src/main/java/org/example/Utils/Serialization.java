package org.example.Utils;

import java.io.*;
import java.util.Base64;

public class Serialization {
    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(bytes);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}
