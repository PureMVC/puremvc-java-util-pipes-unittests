/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
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
 * Test the TeeMerge class.
 */
public class TeeMergeTest implements IPipeListener {

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
	 * Test connecting an output and several input pipes to a merging tee.
	 */
	@Test
	public void testConnectingIOPipes() {
		// create input pipe
		IPipeFitting output1 = new Pipe();

		// create input pipes 1, 2, 3 and 4
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();
		IPipeFitting pipe3 = new Pipe();
		IPipeFitting pipe4 = new Pipe();

		// create splitting tee (args are first two input fittings of tee)
		TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);

		// connect 2 extra inputs for a total of 4
		boolean connectedExtra1 = teeMerge.connectInput(pipe3);
		boolean connectedExtra2 = teeMerge.connectInput(pipe4);

		// connect the single output
		boolean connected = output1.connect(teeMerge);

		// test assertions
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting pipe3 is Pipe", (Pipe) pipe3);
		Assert.assertNotNull("Expecting pipe4 is Pipe", (Pipe) pipe4);
		Assert.assertNotNull("Expecting teeMerge is TeeMerge", (TeeMerge) teeMerge);
		Assert.assertTrue("Expecting connected extra input 1", connectedExtra1);
		Assert.assertTrue("Expecting connected extra input 2", connectedExtra2);
		Assert.assertTrue("Expecting connected input ", connected);
	}

	/**
	 * Test receiving messages from two pipes using a TeeMerge.
	 */
	@Test
	public void testReceiveMessagesFromTwoPipesViaTeeMerge() {
		// create a message to send on pipe 1
		IPipeMessage pipe1Message = new Message(Message.NORMAL, 1, "Pipe 1 Message", Message.PRIORITY_LOW);
		// create a message to send on pipe 2
		IPipeMessage pipe2Message = new Message(Message.NORMAL, 2, "Pipe 2 Message", Message.PRIORITY_HIGH);
		// create pipes 1 and 2
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();

		// create merging tee (args are first two input fittings of tee)
		TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);

		// create listener
		PipeListener listener = new PipeListener(this);

		// connect the listener to the tee and write the messages
		boolean connected = teeMerge.connect(listener);

		// write messages to their respective pipes
		boolean pipe1written = pipe1.write(pipe1Message);
		boolean pipe2written = pipe2.write(pipe2Message);

		// test assertions
		Assert.assertNotNull("Expecting pipe1Message is IPipeMessage", (IPipeMessage) pipe1Message);
		Assert.assertNotNull("Expecting pipe2Message is IPipeMessage", (IPipeMessage) pipe2Message);
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting teeMerge is TeeMerge", (TeeMerge) teeMerge);
		Assert.assertNotNull("Expecting listener is PipeListener", (PipeListener) listener);
		Assert.assertTrue("Expecting connected listener to merging tee", connected);
		Assert.assertTrue("Expecting wrote message to pipe 1", pipe1written);
		Assert.assertTrue("Expecting wrote message to pipe 2", pipe2written);

		// test that both messages were received, then test
		// FIFO order by inspecting the messages themselves
		Assert.assertEquals("Expecting received 2 messages", messagesReceived.size(), 2);

		// test message 1 assertions 
		IPipeMessage message1 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message1 is IPipeMessage", (IPipeMessage) message1);
		Assert.assertEquals("Expecting message1 === pipe1Message", message1, pipe1Message); // object equality
		Assert.assertEquals("Expecting message1.getType() == Message.NORMAL", message1.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message1.getHeader().testProp == 1", message1.getHeader(), 1);
		Assert.assertEquals("Expecting message1.getBody().@testAtt == 'Pipe 1 Message'", message1.getBody(), "Pipe 1 Message");
		Assert.assertEquals("Expecting message1.getPriority() == Message.PRIORITY_LOW", message1.getPriority(), Message.PRIORITY_LOW);

		// test message 2 assertions
		IPipeMessage message2 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message2 is IPipeMessage", (IPipeMessage) message2);
		Assert.assertEquals("Expecting message2 === pipe2Message", message2, pipe2Message); // object equality
		Assert.assertEquals("Expecting message2.getType() == Message.NORMAL", message2.getType() , Message.NORMAL);
		Assert.assertEquals("Expecting message2.getHeader().testProp == 2", message2.getHeader(), 2);
		Assert.assertEquals("Expecting message2.getBody().@testAtt == 'Pipe 2 Message'", message2.getBody(), "Pipe 2 Message");
		Assert.assertEquals("Expecting message2.getPriority() == Message.PRIORITY_HIGH", message2.getPriority(), Message.PRIORITY_HIGH);

	}

	/**
	 * Test receiving messages from four pipes using a TeeMerge.
	 */
	@Test
	public void testReceiveMessagesFromFourPipesViaTeeMerge() {
		// create a message to send on pipe 1
		IPipeMessage pipe1Message = new Message(Message.NORMAL, 1);
		IPipeMessage pipe2Message = new Message(Message.NORMAL, 2);
		IPipeMessage pipe3Message = new Message(Message.NORMAL, 3);
		IPipeMessage pipe4Message = new Message(Message.NORMAL, 4);

		// create pipes 1, 2, 3 and 4
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();
		IPipeFitting pipe3 = new Pipe();
		IPipeFitting pipe4 = new Pipe();

		// create merging tee
		TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);
		boolean connectedExtraInput3 = teeMerge.connectInput(pipe3);
		boolean connectedExtraInput4 = teeMerge.connectInput(pipe4);

		// create listener
		PipeListener listener = new PipeListener(this);

		// connect the listener to the tee and write the messages
		boolean connected = teeMerge.connect(listener);

		// write messages to their respective pipes
		boolean pipe1written = pipe1.write(pipe1Message);
		boolean pipe2written = pipe2.write(pipe2Message);
		boolean pipe3written = pipe3.write(pipe3Message);
		boolean pipe4written = pipe4.write(pipe4Message);

		// test assertions
		Assert.assertNotNull("Expecting pipe1Message is IPipeMessage", (IPipeMessage) pipe1Message);
		Assert.assertNotNull("Expecting pipe2Message is IPipeMessage", (IPipeMessage) pipe2Message);
		Assert.assertNotNull("Expecting pipe3Message is IPipeMessage", (IPipeMessage) pipe3Message);
		Assert.assertNotNull("Expecting pipe4Message is IPipeMessage", (IPipeMessage) pipe4Message);
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting pipe3 is Pipe", (Pipe) pipe3);
		Assert.assertNotNull("Expecting pipe4 is Pipe", (Pipe) pipe4);
		Assert.assertNotNull("Expecting teeMerge is TeeMerge", (TeeMerge) teeMerge);
		Assert.assertNotNull("Expecting listener is PipeListener", (PipeListener) listener);
		Assert.assertTrue("Expecting connected listener to merging tee", connected);
		Assert.assertTrue("Expecting connected extra input pipe3 to merging tee", connectedExtraInput3);
		Assert.assertTrue("Expecting connected extra input pipe4 to merging tee", connectedExtraInput4);
		Assert.assertTrue("Expecting wrote message to pipe 1", pipe1written);
		Assert.assertTrue("Expecting wrote message to pipe 2", pipe2written);
		Assert.assertTrue("Expecting wrote message to pipe 3", pipe3written);
		Assert.assertTrue("Expecting wrote message to pipe 4", pipe4written);

		// test that both messages were received, then test
		// FIFO order by inspecting the messages themselves
		Assert.assertEquals("Expecting received 4 messages", messagesReceived.size(), 4);

		// test message 1 assertions 
		IPipeMessage message1 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message1 is IPipeMessage", (IPipeMessage) message1);
		Assert.assertEquals("Expecting message1 === pipe1Message", message1, pipe1Message); // object equality
		Assert.assertEquals("Expecting message1.getType() == Message.NORMAL", message1.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message1.getHeader().testProp == 1", message1.getHeader(), 1);

		// test message 2 assertions
		IPipeMessage message2 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message2 is IPipeMessage", (IPipeMessage) message2);
		Assert.assertEquals("Expecting message2 === pipe2Message", message2, pipe2Message); // object equality
		Assert.assertEquals("Expecting message2.getType() == Message.NORMAL", message2.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message2.getHeader().testProp == 2", message2.getHeader(), 2);

		// test message 3 assertions 
		IPipeMessage message3 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message3 is IPipeMessage", (IPipeMessage) message3);
		Assert.assertEquals("Expecting message3 === pipe3Message", message3, pipe3Message); // object equality
		Assert.assertEquals("Expecting message3.getType() == Message.NORMAL", message3.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message3.getHeader().testProp == 3", message3.getHeader(), 3);

		// test message 4 assertions
		IPipeMessage message4 = messagesReceived.remove(0);
		Assert.assertNotNull("Expecting message4 is IPipeMessage", (IPipeMessage) message2);
		Assert.assertEquals("Expecting message4 === pipe4Message", message4, pipe4Message); // object equality
		Assert.assertEquals("Expecting message4.getType() == Message.NORMAL", message4.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message4.getHeader().testProp == 4", message4.getHeader(), 4);

	}

}
