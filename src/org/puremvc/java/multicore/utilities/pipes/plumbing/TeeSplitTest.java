/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Test the TeeSplit class.
 */
public class TeeSplitTest implements IPipeListener {

	private List<IPipeMessage> messagesReceived = new ArrayList<IPipeMessage>();

	/**
	 * Callback given to <code>PipeListener</code> for incoming message.
	 * <P>
	 * Used by <code>testReceiveMessageViaPipeListener</code> to get the
	 * output of pipe back into this test to see that a message passes through
	 * the pipe.
	 * </P>
	 */
	public void handlePipeMessage(IPipeMessage message) {
		messagesReceived.add(message);
	}

	/**
	 * Test connecting and disconnecting I/O Pipes.
	 * 
	 * <P>
	 * Connect an input and several output pipes to a splitting tee. 
	 * Then disconnect all outputs in LIFO order by calling disconnect 
	 * repeatedly.</P>
	 */
	@Test
	public void testConnectingAndDisconnectingIOPipes() {
		// create input pipe
		IPipeFitting input1 = new Pipe();

		// create output pipes 1, 2, 3 and 4
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();
		IPipeFitting pipe3 = new Pipe();
		IPipeFitting pipe4 = new Pipe();

		// create splitting tee (args are first two output fittings of tee)
		TeeSplit teeSplit = new TeeSplit(pipe1, pipe2);

		// connect 2 extra outputs for a total of 4
		boolean connectedExtra1 = teeSplit.connect(pipe3);
		boolean connectedExtra2 = teeSplit.connect(pipe4);

		// connect the single input
		boolean inputConnected = input1.connect(teeSplit);

		// test assertions
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting pipe3 is Pipe", (Pipe) pipe3);
		Assert.assertNotNull("Expecting pipe4 is Pipe", (Pipe) pipe4);
		Assert.assertNotNull("Expecting teeSplit is TeeSplit", (TeeSplit) teeSplit);
		Assert.assertTrue("Expecting connected pipe 3", connectedExtra1);
		Assert.assertTrue("Expecting connected pipe 4", connectedExtra2);
		Assert.assertTrue("Expecting connected input", inputConnected);

		// test LIFO order of output disconnection
		Assert.assertEquals("Expecting disconnected pipe 4", teeSplit.disconnect(), pipe4);
		Assert.assertEquals("Expecting disconnected pipe 3", teeSplit.disconnect(), pipe3);
		Assert.assertEquals("Expecting disconnected pipe 2", teeSplit.disconnect(), pipe2);
		Assert.assertEquals("Expecting disconnected pipe 1", teeSplit.disconnect(), pipe1);
	}

	/**
	 * Test receiving messages from two pipes using a TeeMerge.
	 */
	@Test
	public void testReceiveMessagesFromTwoTeeSplitOutputs() {
		// create a message to send on pipe 1
		IPipeMessage message = new Message(Message.NORMAL, 1);

		// create output pipes 1 and 2
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();

		// create and connect anonymous listeners
		boolean connected1 = pipe1.connect(new PipeListener(this));
		boolean connected2 = pipe2.connect(new PipeListener(this));

		// create splitting tee (args are first two output fittings of tee)
		TeeSplit teeSplit = new TeeSplit(pipe1, pipe2);

		// write messages to their respective pipes
		Boolean written = teeSplit.write(message);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message);
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting teeSplit is TeeSplit", (TeeSplit) teeSplit);
		Assert.assertTrue("Expecting connected anonymous listener to pipe 1", connected1);
		Assert.assertTrue("Expecting connected anonymous listener to pipe 2", connected2);
		Assert.assertTrue("Expecting wrote single message to tee", written);

		// test that both messages were received, then test
		// FIFO order by inspecting the messages themselves
		Assert.assertEquals("Expecting received 2 messages", messagesReceived.size(), 2);

		// test message 1 assertions 
		IPipeMessage message1 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message1 is IPipeMessage", (IPipeMessage) message1);
		Assert.assertEquals("Expecting message1 === pipe1Message", message1, message); // object equality

		// test message 2 assertions
		IPipeMessage message2 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message1 is IPipeMessage", (IPipeMessage) message2);
		Assert.assertEquals("Expecting message1 === pipe1Message", message2, message); // object equality

	}
}
