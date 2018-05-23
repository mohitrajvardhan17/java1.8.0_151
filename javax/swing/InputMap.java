package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class InputMap
  implements Serializable
{
  private transient ArrayTable arrayTable;
  private InputMap parent;
  
  public InputMap() {}
  
  public void setParent(InputMap paramInputMap)
  {
    parent = paramInputMap;
  }
  
  public InputMap getParent()
  {
    return parent;
  }
  
  public void put(KeyStroke paramKeyStroke, Object paramObject)
  {
    if (paramKeyStroke == null) {
      return;
    }
    if (paramObject == null)
    {
      remove(paramKeyStroke);
    }
    else
    {
      if (arrayTable == null) {
        arrayTable = new ArrayTable();
      }
      arrayTable.put(paramKeyStroke, paramObject);
    }
  }
  
  public Object get(KeyStroke paramKeyStroke)
  {
    if (arrayTable == null)
    {
      localObject = getParent();
      if (localObject != null) {
        return ((InputMap)localObject).get(paramKeyStroke);
      }
      return null;
    }
    Object localObject = arrayTable.get(paramKeyStroke);
    if (localObject == null)
    {
      InputMap localInputMap = getParent();
      if (localInputMap != null) {
        return localInputMap.get(paramKeyStroke);
      }
    }
    return localObject;
  }
  
  public void remove(KeyStroke paramKeyStroke)
  {
    if (arrayTable != null) {
      arrayTable.remove(paramKeyStroke);
    }
  }
  
  public void clear()
  {
    if (arrayTable != null) {
      arrayTable.clear();
    }
  }
  
  public KeyStroke[] keys()
  {
    if (arrayTable == null) {
      return null;
    }
    KeyStroke[] arrayOfKeyStroke = new KeyStroke[arrayTable.size()];
    arrayTable.getKeys(arrayOfKeyStroke);
    return arrayOfKeyStroke;
  }
  
  public int size()
  {
    if (arrayTable == null) {
      return 0;
    }
    return arrayTable.size();
  }
  
  public KeyStroke[] allKeys()
  {
    int i = size();
    InputMap localInputMap = getParent();
    if (i == 0)
    {
      if (localInputMap != null) {
        return localInputMap.allKeys();
      }
      return keys();
    }
    if (localInputMap == null) {
      return keys();
    }
    KeyStroke[] arrayOfKeyStroke1 = keys();
    KeyStroke[] arrayOfKeyStroke2 = localInputMap.allKeys();
    if (arrayOfKeyStroke2 == null) {
      return arrayOfKeyStroke1;
    }
    if (arrayOfKeyStroke1 == null) {
      return arrayOfKeyStroke2;
    }
    HashMap localHashMap = new HashMap();
    for (int j = arrayOfKeyStroke1.length - 1; j >= 0; j--) {
      localHashMap.put(arrayOfKeyStroke1[j], arrayOfKeyStroke1[j]);
    }
    for (j = arrayOfKeyStroke2.length - 1; j >= 0; j--) {
      localHashMap.put(arrayOfKeyStroke2[j], arrayOfKeyStroke2[j]);
    }
    KeyStroke[] arrayOfKeyStroke3 = new KeyStroke[localHashMap.size()];
    return (KeyStroke[])localHashMap.keySet().toArray(arrayOfKeyStroke3);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    ArrayTable.writeArrayTable(paramObjectOutputStream, arrayTable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = paramObjectInputStream.readInt() - 1; i >= 0; i--) {
      put((KeyStroke)paramObjectInputStream.readObject(), paramObjectInputStream.readObject());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\InputMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */