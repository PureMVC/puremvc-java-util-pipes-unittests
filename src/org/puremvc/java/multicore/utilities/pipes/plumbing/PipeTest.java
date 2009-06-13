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

/**
 * Test the Pipe class.
 */
public class PipeTest {
	/**
	 * Test the constructor.
	 */
	@Test
	public void testConstructor() {
		IPipeFitting pipe = new Pipe();

		// test assertions
		Assert.assertNotNull("Expecting pipe is Pipe", (Pipe) pipe);
	}

	/**
	 * Test connecting and disconnecting two pipes. 
	 */
	@Test
	public void testConnectingAndDisconnectingTwoPipes() {
		// create two pipes
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();
		// connect them
		boolean success = pipe1.connect(pipe2);

		// test assertions
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertTrue("Expecting connected pipe1 to pipe2", success);

		// disconnect pipe 2 from pipe 1
		IPipeFitting disconnectedPipe = pipe1.disconnect();
		Assert.assertEquals("Expecting disconnected pipe2 from pipe1", disconnectedPipe, pipe2);

	}

	/**
	 * Test attempting to connect a pipe to a pipe with an output already connected. 
	 */
	@Test
	public void testConnectingToAConnectedPipe() {
		// create two pipes
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();
		IPipeFitting pipe3 = new Pipe();

		// connect them
		boolean success = pipe1.connect(pipe2);

		// test assertions
		Assert.assertTrue("Expecting connected pipe1 to pipe2", success);
		Assert.assertFalse("Expecting can't connect pipe3 to pipe1", pipe1.connect(pipe3));

	}
}
