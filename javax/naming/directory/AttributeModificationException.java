package javax.naming.directory;

import javax.naming.NamingException;

public class AttributeModificationException
  extends NamingException
{
  private ModificationItem[] unexecs = null;
  private static final long serialVersionUID = 8060676069678710186L;
  
  public AttributeModificationException(String paramString)
  {
    super(paramString);
  }
  
  public AttributeModificationException() {}
  
  public void setUnexecutedModifications(ModificationItem[] paramArrayOfModificationItem)
  {
    unexecs = paramArrayOfModificationItem;
  }
  
  public ModificationItem[] getUnexecutedModifications()
  {
    return unexecs;
  }
  
  public String toString()
  {
    String str = super.toString();
    if (unexecs != null) {
      str = str + "First unexecuted modification: " + unexecs[0].toString();
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\AttributeModificationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */