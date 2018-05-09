package edu.fgu.dclab;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Servant implements Runnable {
    private ObjectOutputStream out = null;
    private String source = null;

    private Socket socket = null;

    private ChatRoom room = null;
    Date date=new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateString = sdf.format(date);

    public Servant(Socket socket, ChatRoom room) {
        this.room = room;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(
                this.socket.getOutputStream()
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        greet();
    }

    public void process(Message message) {
        switch (message.getType()) {
            case Message.ROOM_STATE:
                this.write(message);
                break;

            case Message.CHAT:
                this.write(message);
                break;

            case Message.LOGIN:
                if (this.source == null) {
                    this.source = ((LoginMessage) message).ID;
                    this.room.multicast(new ChatMessage(
                        "MurMur",
                        MessageFormat.format("{0} 進入了聊天室。", this.source)
                    ));

                    this.room.multicast(new RoomMessage(
                        room.getRoomNumber(),
                        room.getNumberOfGuests()
                    ));

                }

                break;

                case  Message.TIME:
                    if(this.source.equals(message.getSource())) {
                        String[] question = {dateString};

                        for (String time : question ) {
                            write(new ChatMessage("現在時間", time));
                        }
                    }
                    //this.write(new ChatMessage("現在時間:",dateString));
                   break;

            default:
        }
    }

    private void write(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greet() {
        String[] greetings = {
            "歡迎來到 MurMur 聊天室",
            "請問你的【暱稱】?"
        };

        for (String msg : greetings) {
            write(new ChatMessage("MurMur", msg));
        }
    }

    @Override
    public void run() {
        Message message;
        //System.out.println(this.source);
        try (
            ObjectInputStream in = new ObjectInputStream(
                this.socket.getInputStream()
            )
        ) {
            this.process((Message)in.readObject());

            while ((message = (Message) in.readObject()) != null) {
                this.room.multicast(message);
            }

            this.out.close();
        }
        catch (IOException e) {
            System.out.println("Servant: I/O Exc eption");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// Servant.java