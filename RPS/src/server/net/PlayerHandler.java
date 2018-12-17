package server.net;

import common.MessageException;
import common.MessageSplitter;
import java.io.IOException;
import java.util.StringJoiner;

import common.MsgType;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import server.controller.Controller;


/**
 *
 * @author yuchen
 */
public class PlayerHandler {
    private final HangmanServer server;
    private final SocketChannel playerChannel;
    private final Controller contr;
    
    private String username = "anonymous";
    public int totalScore = 0;
    public int roundScore = 0;
    private final MessageSplitter msgSplitter = new MessageSplitter();
    private final ByteBuffer msgFromClient = ByteBuffer.allocateDirect(2018);
    
    PlayerHandler(HangmanServer server, SocketChannel playerChannel, Controller contr) {
        this.server = server;
        this.playerChannel = playerChannel;
        this.contr = contr;
    }
    
    public void handlePlayerRequest() {
        while (msgSplitter.hasNext()) {
            Message msg = new Message(msgSplitter.nextMsg());
            switch(msg.msgType) {
                case USER:
                    username = msg.msgBody;
                    server.broadcast(msg.msgType + "##" + username);
                    break;

                case JOIN:
                    //guess = msg.msgBody;
                    if(contr.prepareGame()) {
                        //server.broadcast(MsgType.NEWGAME.toString());
                        server.startGame();
                    }
                    else
                        try {
                        sendMsg(createMessage(MsgType.WAITCONNECT));
                        } catch(IOException e) {
                        System.out.println(e);};
                        
                        
                    /*boolean right = contr.correctWord();
                    int remainingGuessses = contr.remainingGuesses();

                    StringJoiner joiner = new StringJoiner("##");
                    joiner.add(username);
                    joiner.add(guess);
                    joiner.add(contr.showCurrentState());
                    joiner.add(Integer.toString(remainingGuessses));

                    server.broadcast(msg.msgType + "##" + joiner.toString());
                    if(remainingGuessses == 0) {
                        joiner.add(Integer.toString(contr.score()));
                        joiner.add("lose");
                        joiner.add(contr.getWord());
                        server.broadcast(MsgType.ENDGAME + "##" + joiner.toString());
                        server.startGame();
                        //new Thread (()->{ server.startGame(); }).start();
                    } else if(right) {
                        joiner.add(Integer.toString(contr.score()));
                        joiner.add("win");
                        server.broadcast(MsgType.ENDGAME + "##" + joiner.toString());
                        server.startGame();
                        //new Thread (()->{ server.startGame(); }).start();
                    }
                    System.out.println(guess);*/
                    break;
                case PLAY:
                    server.broadcast(MsgType.PLAYERCHOICE + "##" + username);
                    if(contr.sendChoice(msg.msgBody.toLowerCase(), this)) {
                        System.out.println("in handler before broadcast " + contr.getResult());
                        server.broadcast(MsgType.RESULT + "##" + contr.getResult());
                    }
                    else
                        try {
                        sendMsg(createMessage(MsgType.WAITROUND));
                        } catch(IOException e) {
                        System.out.println(e);};
                        
                    break;

                case DISCONNECT:
                    System.out.println(msg);
                    try {
                    disconnectClient();
                    } catch(IOException ioe) {};
                    server.broadcast(msg.msgType + "##" + username);
                    break; 

                default:
                    System.out.println("Command:" + msg.receivedString + "is not known.");
            }
        }
    }

    void sendMsg(ByteBuffer msg) throws IOException {
        playerChannel.write(msg);
        if (msg.hasRemaining()) {
            throw new MessageException("Could not send message");
        }
    }
    
    private ByteBuffer createMessage(MsgType type) {
        StringJoiner joiner = new StringJoiner("##");
        joiner.add(MsgType.BROADCAST.toString());
        joiner.add(type.toString());
        //joiner.add(msg);
        String messageWithLengthHeader = MessageSplitter.prependLengthHeader(joiner.toString());

        return ByteBuffer.wrap(messageWithLengthHeader.getBytes());
    }

    
    void disconnectClient() throws IOException {
        playerChannel.close();
    }
    
    void recvMsg() throws IOException {
        msgFromClient.clear();
        int numOfReadBytes;
        numOfReadBytes = playerChannel.read(msgFromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }
        String recvdString = extractMessageFromBuffer();
        msgSplitter.appendRecvdString(recvdString);
        handlePlayerRequest();
    }
    
    private String extractMessageFromBuffer() {
        msgFromClient.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return new String(bytes);
    }    
    
    private static class Message {
        private String receivedString;
        private MsgType msgType;
        private String msgBody;
        
        private Message (String receivedString) {
            parse(receivedString);
            this.receivedString = receivedString;
        }
        
        private void parse (String strToParse) {
            try {
                String[] msgTokens = strToParse.split("##");
                msgType = MsgType.valueOf(msgTokens[0].toUpperCase());
                if (hasBody(msgTokens)) {
                    msgBody = msgTokens[1];
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        
        private boolean hasBody(String[] msgTokens) {
            return msgTokens.length > 1;
        }
    }
    
    public String getUsername() {
        return username;
    }
            
}
