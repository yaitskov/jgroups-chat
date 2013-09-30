package com.github.jgroups.chat;


import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Daneel Yaitskov
 */
public class EntryPoint extends ReceiverAdapter {

    private static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    JChannel channel;
    String userName;

    public EntryPoint(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) throws Exception {
        new EntryPoint(args[0]).start();
    }

    public void start() throws Exception {
        logger.info("for exit type: quit");
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("chat1");
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                line="[" + userName + "] " + line;
                Message msg = new Message(null, null, line);
                channel.send(msg);
            }
            catch(Exception e) {
                logger.error("failed send", e);
            }
        }
    }

    @Override
    public void receive(Message msg) {
        System.out.println("message from " + msg.getSrc()
                + ": " + msg.getObject());
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("view: " + newView);
    }
}
