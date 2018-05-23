package sun.misc;

class QueueElement<T>
{
  QueueElement<T> next = null;
  QueueElement<T> prev = null;
  T obj = null;
  
  QueueElement(T paramT)
  {
    obj = paramT;
  }
  
  public String toString()
  {
    return "QueueElement[obj=" + obj + (prev == null ? " null" : " prev") + (next == null ? " null" : " next") + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\QueueElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */