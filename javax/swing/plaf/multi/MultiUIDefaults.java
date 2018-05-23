package javax.swing.plaf.multi;

import java.io.PrintStream;
import javax.swing.UIDefaults;

class MultiUIDefaults
  extends UIDefaults
{
  MultiUIDefaults(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  protected void getUIError(String paramString)
  {
    System.err.println("Multiplexing LAF:  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\multi\MultiUIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */