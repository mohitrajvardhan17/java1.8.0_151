package sun.applet;

public class AppletEventMulticaster
  implements AppletListener
{
  private final AppletListener a;
  private final AppletListener b;
  
  public AppletEventMulticaster(AppletListener paramAppletListener1, AppletListener paramAppletListener2)
  {
    a = paramAppletListener1;
    b = paramAppletListener2;
  }
  
  public void appletStateChanged(AppletEvent paramAppletEvent)
  {
    a.appletStateChanged(paramAppletEvent);
    b.appletStateChanged(paramAppletEvent);
  }
  
  public static AppletListener add(AppletListener paramAppletListener1, AppletListener paramAppletListener2)
  {
    return addInternal(paramAppletListener1, paramAppletListener2);
  }
  
  public static AppletListener remove(AppletListener paramAppletListener1, AppletListener paramAppletListener2)
  {
    return removeInternal(paramAppletListener1, paramAppletListener2);
  }
  
  private static AppletListener addInternal(AppletListener paramAppletListener1, AppletListener paramAppletListener2)
  {
    if (paramAppletListener1 == null) {
      return paramAppletListener2;
    }
    if (paramAppletListener2 == null) {
      return paramAppletListener1;
    }
    return new AppletEventMulticaster(paramAppletListener1, paramAppletListener2);
  }
  
  protected AppletListener remove(AppletListener paramAppletListener)
  {
    if (paramAppletListener == a) {
      return b;
    }
    if (paramAppletListener == b) {
      return a;
    }
    AppletListener localAppletListener1 = removeInternal(a, paramAppletListener);
    AppletListener localAppletListener2 = removeInternal(b, paramAppletListener);
    if ((localAppletListener1 == a) && (localAppletListener2 == b)) {
      return this;
    }
    return addInternal(localAppletListener1, localAppletListener2);
  }
  
  private static AppletListener removeInternal(AppletListener paramAppletListener1, AppletListener paramAppletListener2)
  {
    if ((paramAppletListener1 == paramAppletListener2) || (paramAppletListener1 == null)) {
      return null;
    }
    if ((paramAppletListener1 instanceof AppletEventMulticaster)) {
      return ((AppletEventMulticaster)paramAppletListener1).remove(paramAppletListener2);
    }
    return paramAppletListener1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */