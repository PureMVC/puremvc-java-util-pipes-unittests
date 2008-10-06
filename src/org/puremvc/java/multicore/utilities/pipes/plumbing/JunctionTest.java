/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Test the Junction class.
 */
public class JunctionTest implements IPipeListener {
	/**
	 * Array of received messages.
	 * <P>
	 * Used by <code>callBackMedhod</code> as a place to store
	 * the recieved messages.</P>
	 */
	private Vector<IPipeMessage> messagesReceived = new Vector<IPipeMessage>();

	/**
	 * Test registering an INPUT pipe to a junction.
	 * <P>
	 * Tests that the INPUT pipe is successfully registered and
	 * that the hasPipe and hasInputPipe methods work. Then tests
	 * that the pipe can be retrieved by name.</P>
	 * <P>
	 * Finally, it removes the registered INPUT pipe and tests
	 * that all the previous assertions about it's registration
	 * and accessability via the Junction are no longer true.</P>
	 */
	@Test
	public void testRegisterRetrieveAndRemoveInputPipe() {
		// create pipe connected to this test with a pipelistener
		IPipeFitting pipe = new Pipe();

		// create junction
		Junction junction = new Junction();

		// register the pipe with the junction, giving it a name and direction
		boolean registered = junction.registerPipe("testInputPipe", Junction.INPUT, pipe);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertNotNull("Expecting junction is Junction ", (Junction) junction);
		Assert.assertTrue("Expecting success regsitering pipe", registered);

		// assertions about junction methods once input  pipe is registered
		Assert.assertTrue("Expecting junction has pipe", junction.hasPipe("testInputPipe"));
		Assert.assertTrue("Expecting junction has pipe registered as an INPUT type", junction.hasInputPipe("testInputPipe"));
		Assert.assertEquals("Expecting pipe retrieved from junction", junction.retrievePipe("testInputPipe"), pipe); // object equality

		// now remove the pipe and be sure that it is no longer there (same assertions should be false)
		junction.removePipe("testInputPipe");
		Assert.assertFalse("Expecting junction has pipe", junction.hasPipe("testInputPipe"));
		Assert.assertFalse("Expecting junction has pipe registered as an INPUT type", junction.hasInputPipe("testInputPipe"));
		Assert.assertNotSame("Expecting pipe retrieved from junction", junction.retrievePipe("testInputPipe"), pipe); // object equality

	}

	/**
	 * Test registering an OUTPUT pipe to a junction.
	 * <P>
	 * Tests that the OUTPUT pipe is successfully registered and
	 * that the hasPipe and hasOutputPipe methods work. Then tests
	 * that the pipe can be retrieved by name.</P>
	 * <P>
	 * Finally, it removes the registered OUTPUT pipe and tests
	 * that all the previous assertions about it's registration
	 * and accessability via the Junction are no longer true.</P>
	 */
	@Test
	public void testRegisterRetrieveAndRemoveOutputPipe() {
		// create pipe connected to this test with a pipelistener
		IPipeFitting pipe = new Pipe();

		// create junction
		Junction junction = new Junction();

		// register the pipe with the junction, giving it a name and direction
		boolean registered = junction.registerPipe("testOutputPipe", Junction.OUTPUT, pipe);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertNotNull("Expecting junction is Junction ", (Junction) junction);
		Assert.assertTrue("Expecting success regsitering pipe", registered);

		// assertions about junction methods once output pipe is registered
		Assert.assertTrue("Expecting junction has pipe", junction.hasPipe("testOutputPipe"));
		Assert.assertTrue("Expecting junction has pipe registered as an OUTPUT type", junction.hasOutputPipe("testOutputPipe"));
		Assert.assertEquals("Expecting pipe retrieved from junction", junction.retrievePipe("testOutputPipe"), pipe); // object equality

		// now remove the pipe and be sure that it is no longer there (same assertions should be false)
		junction.removePipe("testOutputPipe");
		Assert.assertFalse("Expecting junction no longer has pipe", junction.hasPipe("testOutputPipe"));
		Assert.assertFalse("Expecting junction has pipe registered as an OUTPUT type", junction.hasOutputPipe("testOutputPipe"));
		Assert.assertNotSame("Expecting pipe can't be retrieved from junction", junction.retrievePipe("testOutputPipe"), pipe);
	}

	/**
	 * Callback given to <code>PipeListener</code> for incoming message.
	 * <P>
	 * Used by <code>testReceiveMessageViaPipeListener</code> 
	 * to get the output of pipe back into this  test to see 
	 * that a message passes through the pipe.</P>
	 */
	public void handlePipeMessage(IPipeMessage message) {
		messagesReceived.add(message);
	}

	/**
	 * Test adding a PipeListener to an Input Pipe.
	 * <P>
	 * Registers an INPUT Pipe with a Junction, then tests
	 * the Junction's addPipeListener method, connecting
	 * the output of the pipe back into to the test. If this
	 * is successful, it sends a message down the pipe and 
	 * checks to see that it was received.</P>
	 */
	@Test
	public void testAddingPipeListenerToAnInputPipe() {
		// create pipe 
		IPipeFitting pipe = new Pipe();

		// create junction
		Junction junction = new Junction();

		// create test message
		IPipeMessage message = new Message(Message.NORMAL, 1);

		// register the pipe with the junction, giving it a name and direction
		boolean registered = junction.registerPipe("testInputPipe", Junction.INPUT, pipe);

		// add the pipelistener using the junction method
		boolean listenerAdded = junction.addPipeListener("testInputPipe", this);

		// send the message using our reference to the pipe, 
		// it should show up in messageReceived property via the pipeListener
		boolean sent = pipe.write(message);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertNotNull("Expecting junction is Junction ", (Junction) junction);
		Assert.assertTrue("Expecting regsitered pipe", registered);
		Assert.assertTrue("Expecting added pipeListener", listenerAdded);
		Assert.assertTrue("Expecting successful write to pipe", sent);
		Assert.assertEquals("Expecting 1 message received", messagesReceived.size(), 1);
		Assert.assertEquals("Expecting received message was same instance sent", messagesReceived.firstElement(), message); //object equality

	}

	/**
	 * Test using sendMessage on an OUTPUT pipe.
	 * <P>
	 * Creates a Pipe, Junction and Message. 
	 * Adds the PipeListener to the Pipe.
	 * Adds the Pipe to the Junction as an OUTPUT pipe.
	 * uses the Junction's sendMessage method to send
	 * the Message, then checks that it was received.</P>
	 */
	@Test
	public void testSendMessageOnAnOutputPipe() {
		// create pipe 
		IPipeFitting pipe = new Pipe();

		// add a PipeListener manually 
		boolean listenerAdded = pipe.connect(new PipeListener(this));

		// create junction
		Junction junction = new Junction();

		// create test message
		IPipeMessage message = new Message(Message.NORMAL, 1);

		// register the pipe with the junction, giving it a name and direction
		boolean registered = junction.registerPipe("testOutputPipe", Junction.OUTPUT, pipe);

		// send the message using the Junction's method 
		// it should show up in messageReceived property via the pipeListener
		boolean sent = junction.sendMessage("testOutputPipe", message);

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
		Assert.assertNotNull("Expecting junction is Junction ", (Junction) junction);
		Assert.assertTrue("Expecting regsitered pipe", registered);
		Assert.assertTrue("Expecting added pipeListener", listenerAdded);
		Assert.assertTrue("Expecting message sent", sent);
		Assert.assertEquals("Expecting 1 message received", messagesReceived.size(), 1);
		Assert.assertEquals("Expecting received message was same instance sent", messagesReceived.firstElement(), message); //object equality
	}
}
