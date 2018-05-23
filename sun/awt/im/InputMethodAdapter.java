package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.im.spi.InputMethod;

public abstract class InputMethodAdapter
  implements InputMethod
{
  private Component clientComponent;
  
  public InputMethodAdapter() {}
  
  void setClientComponent(Component paramComponent)
  {
    clientComponent = paramComponent;
  }
  
  protected Component getClientComponent()
  {
    return clientComponent;
  }
  
  protected boolean haveActiveClient()
  {
    return (clientComponent != null) && (clientComponent.getInputMethodRequests() != null);
  }
  
  protected void setAWTFocussedComponent(Component paramComponent) {}
  
  protected boolean supportsBelowTheSpot()
  {
    return false;
  }
  
  protected void stopListening() {}
  
  public void notifyClientWindowChange(Rectangle paramRectangle) {}
  
  public void reconvert()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract void disableInputMethod();
  
  public abstract String getNativeInputMethodInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\InputMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */