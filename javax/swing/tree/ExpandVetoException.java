package javax.swing.tree;

import javax.swing.event.TreeExpansionEvent;

public class ExpandVetoException
  extends Exception
{
  protected TreeExpansionEvent event;
  
  public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent)
  {
    this(paramTreeExpansionEvent, null);
  }
  
  public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent, String paramString)
  {
    super(paramString);
    event = paramTreeExpansionEvent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\ExpandVetoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */