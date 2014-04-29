package com.aimprosoft.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String message;
    private Date date;

    public Message(String username, String message, Date date2){
        this.message = message;
        this.username = username;
        this.date = date2;
    }

    public String getName(){
        return username;
    }

    public String getMessage(){
        return message;
    }

    public Date gerDate(){
        return date;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        bos.close();
        byte [] data = bos.toByteArray();
        return data;
    }
}
