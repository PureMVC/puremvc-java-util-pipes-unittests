/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.Assert;
import org.junit.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Test the PipeListener class.
 */
public class PipeListenerTest implements IPipeListener {

	/**
	 * Recipient of message.
	 * <P>
	 * Used by <code>callBackMedhod</code> as a place to store
	 * the recieved message.</P>
	 */
	private IPipeMessage messageReceived;

	/**
	 * Callback given to <code>PipeListener</code> for incoming message.
	 * <P>
	 * Used by <code>testReceiveMessageViaPipeListener</code> 
	 * to get the output of pipe back into this  test to see 
	 * that a message passes through the pipe.</P>
	 */
	public void handlePipeMessage(IPipeMessage message) {
		messageReceived = message;
	}

	/**
	 * Test connecting a pipe listener to a pipe. 
	 */
	@Test
	public void testConnectingToAPipe() {
		// create pipe and listener
		IPipeFitting pipe = new Pipe();
		PipeListener listener = new PipeListener(this);

		// connect the listener to the pipe
		boolean success = pipe.connect(listener);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertTrue("Expecting successfully connected listener to pipe", success);
	}

	/**
	 * Test receiving a message from a pipe using a PipeListener.
	 */
	@Test
	public void testReceiveMessageViaPipeListener() {
		// create a message
		IPipeMessage messageToSend = new Message(Message.NORMAL, "testval", "Hello", Message.PRIORITY_HIGH);
		// create pipe and listener
		IPipeFitting pipe = new Pipe();
		PipeListener listener = new PipeListener(this);

		// connect the listener to the pipe and write the message
		boolean connected = pipe.connect(listener);
		boolean written = pipe.write(messageToSend);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertTrue("Expecting connected listener to pipe", connected);
		Assert.assertTrue("Expecting wrote message to pipe", written);
		Assert.assertNotNull("Expecting messageReceived is Message", (Message) messageReceived);
		Assert.assertEquals("Expecting messageReceived.getType() == Message.NORMAL", messageReceived.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting messageReceived.getHeader().testProp == 'testval'", messageReceived.getHeader(), "testval");
		Assert.assertEquals("Expecting messageReceived.getBody().@testAtt == 'Hello'", messageReceived.getBody(), "Hello");
		Assert.assertEquals("Expecting messageReceived.getPriority() == Message.PRIORITY_HIGH", messageReceived.getPriority(), Message.PRIORITY_HIGH);

	}
}
