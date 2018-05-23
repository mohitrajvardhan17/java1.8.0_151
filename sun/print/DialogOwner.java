package sun.print;

import java.awt.Frame;
import javax.print.attribute.PrintRequestAttribute;

public final class DialogOwner
  implements PrintRequestAttribute
{
  private Frame dlgOwner;
  
  public DialogOwner(Frame paramFrame)
  {
    dlgOwner = paramFrame;
  }
  
  public Frame getOwner()
  {
    return dlgOwner;
  }
  
  public final Class getCategory()
  {
    return DialogOwner.class;
  }
  
  public final String getName()
  {
    return "dialog-owner";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\DialogOwner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */