package javax.swing;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.awt.EmbeddedFrame;
import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;

public class PopupFactory
{
  private static final Object SharedInstanceKey = new StringBuffer("PopupFactory.SharedInstanceKey");
  private static final int MAX_CACHE_SIZE = 5;
  static final int LIGHT_WEIGHT_POPUP = 0;
  static final int MEDIUM_WEIGHT_POPUP = 1;
  static final int HEAVY_WEIGHT_POPUP = 2;
  private int popupType = 0;
  
  public PopupFactory() {}
  
  public static void setSharedInstance(PopupFactory paramPopupFactory)
  {
    if (paramPopupFactory == null) {
      throw new IllegalArgumentException("PopupFactory can not be null");
    }
    SwingUtilities.appContextPut(SharedInstanceKey, paramPopupFactory);
  }
  
  public static PopupFactory getSharedInstance()
  {
    PopupFactory localPopupFactory = (PopupFactory)SwingUtilities.appContextGet(SharedInstanceKey);
    if (localPopupFactory == null)
    {
      localPopupFactory = new PopupFactory();
      setSharedInstance(localPopupFactory);
    }
    return localPopupFactory;
  }
  
  void setPopupType(int paramInt)
  {
    popupType = paramInt;
  }
  
  int getPopupType()
  {
    return popupType;
  }
  
  public Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    if (paramComponent2 == null) {
      throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
    }
    int i = getPopupType(paramComponent1, paramComponent2, paramInt1, paramInt2);
    Popup localPopup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, i);
    if (localPopup == null) {
      localPopup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, 2);
    }
    return localPopup;
  }
  
  private int getPopupType(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    int i = getPopupType();
    if ((paramComponent1 == null) || (invokerInHeavyWeightPopup(paramComponent1))) {
      i = 2;
    } else if ((i == 0) && (!(paramComponent2 instanceof JToolTip)) && (!(paramComponent2 instanceof JPopupMenu))) {
      i = 1;
    }
    for (Object localObject = paramComponent1; localObject != null; localObject = ((Component)localObject).getParent()) {
      if (((localObject instanceof JComponent)) && (((JComponent)localObject).getClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP) == Boolean.TRUE))
      {
        i = 2;
        break;
      }
    }
    return i;
  }
  
  private Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2, int paramInt3)
  {
    if (GraphicsEnvironment.isHeadless()) {
      return getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
    }
    switch (paramInt3)
    {
    case 0: 
      return getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
    case 1: 
      return getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
    case 2: 
      Popup localPopup = getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
      if ((AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.MACOSX) && (paramComponent1 != null) && (EmbeddedFrame.getAppletIfAncestorOf(paramComponent1) != null)) {
        ((HeavyWeightPopup)localPopup).setCacheEnabled(false);
      }
      return localPopup;
    }
    return null;
  }
  
  private Popup getHeadlessPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    return HeadlessPopup.getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  private Popup getLightWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    return LightWeightPopup.getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  private Popup getMediumWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    return MediumWeightPopup.getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  private Popup getHeavyWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    if (GraphicsEnvironment.isHeadless()) {
      return getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
    }
    return HeavyWeightPopup.getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  private boolean invokerInHeavyWeightPopup(Component paramComponent)
  {
    if (paramComponent != null) {
      for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
        if ((localContainer instanceof Popup.HeavyWeightWindow)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static class ContainerPopup
    extends Popup
  {
    Component owner;
    int x;
    int y;
    
    private ContainerPopup() {}
    
    public void hide()
    {
      Component localComponent = getComponent();
      if (localComponent != null)
      {
        Container localContainer = localComponent.getParent();
        if (localContainer != null)
        {
          Rectangle localRectangle = localComponent.getBounds();
          localContainer.remove(localComponent);
          localContainer.repaint(x, y, width, height);
        }
      }
      owner = null;
    }
    
    public void pack()
    {
      Component localComponent = getComponent();
      if (localComponent != null) {
        localComponent.setSize(localComponent.getPreferredSize());
      }
    }
    
    void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      if (((paramComponent1 instanceof JFrame)) || ((paramComponent1 instanceof JDialog)) || ((paramComponent1 instanceof JWindow))) {
        paramComponent1 = ((RootPaneContainer)paramComponent1).getLayeredPane();
      }
      super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      x = paramInt1;
      y = paramInt2;
      owner = paramComponent1;
    }
    
    boolean overlappedByOwnedWindow()
    {
      Component localComponent = getComponent();
      if ((owner != null) && (localComponent != null))
      {
        Window localWindow1 = SwingUtilities.getWindowAncestor(owner);
        if (localWindow1 == null) {
          return false;
        }
        Window[] arrayOfWindow1 = localWindow1.getOwnedWindows();
        if (arrayOfWindow1 != null)
        {
          Rectangle localRectangle = localComponent.getBounds();
          for (Window localWindow2 : arrayOfWindow1) {
            if ((localWindow2.isVisible()) && (localRectangle.intersects(localWindow2.getBounds()))) {
              return true;
            }
          }
        }
      }
      return false;
    }
    
    boolean fitsOnScreen()
    {
      boolean bool = false;
      Component localComponent = getComponent();
      if ((owner != null) && (localComponent != null))
      {
        int i = localComponent.getWidth();
        int j = localComponent.getHeight();
        Container localContainer = (Container)SwingUtilities.getRoot(owner);
        Rectangle localRectangle1;
        Object localObject;
        if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow)))
        {
          localRectangle1 = localContainer.getBounds();
          localObject = localContainer.getInsets();
          x += left;
          y += top;
          width -= left + right;
          height -= top + bottom;
          if (JPopupMenu.canPopupOverlapTaskBar())
          {
            GraphicsConfiguration localGraphicsConfiguration = localContainer.getGraphicsConfiguration();
            Rectangle localRectangle2 = getContainerPopupArea(localGraphicsConfiguration);
            bool = localRectangle1.intersection(localRectangle2).contains(x, y, i, j);
          }
          else
          {
            bool = localRectangle1.contains(x, y, i, j);
          }
        }
        else if ((localContainer instanceof JApplet))
        {
          localRectangle1 = localContainer.getBounds();
          localObject = localContainer.getLocationOnScreen();
          x = x;
          y = y;
          bool = localRectangle1.contains(x, y, i, j);
        }
      }
      return bool;
    }
    
    Rectangle getContainerPopupArea(GraphicsConfiguration paramGraphicsConfiguration)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      Rectangle localRectangle;
      Insets localInsets;
      if (paramGraphicsConfiguration != null)
      {
        localRectangle = paramGraphicsConfiguration.getBounds();
        localInsets = localToolkit.getScreenInsets(paramGraphicsConfiguration);
      }
      else
      {
        localRectangle = new Rectangle(localToolkit.getScreenSize());
        localInsets = new Insets(0, 0, 0, 0);
      }
      x += left;
      y += top;
      width -= left + right;
      height -= top + bottom;
      return localRectangle;
    }
  }
  
  private static class HeadlessPopup
    extends PopupFactory.ContainerPopup
  {
    private HeadlessPopup()
    {
      super();
    }
    
    static Popup getHeadlessPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      HeadlessPopup localHeadlessPopup = new HeadlessPopup();
      localHeadlessPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      return localHeadlessPopup;
    }
    
    Component createComponent(Component paramComponent)
    {
      return new Panel(new BorderLayout());
    }
    
    public void show() {}
    
    public void hide() {}
  }
  
  private static class HeavyWeightPopup
    extends Popup
  {
    private static final Object heavyWeightPopupCacheKey = new StringBuffer("PopupFactory.heavyWeightPopupCache");
    private volatile boolean isCacheEnabled = true;
    
    private HeavyWeightPopup() {}
    
    static Popup getHeavyWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      Window localWindow = paramComponent1 != null ? SwingUtilities.getWindowAncestor(paramComponent1) : null;
      HeavyWeightPopup localHeavyWeightPopup = null;
      if (localWindow != null) {
        localHeavyWeightPopup = getRecycledHeavyWeightPopup(localWindow);
      }
      int i = 0;
      Object localObject;
      if ((paramComponent2 != null) && (paramComponent2.isFocusable()) && ((paramComponent2 instanceof JPopupMenu)))
      {
        localObject = (JPopupMenu)paramComponent2;
        Component[] arrayOfComponent1 = ((JPopupMenu)localObject).getComponents();
        for (Component localComponent : arrayOfComponent1) {
          if ((!(localComponent instanceof MenuElement)) && (!(localComponent instanceof JSeparator)))
          {
            i = 1;
            break;
          }
        }
      }
      if ((localHeavyWeightPopup == null) || (((JWindow)localHeavyWeightPopup.getComponent()).getFocusableWindowState() != i))
      {
        if (localHeavyWeightPopup != null) {
          localHeavyWeightPopup._dispose();
        }
        localHeavyWeightPopup = new HeavyWeightPopup();
      }
      localHeavyWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      if (i != 0)
      {
        localObject = (JWindow)localHeavyWeightPopup.getComponent();
        ((JWindow)localObject).setFocusableWindowState(true);
        ((JWindow)localObject).setName("###focusableSwingPopup###");
      }
      return localHeavyWeightPopup;
    }
    
    private static HeavyWeightPopup getRecycledHeavyWeightPopup(Window paramWindow)
    {
      synchronized (HeavyWeightPopup.class)
      {
        Map localMap = getHeavyWeightPopupCache();
        List localList;
        if (localMap.containsKey(paramWindow)) {
          localList = (List)localMap.get(paramWindow);
        } else {
          return null;
        }
        if (localList.size() > 0)
        {
          HeavyWeightPopup localHeavyWeightPopup = (HeavyWeightPopup)localList.get(0);
          localList.remove(0);
          return localHeavyWeightPopup;
        }
        return null;
      }
    }
    
    private static Map<Window, List<HeavyWeightPopup>> getHeavyWeightPopupCache()
    {
      synchronized (HeavyWeightPopup.class)
      {
        Object localObject1 = (Map)SwingUtilities.appContextGet(heavyWeightPopupCacheKey);
        if (localObject1 == null)
        {
          localObject1 = new HashMap(2);
          SwingUtilities.appContextPut(heavyWeightPopupCacheKey, localObject1);
        }
        return (Map<Window, List<HeavyWeightPopup>>)localObject1;
      }
    }
    
    private static void recycleHeavyWeightPopup(HeavyWeightPopup paramHeavyWeightPopup)
    {
      synchronized (HeavyWeightPopup.class)
      {
        Window localWindow1 = SwingUtilities.getWindowAncestor(paramHeavyWeightPopup.getComponent());
        Map localMap = getHeavyWeightPopupCache();
        if (((localWindow1 instanceof Popup.DefaultFrame)) || (!localWindow1.isVisible()))
        {
          paramHeavyWeightPopup._dispose();
          return;
        }
        Object localObject1;
        if (localMap.containsKey(localWindow1))
        {
          localObject1 = (List)localMap.get(localWindow1);
        }
        else
        {
          localObject1 = new ArrayList();
          localMap.put(localWindow1, localObject1);
          Window localWindow2 = localWindow1;
          localWindow2.addWindowListener(new WindowAdapter()
          {
            public void windowClosed(WindowEvent paramAnonymousWindowEvent)
            {
              List localList;
              synchronized (PopupFactory.HeavyWeightPopup.class)
              {
                Map localMap = PopupFactory.HeavyWeightPopup.access$000();
                localList = (List)localMap.remove(val$w);
              }
              if (localList != null) {
                for (int i = localList.size() - 1; i >= 0; i--) {
                  ((PopupFactory.HeavyWeightPopup)localList.get(i))._dispose();
                }
              }
            }
          });
        }
        if (((List)localObject1).size() < 5) {
          ((List)localObject1).add(paramHeavyWeightPopup);
        } else {
          paramHeavyWeightPopup._dispose();
        }
      }
    }
    
    void setCacheEnabled(boolean paramBoolean)
    {
      isCacheEnabled = paramBoolean;
    }
    
    public void hide()
    {
      super.hide();
      if (isCacheEnabled) {
        recycleHeavyWeightPopup(this);
      } else {
        _dispose();
      }
    }
    
    void dispose() {}
    
    void _dispose()
    {
      super.dispose();
    }
  }
  
  private static class LightWeightPopup
    extends PopupFactory.ContainerPopup
  {
    private static final Object lightWeightPopupCacheKey = new StringBuffer("PopupFactory.lightPopupCache");
    
    private LightWeightPopup()
    {
      super();
    }
    
    static Popup getLightWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      LightWeightPopup localLightWeightPopup = getRecycledLightWeightPopup();
      if (localLightWeightPopup == null) {
        localLightWeightPopup = new LightWeightPopup();
      }
      localLightWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      if ((!localLightWeightPopup.fitsOnScreen()) || (localLightWeightPopup.overlappedByOwnedWindow()))
      {
        localLightWeightPopup.hide();
        return null;
      }
      return localLightWeightPopup;
    }
    
    private static List<LightWeightPopup> getLightWeightPopupCache()
    {
      Object localObject = (List)SwingUtilities.appContextGet(lightWeightPopupCacheKey);
      if (localObject == null)
      {
        localObject = new ArrayList();
        SwingUtilities.appContextPut(lightWeightPopupCacheKey, localObject);
      }
      return (List<LightWeightPopup>)localObject;
    }
    
    private static void recycleLightWeightPopup(LightWeightPopup paramLightWeightPopup)
    {
      synchronized (LightWeightPopup.class)
      {
        List localList = getLightWeightPopupCache();
        if (localList.size() < 5) {
          localList.add(paramLightWeightPopup);
        }
      }
    }
    
    private static LightWeightPopup getRecycledLightWeightPopup()
    {
      synchronized (LightWeightPopup.class)
      {
        List localList = getLightWeightPopupCache();
        if (localList.size() > 0)
        {
          LightWeightPopup localLightWeightPopup = (LightWeightPopup)localList.get(0);
          localList.remove(0);
          return localLightWeightPopup;
        }
        return null;
      }
    }
    
    public void hide()
    {
      super.hide();
      Container localContainer = (Container)getComponent();
      localContainer.removeAll();
      recycleLightWeightPopup(this);
    }
    
    public void show()
    {
      Object localObject1 = null;
      if (owner != null) {
        localObject1 = (owner instanceof Container) ? (Container)owner : owner.getParent();
      }
      for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((Container)localObject2).getParent()) {
        if ((localObject2 instanceof JRootPane))
        {
          if (!(((Container)localObject2).getParent() instanceof JInternalFrame)) {
            localObject1 = ((JRootPane)localObject2).getLayeredPane();
          }
        }
        else if ((localObject2 instanceof Window))
        {
          if (localObject1 == null) {
            localObject1 = localObject2;
          }
        }
        else {
          if ((localObject2 instanceof JApplet)) {
            break;
          }
        }
      }
      localObject2 = SwingUtilities.convertScreenLocationToParent((Container)localObject1, x, y);
      Component localComponent = getComponent();
      localComponent.setLocation(x, y);
      if ((localObject1 instanceof JLayeredPane)) {
        ((Container)localObject1).add(localComponent, JLayeredPane.POPUP_LAYER, 0);
      } else {
        ((Container)localObject1).add(localComponent);
      }
    }
    
    Component createComponent(Component paramComponent)
    {
      JPanel localJPanel = new JPanel(new BorderLayout(), true);
      localJPanel.setOpaque(true);
      return localJPanel;
    }
    
    void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      JComponent localJComponent = (JComponent)getComponent();
      localJComponent.setOpaque(paramComponent2.isOpaque());
      localJComponent.setLocation(paramInt1, paramInt2);
      localJComponent.add(paramComponent2, "Center");
      paramComponent2.invalidate();
      pack();
    }
  }
  
  private static class MediumWeightPopup
    extends PopupFactory.ContainerPopup
  {
    private static final Object mediumWeightPopupCacheKey = new StringBuffer("PopupFactory.mediumPopupCache");
    private JRootPane rootPane;
    
    private MediumWeightPopup()
    {
      super();
    }
    
    static Popup getMediumWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      MediumWeightPopup localMediumWeightPopup = getRecycledMediumWeightPopup();
      if (localMediumWeightPopup == null) {
        localMediumWeightPopup = new MediumWeightPopup();
      }
      localMediumWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      if ((!localMediumWeightPopup.fitsOnScreen()) || (localMediumWeightPopup.overlappedByOwnedWindow()))
      {
        localMediumWeightPopup.hide();
        return null;
      }
      return localMediumWeightPopup;
    }
    
    private static List<MediumWeightPopup> getMediumWeightPopupCache()
    {
      Object localObject = (List)SwingUtilities.appContextGet(mediumWeightPopupCacheKey);
      if (localObject == null)
      {
        localObject = new ArrayList();
        SwingUtilities.appContextPut(mediumWeightPopupCacheKey, localObject);
      }
      return (List<MediumWeightPopup>)localObject;
    }
    
    private static void recycleMediumWeightPopup(MediumWeightPopup paramMediumWeightPopup)
    {
      synchronized (MediumWeightPopup.class)
      {
        List localList = getMediumWeightPopupCache();
        if (localList.size() < 5) {
          localList.add(paramMediumWeightPopup);
        }
      }
    }
    
    private static MediumWeightPopup getRecycledMediumWeightPopup()
    {
      synchronized (MediumWeightPopup.class)
      {
        List localList = getMediumWeightPopupCache();
        if (localList.size() > 0)
        {
          MediumWeightPopup localMediumWeightPopup = (MediumWeightPopup)localList.get(0);
          localList.remove(0);
          return localMediumWeightPopup;
        }
        return null;
      }
    }
    
    public void hide()
    {
      super.hide();
      rootPane.getContentPane().removeAll();
      recycleMediumWeightPopup(this);
    }
    
    public void show()
    {
      Component localComponent = getComponent();
      Object localObject = null;
      if (owner != null) {}
      for (localObject = owner.getParent(); (!(localObject instanceof Window)) && (!(localObject instanceof Applet)) && (localObject != null); localObject = ((Container)localObject).getParent()) {}
      Point localPoint;
      if ((localObject instanceof RootPaneContainer))
      {
        localObject = ((RootPaneContainer)localObject).getLayeredPane();
        localPoint = SwingUtilities.convertScreenLocationToParent((Container)localObject, x, y);
        localComponent.setVisible(false);
        localComponent.setLocation(x, y);
        ((Container)localObject).add(localComponent, JLayeredPane.POPUP_LAYER, 0);
      }
      else
      {
        localPoint = SwingUtilities.convertScreenLocationToParent((Container)localObject, x, y);
        localComponent.setLocation(x, y);
        localComponent.setVisible(false);
        ((Container)localObject).add(localComponent);
      }
      localComponent.setVisible(true);
    }
    
    Component createComponent(Component paramComponent)
    {
      MediumWeightComponent localMediumWeightComponent = new MediumWeightComponent();
      rootPane = new JRootPane();
      rootPane.setOpaque(true);
      localMediumWeightComponent.add(rootPane, "Center");
      return localMediumWeightComponent;
    }
    
    void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
    {
      super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
      Component localComponent = getComponent();
      localComponent.setLocation(paramInt1, paramInt2);
      rootPane.getContentPane().add(paramComponent2, "Center");
      paramComponent2.invalidate();
      localComponent.validate();
      pack();
    }
    
    private static class MediumWeightComponent
      extends Panel
      implements SwingHeavyWeight
    {
      MediumWeightComponent()
      {
        super();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\PopupFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */