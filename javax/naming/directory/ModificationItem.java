package javax.naming.directory;

import java.io.Serializable;

public class ModificationItem
  implements Serializable
{
  private int mod_op;
  private Attribute attr;
  private static final long serialVersionUID = 7573258562534746850L;
  
  public ModificationItem(int paramInt, Attribute paramAttribute)
  {
    switch (paramInt)
    {
    case 1: 
    case 2: 
    case 3: 
      if (paramAttribute == null) {
        throw new IllegalArgumentException("Must specify non-null attribute for modification");
      }
      mod_op = paramInt;
      attr = paramAttribute;
      break;
    default: 
      throw new IllegalArgumentException("Invalid modification code " + paramInt);
    }
  }
  
  public int getModificationOp()
  {
    return mod_op;
  }
  
  public Attribute getAttribute()
  {
    return attr;
  }
  
  public String toString()
  {
    switch (mod_op)
    {
    case 1: 
      return "Add attribute: " + attr.toString();
    case 2: 
      return "Replace attribute: " + attr.toString();
    case 3: 
      return "Remove attribute: " + attr.toString();
    }
    return "";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\ModificationItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */