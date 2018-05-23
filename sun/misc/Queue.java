package sun.misc;

import java.io.PrintStream;
import java.util.Enumeration;

public class Queue<T>
{
  int length = 0;
  QueueElement<T> head = null;
  QueueElement<T> tail = null;
  
  public Queue() {}
  
  public synchronized void enqueue(T paramT)
  {
    QueueElement localQueueElement = new QueueElement(paramT);
    if (head == null)
    {
      head = localQueueElement;
      tail = localQueueElement;
      length = 1;
    }
    else
    {
      next = head;
      head.prev = localQueueElement;
      head = localQueueElement;
      length += 1;
    }
    notify();
  }
  
  public T dequeue()
    throws InterruptedException
  {
    return (T)dequeue(0L);
  }
  
  public synchronized T dequeue(long paramLong)
    throws InterruptedException
  {
    while (tail == null) {
      wait(paramLong);
    }
    QueueElement localQueueElement = tail;
    tail = prev;
    if (tail == null) {
      head = null;
    } else {
      tail.next = null;
    }
    length -= 1;
    return (T)obj;
  }
  
  public synchronized boolean isEmpty()
  {
    return tail == null;
  }
  
  public final synchronized Enumeration<T> elements()
  {
    return new LIFOQueueEnumerator(this);
  }
  
  public final synchronized Enumeration<T> reverseElements()
  {
    return new FIFOQueueEnumerator(this);
  }
  
  public synchronized void dump(String paramString)
  {
    System.err.println(">> " + paramString);
    System.err.println("[" + length + " elt(s); head = " + (head == null ? "null" : new StringBuilder().append(head.obj).append("").toString()) + " tail = " + (tail == null ? "null" : new StringBuilder().append(tail.obj).append("").toString()));
    QueueElement localQueueElement1 = head;
    QueueElement localQueueElement2 = null;
    while (localQueueElement1 != null)
    {
      System.err.println("  " + localQueueElement1);
      localQueueElement2 = localQueueElement1;
      localQueueElement1 = next;
    }
    if (localQueueElement2 != tail) {
      System.err.println("  tail != last: " + tail + ", " + localQueueElement2);
    }
    System.err.println("]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Queue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */