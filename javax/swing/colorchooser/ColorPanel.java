package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;

final class ColorPanel
  extends JPanel
  implements ActionListener
{
  private final SlidingSpinner[] spinners = new SlidingSpinner[5];
  private final float[] values = new float[spinners.length];
  private final ColorModel model;
  private Color color;
  private int x = 1;
  private int y = 2;
  private int z;
  
  ColorPanel(ColorModel paramColorModel)
  {
    super(new GridBagLayout());
    GridBagConstraints localGridBagConstraints = new GridBagConstraints();
    fill = 2;
    gridx = 1;
    ButtonGroup localButtonGroup = new ButtonGroup();
    EmptyBorder localEmptyBorder = null;
    for (int i = 0; i < spinners.length; i++)
    {
      Object localObject;
      if (i < 3)
      {
        localObject = new JRadioButton();
        if (i == 0)
        {
          Insets localInsets = ((JRadioButton)localObject).getInsets();
          left = getPreferredSizewidth;
          localEmptyBorder = new EmptyBorder(localInsets);
          ((JRadioButton)localObject).setSelected(true);
          insets.top = 5;
        }
        add((Component)localObject, localGridBagConstraints);
        localButtonGroup.add((AbstractButton)localObject);
        ((JRadioButton)localObject).setActionCommand(Integer.toString(i));
        ((JRadioButton)localObject).addActionListener(this);
        spinners[i] = new SlidingSpinner(this, (JComponent)localObject);
      }
      else
      {
        localObject = new JLabel();
        add((Component)localObject, localGridBagConstraints);
        ((JLabel)localObject).setBorder(localEmptyBorder);
        ((JLabel)localObject).setFocusable(false);
        spinners[i] = new SlidingSpinner(this, (JComponent)localObject);
      }
    }
    gridx = 2;
    weightx = 1.0D;
    insets.top = 0;
    insets.left = 5;
    SlidingSpinner localSlidingSpinner;
    for (localSlidingSpinner : spinners)
    {
      add(localSlidingSpinner.getSlider(), localGridBagConstraints);
      insets.top = 5;
    }
    gridx = 3;
    weightx = 0.0D;
    insets.top = 0;
    for (localSlidingSpinner : spinners)
    {
      add(localSlidingSpinner.getSpinner(), localGridBagConstraints);
      insets.top = 5;
    }
    setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
    setFocusTraversalPolicyProvider(true);
    setFocusable(false);
    model = paramColorModel;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    try
    {
      z = Integer.parseInt(paramActionEvent.getActionCommand());
      y = (z != 2 ? 2 : 1);
      x = (z != 0 ? 0 : 1);
      getParent().repaint();
    }
    catch (NumberFormatException localNumberFormatException) {}
  }
  
  void buildPanel()
  {
    int i = model.getCount();
    spinners[4].setVisible(i > 4);
    for (int j = 0; j < i; j++)
    {
      String str = model.getLabel(this, j);
      JComponent localJComponent = spinners[j].getLabel();
      if ((localJComponent instanceof JRadioButton))
      {
        localObject = (JRadioButton)localJComponent;
        ((JRadioButton)localObject).setText(str);
        ((JRadioButton)localObject).getAccessibleContext().setAccessibleDescription(str);
      }
      else if ((localJComponent instanceof JLabel))
      {
        localObject = (JLabel)localJComponent;
        ((JLabel)localObject).setText(str);
      }
      spinners[j].setRange(model.getMinimum(j), model.getMaximum(j));
      spinners[j].setValue(values[j]);
      spinners[j].getSlider().getAccessibleContext().setAccessibleName(str);
      spinners[j].getSpinner().getAccessibleContext().setAccessibleName(str);
      Object localObject = (JSpinner.DefaultEditor)spinners[j].getSpinner().getEditor();
      ((JSpinner.DefaultEditor)localObject).getTextField().getAccessibleContext().setAccessibleName(str);
      spinners[j].getSlider().getAccessibleContext().setAccessibleDescription(str);
      spinners[j].getSpinner().getAccessibleContext().setAccessibleDescription(str);
      ((JSpinner.DefaultEditor)localObject).getTextField().getAccessibleContext().setAccessibleDescription(str);
    }
  }
  
  void colorChanged()
  {
    color = new Color(getColor(0), true);
    Container localContainer = getParent();
    if ((localContainer instanceof ColorChooserPanel))
    {
      ColorChooserPanel localColorChooserPanel = (ColorChooserPanel)localContainer;
      localColorChooserPanel.setSelectedColor(color);
      localColorChooserPanel.repaint();
    }
  }
  
  float getValueX()
  {
    return spinners[x].getValue();
  }
  
  float getValueY()
  {
    return 1.0F - spinners[y].getValue();
  }
  
  float getValueZ()
  {
    return 1.0F - spinners[z].getValue();
  }
  
  void setValue(float paramFloat)
  {
    spinners[z].setValue(1.0F - paramFloat);
    colorChanged();
  }
  
  void setValue(float paramFloat1, float paramFloat2)
  {
    spinners[x].setValue(paramFloat1);
    spinners[y].setValue(1.0F - paramFloat2);
    colorChanged();
  }
  
  int getColor(float paramFloat)
  {
    setDefaultValue(x);
    setDefaultValue(y);
    values[z] = (1.0F - paramFloat);
    return getColor(3);
  }
  
  int getColor(float paramFloat1, float paramFloat2)
  {
    values[x] = paramFloat1;
    values[y] = (1.0F - paramFloat2);
    setValue(z);
    return getColor(3);
  }
  
  void setColor(Color paramColor)
  {
    if (!paramColor.equals(color))
    {
      color = paramColor;
      model.setColor(paramColor.getRGB(), values);
      for (int i = 0; i < model.getCount(); i++) {
        spinners[i].setValue(values[i]);
      }
    }
  }
  
  private int getColor(int paramInt)
  {
    while (paramInt < model.getCount()) {
      setValue(paramInt++);
    }
    return model.getColor(values);
  }
  
  private void setValue(int paramInt)
  {
    values[paramInt] = spinners[paramInt].getValue();
  }
  
  private void setDefaultValue(int paramInt)
  {
    float f = model.getDefault(paramInt);
    values[paramInt] = (f < 0.0F ? spinners[paramInt].getValue() : f);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\ColorPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */