package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;

public class JMenuBar
  extends JComponent
  implements Accessible, MenuElement
{
  private static final String uiClassID = "MenuBarUI";
  private transient SingleSelectionModel selectionModel;
  private boolean paintBorder = true;
  private Insets margin = null;
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  
  public JMenuBar()
  {
    setFocusTraversalKeysEnabled(false);
    setSelectionModel(new DefaultSingleSelectionModel());
    updateUI();
  }
  
  public MenuBarUI getUI()
  {
    return (MenuBarUI)ui;
  }
  
  public void setUI(MenuBarUI paramMenuBarUI)
  {
    super.setUI(paramMenuBarUI);
  }
  
  public void updateUI()
  {
    setUI((MenuBarUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "MenuBarUI";
  }
  
  public SingleSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  public void setSelectionModel(SingleSelectionModel paramSingleSelectionModel)
  {
    SingleSelectionModel localSingleSelectionModel = selectionModel;
    selectionModel = paramSingleSelectionModel;
    firePropertyChange("selectionModel", localSingleSelectionModel, selectionModel);
  }
  
  public JMenu add(JMenu paramJMenu)
  {
    super.add(paramJMenu);
    return paramJMenu;
  }
  
  public JMenu getMenu(int paramInt)
  {
    Component localComponent = getComponentAtIndex(paramInt);
    if ((localComponent instanceof JMenu)) {
      return (JMenu)localComponent;
    }
    return null;
  }
  
  public int getMenuCount()
  {
    return getComponentCount();
  }
  
  public void setHelpMenu(JMenu paramJMenu)
  {
    throw new Error("setHelpMenu() not yet implemented.");
  }
  
  @Transient
  public JMenu getHelpMenu()
  {
    throw new Error("getHelpMenu() not yet implemented.");
  }
  
  @Deprecated
  public Component getComponentAtIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getComponentCount())) {
      return null;
    }
    return getComponent(paramInt);
  }
  
  public int getComponentIndex(Component paramComponent)
  {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = arrayOfComponent[j];
      if (localComponent == paramComponent) {
        return j;
      }
    }
    return -1;
  }
  
  public void setSelected(Component paramComponent)
  {
    SingleSelectionModel localSingleSelectionModel = getSelectionModel();
    int i = getComponentIndex(paramComponent);
    localSingleSelectionModel.setSelectedIndex(i);
  }
  
  public boolean isSelected()
  {
    return selectionModel.isSelected();
  }
  
  public boolean isBorderPainted()
  {
    return paintBorder;
  }
  
  public void setBorderPainted(boolean paramBoolean)
  {
    boolean bool = paintBorder;
    paintBorder = paramBoolean;
    firePropertyChange("borderPainted", bool, paintBorder);
    if (paramBoolean != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  protected void paintBorder(Graphics paramGraphics)
  {
    if (isBorderPainted()) {
      super.paintBorder(paramGraphics);
    }
  }
  
  public void setMargin(Insets paramInsets)
  {
    Insets localInsets = margin;
    margin = paramInsets;
    firePropertyChange("margin", localInsets, paramInsets);
    if ((localInsets == null) || (!localInsets.equals(paramInsets)))
    {
      revalidate();
      repaint();
    }
  }
  
  public Insets getMargin()
  {
    if (margin == null) {
      return new Insets(0, 0, 0, 0);
    }
    return margin;
  }
  
  public void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void menuSelectionChanged(boolean paramBoolean) {}
  
  public MenuElement[] getSubElements()
  {
    Vector localVector = new Vector();
    int i = getComponentCount();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = getComponent(j);
      if ((localComponent instanceof MenuElement)) {
        localVector.addElement((MenuElement)localComponent);
      }
    }
    MenuElement[] arrayOfMenuElement = new MenuElement[localVector.size()];
    j = 0;
    i = localVector.size();
    while (j < i)
    {
      arrayOfMenuElement[j] = ((MenuElement)localVector.elementAt(j));
      j++;
    }
    return arrayOfMenuElement;
  }
  
  public Component getComponent()
  {
    return this;
  }
  
  protected String paramString()
  {
    String str1 = paintBorder ? "true" : "false";
    String str2 = margin != null ? margin.toString() : "";
    return super.paramString() + ",margin=" + str2 + ",paintBorder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJMenuBar();
    }
    return accessibleContext;
  }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
  {
    boolean bool = super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean);
    if (!bool)
    {
      MenuElement[] arrayOfMenuElement1 = getSubElements();
      for (MenuElement localMenuElement : arrayOfMenuElement1) {
        if (processBindingForKeyStrokeRecursive(localMenuElement, paramKeyStroke, paramKeyEvent, paramInt, paramBoolean)) {
          return true;
        }
      }
    }
    return bool;
  }
  
  static boolean processBindingForKeyStrokeRecursive(MenuElement paramMenuElement, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
  {
    if (paramMenuElement == null) {
      return false;
    }
    Component localComponent = paramMenuElement.getComponent();
    if (((!localComponent.isVisible()) && (!(localComponent instanceof JPopupMenu))) || (!localComponent.isEnabled())) {
      return false;
    }
    if ((localComponent != null) && ((localComponent instanceof JComponent)) && (((JComponent)localComponent).processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean))) {
      return true;
    }
    MenuElement[] arrayOfMenuElement1 = paramMenuElement.getSubElements();
    for (MenuElement localMenuElement : arrayOfMenuElement1) {
      if (processBindingForKeyStrokeRecursive(localMenuElement, paramKeyStroke, paramKeyEvent, paramInt, paramBoolean)) {
        return true;
      }
    }
    return false;
  }
  
  public void addNotify()
  {
    super.addNotify();
    KeyboardManager.getCurrentManager().registerMenuBar(this);
  }
  
  public void removeNotify()
  {
    super.removeNotify();
    KeyboardManager.getCurrentManager().unregisterMenuBar(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("MenuBarUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
    Object[] arrayOfObject = new Object[4];
    int i = 0;
    if ((selectionModel instanceof Serializable))
    {
      arrayOfObject[(i++)] = "selectionModel";
      arrayOfObject[(i++)] = selectionModel;
    }
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    for (int i = 0; (i < arrayOfObject.length) && (arrayOfObject[i] != null); i += 2) {
      if (arrayOfObject[i].equals("selectionModel")) {
        selectionModel = ((SingleSelectionModel)arrayOfObject[(i + 1)]);
      }
    }
  }
  
  protected class AccessibleJMenuBar
    extends JComponent.AccessibleJComponent
    implements AccessibleSelection
  {
    protected AccessibleJMenuBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU_BAR;
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      if (isSelected()) {
        return 1;
      }
      return 0;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      if (isSelected())
      {
        if (paramInt != 0) {
          return null;
        }
        int i = getSelectionModel().getSelectedIndex();
        if ((getComponentAtIndex(i) instanceof Accessible)) {
          return (Accessible)getComponentAtIndex(i);
        }
      }
      return null;
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return paramInt == getSelectionModel().getSelectedIndex();
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      int i = getSelectionModel().getSelectedIndex();
      if (paramInt == i) {
        return;
      }
      if ((i >= 0) && (i < getMenuCount()))
      {
        localJMenu = getMenu(i);
        if (localJMenu != null) {
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }
      }
      getSelectionModel().setSelectedIndex(paramInt);
      JMenu localJMenu = getMenu(paramInt);
      if (localJMenu != null)
      {
        MenuElement[] arrayOfMenuElement = new MenuElement[3];
        arrayOfMenuElement[0] = JMenuBar.this;
        arrayOfMenuElement[1] = localJMenu;
        arrayOfMenuElement[2] = localJMenu.getPopupMenu();
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
      }
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < getMenuCount()))
      {
        JMenu localJMenu = getMenu(paramInt);
        if (localJMenu != null) {
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }
        getSelectionModel().setSelectedIndex(-1);
      }
    }
    
    public void clearAccessibleSelection()
    {
      int i = getSelectionModel().getSelectedIndex();
      if ((i >= 0) && (i < getMenuCount()))
      {
        JMenu localJMenu = getMenu(i);
        if (localJMenu != null) {
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }
      }
      getSelectionModel().setSelectedIndex(-1);
    }
    
    public void selectAllAccessibleSelection() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JMenuBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */