package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class PreferenceChangeEvent
  extends EventObject
{
  private String key;
  private String newValue;
  private static final long serialVersionUID = 793724513368024975L;
  
  public PreferenceChangeEvent(Preferences paramPreferences, String paramString1, String paramString2)
  {
    super(paramPreferences);
    key = paramString1;
    newValue = paramString2;
  }
  
  public Preferences getNode()
  {
    return (Preferences)getSource();
  }
  
  public String getKey()
  {
    return key;
  }
  
  public String getNewValue()
  {
    return newValue;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\PreferenceChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */