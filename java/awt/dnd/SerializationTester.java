package java.awt.dnd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

final class SerializationTester
{
  private static ObjectOutputStream stream;
  
  static boolean test(Object paramObject)
  {
    if (!(paramObject instanceof Serializable)) {
      return false;
    }
    try
    {
      stream.writeObject(paramObject);
      boolean bool;
      return true;
    }
    catch (IOException localIOException2)
    {
      bool = false;
      return bool;
    }
    finally
    {
      try
      {
        stream.reset();
      }
      catch (IOException localIOException4) {}
    }
  }
  
  private SerializationTester() {}
  
  static
  {
    try
    {
      stream = new ObjectOutputStream(new OutputStream()
      {
        public void write(int paramAnonymousInt) {}
      });
    }
    catch (IOException localIOException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\SerializationTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */