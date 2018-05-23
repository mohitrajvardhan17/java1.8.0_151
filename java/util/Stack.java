package java.util;

public class Stack<E>
  extends Vector<E>
{
  private static final long serialVersionUID = 1224463164541339165L;
  
  public Stack() {}
  
  public E push(E paramE)
  {
    addElement(paramE);
    return paramE;
  }
  
  public synchronized E pop()
  {
    int i = size();
    Object localObject = peek();
    removeElementAt(i - 1);
    return (E)localObject;
  }
  
  public synchronized E peek()
  {
    int i = size();
    if (i == 0) {
      throw new EmptyStackException();
    }
    return (E)elementAt(i - 1);
  }
  
  public boolean empty()
  {
    return size() == 0;
  }
  
  public synchronized int search(Object paramObject)
  {
    int i = lastIndexOf(paramObject);
    if (i >= 0) {
      return size() - i;
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */