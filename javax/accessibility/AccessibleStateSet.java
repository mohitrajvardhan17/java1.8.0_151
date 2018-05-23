package javax.accessibility;

import java.util.Vector;

public class AccessibleStateSet
{
  protected Vector<AccessibleState> states = null;
  
  public AccessibleStateSet()
  {
    states = null;
  }
  
  public AccessibleStateSet(AccessibleState[] paramArrayOfAccessibleState)
  {
    if (paramArrayOfAccessibleState.length != 0)
    {
      states = new Vector(paramArrayOfAccessibleState.length);
      for (int i = 0; i < paramArrayOfAccessibleState.length; i++) {
        if (!states.contains(paramArrayOfAccessibleState[i])) {
          states.addElement(paramArrayOfAccessibleState[i]);
        }
      }
    }
  }
  
  public boolean add(AccessibleState paramAccessibleState)
  {
    if (states == null) {
      states = new Vector();
    }
    if (!states.contains(paramAccessibleState))
    {
      states.addElement(paramAccessibleState);
      return true;
    }
    return false;
  }
  
  public void addAll(AccessibleState[] paramArrayOfAccessibleState)
  {
    if (paramArrayOfAccessibleState.length != 0)
    {
      if (states == null) {
        states = new Vector(paramArrayOfAccessibleState.length);
      }
      for (int i = 0; i < paramArrayOfAccessibleState.length; i++) {
        if (!states.contains(paramArrayOfAccessibleState[i])) {
          states.addElement(paramArrayOfAccessibleState[i]);
        }
      }
    }
  }
  
  public boolean remove(AccessibleState paramAccessibleState)
  {
    if (states == null) {
      return false;
    }
    return states.removeElement(paramAccessibleState);
  }
  
  public void clear()
  {
    if (states != null) {
      states.removeAllElements();
    }
  }
  
  public boolean contains(AccessibleState paramAccessibleState)
  {
    if (states == null) {
      return false;
    }
    return states.contains(paramAccessibleState);
  }
  
  public AccessibleState[] toArray()
  {
    if (states == null) {
      return new AccessibleState[0];
    }
    AccessibleState[] arrayOfAccessibleState = new AccessibleState[states.size()];
    for (int i = 0; i < arrayOfAccessibleState.length; i++) {
      arrayOfAccessibleState[i] = ((AccessibleState)states.elementAt(i));
    }
    return arrayOfAccessibleState;
  }
  
  public String toString()
  {
    String str = null;
    if ((states != null) && (states.size() > 0))
    {
      str = ((AccessibleState)states.elementAt(0)).toDisplayString();
      for (int i = 1; i < states.size(); i++) {
        str = str + "," + ((AccessibleState)states.elementAt(i)).toDisplayString();
      }
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleStateSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */