package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class DesktopProperty
  implements UIDefaults.ActiveValue
{
  private static boolean updatePending;
  private static final ReferenceQueue<DesktopProperty> queue = new ReferenceQueue();
  private WeakPCL pcl;
  private final String key;
  private Object value;
  private final Object fallback;
  
  static void flushUnreferencedProperties()
  {
    WeakPCL localWeakPCL;
    while ((localWeakPCL = (WeakPCL)queue.poll()) != null) {
      localWeakPCL.dispose();
    }
  }
  
  private static synchronized void setUpdatePending(boolean paramBoolean)
  {
    updatePending = paramBoolean;
  }
  
  private static synchronized boolean isUpdatePending()
  {
    return updatePending;
  }
  
  private static void updateAllUIs()
  {
    Class localClass = UIManager.getLookAndFeel().getClass();
    if (localClass.getPackage().equals(DesktopProperty.class.getPackage())) {
      XPStyle.invalidateStyle();
    }
    Frame[] arrayOfFrame1 = Frame.getFrames();
    for (Frame localFrame : arrayOfFrame1) {
      updateWindowUI(localFrame);
    }
  }
  
  private static void updateWindowUI(Window paramWindow)
  {
    SwingUtilities.updateComponentTreeUI(paramWindow);
    Window[] arrayOfWindow1 = paramWindow.getOwnedWindows();
    for (Window localWindow : arrayOfWindow1) {
      updateWindowUI(localWindow);
    }
  }
  
  public DesktopProperty(String paramString, Object paramObject)
  {
    key = paramString;
    fallback = paramObject;
    flushUnreferencedProperties();
  }
  
  public Object createValue(UIDefaults paramUIDefaults)
  {
    if (value == null)
    {
      value = configureValue(getValueFromDesktop());
      if (value == null) {
        value = configureValue(getDefaultValue());
      }
    }
    return value;
  }
  
  protected Object getValueFromDesktop()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (pcl == null)
    {
      pcl = new WeakPCL(this, getKey(), UIManager.getLookAndFeel());
      localToolkit.addPropertyChangeListener(getKey(), pcl);
    }
    return localToolkit.getDesktopProperty(getKey());
  }
  
  protected Object getDefaultValue()
  {
    return fallback;
  }
  
  public void invalidate(LookAndFeel paramLookAndFeel)
  {
    invalidate();
  }
  
  public void invalidate()
  {
    value = null;
  }
  
  protected void updateUI()
  {
    if (!isUpdatePending())
    {
      setUpdatePending(true);
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          DesktopProperty.access$000();
          DesktopProperty.setUpdatePending(false);
        }
      };
      SwingUtilities.invokeLater(local1);
    }
  }
  
  protected Object configureValue(Object paramObject)
  {
    if (paramObject != null)
    {
      if ((paramObject instanceof Color)) {
        return new ColorUIResource((Color)paramObject);
      }
      if ((paramObject instanceof Font)) {
        return new FontUIResource((Font)paramObject);
      }
      if ((paramObject instanceof UIDefaults.LazyValue)) {
        paramObject = ((UIDefaults.LazyValue)paramObject).createValue(null);
      } else if ((paramObject instanceof UIDefaults.ActiveValue)) {
        paramObject = ((UIDefaults.ActiveValue)paramObject).createValue(null);
      }
    }
    return paramObject;
  }
  
  protected String getKey()
  {
    return key;
  }
  
  private static class WeakPCL
    extends WeakReference<DesktopProperty>
    implements PropertyChangeListener
  {
    private String key;
    private LookAndFeel laf;
    
    WeakPCL(DesktopProperty paramDesktopProperty, String paramString, LookAndFeel paramLookAndFeel)
    {
      super(DesktopProperty.queue);
      key = paramString;
      laf = paramLookAndFeel;
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      DesktopProperty localDesktopProperty = (DesktopProperty)get();
      if ((localDesktopProperty == null) || (laf != UIManager.getLookAndFeel()))
      {
        dispose();
      }
      else
      {
        localDesktopProperty.invalidate(laf);
        localDesktopProperty.updateUI();
      }
    }
    
    void dispose()
    {
      Toolkit.getDefaultToolkit().removePropertyChangeListener(key, this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\DesktopProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */