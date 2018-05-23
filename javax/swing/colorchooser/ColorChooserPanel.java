package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

final class ColorChooserPanel
  extends AbstractColorChooserPanel
  implements PropertyChangeListener
{
  private static final int MASK = -16777216;
  private final ColorModel model;
  private final ColorPanel panel;
  private final DiagramComponent slider;
  private final DiagramComponent diagram;
  private final JFormattedTextField text;
  private final JLabel label;
  
  ColorChooserPanel(ColorModel paramColorModel)
  {
    model = paramColorModel;
    panel = new ColorPanel(model);
    slider = new DiagramComponent(panel, false);
    diagram = new DiagramComponent(panel, true);
    text = new JFormattedTextField();
    label = new JLabel(null, null, 4);
    ValueFormatter.init(6, true, text);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    setEnabled(this, paramBoolean);
  }
  
  private static void setEnabled(Container paramContainer, boolean paramBoolean)
  {
    for (Component localComponent : paramContainer.getComponents())
    {
      localComponent.setEnabled(paramBoolean);
      if ((localComponent instanceof Container)) {
        setEnabled((Container)localComponent, paramBoolean);
      }
    }
  }
  
  public void updateChooser()
  {
    Color localColor = getColorFromModel();
    if (localColor != null)
    {
      panel.setColor(localColor);
      text.setValue(Integer.valueOf(localColor.getRGB()));
      slider.repaint();
      diagram.repaint();
    }
  }
  
  protected void buildChooser()
  {
    if (0 == getComponentCount())
    {
      setLayout(new GridBagLayout());
      localObject = new GridBagConstraints();
      gridx = 3;
      gridwidth = 2;
      weighty = 1.0D;
      anchor = 11;
      fill = 2;
      insets.top = 10;
      insets.right = 10;
      add(panel, localObject);
      gridwidth = 1;
      weightx = 1.0D;
      weighty = 0.0D;
      anchor = 10;
      insets.right = 5;
      insets.bottom = 10;
      add(label, localObject);
      gridx = 4;
      weightx = 0.0D;
      insets.right = 10;
      add(text, localObject);
      gridx = 2;
      gridheight = 2;
      anchor = 11;
      ipadx = text.getPreferredSize().height;
      ipady = getPreferredSizeheight;
      add(slider, localObject);
      gridx = 1;
      insets.left = 10;
      ipadx = ipady;
      add(diagram, localObject);
      label.setLabelFor(text);
      text.addPropertyChangeListener("value", this);
      slider.setBorder(text.getBorder());
      diagram.setBorder(text.getBorder());
      setInheritsPopupMenu(this, true);
    }
    Object localObject = model.getText(this, "HexCode");
    boolean bool = localObject != null;
    text.setVisible(bool);
    text.getAccessibleContext().setAccessibleDescription((String)localObject);
    label.setVisible(bool);
    if (bool)
    {
      label.setText((String)localObject);
      int i = model.getInteger(this, "HexCodeMnemonic");
      if (i > 0)
      {
        label.setDisplayedMnemonic(i);
        i = model.getInteger(this, "HexCodeMnemonicIndex");
        if (i >= 0) {
          label.setDisplayedMnemonicIndex(i);
        }
      }
    }
    panel.buildPanel();
  }
  
  public String getDisplayName()
  {
    return model.getText(this, "Name");
  }
  
  public int getMnemonic()
  {
    return model.getInteger(this, "Mnemonic");
  }
  
  public int getDisplayedMnemonicIndex()
  {
    return model.getInteger(this, "DisplayedMnemonicIndex");
  }
  
  public Icon getSmallDisplayIcon()
  {
    return null;
  }
  
  public Icon getLargeDisplayIcon()
  {
    return null;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    ColorSelectionModel localColorSelectionModel = getColorSelectionModel();
    if (localColorSelectionModel != null)
    {
      Object localObject = paramPropertyChangeEvent.getNewValue();
      if ((localObject instanceof Integer))
      {
        int i = 0xFF000000 & localColorSelectionModel.getSelectedColor().getRGB() | ((Integer)localObject).intValue();
        localColorSelectionModel.setSelectedColor(new Color(i, true));
      }
    }
    text.selectAll();
  }
  
  private static void setInheritsPopupMenu(JComponent paramJComponent, boolean paramBoolean)
  {
    paramJComponent.setInheritsPopupMenu(paramBoolean);
    for (Component localComponent : paramJComponent.getComponents()) {
      if ((localComponent instanceof JComponent)) {
        setInheritsPopupMenu((JComponent)localComponent, paramBoolean);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\ColorChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */