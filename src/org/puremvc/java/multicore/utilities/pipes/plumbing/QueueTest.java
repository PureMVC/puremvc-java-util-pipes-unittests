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
import org.puremvc.java.multicore.utilities.pipes.messages.QueueControlMessage;

/**
 * Test the Queue class.
 */
public class QueueTest implements IPipeListener{

	/**
	 * Array of received messages.
	 * <P>
	 * Used by <code>callBackMedhod</code> as a place to store the recieved
	 * messages.
	 * </P>
	 */
	private Vector<IPipeMessage> messagesReceived = new Vector<IPipeMessage>();

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
	 * Test connecting input and output pipes to a queue.
	 */
	@Test
	public void testConnectingIOPipes() {

		// create output pipes 1
		IPipeFitting pipe1 = new Pipe();
		IPipeFitting pipe2 = new Pipe();

		// create queue
		Queue queue = new Queue();

		// connect input fitting
		boolean connectedInput = pipe1.connect(queue);

		// connect output fitting
		boolean connectedOutput = queue.connect(pipe2);

		// test assertions
		Assert.assertNotNull("Expecting pipe1 is Pipe", (Pipe) pipe1);
		Assert.assertNotNull("Expecting pipe2 is Pipe", (Pipe) pipe2);
		Assert.assertNotNull("Expecting queue is Queue", (Queue) queue);
		Assert.assertTrue("Expecting connected input", connectedInput);
		Assert.assertTrue("Expecting connected output", connectedOutput);
	}

	/**
	 * Test writing multiple messages to the Queue followed by a Flush message.
	 * <P>
	 * Creates messages to send to the queue. Creates queue, attaching an
	 * anonymous listener to its output. Writes messages to the queue. Tests
	 * that no messages have been received yet (they've been enqueued). Sends
	 * FLUSH message. Tests that messages were receieved, and in the order sent
	 * (FIFO).
	 * <P>
	 */
	@Test
	public void testWritingMultipleMessagesAndFlush() {
		// create messages to send to the queue
		IPipeMessage message1 = new Message(Message.NORMAL, 1);
		IPipeMessage message2 = new Message(Message.NORMAL, 2);
		IPipeMessage message3 = new Message(Message.NORMAL, 3);

		// create queue control flush message
		IPipeMessage flush = new QueueControlMessage(QueueControlMessage.FLUSH);

		// create queue, attaching an anonymous listener to its output
		Queue queue = new Queue(new PipeListener(this));

		// write messages to the queue
		boolean message1written = queue.write(message1);
		boolean message2written = queue.write(message2);
		boolean message3written = queue.write(message3);

		// test assertions
		Assert.assertNotNull("Expecting message1 is IPipeMessage", (IPipeMessage) message1);
		Assert.assertNotNull("Expecting message2 is IPipeMessage", (IPipeMessage) message2);
		Assert.assertNotNull("Expecting message3 is IPipeMessage", (IPipeMessage) message3);
		Assert.assertNotNull("Expecting flush is IPipeMessage", (IPipeMessage) flush);
		Assert.assertNotNull("Expecting queue is Queue", (Queue) queue);

		Assert.assertTrue("Expecting wrote message1 to queue", message1written);
		Assert.assertTrue("Expecting wrote message2 to queue", message2written);
		Assert.assertTrue("Expecting wrote message3 to queue", message3written);

		// test that no messages were received (they've been enqueued)
		Assert.assertEquals("Expecting received 0 messages", messagesReceived.size(), 0);

		// write flush control message to the queue
		boolean flushWritten = queue.write(flush);

		// test that all messages were received, then test
		// FIFO order by inspecting the messages themselves
		Assert.assertEquals("Expecting received 3 messages", messagesReceived.size(), 3);
		Assert.assertTrue("Expecting wrote flush message to queue", flushWritten);

		// test message 1 assertions
		IPipeMessage recieved1 = messagesReceived.firstElement();
		messagesReceived.remove(recieved1);
		Assert.assertNotNull("Expecting recieved1 is IPipeMessage", (IPipeMessage) recieved1);
		Assert.assertEquals("Expecting recieved1 === message1", recieved1, message1); // object
																						// equality

		// test message 2 assertions
		IPipeMessage recieved2 = messagesReceived.firstElement();
		messagesReceived.remove(recieved2);
		Assert.assertNotNull("Expecting recieved2 is IPipeMessage", (IPipeMessage) recieved2);
		Assert.assertEquals("Expecting recieved2 === message2", recieved2, message2); // object
																						// equality

		// test message 3 assertions
		IPipeMessage recieved3 = messagesReceived.firstElement();
		Assert.assertNotNull("Expecting recieved3 is IPipeMessage", (IPipeMessage) recieved3);
		Assert.assertEquals("Expecting recieved3 === message3", recieved3, message3); // object
																						// equality

	}

	/**
	 * Test the Sort-by-Priority and FIFO modes.
	 * <P>
	 * Creates messages to send to the queue, priorities unsorted. Creates
	 * queue, attaching an anonymous listener to its output. Sends SORT message
	 * to start sort-by-priority order mode. Writes messages to the queue. Sends
	 * FLUSH message, tests that messages were receieved in order of priority,
	 * not how they were sent.
	 * <P>
	 * <P>
	 * Then sends a FIFO message to switch the queue back to default FIFO
	 * behavior, sends messages again, flushes again, tests that the messages
	 * were recieved and in the order they were originally sent.
	 * </P>
	 */
	@Test
	public void testSortByPriorityAndFIFO() {
		// create messages to send to the queue
		IPipeMessage message1 = new Message(Message.NORMAL, null, null, Message.PRIORITY_MED);
		IPipeMessage message2 = new Message(Message.NORMAL, null, null, Message.PRIORITY_LOW);
		IPipeMessage message3 = new Message(Message.NORMAL, null, null, Message.PRIORITY_HIGH);

		// create queue, attaching an anonymous listener to its output
		Queue queue = new Queue(new PipeListener(this));

		// begin sort-by-priority order mode
		boolean sortWritten = queue.write(new QueueControlMessage(QueueControlMessage.SORT));

		// write messages to the queue
		boolean message1written = queue.write(message1);
		boolean message2written = queue.write(message2);
		boolean message3written = queue.write(message3);

		// flush the queue
		Boolean flushWritten = queue.write(new QueueControlMessage(QueueControlMessage.FLUSH));

		// test assertions
		Assert.assertTrue("Expecting wrote sort message to queue", sortWritten);
		Assert.assertTrue("Expecting wrote message1 to queue", message1written);
		Assert.assertTrue("Expecting wrote message2 to queue", message2written);
		Assert.assertTrue("Expecting wrote message3 to queue", message3written);
		Assert.assertTrue("Expecting wrote flush message to queue", flushWritten);

		// test that 3 messages were received
		Assert.assertEquals("Expecting received 3 messages", messagesReceived.size(), 3);

		// get the messages
		IPipeMessage recieved1 = messagesReceived.firstElement();
		messagesReceived.remove(recieved1);
		IPipeMessage recieved2 = messagesReceived.firstElement();
		messagesReceived.remove(recieved2);
		IPipeMessage recieved3 = messagesReceived.firstElement();
		messagesReceived.remove(recieved3);

		// test that the message order is sorted
		Assert.assertTrue("Expecting recieved1 is higher priority than recieved 2", recieved1.getPriority() < recieved2.getPriority());
		Assert.assertTrue("Expecting recieved2 is higher priority than recieved 3", recieved2.getPriority() < recieved3.getPriority());
		Assert.assertEquals("Expecting recieved1 === message3", recieved1, message3); // object
																						// equality
		Assert.assertEquals("Expecting recieved2 === message1", recieved2, message1); // object
																						// equality
		Assert.assertEquals("Expecting recieved3 === message2", recieved3, message2); // object
																						// equality

		// begin FIFO order mode
		boolean fifoWritten = queue.write(new QueueControlMessage(QueueControlMessage.FIFO));

		// write messages to the queue
		Boolean message1writtenAgain = queue.write(message1);
		Boolean message2writtenAgain = queue.write(message2);
		Boolean message3writtenAgain = queue.write(message3);

		// flush the queue
		Boolean flushWrittenAgain = queue.write(new QueueControlMessage(QueueControlMessage.FLUSH));

		// test assertions
		Assert.assertTrue("Expecting wrote fifo message to queue", fifoWritten);
		Assert.assertTrue("Expecting wrote message1 to queue again", message1writtenAgain);
		Assert.assertTrue("Expecting wrote message2 to queue again", message2writtenAgain);
		Assert.assertTrue("Expecting wrote message3 to queue again", message3writtenAgain);
		Assert.assertTrue("Expecting wrote flush message to queue again", flushWrittenAgain);

		// test that 3 messages were received
		Assert.assertEquals("Expecting received 3 messages", messagesReceived.size(), 3);

		// get the messages
		IPipeMessage recieved1Again = messagesReceived.firstElement();
		messagesReceived.remove(recieved1Again);
		IPipeMessage recieved2Again = messagesReceived.firstElement();
		messagesReceived.remove(recieved2Again);
		IPipeMessage recieved3Again = messagesReceived.firstElement();
		messagesReceived.remove(recieved3Again);

		// test message order is FIFO
		Assert.assertEquals("Expecting recieved1Again === message1", recieved1Again, message1); // object
																								// equality
		Assert.assertEquals("Expecting recieved2Again === message2", recieved2Again, message2); // object
																								// equality
		Assert.assertEquals("Expecting recieved3Again === message3", recieved3Again, message3); // object
																								// equality
		Assert.assertEquals("Expecting recieved1Again is priority med ", recieved1Again.getPriority(), Message.PRIORITY_MED);
		Assert.assertEquals("Expecting recieved2Again is priority low ", recieved2Again.getPriority(), Message.PRIORITY_LOW);
		Assert.assertEquals("Expecting recieved3Again is priority high ", recieved3Again.getPriority(), Message.PRIORITY_HIGH);

	}
}
