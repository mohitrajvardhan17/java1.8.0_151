package javax.swing.colorchooser;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class SlidingSpinner
  implements ChangeListener
{
  private final ColorPanel panel;
  private final JComponent label;
  private final SpinnerNumberModel model = new SpinnerNumberModel();
  private final JSlider slider = new JSlider();
  private final JSpinner spinner = new JSpinner(model);
  private float value;
  private boolean internal;
  
  SlidingSpinner(ColorPanel paramColorPanel, JComponent paramJComponent)
  {
    panel = paramColorPanel;
    label = paramJComponent;
    slider.addChangeListener(this);
    spinner.addChangeListener(this);
    JSpinner.DefaultEditor localDefaultEditor = (JSpinner.DefaultEditor)spinner.getEditor();
    ValueFormatter.init(3, false, localDefaultEditor.getTextField());
    localDefaultEditor.setFocusable(false);
    spinner.setFocusable(false);
  }
  
  JComponent getLabel()
  {
    return label;
  }
  
  JSlider getSlider()
  {
    return slider;
  }
  
  JSpinner getSpinner()
  {
    return spinner;
  }
  
  float getValue()
  {
    return value;
  }
  
  void setValue(float paramFloat)
  {
    int i = slider.getMinimum();
    int j = slider.getMaximum();
    internal = true;
    slider.setValue(i + (int)(paramFloat * (j - i)));
    spinner.setValue(Integer.valueOf(slider.getValue()));
    internal = false;
    value = paramFloat;
  }
  
  void setRange(int paramInt1, int paramInt2)
  {
    internal = true;
    slider.setMinimum(paramInt1);
    slider.setMaximum(paramInt2);
    model.setMinimum(Integer.valueOf(paramInt1));
    model.setMaximum(Integer.valueOf(paramInt2));
    internal = false;
  }
  
  void setVisible(boolean paramBoolean)
  {
    label.setVisible(paramBoolean);
    slider.setVisible(paramBoolean);
    spinner.setVisible(paramBoolean);
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent)
  {
    if (!internal)
    {
      if (spinner == paramChangeEvent.getSource())
      {
        Object localObject = spinner.getValue();
        if ((localObject instanceof Integer))
        {
          internal = true;
          slider.setValue(((Integer)localObject).intValue());
          internal = false;
        }
      }
      int i = slider.getValue();
      internal = true;
      spinner.setValue(Integer.valueOf(i));
      internal = false;
      int j = slider.getMinimum();
      int k = slider.getMaximum();
      value = ((i - j) / (k - j));
      panel.colorChanged();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\SlidingSpinner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */