package javax.swing;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Hashtable;

class DebugGraphicsInfo
{
  Color flashColor = Color.red;
  int flashTime = 100;
  int flashCount = 2;
  Hashtable<JComponent, Integer> componentToDebug;
  JFrame debugFrame = null;
  PrintStream stream = System.out;
  
  DebugGraphicsInfo() {}
  
  void setDebugOptions(JComponent paramJComponent, int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    if (componentToDebug == null) {
      componentToDebug = new Hashtable();
    }
    if (paramInt > 0) {
      componentToDebug.put(paramJComponent, Integer.valueOf(paramInt));
    } else {
      componentToDebug.remove(paramJComponent);
    }
  }
  
  int getDebugOptions(JComponent paramJComponent)
  {
    if (componentToDebug == null) {
      return 0;
    }
    Integer localInteger = (Integer)componentToDebug.get(paramJComponent);
    return localInteger == null ? 0 : localInteger.intValue();
  }
  
  void log(String paramString)
  {
    stream.println(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DebugGraphicsInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */