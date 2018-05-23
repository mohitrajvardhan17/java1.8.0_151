package javax.xml.xpath;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.PrintStream;
import java.io.PrintWriter;

public class XPathException
  extends Exception
{
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("cause", Throwable.class) };
  private static final long serialVersionUID = -1837080260374986980L;
  
  public XPathException(String paramString)
  {
    super(paramString);
    if (paramString == null) {
      throw new NullPointerException("message can't be null");
    }
  }
  
  public XPathException(Throwable paramThrowable)
  {
    super(paramThrowable);
    if (paramThrowable == null) {
      throw new NullPointerException("cause can't be null");
    }
  }
  
  public Throwable getCause()
  {
    return super.getCause();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("cause", super.getCause());
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Throwable localThrowable = (Throwable)localGetField.get("cause", null);
    if ((super.getCause() == null) && (localThrowable != null)) {
      try
      {
        super.initCause(localThrowable);
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw new InvalidClassException("Inconsistent state: two causes");
      }
    }
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    if (getCause() != null)
    {
      getCause().printStackTrace(paramPrintStream);
      paramPrintStream.println("--------------- linked to ------------------");
    }
    super.printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    if (getCause() != null)
    {
      getCause().printStackTrace(paramPrintWriter);
      paramPrintWriter.println("--------------- linked to ------------------");
    }
    super.printStackTrace(paramPrintWriter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */