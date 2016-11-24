package main;

import accountserver.AccountServer;
import mechanics.Mechanics;
import messageSystem.MessageSystem;
import network.ClientConnectionServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by apomosov on 14.05.16
 *
 * Initializes all services.
 */
public class MasterServer {
  @NotNull
  private final static Logger log = LogManager.getLogger(MasterServer.class);
  @NotNull
  private static final List<Service> services = new ArrayList<>();

    private static void loadConfig(@Nullable String path) throws Exception {
        File file;
        if (path == null) {
            //try to load from resource folder
            log.info("Loading config from resources");
            ClassLoader cl = MasterServer.class.getClassLoader();
            URL fname = cl.getResource("servercfg.ini");
            if (fname == null) throw new FileNotFoundException();
            file = new File(fname.getFile());
        } else {
            log.info("Loading config from {}", path);
            file = new File(path);
        }
        Ini ini = new Ini();
        ini.load(file);
        Map<String, String> serverCfg = ini.get("server");
        List<String> services = Arrays.asList(serverCfg.get("services").split(","));
        int accountServerPort = Integer.parseInt(serverCfg.get("accountServerPort"));
        int clientConnectionPort = Integer.parseInt(serverCfg.get("clientConnectionPort"));
        List<Class<?>> serviceClasses = services.stream()
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException e) {
                        log.error("Cannot find class for service {}", s);
                        System.exit(-1);
                        return null;
                    }
                })
                .collect(Collectors.toList());

        MessageSystem messageSystem = new MessageSystem();
        ApplicationContext.instance().put(MessageSystem.class, messageSystem);
        serviceClasses.forEach(serviceClass -> {
            if (serviceClass.equals(Mechanics.class)) {
                messageSystem.registerService(Mechanics.class, new Mechanics());
            } else if (serviceClass.equals(AccountServer.class)) {
                messageSystem.registerService(AccountServer.class, new AccountServer(accountServerPort));
            } else if (serviceClass.equals(ClientConnectionServer.class)) {
                messageSystem.registerService(ClientConnectionServer.class,
                        new ClientConnectionServer(clientConnectionPort));
            }
        });
        messageSystem.getServices().forEach(Service::start);

        Map<String, String> implementations = ini.get("implementations");
        implementations.entrySet().forEach(entry -> {
            Class<?> interfClass, implClass;
            try {
                interfClass = Class.forName(entry.getKey());
                implClass = Class.forName(entry.getValue());
                ApplicationContext.instance().put(interfClass, implClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
    }

    public static void main(@NotNull String[] args) throws Exception {
    MasterServer.start();
  }

  public static void stop() {
    services.forEach(Service::interrupt);
    services.clear();
    ApplicationContext.instance().clear();
    log.info("MasterServer stopped");
  }

    public static void start() throws Exception {
        log.info("MasterServer starting");
    MessageSystem messageSystem = new MessageSystem();
        loadConfig(null);
    for (Service service : services) {
      service.join();
    }
  }
}
