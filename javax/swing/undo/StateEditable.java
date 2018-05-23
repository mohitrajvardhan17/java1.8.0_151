package javax.swing.undo;

import java.util.Hashtable;

public abstract interface StateEditable
{
  public static final String RCSID = "$Id: StateEditable.java,v 1.2 1997/09/08 19:39:08 marklin Exp $";
  
  public abstract void storeState(Hashtable<Object, Object> paramHashtable);
  
  public abstract void restoreState(Hashtable<?, ?> paramHashtable);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\StateEditable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */