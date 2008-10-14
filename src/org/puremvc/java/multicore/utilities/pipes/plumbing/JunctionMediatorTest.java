/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests by Ima OpenSource <opensource@ima.eu>
 Maintained by Matthieu Mauny <matthieu.mauny@puremvc.org>
 And Anthony Quinault <aquinault@gmail.com>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Junction mediator class.
 */
public class JunctionMediatorTest {
	
	/**
	 * Test the constructor.
	 */
	@Test
	public void testConstructor() {
		Junction junction = new Junction();
		JunctionMediator junctionMediator = new JunctionMediator("Junction Mediator", junction);

		// test assertions
		Assert.assertNotNull("Expecting junctionMediator is JunctionMediator", (JunctionMediator) junctionMediator);
		Assert.assertNotNull("Expecting junction is Junction", (Junction) junctionMediator.getViewComponent());
		Assert.assertEquals("Expecting junctionMediator.getMediatorName() == 'Junction Mediator'", junctionMediator.getMediatorName(),"Junction Mediator");
	}
	
	/**
	 * Test the Notification Interests.
	 */
	@Test
	public void testNotificationInterests() {
		Junction junction = new Junction();
		JunctionMediator junctionMediator = new JunctionMediator("Junction Mediator", junction);

		// test assertions
		Assert.assertNotNull("Expecting junctionMediator is JunctionMediator", (JunctionMediator) junctionMediator);
		Assert.assertEquals("Expecting 2 interests", junctionMediator.listNotificationInterests().length, 2);
		Assert.assertEquals("Expecting first interest is ACCEPT_INPUT_PIPE", junctionMediator.listNotificationInterests()[0],JunctionMediator.ACCEPT_INPUT_PIPE);
		Assert.assertEquals("Expecting second interest is ACCEPT_OUTPUT_PIPE", junctionMediator.listNotificationInterests()[1],JunctionMediator.ACCEPT_OUTPUT_PIPE);
	}
}
