package ChatSystem;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.*;

public class Controller {

	private User user;
	private volatile ArrayList<User> connectedUsers;
	private ArrayList<Message> messages;
	private ArrayList<Group> groups;
	private Udp udp;
	private InetAddress ipBroadcast;
	private volatile Message messageToSend = null;
	private Timer timer;
	
}
