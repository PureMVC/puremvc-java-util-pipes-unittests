/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.Assert;
import org.junit.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Test the Message class.
 */
public class MessageTest {
	/**
	 * Tests the constructor parameters and getters.
	 */
	@Test
	public void testConstructorAndGetters() {
		// create a message with complete constructor args
		IPipeMessage message = new Message(Message.NORMAL, "testval", "Hello", Message.PRIORITY_HIGH);

		// test assertions
		Assert.assertNotNull("Expecting message is Message", (Message) message);
		Assert.assertEquals("Expecting message.getType() == Message.NORMAL", message.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message.getHeader().testProp == 'testval'", message.getHeader(), "testval");
		Assert.assertEquals("Expecting message.getBody().@testAtt == 'Hello'", message.getBody(), "Hello");
		Assert.assertEquals("Expecting message.getPriority() == Message.PRIORITY_HIGH", message.getPriority(), Message.PRIORITY_HIGH);

	}

	/**
	 * Tests message default priority.
	 */
	@Test
	public void testDefaultPriority() {
		// Create a message with minimum constructor args
		IPipeMessage message = new Message(Message.NORMAL);

		// test assertions
		Assert.assertEquals("Expecting message.getPriority() == Message.PRIORITY_MED", message.getPriority(), Message.PRIORITY_MED);

	}

	/**
	 * Tests the setters and getters.
	 */
	@Test
	public void testSettersAndGetters() {
		// create a message with minimum constructor args
		IPipeMessage message = new Message(Message.NORMAL);

		// Set remainder via setters
		message.setHeader("testval");
		message.setBody("Hello");
		message.setPriority(Message.PRIORITY_LOW);

		// test assertions
		Assert.assertNotNull("Expecting message is Message", (Message) message);
		Assert.assertEquals("Expecting message.getType() == Message.NORMAL", message.getType(), Message.NORMAL);
		Assert.assertEquals("Expecting message.getHeader().testProp == 'testval'", message.getHeader(), "testval");
		Assert.assertEquals("Expecting message.getBody().@testAtt == 'Hello'", message.getBody(), "Hello");
		Assert.assertEquals("Expecting message.getPriority() == Message.PRIORITY_LOW", message.getPriority(), Message.PRIORITY_LOW);

	}
}
