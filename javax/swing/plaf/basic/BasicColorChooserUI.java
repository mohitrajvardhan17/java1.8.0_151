package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.AccessibleContext;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class BasicColorChooserUI
  extends ColorChooserUI
{
  protected JColorChooser chooser;
  JTabbedPane tabbedPane;
  JPanel singlePanel;
  JPanel previewPanelHolder;
  JComponent previewPanel;
  boolean isMultiPanel = false;
  private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
  protected AbstractColorChooserPanel[] defaultChoosers;
  protected ChangeListener previewListener;
  protected PropertyChangeListener propertyChangeListener;
  private Handler handler;
  
  public BasicColorChooserUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicColorChooserUI();
  }
  
  protected AbstractColorChooserPanel[] createDefaultChoosers()
  {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels();
    return arrayOfAbstractColorChooserPanel;
  }
  
  protected void uninstallDefaultChoosers()
  {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = chooser.getChooserPanels();
    for (int i = 0; i < arrayOfAbstractColorChooserPanel.length; i++) {
      chooser.removeChooserPanel(arrayOfAbstractColorChooserPanel[i]);
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    chooser = ((JColorChooser)paramJComponent);
    super.installUI(paramJComponent);
    installDefaults();
    installListeners();
    tabbedPane = new JTabbedPane();
    tabbedPane.setName("ColorChooser.tabPane");
    tabbedPane.setInheritsPopupMenu(true);
    tabbedPane.getAccessibleContext().setAccessibleDescription(tabbedPane.getName());
    singlePanel = new JPanel(new CenterLayout());
    singlePanel.setName("ColorChooser.panel");
    singlePanel.setInheritsPopupMenu(true);
    chooser.setLayout(new BorderLayout());
    defaultChoosers = createDefaultChoosers();
    chooser.setChooserPanels(defaultChoosers);
    previewPanelHolder = new JPanel(new CenterLayout());
    previewPanelHolder.setName("ColorChooser.previewPanelHolder");
    if (DefaultLookup.getBoolean(chooser, this, "ColorChooser.showPreviewPanelText", true))
    {
      String str = UIManager.getString("ColorChooser.previewText", chooser.getLocale());
      previewPanelHolder.setBorder(new TitledBorder(str));
    }
    previewPanelHolder.setInheritsPopupMenu(true);
    installPreviewPanel();
    chooser.applyComponentOrientation(paramJComponent.getComponentOrientation());
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    chooser.remove(tabbedPane);
    chooser.remove(singlePanel);
    chooser.remove(previewPanelHolder);
    uninstallDefaultChoosers();
    uninstallListeners();
    uninstallPreviewPanel();
    uninstallDefaults();
    previewPanelHolder = null;
    previewPanel = null;
    defaultChoosers = null;
    chooser = null;
    tabbedPane = null;
    handler = null;
  }
  
  protected void installPreviewPanel()
  {
    JComponent localJComponent = chooser.getPreviewPanel();
    if (localJComponent == null) {
      localJComponent = ColorChooserComponentFactory.getPreviewPanel();
    } else if ((JPanel.class.equals(localJComponent.getClass())) && (0 == localJComponent.getComponentCount())) {
      localJComponent = null;
    }
    previewPanel = localJComponent;
    if (localJComponent != null)
    {
      chooser.add(previewPanelHolder, "South");
      localJComponent.setForeground(chooser.getColor());
      previewPanelHolder.add(localJComponent);
      localJComponent.addMouseListener(getHandler());
      localJComponent.setInheritsPopupMenu(true);
    }
  }
  
  protected void uninstallPreviewPanel()
  {
    if (previewPanel != null)
    {
      previewPanel.removeMouseListener(getHandler());
      previewPanelHolder.remove(previewPanel);
    }
    chooser.remove(previewPanelHolder);
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(chooser, "ColorChooser.background", "ColorChooser.foreground", "ColorChooser.font");
    LookAndFeel.installProperty(chooser, "opaque", Boolean.TRUE);
    TransferHandler localTransferHandler = chooser.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource))) {
      chooser.setTransferHandler(defaultTransferHandler);
    }
  }
  
  protected void uninstallDefaults()
  {
    if ((chooser.getTransferHandler() instanceof UIResource)) {
      chooser.setTransferHandler(null);
    }
  }
  
  protected void installListeners()
  {
    propertyChangeListener = createPropertyChangeListener();
    chooser.addPropertyChangeListener(propertyChangeListener);
    previewListener = getHandler();
    chooser.getSelectionModel().addChangeListener(previewListener);
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected void uninstallListeners()
  {
    chooser.removePropertyChangeListener(propertyChangeListener);
    chooser.getSelectionModel().removeChangeListener(previewListener);
    previewListener = null;
  }
  
  private void selectionChanged(ColorSelectionModel paramColorSelectionModel)
  {
    JComponent localJComponent = chooser.getPreviewPanel();
    if (localJComponent != null)
    {
      localJComponent.setForeground(paramColorSelectionModel.getSelectedColor());
      localJComponent.repaint();
    }
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = chooser.getChooserPanels();
    if (arrayOfAbstractColorChooserPanel1 != null) {
      for (AbstractColorChooserPanel localAbstractColorChooserPanel : arrayOfAbstractColorChooserPanel1) {
        if (localAbstractColorChooserPanel != null) {
          localAbstractColorChooserPanel.updateChooser();
        }
      }
    }
  }
  
  static class ColorTransferHandler
    extends TransferHandler
    implements UIResource
  {
    ColorTransferHandler()
    {
      super();
    }
  }
  
  private class Handler
    implements ChangeListener, MouseListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)paramChangeEvent.getSource());
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (chooser.getDragEnabled())
      {
        TransferHandler localTransferHandler = chooser.getTransferHandler();
        localTransferHandler.exportAsDrag(chooser, paramMouseEvent, 1);
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str1 = paramPropertyChangeEvent.getPropertyName();
      Object localObject1;
      Object localObject2;
      if (str1 == "chooserPanels")
      {
        localObject1 = (AbstractColorChooserPanel[])paramPropertyChangeEvent.getOldValue();
        localObject2 = (AbstractColorChooserPanel[])paramPropertyChangeEvent.getNewValue();
        Object localObject3;
        Object localObject4;
        for (int i = 0; i < localObject1.length; i++)
        {
          localObject3 = localObject1[i].getParent();
          if (localObject3 != null)
          {
            localObject4 = ((Container)localObject3).getParent();
            if (localObject4 != null) {
              ((Container)localObject4).remove((Component)localObject3);
            }
            localObject1[i].uninstallChooserPanel(chooser);
          }
        }
        i = localObject2.length;
        if (i == 0)
        {
          chooser.remove(tabbedPane);
          return;
        }
        if (i == 1)
        {
          chooser.remove(tabbedPane);
          localObject3 = new JPanel(new CenterLayout());
          ((JPanel)localObject3).setInheritsPopupMenu(true);
          ((JPanel)localObject3).add(localObject2[0]);
          singlePanel.add((Component)localObject3, "Center");
          chooser.add(singlePanel);
        }
        else
        {
          if (localObject1.length < 2)
          {
            chooser.remove(singlePanel);
            chooser.add(tabbedPane, "Center");
          }
          for (j = 0; j < localObject2.length; j++)
          {
            localObject4 = new JPanel(new CenterLayout());
            ((JPanel)localObject4).setInheritsPopupMenu(true);
            String str2 = localObject2[j].getDisplayName();
            int k = localObject2[j].getMnemonic();
            ((JPanel)localObject4).add(localObject2[j]);
            tabbedPane.addTab(str2, (Component)localObject4);
            if (k > 0)
            {
              tabbedPane.setMnemonicAt(j, k);
              int m = localObject2[j].getDisplayedMnemonicIndex();
              if (m >= 0) {
                tabbedPane.setDisplayedMnemonicIndexAt(j, m);
              }
            }
          }
        }
        chooser.applyComponentOrientation(chooser.getComponentOrientation());
        for (int j = 0; j < localObject2.length; j++) {
          localObject2[j].installChooserPanel(chooser);
        }
      }
      else if (str1 == "previewPanel")
      {
        uninstallPreviewPanel();
        installPreviewPanel();
      }
      else if (str1 == "selectionModel")
      {
        localObject1 = (ColorSelectionModel)paramPropertyChangeEvent.getOldValue();
        ((ColorSelectionModel)localObject1).removeChangeListener(previewListener);
        localObject2 = (ColorSelectionModel)paramPropertyChangeEvent.getNewValue();
        ((ColorSelectionModel)localObject2).addChangeListener(previewListener);
        BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)localObject2);
      }
      else if (str1 == "componentOrientation")
      {
        localObject1 = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
        localObject2 = (JColorChooser)paramPropertyChangeEvent.getSource();
        if (localObject1 != (ComponentOrientation)paramPropertyChangeEvent.getOldValue())
        {
          ((JColorChooser)localObject2).applyComponentOrientation((ComponentOrientation)localObject1);
          ((JColorChooser)localObject2).updateUI();
        }
      }
    }
  }
  
  public class PropertyHandler
    implements PropertyChangeListener
  {
    public PropertyHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicColorChooserUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicColorChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */