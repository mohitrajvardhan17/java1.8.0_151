package java.awt;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class MenuShortcut
  implements Serializable
{
  int key;
  boolean usesShift;
  private static final long serialVersionUID = 143448358473180225L;
  
  public MenuShortcut(int paramInt)
  {
    this(paramInt, false);
  }
  
  public MenuShortcut(int paramInt, boolean paramBoolean)
  {
    key = paramInt;
    usesShift = paramBoolean;
  }
  
  public int getKey()
  {
    return key;
  }
  
  public boolean usesShiftModifier()
  {
    return usesShift;
  }
  
  public boolean equals(MenuShortcut paramMenuShortcut)
  {
    return (paramMenuShortcut != null) && (paramMenuShortcut.getKey() == key) && (paramMenuShortcut.usesShiftModifier() == usesShift);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MenuShortcut)) {
      return equals((MenuShortcut)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return usesShift ? key ^ 0xFFFFFFFF : key;
  }
  
  public String toString()
  {
    int i = 0;
    if (!GraphicsEnvironment.isHeadless()) {
      i = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    }
    if (usesShiftModifier()) {
      i |= 0x1;
    }
    return KeyEvent.getKeyModifiersText(i) + "+" + KeyEvent.getKeyText(key);
  }
  
  protected String paramString()
  {
    String str = "key=" + key;
    if (usesShiftModifier()) {
      str = str + ",usesShiftModifier";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MenuShortcut.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */