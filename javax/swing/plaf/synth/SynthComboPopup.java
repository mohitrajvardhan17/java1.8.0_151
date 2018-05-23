package javax.swing.plaf.synth;

import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;

class SynthComboPopup
  extends BasicComboPopup
{
  public SynthComboPopup(JComboBox paramJComboBox)
  {
    super(paramJComboBox);
  }
  
  protected void configureList()
  {
    list.setFont(comboBox.getFont());
    list.setCellRenderer(comboBox.getRenderer());
    list.setFocusable(false);
    list.setSelectionMode(0);
    int i = comboBox.getSelectedIndex();
    if (i == -1)
    {
      list.clearSelection();
    }
    else
    {
      list.setSelectedIndex(i);
      list.ensureIndexIsVisible(i);
    }
    installListListeners();
  }
  
  protected Rectangle computePopupBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ComboBoxUI localComboBoxUI = comboBox.getUI();
    if ((localComboBoxUI instanceof SynthComboBoxUI))
    {
      SynthComboBoxUI localSynthComboBoxUI = (SynthComboBoxUI)localComboBoxUI;
      if (popupInsets != null)
      {
        Insets localInsets = popupInsets;
        return super.computePopupBounds(paramInt1 + left, paramInt2 + top, paramInt3 - left - right, paramInt4 - top - bottom);
      }
    }
    return super.computePopupBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */