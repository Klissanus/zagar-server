package messageSystem.messages;

import main.ApplicationContext;
import mechanics.Mechanics;
import messageSystem.Abonent;
import messageSystem.Message;
import network.ClientConnectionServer;

/**
 * Created by Klissan on 28.11.2016.
 */
public class SplitMsg extends Message {

    public SplitMsg(){
        super(Message.getMessageSystem().getService(ClientConnectionServer.class).getAddress(),
                Message.getMessageSystem().getService(Mechanics.class).getAddress());
        log.info("SplitMsg created");

    }

    @Override
    public void exec(Abonent abonent) {
        log.info("SplitMsg exec() call");
        ApplicationContext.instance().get(Mechanics.class).split();
    }
}
