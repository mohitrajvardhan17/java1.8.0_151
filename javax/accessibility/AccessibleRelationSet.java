package javax.accessibility;

import java.util.Vector;

public class AccessibleRelationSet
{
  protected Vector<AccessibleRelation> relations = null;
  
  public AccessibleRelationSet()
  {
    relations = null;
  }
  
  public AccessibleRelationSet(AccessibleRelation[] paramArrayOfAccessibleRelation)
  {
    if (paramArrayOfAccessibleRelation.length != 0)
    {
      relations = new Vector(paramArrayOfAccessibleRelation.length);
      for (int i = 0; i < paramArrayOfAccessibleRelation.length; i++) {
        add(paramArrayOfAccessibleRelation[i]);
      }
    }
  }
  
  public boolean add(AccessibleRelation paramAccessibleRelation)
  {
    if (relations == null) {
      relations = new Vector();
    }
    AccessibleRelation localAccessibleRelation = get(paramAccessibleRelation.getKey());
    if (localAccessibleRelation == null)
    {
      relations.addElement(paramAccessibleRelation);
      return true;
    }
    Object[] arrayOfObject1 = localAccessibleRelation.getTarget();
    Object[] arrayOfObject2 = paramAccessibleRelation.getTarget();
    int i = arrayOfObject1.length + arrayOfObject2.length;
    Object[] arrayOfObject3 = new Object[i];
    for (int j = 0; j < arrayOfObject1.length; j++) {
      arrayOfObject3[j] = arrayOfObject1[j];
    }
    j = arrayOfObject1.length;
    for (int k = 0; j < i; k++)
    {
      arrayOfObject3[j] = arrayOfObject2[k];
      j++;
    }
    localAccessibleRelation.setTarget(arrayOfObject3);
    return true;
  }
  
  public void addAll(AccessibleRelation[] paramArrayOfAccessibleRelation)
  {
    if (paramArrayOfAccessibleRelation.length != 0)
    {
      if (relations == null) {
        relations = new Vector(paramArrayOfAccessibleRelation.length);
      }
      for (int i = 0; i < paramArrayOfAccessibleRelation.length; i++) {
        add(paramArrayOfAccessibleRelation[i]);
      }
    }
  }
  
  public boolean remove(AccessibleRelation paramAccessibleRelation)
  {
    if (relations == null) {
      return false;
    }
    return relations.removeElement(paramAccessibleRelation);
  }
  
  public void clear()
  {
    if (relations != null) {
      relations.removeAllElements();
    }
  }
  
  public int size()
  {
    if (relations == null) {
      return 0;
    }
    return relations.size();
  }
  
  public boolean contains(String paramString)
  {
    return get(paramString) != null;
  }
  
  public AccessibleRelation get(String paramString)
  {
    if (relations == null) {
      return null;
    }
    int i = relations.size();
    for (int j = 0; j < i; j++)
    {
      AccessibleRelation localAccessibleRelation = (AccessibleRelation)relations.elementAt(j);
      if ((localAccessibleRelation != null) && (localAccessibleRelation.getKey().equals(paramString))) {
        return localAccessibleRelation;
      }
    }
    return null;
  }
  
  public AccessibleRelation[] toArray()
  {
    if (relations == null) {
      return new AccessibleRelation[0];
    }
    AccessibleRelation[] arrayOfAccessibleRelation = new AccessibleRelation[relations.size()];
    for (int i = 0; i < arrayOfAccessibleRelation.length; i++) {
      arrayOfAccessibleRelation[i] = ((AccessibleRelation)relations.elementAt(i));
    }
    return arrayOfAccessibleRelation;
  }
  
  public String toString()
  {
    String str = "";
    if ((relations != null) && (relations.size() > 0))
    {
      str = ((AccessibleRelation)relations.elementAt(0)).toDisplayString();
      for (int i = 1; i < relations.size(); i++) {
        str = str + "," + ((AccessibleRelation)relations.elementAt(i)).toDisplayString();
      }
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleRelationSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */