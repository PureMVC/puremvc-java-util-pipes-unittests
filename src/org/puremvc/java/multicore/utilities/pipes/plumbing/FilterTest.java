/* 
 PureMVC Java MultiCore Pipes Utility Unit Tests by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IFilter;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.FilterControlMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Test the Filter class.
 */
public class FilterTest implements IPipeListener{

	/**
	 * Array of received messages.
	 * <P>
	 * Used by <code>callBackMedhod</code> as a place to store
	 * the recieved messages.</P>
	 */
	private Vector<IPipeMessage> messagesReceived = new Vector<IPipeMessage>();

	
	/**
	 * Test connecting input and output pipes to a filter as well as disconnecting the output.
	 */
	@Test
	public void testConnectingAndDisconnectingIOPipes() {

		// create output pipes 1
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();

		// create filter
		Filter filter = new Filter("TestFilter");

		// connect input fitting
		boolean connectedInput = pipe1.connect(filter);

		// connect output fitting
		boolean connectedOutput = filter.connect(pipe2);

		// test assertions
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertTrue("Expecting connected input", connectedInput);
		Assert.assertTrue("Expecting connected output", connectedOutput);

		// disconnect pipe 2 from filter
		IPipeFitting disconnectedPipe = filter.disconnect();
		Assert.assertEquals("Expecting disconnected pipe2 from filter", disconnectedPipe, pipe2);
	}

	private class Rectangle {
		public float width;

		public float height;

		public Rectangle(float pWidth, float pHeight) {
			this.width = pWidth;
			this.height = pHeight;
		}
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


	private class MultPipeFilterTest implements IFilter {

		public IPipeMessage apply(IPipeMessage message, Object params) {
			Rectangle header = (Rectangle) message.getHeader();

			header.width *= (Integer) params;
			header.height *= (Integer) params;
			return message;
		}
	}

	private class DivPipeFilterTest implements IFilter {

		public IPipeMessage apply(IPipeMessage message, Object params) {
			Rectangle header = (Rectangle) message.getHeader();

			header.width /= (Integer) params;
			header.height /= (Integer) params;
			return message;
		}
	}

	/**
	 * Test applying filter to a normal message.
	 */
	@Test
	public void testFilteringNormalMessage() {
		// create messages to send to the queue
		IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

		// create filter, attach an anonymous listener to the filter output to receive the message,
		// pass in an anonymous function an parameter object
		Filter filter = new Filter("scale", new PipeListener(this), new MultPipeFilterTest(), 10);

		// write messages to the filter
		boolean written = filter.write(message);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertTrue("Expecting wrote message to filter", written);
		Assert.assertEquals("Expecting received 1 messages", messagesReceived.size(), 1);

		// test filtered message assertions 
		IPipeMessage recieved = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved is IPipeMessage", (IPipeMessage) recieved);
		Assert.assertEquals("Expecting recieved === message", recieved, message); // object equality
		Rectangle header = (Rectangle) recieved.getHeader();
		Assert.assertEquals("Expecting recieved.getHeader().width == 100", header.width, 100, 0);
		Assert.assertEquals("Expecting recieved.getHeader().height == 20", header.height, 20, 0);
	}

	/**
	 * Test setting filter to bypass mode, writing, then setting back to filter mode and writing. 
	 */
	@Test
	public void testBypassAndFilterModeToggle() {
		// create messages to send to the queue
		IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

		// create filter, attach an anonymous listener to the filter output to receive the message,
		// pass in an anonymous function an parameter object
		Filter filter = new Filter("scale", new PipeListener(this), new MultPipeFilterTest(), 10);

		// create bypass control message	
		FilterControlMessage bypassMessage = new FilterControlMessage(FilterControlMessage.BYPASS, "scale");

		// write bypass control message to the filter
		boolean bypassWritten = filter.write(bypassMessage);

		// write normal message to the filter
		boolean written1 = filter.write(message);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertTrue("Expecting wrote bypass message to filter", bypassWritten);
		Assert.assertTrue("Expecting wrote normal message to filter", written1);
		Assert.assertEquals("Expecting received 1 messages", messagesReceived.size(), 1);

		// test filtered message assertions (no change to message)
		IPipeMessage recieved1 = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved1 is IPipeMessage", (IPipeMessage) recieved1);
		Assert.assertEquals("Expecting recieved1 === message", recieved1, message); // object equality
		Rectangle header = (Rectangle) recieved1.getHeader();
		Assert.assertEquals("Expecting recieved1.getHeader().width == 10", header.width, 10, 0);
		Assert.assertEquals("Expecting recieved1.getHeader().height == 2", header.height, 2, 0);

		// create filter control message	
		FilterControlMessage filterMessage = new FilterControlMessage(FilterControlMessage.FILTER, "scale");

		// write bypass control message to the filter
		boolean filterWritten = filter.write(filterMessage);

		// write normal message to the filter again
		boolean written2 = filter.write(message);

		// test assertions   			
		Assert.assertTrue("Expecting wrote filter message to filter", filterWritten);
		Assert.assertTrue("Expecting wrote normal message to filter", written2);
		Assert.assertEquals("Expecting received 2 messages", messagesReceived.size(), 2);

		// test filtered message assertions (message filtered)
		IPipeMessage recieved2 = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved2 is IPipeMessage", (IPipeMessage) recieved2);
		Assert.assertEquals("Expecting recieved2 === message", recieved2, message); // object equality
		header = (Rectangle) recieved2.getHeader();
		Assert.assertEquals("Expecting recieved2.getHeader().width == 100", header.width, 100, 0);
		Assert.assertEquals("Expecting recieved2.getHeader().height == 20", header.height, 20, 0);
	}

	/**
	 * Test setting filter parameters by sending control message. 
	 */
	@Test
	public void testSetParamsByControlMessage() {
		// create messages to send to the queue
		IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

		// create filter, attach an anonymous listener to the filter output to receive the message,
		// pass in an anonymous function an parameter object
		Filter filter = new Filter("scale", new PipeListener(this), new MultPipeFilterTest(), 10);

		// create setParams control message	
		FilterControlMessage setParamsMessage = new FilterControlMessage(FilterControlMessage.SET_PARAMS, "scale", null, 5);

		// write filter control message to the filter
		boolean setParamsWritten = filter.write(setParamsMessage);

		// write normal message to the filter
		boolean written = filter.write(message);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertTrue("Expecting wrote set_params message to filter", setParamsWritten);
		Assert.assertTrue("Expecting wrote normal message to filter", written);
		Assert.assertEquals("Expecting received 1 messages", messagesReceived.size(), 1);

		// test filtered message assertions (message filtered with overridden parameters)
		IPipeMessage recieved = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved is IPipeMessage", (IPipeMessage) recieved);
		Assert.assertEquals("Expecting recieved === message", recieved, message); // object equality
		Rectangle header = (Rectangle) recieved.getHeader();
		Assert.assertEquals("Expecting recieved.getHeader().width == 50", header.width, 50, 0);
		Assert.assertEquals("Expecting recieved.getHeader().height == 10", header.height, 10, 0);

	}

	/**
	 * Test setting filter function by sending control message. 
	 */
	@Test
	public void testSetFilterByControlMessage() {
		// create messages to send to the queue
		IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

		// create filter, attach an anonymous listener to the filter output to receive the message,
		// pass in an anonymous function an parameter object
		Filter filter = new Filter("scale", new PipeListener(this), new MultPipeFilterTest(), 10);

		// create setFilter control message	
		FilterControlMessage setFilterMessage = new FilterControlMessage(FilterControlMessage.SET_FILTER, "scale", new DivPipeFilterTest());

		// write filter control message to the filter
		boolean setFilterWritten = filter.write(setFilterMessage);

		// write normal message to the filter
		boolean written = filter.write(message);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertTrue("Expecting wrote message to filter", setFilterWritten);
		Assert.assertTrue("Expecting wrote normal message to filter", written);
		Assert.assertEquals("Expecting received 1 messages", messagesReceived.size(), 1);

		// test filtered message assertions (message filtered with overridden filter function)
		IPipeMessage recieved = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved is IPipeMessage", (IPipeMessage) recieved);
		Assert.assertEquals("Expecting recieved === message", recieved, message); // object equality
		Rectangle header = (Rectangle) recieved.getHeader();
		Assert.assertEquals("Expecting recieved.getHeader().width == 1", header.width, 1, 0);
		Assert.assertEquals("Expecting recieved.getHeader().height == .2", header.height, .2, 0.1);

	}

	private class BozoUser {
		public int bozoLevel;

		public String user;

		public BozoUser(int pBozoLevel, String pUser) {
			this.bozoLevel = pBozoLevel;
			this.user = pUser;
		}
	}

	private class BozoPipeFilterTest implements IFilter {

		public IPipeMessage apply(IPipeMessage message, Object params) {
			BozoUser user = (BozoUser) message.getHeader();
			if (user.bozoLevel > (Integer) params)
				throw new Error("bozoFiltered");
			return message;
		}
	}

	/**
	 * Test using a filter function to stop propagation of a message. 
	 * <P>
	 * The way to stop propagation of a message from within a filter
	 * is to throw an error from the filter function. This test creates
	 * two NORMAL messages, each with header objects that contain 
	 * a <code>bozoLevel</code> property. One has this property set to 
	 * 10, the other to 3.</P>
	 * <P>
	 * Creates a Filter, named 'bozoFilter' with an anonymous pipe listener
	 * feeding the output back into this test. The filter funciton is an 
	 * anonymous function that throws an error if the message's bozoLevel 
	 * property is greater than the filter parameter <code>bozoThreshold</code>.
	 * the anonymous filter parameters object has a <code>bozoThreshold</code>
	 * value of 5.</P>
	 * <P>
	 * The messages are written to the filter and it is shown that the 
	 * message with the <code>bozoLevel</code> of 10 is not written, while
	 * the message with the <code>bozoLevel</code> of 3 is.</P> 
	 */
	@Test
	public void testUseFilterToStopAMessage() {
		// create messages to send to the queue
		IPipeMessage message1 = new Message(Message.NORMAL, new BozoUser(10, "Dastardly Dan"));
		IPipeMessage message2 = new Message(Message.NORMAL, new BozoUser(3, "Dudley Doright"));

		// create filter, attach an anonymous listener to the filter output to receive the message,
		// pass in an anonymous function and an anonymous parameter object
		Filter filter = new Filter("bozoFilter", new PipeListener(this), new BozoPipeFilterTest(), 5);

		// write normal message to the filter
		boolean written1 = filter.write(message1);
		boolean written2 = filter.write(message2);

		// test assertions
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message1);
		Assert.assertNotNull("Expecting message is IPipeMessage", (IPipeMessage) message2);
		Assert.assertNotNull("Expecting filter is Filter", (Filter) filter);
		Assert.assertFalse("Expecting failed to write bad message", written1);
		Assert.assertTrue("Expecting wrote good message", written2 == true);
		Assert.assertEquals("Expecting received 1 messages", messagesReceived.size(), 1);

		// test filtered message assertions (message with good auth token passed
		IPipeMessage recieved = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved is IPipeMessage", (IPipeMessage) recieved);
		Assert.assertEquals("Expecting recieved === message2", recieved, message2); // object equality

	}


}
