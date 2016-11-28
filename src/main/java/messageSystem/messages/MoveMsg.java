package messageSystem.messages;

import main.ApplicationContext;
import mechanics.Mechanics;
import messageSystem.Abonent;
import messageSystem.Message;
import network.ClientConnectionServer;

/**
 * Created by Klissan on 28.11.2016.
 */
public class MoveMsg extends Message {

    public MoveMsg(){
        super(Message.getMessageSystem().getService(ClientConnectionServer.class).getAddress(),
                Message.getMessageSystem().getService(Mechanics.class).getAddress());
        log.info("MoveMsg created");

    }

    @Override
    public void exec(Abonent abonent) {
        log.info("MoveMsg exec() call");
        Message.getMessageSystem().getService(Mechanics.class).move();
    }
}
