package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class FIFOQueueEnumerator<T>
  implements Enumeration<T>
{
  Queue<T> queue;
  QueueElement<T> cursor;
  
  FIFOQueueEnumerator(Queue<T> paramQueue)
  {
    queue = paramQueue;
    cursor = tail;
  }
  
  public boolean hasMoreElements()
  {
    return cursor != null;
  }
  
  public T nextElement()
  {
    synchronized (queue)
    {
      if (cursor != null)
      {
        QueueElement localQueueElement = cursor;
        cursor = cursor.prev;
        return (T)obj;
      }
    }
    throw new NoSuchElementException("FIFOQueueEnumerator");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\FIFOQueueEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */