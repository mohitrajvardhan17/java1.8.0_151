package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class VetoableChangeSupport
  implements Serializable
{
  private VetoableChangeListenerMap map = new VetoableChangeListenerMap(null);
  private Object source;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("vetoableChangeSupportSerializedDataVersion", Integer.TYPE) };
  static final long serialVersionUID = -5090210921595982017L;
  
  public VetoableChangeSupport(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    source = paramObject;
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener == null) {
      return;
    }
    if ((paramVetoableChangeListener instanceof VetoableChangeListenerProxy))
    {
      VetoableChangeListenerProxy localVetoableChangeListenerProxy = (VetoableChangeListenerProxy)paramVetoableChangeListener;
      addVetoableChangeListener(localVetoableChangeListenerProxy.getPropertyName(), (VetoableChangeListener)localVetoableChangeListenerProxy.getListener());
    }
    else
    {
      map.add(null, paramVetoableChangeListener);
    }
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener == null) {
      return;
    }
    if ((paramVetoableChangeListener instanceof VetoableChangeListenerProxy))
    {
      VetoableChangeListenerProxy localVetoableChangeListenerProxy = (VetoableChangeListenerProxy)paramVetoableChangeListener;
      removeVetoableChangeListener(localVetoableChangeListenerProxy.getPropertyName(), (VetoableChangeListener)localVetoableChangeListenerProxy.getListener());
    }
    else
    {
      map.remove(null, paramVetoableChangeListener);
    }
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners()
  {
    return (VetoableChangeListener[])map.getListeners();
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    if ((paramVetoableChangeListener == null) || (paramString == null)) {
      return;
    }
    paramVetoableChangeListener = map.extract(paramVetoableChangeListener);
    if (paramVetoableChangeListener != null) {
      map.add(paramString, paramVetoableChangeListener);
    }
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    if ((paramVetoableChangeListener == null) || (paramString == null)) {
      return;
    }
    paramVetoableChangeListener = map.extract(paramVetoableChangeListener);
    if (paramVetoableChangeListener != null) {
      map.remove(paramString, paramVetoableChangeListener);
    }
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners(String paramString)
  {
    return (VetoableChangeListener[])map.getListeners(paramString);
  }
  
  public void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
    throws PropertyVetoException
  {
    if ((paramObject1 == null) || (paramObject2 == null) || (!paramObject1.equals(paramObject2))) {
      fireVetoableChange(new PropertyChangeEvent(source, paramString, paramObject1, paramObject2));
    }
  }
  
  public void fireVetoableChange(String paramString, int paramInt1, int paramInt2)
    throws PropertyVetoException
  {
    if (paramInt1 != paramInt2) {
      fireVetoableChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    }
  }
  
  public void fireVetoableChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws PropertyVetoException
  {
    if (paramBoolean1 != paramBoolean2) {
      fireVetoableChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2));
    }
  }
  
  public void fireVetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    Object localObject1 = paramPropertyChangeEvent.getOldValue();
    Object localObject2 = paramPropertyChangeEvent.getNewValue();
    if ((localObject1 == null) || (localObject2 == null) || (!localObject1.equals(localObject2)))
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      VetoableChangeListener[] arrayOfVetoableChangeListener = (VetoableChangeListener[])map.get(null);
      Object localObject3 = str != null ? (VetoableChangeListener[])map.get(str) : null;
      Object localObject4;
      if (arrayOfVetoableChangeListener == null)
      {
        localObject4 = localObject3;
      }
      else if (localObject3 == null)
      {
        localObject4 = arrayOfVetoableChangeListener;
      }
      else
      {
        localObject4 = new VetoableChangeListener[arrayOfVetoableChangeListener.length + localObject3.length];
        System.arraycopy(arrayOfVetoableChangeListener, 0, localObject4, 0, arrayOfVetoableChangeListener.length);
        System.arraycopy(localObject3, 0, localObject4, arrayOfVetoableChangeListener.length, localObject3.length);
      }
      if (localObject4 != null)
      {
        int i = 0;
        try
        {
          while (i < localObject4.length)
          {
            localObject4[i].vetoableChange(paramPropertyChangeEvent);
            i++;
          }
        }
        catch (PropertyVetoException localPropertyVetoException1)
        {
          paramPropertyChangeEvent = new PropertyChangeEvent(source, str, localObject2, localObject1);
          for (int j = 0; j < i; j++) {
            try
            {
              localObject4[j].vetoableChange(paramPropertyChangeEvent);
            }
            catch (PropertyVetoException localPropertyVetoException2) {}
          }
          throw localPropertyVetoException1;
        }
      }
    }
  }
  
  public boolean hasListeners(String paramString)
  {
    return map.hasListeners(paramString);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = null;
    VetoableChangeListener[] arrayOfVetoableChangeListener = null;
    Object localObject1;
    VetoableChangeSupport localVetoableChangeSupport;
    synchronized (map)
    {
      localObject1 = map.getEntries().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
        String str = (String)localEntry.getKey();
        if (str == null)
        {
          arrayOfVetoableChangeListener = (VetoableChangeListener[])localEntry.getValue();
        }
        else
        {
          if (localHashtable == null) {
            localHashtable = new Hashtable();
          }
          localVetoableChangeSupport = new VetoableChangeSupport(source);
          map.set(null, (EventListener[])localEntry.getValue());
          localHashtable.put(str, localVetoableChangeSupport);
        }
      }
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("children", localHashtable);
    ((ObjectOutputStream.PutField)???).put("source", source);
    ((ObjectOutputStream.PutField)???).put("vetoableChangeSupportSerializedDataVersion", 2);
    paramObjectOutputStream.writeFields();
    if (arrayOfVetoableChangeListener != null) {
      for (localVetoableChangeSupport : arrayOfVetoableChangeListener) {
        if ((localVetoableChangeSupport instanceof Serializable)) {
          paramObjectOutputStream.writeObject(localVetoableChangeSupport);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    map = new VetoableChangeListenerMap(null);
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Hashtable localHashtable = (Hashtable)localGetField.get("children", null);
    source = localGetField.get("source", null);
    localGetField.get("vetoableChangeSupportSerializedDataVersion", 2);
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject())) {
      map.add(null, (VetoableChangeListener)localObject);
    }
    if (localHashtable != null)
    {
      Iterator localIterator = localHashtable.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        for (VetoableChangeListener localVetoableChangeListener : ((VetoableChangeSupport)localEntry.getValue()).getVetoableChangeListeners()) {
          map.add((String)localEntry.getKey(), localVetoableChangeListener);
        }
      }
    }
  }
  
  private static final class VetoableChangeListenerMap
    extends ChangeListenerMap<VetoableChangeListener>
  {
    private static final VetoableChangeListener[] EMPTY = new VetoableChangeListener[0];
    
    private VetoableChangeListenerMap() {}
    
    protected VetoableChangeListener[] newArray(int paramInt)
    {
      return 0 < paramInt ? new VetoableChangeListener[paramInt] : EMPTY;
    }
    
    protected VetoableChangeListener newProxy(String paramString, VetoableChangeListener paramVetoableChangeListener)
    {
      return new VetoableChangeListenerProxy(paramString, paramVetoableChangeListener);
    }
    
    public final VetoableChangeListener extract(VetoableChangeListener paramVetoableChangeListener)
    {
      while ((paramVetoableChangeListener instanceof VetoableChangeListenerProxy)) {
        paramVetoableChangeListener = (VetoableChangeListener)((VetoableChangeListenerProxy)paramVetoableChangeListener).getListener();
      }
      return paramVetoableChangeListener;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\VetoableChangeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */