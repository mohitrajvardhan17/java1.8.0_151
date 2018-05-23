package javax.naming;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

final class NameImplEnumerator
  implements Enumeration<String>
{
  Vector<String> vector;
  int count;
  int limit;
  
  NameImplEnumerator(Vector<String> paramVector, int paramInt1, int paramInt2)
  {
    vector = paramVector;
    count = paramInt1;
    limit = paramInt2;
  }
  
  public boolean hasMoreElements()
  {
    return count < limit;
  }
  
  public String nextElement()
  {
    if (count < limit) {
      return (String)vector.elementAt(count++);
    }
    throw new NoSuchElementException("NameImplEnumerator");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\NameImplEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */