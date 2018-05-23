package java.beans;

import java.applet.Applet;
import java.beans.beancontext.BeanContext;

public abstract interface AppletInitializer
{
  public abstract void initialize(Applet paramApplet, BeanContext paramBeanContext);
  
  public abstract void activate(Applet paramApplet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\AppletInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */