package messageSystem.messages;

import main.ApplicationContext;
import mechanics.Mechanics;
import messageSystem.Abonent;
import messageSystem.Address;
import messageSystem.Message;
import messageSystem.MessageSystem;
import network.ClientConnectionServer;

/**
 * Created by Klissan on 28.11.2016.
 */
public class EjectMassMsg extends Message {

    public EjectMassMsg(){
        super(Message.getMessageSystem().getService(ClientConnectionServer.class).getAddress(),
                Message.getMessageSystem().getService(Mechanics.class).getAddress());
        log.info("Eject mass msg created");

    }

    @Override
    public void exec(Abonent abonent) {
        log.info("EjectMassMsg exec() call");
        Message.getMessageSystem().getService(Mechanics.class).ejectMass();
    }
}
