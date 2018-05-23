package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.AccessController;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.RootPaneUI;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.security.action.GetBooleanAction;

public class JRootPane
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "RootPaneUI";
  private static final boolean LOG_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.logDoubleBufferingDisable"))).booleanValue();
  private static final boolean IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.ignoreDoubleBufferingDisable"))).booleanValue();
  public static final int NONE = 0;
  public static final int FRAME = 1;
  public static final int PLAIN_DIALOG = 2;
  public static final int INFORMATION_DIALOG = 3;
  public static final int ERROR_DIALOG = 4;
  public static final int COLOR_CHOOSER_DIALOG = 5;
  public static final int FILE_CHOOSER_DIALOG = 6;
  public static final int QUESTION_DIALOG = 7;
  public static final int WARNING_DIALOG = 8;
  private int windowDecorationStyle;
  protected JMenuBar menuBar;
  protected Container contentPane;
  protected JLayeredPane layeredPane;
  protected Component glassPane;
  protected JButton defaultButton;
  @Deprecated
  protected DefaultAction defaultPressAction;
  @Deprecated
  protected DefaultAction defaultReleaseAction;
  boolean useTrueDoubleBuffering = true;
  
  public JRootPane()
  {
    setGlassPane(createGlassPane());
    setLayeredPane(createLayeredPane());
    setContentPane(createContentPane());
    setLayout(createRootLayout());
    setDoubleBuffered(true);
    updateUI();
  }
  
  public void setDoubleBuffered(boolean paramBoolean)
  {
    if (isDoubleBuffered() != paramBoolean)
    {
      super.setDoubleBuffered(paramBoolean);
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    }
  }
  
  public int getWindowDecorationStyle()
  {
    return windowDecorationStyle;
  }
  
  public void setWindowDecorationStyle(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 8)) {
      throw new IllegalArgumentException("Invalid decoration style");
    }
    int i = getWindowDecorationStyle();
    windowDecorationStyle = paramInt;
    firePropertyChange("windowDecorationStyle", i, paramInt);
  }
  
  public RootPaneUI getUI()
  {
    return (RootPaneUI)ui;
  }
  
  public void setUI(RootPaneUI paramRootPaneUI)
  {
    super.setUI(paramRootPaneUI);
  }
  
  public void updateUI()
  {
    setUI((RootPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "RootPaneUI";
  }
  
  protected JLayeredPane createLayeredPane()
  {
    JLayeredPane localJLayeredPane = new JLayeredPane();
    localJLayeredPane.setName(getName() + ".layeredPane");
    return localJLayeredPane;
  }
  
  protected Container createContentPane()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setName(getName() + ".contentPane");
    localJPanel.setLayout(new BorderLayout()
    {
      public void addLayoutComponent(Component paramAnonymousComponent, Object paramAnonymousObject)
      {
        if (paramAnonymousObject == null) {
          paramAnonymousObject = "Center";
        }
        super.addLayoutComponent(paramAnonymousComponent, paramAnonymousObject);
      }
    });
    return localJPanel;
  }
  
  protected Component createGlassPane()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setName(getName() + ".glassPane");
    localJPanel.setVisible(false);
    ((JPanel)localJPanel).setOpaque(false);
    return localJPanel;
  }
  
  protected LayoutManager createRootLayout()
  {
    return new RootLayout();
  }
  
  public void setJMenuBar(JMenuBar paramJMenuBar)
  {
    if ((menuBar != null) && (menuBar.getParent() == layeredPane)) {
      layeredPane.remove(menuBar);
    }
    menuBar = paramJMenuBar;
    if (menuBar != null) {
      layeredPane.add(menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
    }
  }
  
  @Deprecated
  public void setMenuBar(JMenuBar paramJMenuBar)
  {
    if ((menuBar != null) && (menuBar.getParent() == layeredPane)) {
      layeredPane.remove(menuBar);
    }
    menuBar = paramJMenuBar;
    if (menuBar != null) {
      layeredPane.add(menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
    }
  }
  
  public JMenuBar getJMenuBar()
  {
    return menuBar;
  }
  
  @Deprecated
  public JMenuBar getMenuBar()
  {
    return menuBar;
  }
  
  public void setContentPane(Container paramContainer)
  {
    if (paramContainer == null) {
      throw new IllegalComponentStateException("contentPane cannot be set to null.");
    }
    if ((contentPane != null) && (contentPane.getParent() == layeredPane)) {
      layeredPane.remove(contentPane);
    }
    contentPane = paramContainer;
    layeredPane.add(contentPane, JLayeredPane.FRAME_CONTENT_LAYER);
  }
  
  public Container getContentPane()
  {
    return contentPane;
  }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane)
  {
    if (paramJLayeredPane == null) {
      throw new IllegalComponentStateException("layeredPane cannot be set to null.");
    }
    if ((layeredPane != null) && (layeredPane.getParent() == this)) {
      remove(layeredPane);
    }
    layeredPane = paramJLayeredPane;
    add(layeredPane, -1);
  }
  
  public JLayeredPane getLayeredPane()
  {
    return layeredPane;
  }
  
  public void setGlassPane(Component paramComponent)
  {
    if (paramComponent == null) {
      throw new NullPointerException("glassPane cannot be set to null.");
    }
    AWTAccessor.getComponentAccessor().setMixingCutoutShape(paramComponent, new Rectangle());
    boolean bool = false;
    if ((glassPane != null) && (glassPane.getParent() == this))
    {
      remove(glassPane);
      bool = glassPane.isVisible();
    }
    paramComponent.setVisible(bool);
    glassPane = paramComponent;
    add(glassPane, 0);
    if (bool) {
      repaint();
    }
  }
  
  public Component getGlassPane()
  {
    return glassPane;
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  public boolean isOptimizedDrawingEnabled()
  {
    return !glassPane.isVisible();
  }
  
  public void addNotify()
  {
    super.addNotify();
    enableEvents(8L);
  }
  
  public void removeNotify()
  {
    super.removeNotify();
  }
  
  public void setDefaultButton(JButton paramJButton)
  {
    JButton localJButton = defaultButton;
    if (localJButton != paramJButton)
    {
      defaultButton = paramJButton;
      if (localJButton != null) {
        localJButton.repaint();
      }
      if (paramJButton != null) {
        paramJButton.repaint();
      }
    }
    firePropertyChange("defaultButton", localJButton, paramJButton);
  }
  
  public JButton getDefaultButton()
  {
    return defaultButton;
  }
  
  final void setUseTrueDoubleBuffering(boolean paramBoolean)
  {
    useTrueDoubleBuffering = paramBoolean;
  }
  
  final boolean getUseTrueDoubleBuffering()
  {
    return useTrueDoubleBuffering;
  }
  
  final void disableTrueDoubleBuffering()
  {
    if ((useTrueDoubleBuffering) && (!IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING))
    {
      if (LOG_DISABLE_TRUE_DOUBLE_BUFFERING)
      {
        System.out.println("Disabling true double buffering for " + this);
        Thread.dumpStack();
      }
      useTrueDoubleBuffering = false;
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    }
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    super.addImpl(paramComponent, paramObject, paramInt);
    if ((glassPane != null) && (glassPane.getParent() == this) && (getComponent(0) != glassPane)) {
      add(glassPane, 0);
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJRootPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJRootPane
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJRootPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.ROOT_PANE;
    }
    
    public int getAccessibleChildrenCount()
    {
      return super.getAccessibleChildrenCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return super.getAccessibleChild(paramInt);
    }
  }
  
  static class DefaultAction
    extends AbstractAction
  {
    JButton owner;
    JRootPane root;
    boolean press;
    
    DefaultAction(JRootPane paramJRootPane, boolean paramBoolean)
    {
      root = paramJRootPane;
      press = paramBoolean;
    }
    
    public void setOwner(JButton paramJButton)
    {
      owner = paramJButton;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((owner != null) && (SwingUtilities.getRootPane(owner) == root))
      {
        ButtonModel localButtonModel = owner.getModel();
        if (press)
        {
          localButtonModel.setArmed(true);
          localButtonModel.setPressed(true);
        }
        else
        {
          localButtonModel.setPressed(false);
        }
      }
    }
    
    public boolean isEnabled()
    {
      return owner.getModel().isEnabled();
    }
  }
  
  protected class RootLayout
    implements LayoutManager2, Serializable
  {
    protected RootLayout() {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Insets localInsets = getInsets();
      Dimension localDimension1;
      if (contentPane != null) {
        localDimension1 = contentPane.getPreferredSize();
      } else {
        localDimension1 = paramContainer.getSize();
      }
      Dimension localDimension2;
      if ((menuBar != null) && (menuBar.isVisible())) {
        localDimension2 = menuBar.getPreferredSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      return new Dimension(Math.max(width, width) + left + right, height + height + top + bottom);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      Insets localInsets = getInsets();
      Dimension localDimension1;
      if (contentPane != null) {
        localDimension1 = contentPane.getMinimumSize();
      } else {
        localDimension1 = paramContainer.getSize();
      }
      Dimension localDimension2;
      if ((menuBar != null) && (menuBar.isVisible())) {
        localDimension2 = menuBar.getMinimumSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      return new Dimension(Math.max(width, width) + left + right, height + height + top + bottom);
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      Insets localInsets = getInsets();
      Dimension localDimension2;
      if ((menuBar != null) && (menuBar.isVisible())) {
        localDimension2 = menuBar.getMaximumSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      Dimension localDimension1;
      if (contentPane != null) {
        localDimension1 = contentPane.getMaximumSize();
      } else {
        localDimension1 = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE - top - bottom - height - 1);
      }
      return new Dimension(Math.min(width, width) + left + right, height + height + top + bottom);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Rectangle localRectangle = paramContainer.getBounds();
      Insets localInsets = getInsets();
      int i = 0;
      int j = width - right - left;
      int k = height - top - bottom;
      if (layeredPane != null) {
        layeredPane.setBounds(left, top, j, k);
      }
      if (glassPane != null) {
        glassPane.setBounds(left, top, j, k);
      }
      if ((menuBar != null) && (menuBar.isVisible()))
      {
        Dimension localDimension = menuBar.getPreferredSize();
        menuBar.setBounds(0, 0, j, height);
        i += height;
      }
      if (contentPane != null) {
        contentPane.setBounds(0, i, j, k - i);
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(Component paramComponent, Object paramObject) {}
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return 0.0F;
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return 0.0F;
    }
    
    public void invalidateLayout(Container paramContainer) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JRootPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */