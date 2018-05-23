package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

class ArrayTable
  implements Cloneable
{
  private Object table = null;
  private static final int ARRAY_BOUNDARY = 8;
  
  ArrayTable() {}
  
  static void writeArrayTable(ObjectOutputStream paramObjectOutputStream, ArrayTable paramArrayTable)
    throws IOException
  {
    Object[] arrayOfObject1;
    if ((paramArrayTable == null) || ((arrayOfObject1 = paramArrayTable.getKeys(null)) == null))
    {
      paramObjectOutputStream.writeInt(0);
    }
    else
    {
      int i = 0;
      for (int j = 0; j < arrayOfObject1.length; j++)
      {
        Object localObject1 = arrayOfObject1[j];
        if ((((localObject1 instanceof Serializable)) && ((paramArrayTable.get(localObject1) instanceof Serializable))) || (((localObject1 instanceof ClientPropertyKey)) && (((ClientPropertyKey)localObject1).getReportValueNotSerializable()))) {
          i++;
        } else {
          arrayOfObject1[j] = null;
        }
      }
      paramObjectOutputStream.writeInt(i);
      if (i > 0) {
        for (Object localObject2 : arrayOfObject1) {
          if (localObject2 != null)
          {
            paramObjectOutputStream.writeObject(localObject2);
            paramObjectOutputStream.writeObject(paramArrayTable.get(localObject2));
            i--;
            if (i == 0) {
              break;
            }
          }
        }
      }
    }
  }
  
  public void put(Object paramObject1, Object paramObject2)
  {
    if (table == null)
    {
      table = new Object[] { paramObject1, paramObject2 };
    }
    else
    {
      int i = size();
      if (i < 8)
      {
        Object[] arrayOfObject1;
        int j;
        if (containsKey(paramObject1))
        {
          arrayOfObject1 = (Object[])table;
          for (j = 0; j < arrayOfObject1.length - 1; j += 2) {
            if (arrayOfObject1[j].equals(paramObject1))
            {
              arrayOfObject1[(j + 1)] = paramObject2;
              break;
            }
          }
        }
        else
        {
          arrayOfObject1 = (Object[])table;
          j = arrayOfObject1.length;
          Object[] arrayOfObject2 = new Object[j + 2];
          System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, j);
          arrayOfObject2[j] = paramObject1;
          arrayOfObject2[(j + 1)] = paramObject2;
          table = arrayOfObject2;
        }
      }
      else
      {
        if ((i == 8) && (isArray())) {
          grow();
        }
        ((Hashtable)table).put(paramObject1, paramObject2);
      }
    }
  }
  
  public Object get(Object paramObject)
  {
    Object localObject = null;
    if (table != null) {
      if (isArray())
      {
        Object[] arrayOfObject = (Object[])table;
        for (int i = 0; i < arrayOfObject.length - 1; i += 2) {
          if (arrayOfObject[i].equals(paramObject))
          {
            localObject = arrayOfObject[(i + 1)];
            break;
          }
        }
      }
      else
      {
        localObject = ((Hashtable)table).get(paramObject);
      }
    }
    return localObject;
  }
  
  public int size()
  {
    if (table == null) {
      return 0;
    }
    int i;
    if (isArray()) {
      i = ((Object[])table).length / 2;
    } else {
      i = ((Hashtable)table).size();
    }
    return i;
  }
  
  public boolean containsKey(Object paramObject)
  {
    boolean bool = false;
    if (table != null) {
      if (isArray())
      {
        Object[] arrayOfObject = (Object[])table;
        for (int i = 0; i < arrayOfObject.length - 1; i += 2) {
          if (arrayOfObject[i].equals(paramObject))
          {
            bool = true;
            break;
          }
        }
      }
      else
      {
        bool = ((Hashtable)table).containsKey(paramObject);
      }
    }
    return bool;
  }
  
  public Object remove(Object paramObject)
  {
    Object localObject = null;
    if (paramObject == null) {
      return null;
    }
    if (table != null)
    {
      if (isArray())
      {
        int i = -1;
        Object[] arrayOfObject1 = (Object[])table;
        for (int j = arrayOfObject1.length - 2; j >= 0; j -= 2) {
          if (arrayOfObject1[j].equals(paramObject))
          {
            i = j;
            localObject = arrayOfObject1[(j + 1)];
            break;
          }
        }
        if (i != -1)
        {
          Object[] arrayOfObject2 = new Object[arrayOfObject1.length - 2];
          System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, i);
          if (i < arrayOfObject2.length) {
            System.arraycopy(arrayOfObject1, i + 2, arrayOfObject2, i, arrayOfObject2.length - i);
          }
          table = (arrayOfObject2.length == 0 ? null : arrayOfObject2);
        }
      }
      else
      {
        localObject = ((Hashtable)table).remove(paramObject);
      }
      if ((size() == 7) && (!isArray())) {
        shrink();
      }
    }
    return localObject;
  }
  
  public void clear()
  {
    table = null;
  }
  
  public Object clone()
  {
    ArrayTable localArrayTable = new ArrayTable();
    Object localObject1;
    if (isArray())
    {
      localObject1 = (Object[])table;
      for (int i = 0; i < localObject1.length - 1; i += 2) {
        localArrayTable.put(localObject1[i], localObject1[(i + 1)]);
      }
    }
    else
    {
      localObject1 = (Hashtable)table;
      Enumeration localEnumeration = ((Hashtable)localObject1).keys();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject2 = localEnumeration.nextElement();
        localArrayTable.put(localObject2, ((Hashtable)localObject1).get(localObject2));
      }
    }
    return localArrayTable;
  }
  
  public Object[] getKeys(Object[] paramArrayOfObject)
  {
    if (table == null) {
      return null;
    }
    Object localObject;
    int j;
    if (isArray())
    {
      localObject = (Object[])table;
      if (paramArrayOfObject == null) {
        paramArrayOfObject = new Object[localObject.length / 2];
      }
      int i = 0;
      for (j = 0; i < localObject.length - 1; j++)
      {
        paramArrayOfObject[j] = localObject[i];
        i += 2;
      }
    }
    else
    {
      localObject = (Hashtable)table;
      Enumeration localEnumeration = ((Hashtable)localObject).keys();
      j = ((Hashtable)localObject).size();
      if (paramArrayOfObject == null) {
        paramArrayOfObject = new Object[j];
      }
      while (j > 0) {
        paramArrayOfObject[(--j)] = localEnumeration.nextElement();
      }
    }
    return paramArrayOfObject;
  }
  
  private boolean isArray()
  {
    return table instanceof Object[];
  }
  
  private void grow()
  {
    Object[] arrayOfObject = (Object[])table;
    Hashtable localHashtable = new Hashtable(arrayOfObject.length / 2);
    for (int i = 0; i < arrayOfObject.length; i += 2) {
      localHashtable.put(arrayOfObject[i], arrayOfObject[(i + 1)]);
    }
    table = localHashtable;
  }
  
  private void shrink()
  {
    Hashtable localHashtable = (Hashtable)table;
    Object[] arrayOfObject = new Object[localHashtable.size() * 2];
    Enumeration localEnumeration = localHashtable.keys();
    for (int i = 0; localEnumeration.hasMoreElements(); i += 2)
    {
      Object localObject = localEnumeration.nextElement();
      arrayOfObject[i] = localObject;
      arrayOfObject[(i + 1)] = localHashtable.get(localObject);
    }
    table = arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ArrayTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */