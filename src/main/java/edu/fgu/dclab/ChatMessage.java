package edu.fgu.dclab;
import java.util.Date;

public class ChatMessage extends AbstractMessage {
    public final String MESSAGE;
    Date date=new Date();

    public ChatMessage(String source, String message) {
        this.source = source;
        this.MESSAGE = message;
        }

    public int getType() {

        if(this.MESSAGE.equals("time")||this.MESSAGE.equals("time?")){
            return  Message.TIME;
        }
        else {
            return Message.CHAT;
        }
        //return 0;
    }
}
