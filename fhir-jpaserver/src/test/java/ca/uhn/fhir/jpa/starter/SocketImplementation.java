
package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.rest.api.EncodingEnum;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@WebSocket
public class SocketImplementation {

	private static final Logger ourLog = org.slf4j.LoggerFactory.getLogger(SocketImplementation.class);
	private final String myCriteria;
	protected String myError;
	protected boolean myGotBound;
	private final List<String> myMessages = new ArrayList<String>();
	protected int myPingCount;
	protected String mySubsId;
	private Session session;

	public SocketImplementation(String theCriteria, EncodingEnum theEncoding) {
		myCriteria = theCriteria;
	}

	public List<String> getMessages() {
		return myMessages;
	}

	public void keepAlive() {
		if (this.session != null) {
			try {
				session.getRemote().sendString("keep alive");
			} catch (Throwable t) {
				ourLog.error("Failure", t);
			}
		}
	}

	/**
	 * This method is executed when the client is connecting to the server.
	 * In this case, we are sending a message to create the subscription dynamiclly
	 *
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		ourLog.debug("Got connect: {}", session);
		this.session = session;
		try {
			String sending = "bind " + myCriteria;
			ourLog.debug("Sending: {}", sending);
			session.getRemote().sendString(sending);

			ourLog.debug("Connection: DONE");
		} catch (Throwable t) {
			t.printStackTrace();
			ourLog.error("Failure", t);
		}
	}

	/**
	 * This is the message handler for the client
	 *
	 * @param theMsg
	 */
	@OnWebSocketMessage
	public void onMessage(String theMsg) {
		ourLog.info("Got msg: " + theMsg);
		myMessages.add(theMsg);

		if (theMsg.startsWith("bound ")) {
			myGotBound = true;
			mySubsId = (theMsg.substring("bound ".length()));
		} else if (myGotBound && theMsg.startsWith("add " + mySubsId + "\n")) {
			String text = theMsg.substring(("add " + mySubsId + "\n").length());
			ourLog.info("text: " + text);
		} else if (theMsg.startsWith("ping ")) {
			myPingCount++;
		} else {
			myError = "Unexpected message: " + theMsg;
		}
	}
}