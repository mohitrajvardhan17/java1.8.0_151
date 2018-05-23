package java.awt.event;

import java.awt.AWTEvent;

public class ActionEvent
  extends AWTEvent
{
  public static final int SHIFT_MASK = 1;
  public static final int CTRL_MASK = 2;
  public static final int META_MASK = 4;
  public static final int ALT_MASK = 8;
  public static final int ACTION_FIRST = 1001;
  public static final int ACTION_LAST = 1001;
  public static final int ACTION_PERFORMED = 1001;
  String actionCommand;
  long when;
  int modifiers;
  private static final long serialVersionUID = -7671078796273832149L;
  
  public ActionEvent(Object paramObject, int paramInt, String paramString)
  {
    this(paramObject, paramInt, paramString, 0);
  }
  
  public ActionEvent(Object paramObject, int paramInt1, String paramString, int paramInt2)
  {
    this(paramObject, paramInt1, paramString, 0L, paramInt2);
  }
  
  public ActionEvent(Object paramObject, int paramInt1, String paramString, long paramLong, int paramInt2)
  {
    super(paramObject, paramInt1);
    actionCommand = paramString;
    when = paramLong;
    modifiers = paramInt2;
  }
  
  public String getActionCommand()
  {
    return actionCommand;
  }
  
  public long getWhen()
  {
    return when;
  }
  
  public int getModifiers()
  {
    return modifiers;
  }
  
  public String paramString()
  {
    String str;
    switch (id)
    {
    case 1001: 
      str = "ACTION_PERFORMED";
      break;
    default: 
      str = "unknown type";
    }
    return str + ",cmd=" + actionCommand + ",when=" + when + ",modifiers=" + KeyEvent.getKeyModifiersText(modifiers);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\ActionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */