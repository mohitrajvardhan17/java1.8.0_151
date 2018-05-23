package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;

public class JColorChooser
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "ColorChooserUI";
  private ColorSelectionModel selectionModel;
  private JComponent previewPanel = ColorChooserComponentFactory.getPreviewPanel();
  private AbstractColorChooserPanel[] chooserPanels = new AbstractColorChooserPanel[0];
  private boolean dragEnabled;
  public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
  public static final String PREVIEW_PANEL_PROPERTY = "previewPanel";
  public static final String CHOOSER_PANELS_PROPERTY = "chooserPanels";
  protected AccessibleContext accessibleContext = null;
  
  public static Color showDialog(Component paramComponent, String paramString, Color paramColor)
    throws HeadlessException
  {
    JColorChooser localJColorChooser = new JColorChooser(paramColor != null ? paramColor : Color.white);
    ColorTracker localColorTracker = new ColorTracker(localJColorChooser);
    JDialog localJDialog = createDialog(paramComponent, paramString, true, localJColorChooser, localColorTracker, null);
    localJDialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());
    localJDialog.show();
    return localColorTracker.getColor();
  }
  
  public static JDialog createDialog(Component paramComponent, String paramString, boolean paramBoolean, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
    throws HeadlessException
  {
    Window localWindow = JOptionPane.getWindowForComponent(paramComponent);
    ColorChooserDialog localColorChooserDialog;
    if ((localWindow instanceof Frame)) {
      localColorChooserDialog = new ColorChooserDialog((Frame)localWindow, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
    } else {
      localColorChooserDialog = new ColorChooserDialog((Dialog)localWindow, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
    }
    localColorChooserDialog.getAccessibleContext().setAccessibleDescription(paramString);
    return localColorChooserDialog;
  }
  
  public JColorChooser()
  {
    this(Color.white);
  }
  
  public JColorChooser(Color paramColor)
  {
    this(new DefaultColorSelectionModel(paramColor));
  }
  
  public JColorChooser(ColorSelectionModel paramColorSelectionModel)
  {
    selectionModel = paramColorSelectionModel;
    updateUI();
    dragEnabled = false;
  }
  
  public ColorChooserUI getUI()
  {
    return (ColorChooserUI)ui;
  }
  
  public void setUI(ColorChooserUI paramColorChooserUI)
  {
    super.setUI(paramColorChooserUI);
  }
  
  public void updateUI()
  {
    setUI((ColorChooserUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ColorChooserUI";
  }
  
  public Color getColor()
  {
    return selectionModel.getSelectedColor();
  }
  
  public void setColor(Color paramColor)
  {
    selectionModel.setSelectedColor(paramColor);
  }
  
  public void setColor(int paramInt1, int paramInt2, int paramInt3)
  {
    setColor(new Color(paramInt1, paramInt2, paramInt3));
  }
  
  public void setColor(int paramInt)
  {
    setColor(paramInt >> 16 & 0xFF, paramInt >> 8 & 0xFF, paramInt & 0xFF);
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return dragEnabled;
  }
  
  public void setPreviewPanel(JComponent paramJComponent)
  {
    if (previewPanel != paramJComponent)
    {
      JComponent localJComponent = previewPanel;
      previewPanel = paramJComponent;
      firePropertyChange("previewPanel", localJComponent, paramJComponent);
    }
  }
  
  public JComponent getPreviewPanel()
  {
    return previewPanel;
  }
  
  public void addChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel)
  {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = getChooserPanels();
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel2 = new AbstractColorChooserPanel[arrayOfAbstractColorChooserPanel1.length + 1];
    System.arraycopy(arrayOfAbstractColorChooserPanel1, 0, arrayOfAbstractColorChooserPanel2, 0, arrayOfAbstractColorChooserPanel1.length);
    arrayOfAbstractColorChooserPanel2[(arrayOfAbstractColorChooserPanel2.length - 1)] = paramAbstractColorChooserPanel;
    setChooserPanels(arrayOfAbstractColorChooserPanel2);
  }
  
  public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel)
  {
    int i = -1;
    for (int j = 0; j < chooserPanels.length; j++) {
      if (chooserPanels[j] == paramAbstractColorChooserPanel)
      {
        i = j;
        break;
      }
    }
    if (i == -1) {
      throw new IllegalArgumentException("chooser panel not in this chooser");
    }
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = new AbstractColorChooserPanel[chooserPanels.length - 1];
    if (i == chooserPanels.length - 1)
    {
      System.arraycopy(chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
    }
    else if (i == 0)
    {
      System.arraycopy(chooserPanels, 1, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
    }
    else
    {
      System.arraycopy(chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, i);
      System.arraycopy(chooserPanels, i + 1, arrayOfAbstractColorChooserPanel, i, chooserPanels.length - i - 1);
    }
    setChooserPanels(arrayOfAbstractColorChooserPanel);
    return paramAbstractColorChooserPanel;
  }
  
  public void setChooserPanels(AbstractColorChooserPanel[] paramArrayOfAbstractColorChooserPanel)
  {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = chooserPanels;
    chooserPanels = paramArrayOfAbstractColorChooserPanel;
    firePropertyChange("chooserPanels", arrayOfAbstractColorChooserPanel, paramArrayOfAbstractColorChooserPanel);
  }
  
  public AbstractColorChooserPanel[] getChooserPanels()
  {
    return chooserPanels;
  }
  
  public ColorSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  public void setSelectionModel(ColorSelectionModel paramColorSelectionModel)
  {
    ColorSelectionModel localColorSelectionModel = selectionModel;
    selectionModel = paramColorSelectionModel;
    firePropertyChange("selectionModel", localColorSelectionModel, paramColorSelectionModel);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ColorChooserUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < chooserPanels.length; i++) {
      localStringBuffer.append("[" + chooserPanels[i].toString() + "]");
    }
    String str = previewPanel != null ? previewPanel.toString() : "";
    return super.paramString() + ",chooserPanels=" + localStringBuffer.toString() + ",previewPanel=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJColorChooser();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJColorChooser
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJColorChooser()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.COLOR_CHOOSER;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JColorChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */