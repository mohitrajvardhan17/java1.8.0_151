package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class NodeChangeEvent
  extends EventObject
{
  private Preferences child;
  private static final long serialVersionUID = 8068949086596572957L;
  
  public NodeChangeEvent(Preferences paramPreferences1, Preferences paramPreferences2)
  {
    super(paramPreferences1);
    child = paramPreferences2;
  }
  
  public Preferences getParent()
  {
    return (Preferences)getSource();
  }
  
  public Preferences getChild()
  {
    return child;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws NotSerializableException
  {
    throw new NotSerializableException("Not serializable.");
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws NotSerializableException
  {
    throw new NotSerializableException("Not serializable.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\NodeChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */