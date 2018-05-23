package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class JTabbedPane
  extends JComponent
  implements Serializable, Accessible, SwingConstants
{
  public static final int WRAP_TAB_LAYOUT = 0;
  public static final int SCROLL_TAB_LAYOUT = 1;
  private static final String uiClassID = "TabbedPaneUI";
  protected int tabPlacement = 1;
  private int tabLayoutPolicy;
  protected SingleSelectionModel model;
  private boolean haveRegistered;
  protected ChangeListener changeListener = null;
  private final List<Page> pages;
  private Component visComp = null;
  protected transient ChangeEvent changeEvent = null;
  
  public JTabbedPane()
  {
    this(1, 0);
  }
  
  public JTabbedPane(int paramInt)
  {
    this(paramInt, 0);
  }
  
  public JTabbedPane(int paramInt1, int paramInt2)
  {
    setTabPlacement(paramInt1);
    setTabLayoutPolicy(paramInt2);
    pages = new ArrayList(1);
    setModel(new DefaultSingleSelectionModel());
    updateUI();
  }
  
  public TabbedPaneUI getUI()
  {
    return (TabbedPaneUI)ui;
  }
  
  public void setUI(TabbedPaneUI paramTabbedPaneUI)
  {
    super.setUI(paramTabbedPaneUI);
    for (int i = 0; i < getTabCount(); i++)
    {
      Icon localIcon = pages.get(i)).disabledIcon;
      if ((localIcon instanceof UIResource)) {
        setDisabledIconAt(i, null);
      }
    }
  }
  
  public void updateUI()
  {
    setUI((TabbedPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "TabbedPaneUI";
  }
  
  protected ChangeListener createChangeListener()
  {
    return new ModelListener();
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    int i = getSelectedIndex();
    if (i < 0)
    {
      if ((visComp != null) && (visComp.isVisible())) {
        visComp.setVisible(false);
      }
      visComp = null;
    }
    else
    {
      localObject = getComponentAt(i);
      if ((localObject != null) && (localObject != visComp))
      {
        j = 0;
        if (visComp != null)
        {
          j = SwingUtilities.findFocusOwner(visComp) != null ? 1 : 0;
          if (visComp.isVisible()) {
            visComp.setVisible(false);
          }
        }
        if (!((Component)localObject).isVisible()) {
          ((Component)localObject).setVisible(true);
        }
        if (j != 0) {
          SwingUtilities2.tabbedPaneChangeFocusTo((Component)localObject);
        }
        visComp = ((Component)localObject);
      }
    }
    Object localObject = listenerList.getListenerList();
    for (int j = localObject.length - 2; j >= 0; j -= 2) {
      if (localObject[j] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)localObject[(j + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public SingleSelectionModel getModel()
  {
    return model;
  }
  
  public void setModel(SingleSelectionModel paramSingleSelectionModel)
  {
    SingleSelectionModel localSingleSelectionModel = getModel();
    if (localSingleSelectionModel != null)
    {
      localSingleSelectionModel.removeChangeListener(changeListener);
      changeListener = null;
    }
    model = paramSingleSelectionModel;
    if (paramSingleSelectionModel != null)
    {
      changeListener = createChangeListener();
      paramSingleSelectionModel.addChangeListener(changeListener);
    }
    firePropertyChange("model", localSingleSelectionModel, paramSingleSelectionModel);
    repaint();
  }
  
  public int getTabPlacement()
  {
    return tabPlacement;
  }
  
  public void setTabPlacement(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 3) && (paramInt != 4)) {
      throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT");
    }
    if (tabPlacement != paramInt)
    {
      int i = tabPlacement;
      tabPlacement = paramInt;
      firePropertyChange("tabPlacement", i, paramInt);
      revalidate();
      repaint();
    }
  }
  
  public int getTabLayoutPolicy()
  {
    return tabLayoutPolicy;
  }
  
  public void setTabLayoutPolicy(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("illegal tab layout policy: must be WRAP_TAB_LAYOUT or SCROLL_TAB_LAYOUT");
    }
    if (tabLayoutPolicy != paramInt)
    {
      int i = tabLayoutPolicy;
      tabLayoutPolicy = paramInt;
      firePropertyChange("tabLayoutPolicy", i, paramInt);
      revalidate();
      repaint();
    }
  }
  
  @Transient
  public int getSelectedIndex()
  {
    return model.getSelectedIndex();
  }
  
  public void setSelectedIndex(int paramInt)
  {
    if (paramInt != -1) {
      checkIndex(paramInt);
    }
    setSelectedIndexImpl(paramInt, true);
  }
  
  private void setSelectedIndexImpl(int paramInt, boolean paramBoolean)
  {
    int i = model.getSelectedIndex();
    Page localPage1 = null;
    Page localPage2 = null;
    String str = null;
    paramBoolean = (paramBoolean) && (i != paramInt);
    if (paramBoolean)
    {
      if (accessibleContext != null) {
        str = accessibleContext.getAccessibleName();
      }
      if (i >= 0) {
        localPage1 = (Page)pages.get(i);
      }
      if (paramInt >= 0) {
        localPage2 = (Page)pages.get(paramInt);
      }
    }
    model.setSelectedIndex(paramInt);
    if (paramBoolean) {
      changeAccessibleSelection(localPage1, str, localPage2);
    }
  }
  
  private void changeAccessibleSelection(Page paramPage1, String paramString, Page paramPage2)
  {
    if (accessibleContext == null) {
      return;
    }
    if (paramPage1 != null) {
      paramPage1.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
    }
    if (paramPage2 != null) {
      paramPage2.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
    }
    accessibleContext.firePropertyChange("AccessibleName", paramString, accessibleContext.getAccessibleName());
  }
  
  @Transient
  public Component getSelectedComponent()
  {
    int i = getSelectedIndex();
    if (i == -1) {
      return null;
    }
    return getComponentAt(i);
  }
  
  public void setSelectedComponent(Component paramComponent)
  {
    int i = indexOfComponent(paramComponent);
    if (i != -1) {
      setSelectedIndex(i);
    } else {
      throw new IllegalArgumentException("component not found in tabbed pane");
    }
  }
  
  public void insertTab(String paramString1, Icon paramIcon, Component paramComponent, String paramString2, int paramInt)
  {
    int i = paramInt;
    int j = indexOfComponent(paramComponent);
    if ((paramComponent != null) && (j != -1))
    {
      removeTabAt(j);
      if (i > j) {
        i--;
      }
    }
    int k = getSelectedIndex();
    pages.add(i, new Page(this, paramString1 != null ? paramString1 : "", paramIcon, null, paramComponent, paramString2));
    if (paramComponent != null)
    {
      addImpl(paramComponent, null, -1);
      paramComponent.setVisible(false);
    }
    else
    {
      firePropertyChange("indexForNullComponent", -1, paramInt);
    }
    if (pages.size() == 1) {
      setSelectedIndex(0);
    }
    if (k >= i) {
      setSelectedIndexImpl(k + 1, false);
    }
    if ((!haveRegistered) && (paramString2 != null))
    {
      ToolTipManager.sharedInstance().registerComponent(this);
      haveRegistered = true;
    }
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", null, paramComponent);
    }
    revalidate();
    repaint();
  }
  
  public void addTab(String paramString1, Icon paramIcon, Component paramComponent, String paramString2)
  {
    insertTab(paramString1, paramIcon, paramComponent, paramString2, pages.size());
  }
  
  public void addTab(String paramString, Icon paramIcon, Component paramComponent)
  {
    insertTab(paramString, paramIcon, paramComponent, null, pages.size());
  }
  
  public void addTab(String paramString, Component paramComponent)
  {
    insertTab(paramString, null, paramComponent, null, pages.size());
  }
  
  public Component add(Component paramComponent)
  {
    if (!(paramComponent instanceof UIResource)) {
      addTab(paramComponent.getName(), paramComponent);
    } else {
      super.add(paramComponent);
    }
    return paramComponent;
  }
  
  public Component add(String paramString, Component paramComponent)
  {
    if (!(paramComponent instanceof UIResource)) {
      addTab(paramString, paramComponent);
    } else {
      super.add(paramString, paramComponent);
    }
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt)
  {
    if (!(paramComponent instanceof UIResource)) {
      insertTab(paramComponent.getName(), null, paramComponent, null, paramInt == -1 ? getTabCount() : paramInt);
    } else {
      super.add(paramComponent, paramInt);
    }
    return paramComponent;
  }
  
  public void add(Component paramComponent, Object paramObject)
  {
    if (!(paramComponent instanceof UIResource))
    {
      if ((paramObject instanceof String)) {
        addTab((String)paramObject, paramComponent);
      } else if ((paramObject instanceof Icon)) {
        addTab(null, (Icon)paramObject, paramComponent);
      } else {
        add(paramComponent);
      }
    }
    else {
      super.add(paramComponent, paramObject);
    }
  }
  
  public void add(Component paramComponent, Object paramObject, int paramInt)
  {
    if (!(paramComponent instanceof UIResource))
    {
      Icon localIcon = (paramObject instanceof Icon) ? (Icon)paramObject : null;
      String str = (paramObject instanceof String) ? (String)paramObject : null;
      insertTab(str, localIcon, paramComponent, null, paramInt == -1 ? getTabCount() : paramInt);
    }
    else
    {
      super.add(paramComponent, paramObject, paramInt);
    }
  }
  
  public void removeTabAt(int paramInt)
  {
    checkIndex(paramInt);
    Component localComponent = getComponentAt(paramInt);
    int i = 0;
    int j = getSelectedIndex();
    String str = null;
    if (localComponent == visComp)
    {
      i = SwingUtilities.findFocusOwner(visComp) != null ? 1 : 0;
      visComp = null;
    }
    if (accessibleContext != null)
    {
      if (paramInt == j)
      {
        ((Page)pages.get(paramInt)).firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
        str = accessibleContext.getAccessibleName();
      }
      accessibleContext.firePropertyChange("AccessibleVisibleData", localComponent, null);
    }
    setTabComponentAt(paramInt, null);
    pages.remove(paramInt);
    putClientProperty("__index_to_remove__", Integer.valueOf(paramInt));
    Object localObject;
    if (j > paramInt)
    {
      setSelectedIndexImpl(j - 1, false);
    }
    else if (j >= getTabCount())
    {
      setSelectedIndexImpl(j - 1, false);
      localObject = j != 0 ? (Page)pages.get(j - 1) : null;
      changeAccessibleSelection(null, str, (Page)localObject);
    }
    else if (paramInt == j)
    {
      fireStateChanged();
      changeAccessibleSelection(null, str, (Page)pages.get(paramInt));
    }
    if (localComponent != null)
    {
      localObject = getComponents();
      int k = localObject.length;
      do
      {
        k--;
        if (k < 0) {
          break;
        }
      } while (localObject[k] != localComponent);
      super.remove(k);
      localComponent.setVisible(true);
    }
    if (i != 0) {
      SwingUtilities2.tabbedPaneChangeFocusTo(getSelectedComponent());
    }
    revalidate();
    repaint();
  }
  
  public void remove(Component paramComponent)
  {
    int i = indexOfComponent(paramComponent);
    if (i != -1)
    {
      removeTabAt(i);
    }
    else
    {
      Component[] arrayOfComponent = getComponents();
      for (int j = 0; j < arrayOfComponent.length; j++) {
        if (paramComponent == arrayOfComponent[j])
        {
          super.remove(j);
          break;
        }
      }
    }
  }
  
  public void remove(int paramInt)
  {
    removeTabAt(paramInt);
  }
  
  public void removeAll()
  {
    setSelectedIndexImpl(-1, true);
    int i = getTabCount();
    while (i-- > 0) {
      removeTabAt(i);
    }
  }
  
  public int getTabCount()
  {
    return pages.size();
  }
  
  public int getTabRunCount()
  {
    if (ui != null) {
      return ((TabbedPaneUI)ui).getTabRunCount(this);
    }
    return 0;
  }
  
  public String getTitleAt(int paramInt)
  {
    return pages.get(paramInt)).title;
  }
  
  public Icon getIconAt(int paramInt)
  {
    return pages.get(paramInt)).icon;
  }
  
  public Icon getDisabledIconAt(int paramInt)
  {
    Page localPage = (Page)pages.get(paramInt);
    if (disabledIcon == null) {
      disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, icon);
    }
    return disabledIcon;
  }
  
  public String getToolTipTextAt(int paramInt)
  {
    return pages.get(paramInt)).tip;
  }
  
  public Color getBackgroundAt(int paramInt)
  {
    return ((Page)pages.get(paramInt)).getBackground();
  }
  
  public Color getForegroundAt(int paramInt)
  {
    return ((Page)pages.get(paramInt)).getForeground();
  }
  
  public boolean isEnabledAt(int paramInt)
  {
    return ((Page)pages.get(paramInt)).isEnabled();
  }
  
  public Component getComponentAt(int paramInt)
  {
    return pages.get(paramInt)).component;
  }
  
  public int getMnemonicAt(int paramInt)
  {
    checkIndex(paramInt);
    Page localPage = (Page)pages.get(paramInt);
    return localPage.getMnemonic();
  }
  
  public int getDisplayedMnemonicIndexAt(int paramInt)
  {
    checkIndex(paramInt);
    Page localPage = (Page)pages.get(paramInt);
    return localPage.getDisplayedMnemonicIndex();
  }
  
  public Rectangle getBoundsAt(int paramInt)
  {
    checkIndex(paramInt);
    if (ui != null) {
      return ((TabbedPaneUI)ui).getTabBounds(this, paramInt);
    }
    return null;
  }
  
  public void setTitleAt(int paramInt, String paramString)
  {
    Page localPage = (Page)pages.get(paramInt);
    String str = title;
    title = paramString;
    if (str != paramString) {
      firePropertyChange("indexForTitle", -1, paramInt);
    }
    localPage.updateDisplayedMnemonicIndex();
    if ((str != paramString) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString);
    }
    if ((paramString == null) || (str == null) || (!paramString.equals(str)))
    {
      revalidate();
      repaint();
    }
  }
  
  public void setIconAt(int paramInt, Icon paramIcon)
  {
    Page localPage = (Page)pages.get(paramInt);
    Icon localIcon = icon;
    if (paramIcon != localIcon)
    {
      icon = paramIcon;
      if ((disabledIcon instanceof UIResource)) {
        disabledIcon = null;
      }
      if (accessibleContext != null) {
        accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
      }
      revalidate();
      repaint();
    }
  }
  
  public void setDisabledIconAt(int paramInt, Icon paramIcon)
  {
    Icon localIcon = pages.get(paramInt)).disabledIcon;
    pages.get(paramInt)).disabledIcon = paramIcon;
    if ((paramIcon != localIcon) && (!isEnabledAt(paramInt)))
    {
      revalidate();
      repaint();
    }
  }
  
  public void setToolTipTextAt(int paramInt, String paramString)
  {
    String str = pages.get(paramInt)).tip;
    pages.get(paramInt)).tip = paramString;
    if ((str != paramString) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString);
    }
    if ((!haveRegistered) && (paramString != null))
    {
      ToolTipManager.sharedInstance().registerComponent(this);
      haveRegistered = true;
    }
  }
  
  public void setBackgroundAt(int paramInt, Color paramColor)
  {
    Color localColor = pages.get(paramInt)).background;
    ((Page)pages.get(paramInt)).setBackground(paramColor);
    if ((paramColor == null) || (localColor == null) || (!paramColor.equals(localColor)))
    {
      Rectangle localRectangle = getBoundsAt(paramInt);
      if (localRectangle != null) {
        repaint(localRectangle);
      }
    }
  }
  
  public void setForegroundAt(int paramInt, Color paramColor)
  {
    Color localColor = pages.get(paramInt)).foreground;
    ((Page)pages.get(paramInt)).setForeground(paramColor);
    if ((paramColor == null) || (localColor == null) || (!paramColor.equals(localColor)))
    {
      Rectangle localRectangle = getBoundsAt(paramInt);
      if (localRectangle != null) {
        repaint(localRectangle);
      }
    }
  }
  
  public void setEnabledAt(int paramInt, boolean paramBoolean)
  {
    boolean bool = ((Page)pages.get(paramInt)).isEnabled();
    ((Page)pages.get(paramInt)).setEnabled(paramBoolean);
    if (paramBoolean != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  public void setComponentAt(int paramInt, Component paramComponent)
  {
    Page localPage = (Page)pages.get(paramInt);
    if (paramComponent != component)
    {
      int i = 0;
      if (component != null)
      {
        i = SwingUtilities.findFocusOwner(component) != null ? 1 : 0;
        synchronized (getTreeLock())
        {
          int j = getComponentCount();
          Component[] arrayOfComponent = getComponents();
          for (int k = 0; k < j; k++) {
            if (arrayOfComponent[k] == component) {
              super.remove(k);
            }
          }
        }
      }
      component = paramComponent;
      boolean bool = getSelectedIndex() == paramInt;
      if (bool) {
        visComp = paramComponent;
      }
      if (paramComponent != null)
      {
        paramComponent.setVisible(bool);
        addImpl(paramComponent, null, -1);
        if (i != 0) {
          SwingUtilities2.tabbedPaneChangeFocusTo(paramComponent);
        }
      }
      else
      {
        repaint();
      }
      revalidate();
    }
  }
  
  public void setDisplayedMnemonicIndexAt(int paramInt1, int paramInt2)
  {
    checkIndex(paramInt1);
    Page localPage = (Page)pages.get(paramInt1);
    localPage.setDisplayedMnemonicIndex(paramInt2);
  }
  
  public void setMnemonicAt(int paramInt1, int paramInt2)
  {
    checkIndex(paramInt1);
    Page localPage = (Page)pages.get(paramInt1);
    localPage.setMnemonic(paramInt2);
    firePropertyChange("mnemonicAt", null, null);
  }
  
  public int indexOfTab(String paramString)
  {
    for (int i = 0; i < getTabCount(); i++) {
      if (getTitleAt(i).equals(paramString == null ? "" : paramString)) {
        return i;
      }
    }
    return -1;
  }
  
  public int indexOfTab(Icon paramIcon)
  {
    for (int i = 0; i < getTabCount(); i++)
    {
      Icon localIcon = getIconAt(i);
      if (((localIcon != null) && (localIcon.equals(paramIcon))) || ((localIcon == null) && (localIcon == paramIcon))) {
        return i;
      }
    }
    return -1;
  }
  
  public int indexOfComponent(Component paramComponent)
  {
    for (int i = 0; i < getTabCount(); i++)
    {
      Component localComponent = getComponentAt(i);
      if (((localComponent != null) && (localComponent.equals(paramComponent))) || ((localComponent == null) && (localComponent == paramComponent))) {
        return i;
      }
    }
    return -1;
  }
  
  public int indexAtLocation(int paramInt1, int paramInt2)
  {
    if (ui != null) {
      return ((TabbedPaneUI)ui).tabForCoordinate(this, paramInt1, paramInt2);
    }
    return -1;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    if (ui != null)
    {
      int i = ((TabbedPaneUI)ui).tabForCoordinate(this, paramMouseEvent.getX(), paramMouseEvent.getY());
      if (i != -1) {
        return pages.get(i)).tip;
      }
    }
    return super.getToolTipText(paramMouseEvent);
  }
  
  private void checkIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= pages.size())) {
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Tab count: " + pages.size());
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TabbedPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  void compWriteObjectNotify()
  {
    super.compWriteObjectNotify();
    if ((getToolTipText() == null) && (haveRegistered)) {
      ToolTipManager.sharedInstance().unregisterComponent(this);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((ui != null) && (getUIClassID().equals("TabbedPaneUI"))) {
      ui.installUI(this);
    }
    if ((getToolTipText() == null) && (haveRegistered)) {
      ToolTipManager.sharedInstance().registerComponent(this);
    }
  }
  
  protected String paramString()
  {
    String str1;
    if (tabPlacement == 1) {
      str1 = "TOP";
    } else if (tabPlacement == 3) {
      str1 = "BOTTOM";
    } else if (tabPlacement == 2) {
      str1 = "LEFT";
    } else if (tabPlacement == 4) {
      str1 = "RIGHT";
    } else {
      str1 = "";
    }
    String str2 = haveRegistered ? "true" : "false";
    return super.paramString() + ",haveRegistered=" + str2 + ",tabPlacement=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null)
    {
      accessibleContext = new AccessibleJTabbedPane();
      int i = getTabCount();
      for (int j = 0; j < i; j++) {
        ((Page)pages.get(j)).initAccessibleContext();
      }
    }
    return accessibleContext;
  }
  
  public void setTabComponentAt(int paramInt, Component paramComponent)
  {
    if ((paramComponent != null) && (indexOfComponent(paramComponent) != -1)) {
      throw new IllegalArgumentException("Component is already added to this JTabbedPane");
    }
    Component localComponent = getTabComponentAt(paramInt);
    if (paramComponent != localComponent)
    {
      int i = indexOfTabComponent(paramComponent);
      if (i != -1) {
        setTabComponentAt(i, null);
      }
      pages.get(paramInt)).tabComponent = paramComponent;
      firePropertyChange("indexForTabComponent", -1, paramInt);
    }
  }
  
  public Component getTabComponentAt(int paramInt)
  {
    return pages.get(paramInt)).tabComponent;
  }
  
  public int indexOfTabComponent(Component paramComponent)
  {
    for (int i = 0; i < getTabCount(); i++)
    {
      Component localComponent = getTabComponentAt(i);
      if (localComponent == paramComponent) {
        return i;
      }
    }
    return -1;
  }
  
  protected class AccessibleJTabbedPane
    extends JComponent.AccessibleJComponent
    implements AccessibleSelection, ChangeListener
  {
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      String str = (String)getClientProperty("AccessibleName");
      if (str != null) {
        return str;
      }
      int i = getSelectedIndex();
      if (i >= 0) {
        return ((JTabbedPane.Page)pages.get(i)).getAccessibleName();
      }
      return super.getAccessibleName();
    }
    
    public AccessibleJTabbedPane()
    {
      super();
      model.addChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      Object localObject = paramChangeEvent.getSource();
      firePropertyChange("AccessibleSelection", null, localObject);
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PAGE_TAB_LIST;
    }
    
    public int getAccessibleChildrenCount()
    {
      return getTabCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= getTabCount())) {
        return null;
      }
      return (Accessible)pages.get(paramInt);
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      int i = ((TabbedPaneUI)ui).tabForCoordinate(JTabbedPane.this, x, y);
      if (i == -1) {
        i = getSelectedIndex();
      }
      return getAccessibleChild(i);
    }
    
    public int getAccessibleSelectionCount()
    {
      return 1;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      int i = getSelectedIndex();
      if (i == -1) {
        return null;
      }
      return (Accessible)pages.get(i);
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return paramInt == getSelectedIndex();
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      setSelectedIndex(paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt) {}
    
    public void clearAccessibleSelection() {}
    
    public void selectAllAccessibleSelection() {}
  }
  
  protected class ModelListener
    implements ChangeListener, Serializable
  {
    protected ModelListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      fireStateChanged();
    }
  }
  
  private class Page
    extends AccessibleContext
    implements Serializable, Accessible, AccessibleComponent
  {
    String title;
    Color background;
    Color foreground;
    Icon icon;
    Icon disabledIcon;
    JTabbedPane parent;
    Component component;
    String tip;
    boolean enabled = true;
    boolean needsUIUpdate;
    int mnemonic = -1;
    int mnemonicIndex = -1;
    Component tabComponent;
    
    Page(JTabbedPane paramJTabbedPane, String paramString1, Icon paramIcon1, Icon paramIcon2, Component paramComponent, String paramString2)
    {
      title = paramString1;
      icon = paramIcon1;
      disabledIcon = paramIcon2;
      parent = paramJTabbedPane;
      setAccessibleParent(paramJTabbedPane);
      component = paramComponent;
      tip = paramString2;
      initAccessibleContext();
    }
    
    void initAccessibleContext()
    {
      if ((accessibleContext != null) && ((component instanceof Accessible)))
      {
        AccessibleContext localAccessibleContext = component.getAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleParent(this);
        }
      }
    }
    
    void setMnemonic(int paramInt)
    {
      mnemonic = paramInt;
      updateDisplayedMnemonicIndex();
    }
    
    int getMnemonic()
    {
      return mnemonic;
    }
    
    void setDisplayedMnemonicIndex(int paramInt)
    {
      if (mnemonicIndex != paramInt)
      {
        if ((paramInt != -1) && ((title == null) || (paramInt < 0) || (paramInt >= title.length()))) {
          throw new IllegalArgumentException("Invalid mnemonic index: " + paramInt);
        }
        mnemonicIndex = paramInt;
        firePropertyChange("displayedMnemonicIndexAt", null, null);
      }
    }
    
    int getDisplayedMnemonicIndex()
    {
      return mnemonicIndex;
    }
    
    void updateDisplayedMnemonicIndex()
    {
      setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(title, mnemonic));
    }
    
    public AccessibleContext getAccessibleContext()
    {
      return this;
    }
    
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      if (title != null) {
        return title;
      }
      return null;
    }
    
    public String getAccessibleDescription()
    {
      if (accessibleDescription != null) {
        return accessibleDescription;
      }
      if (tip != null) {
        return tip;
      }
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PAGE_TAB;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = parent.getAccessibleContext().getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.SELECTABLE);
      int i = parent.indexOfTab(title);
      if (i == parent.getSelectedIndex()) {
        localAccessibleStateSet.add(AccessibleState.SELECTED);
      }
      return localAccessibleStateSet;
    }
    
    public int getAccessibleIndexInParent()
    {
      return parent.indexOfTab(title);
    }
    
    public int getAccessibleChildrenCount()
    {
      if ((component instanceof Accessible)) {
        return 1;
      }
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if ((component instanceof Accessible)) {
        return (Accessible)component;
      }
      return null;
    }
    
    public Locale getLocale()
    {
      return parent.getLocale();
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      return this;
    }
    
    public Color getBackground()
    {
      return background != null ? background : parent.getBackground();
    }
    
    public void setBackground(Color paramColor)
    {
      background = paramColor;
    }
    
    public Color getForeground()
    {
      return foreground != null ? foreground : parent.getForeground();
    }
    
    public void setForeground(Color paramColor)
    {
      foreground = paramColor;
    }
    
    public Cursor getCursor()
    {
      return parent.getCursor();
    }
    
    public void setCursor(Cursor paramCursor)
    {
      parent.setCursor(paramCursor);
    }
    
    public Font getFont()
    {
      return parent.getFont();
    }
    
    public void setFont(Font paramFont)
    {
      parent.setFont(paramFont);
    }
    
    public FontMetrics getFontMetrics(Font paramFont)
    {
      return parent.getFontMetrics(paramFont);
    }
    
    public boolean isEnabled()
    {
      return enabled;
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      enabled = paramBoolean;
    }
    
    public boolean isVisible()
    {
      return parent.isVisible();
    }
    
    public void setVisible(boolean paramBoolean)
    {
      parent.setVisible(paramBoolean);
    }
    
    public boolean isShowing()
    {
      return parent.isShowing();
    }
    
    public boolean contains(Point paramPoint)
    {
      Rectangle localRectangle = getBounds();
      return localRectangle.contains(paramPoint);
    }
    
    public Point getLocationOnScreen()
    {
      Point localPoint1 = parent.getLocationOnScreen();
      Point localPoint2 = getLocation();
      localPoint2.translate(x, y);
      return localPoint2;
    }
    
    public Point getLocation()
    {
      Rectangle localRectangle = getBounds();
      return new Point(x, y);
    }
    
    public void setLocation(Point paramPoint) {}
    
    public Rectangle getBounds()
    {
      return parent.getUI().getTabBounds(parent, parent.indexOfTab(title));
    }
    
    public void setBounds(Rectangle paramRectangle) {}
    
    public Dimension getSize()
    {
      Rectangle localRectangle = getBounds();
      return new Dimension(width, height);
    }
    
    public void setSize(Dimension paramDimension) {}
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      if ((component instanceof Accessible)) {
        return (Accessible)component;
      }
      return null;
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public void addFocusListener(FocusListener paramFocusListener) {}
    
    public void removeFocusListener(FocusListener paramFocusListener) {}
    
    public AccessibleIcon[] getAccessibleIcon()
    {
      AccessibleIcon localAccessibleIcon = null;
      Object localObject;
      if ((enabled) && ((icon instanceof ImageIcon)))
      {
        localObject = ((ImageIcon)icon).getAccessibleContext();
        localAccessibleIcon = (AccessibleIcon)localObject;
      }
      else if ((!enabled) && ((disabledIcon instanceof ImageIcon)))
      {
        localObject = ((ImageIcon)disabledIcon).getAccessibleContext();
        localAccessibleIcon = (AccessibleIcon)localObject;
      }
      if (localAccessibleIcon != null)
      {
        localObject = new AccessibleIcon[1];
        localObject[0] = localAccessibleIcon;
        return (AccessibleIcon[])localObject;
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JTabbedPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */