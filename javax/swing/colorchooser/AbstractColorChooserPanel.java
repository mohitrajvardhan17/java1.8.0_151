package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

public abstract class AbstractColorChooserPanel
  extends JPanel
{
  private final PropertyChangeListener enabledListener = new PropertyChangeListener()
  {
    public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
    {
      Object localObject = paramAnonymousPropertyChangeEvent.getNewValue();
      if ((localObject instanceof Boolean)) {
        setEnabled(((Boolean)localObject).booleanValue());
      }
    }
  };
  private JColorChooser chooser;
  
  public AbstractColorChooserPanel() {}
  
  public abstract void updateChooser();
  
  protected abstract void buildChooser();
  
  public abstract String getDisplayName();
  
  public int getMnemonic()
  {
    return 0;
  }
  
  public int getDisplayedMnemonicIndex()
  {
    return -1;
  }
  
  public abstract Icon getSmallDisplayIcon();
  
  public abstract Icon getLargeDisplayIcon();
  
  public void installChooserPanel(JColorChooser paramJColorChooser)
  {
    if (chooser != null) {
      throw new RuntimeException("This chooser panel is already installed");
    }
    chooser = paramJColorChooser;
    chooser.addPropertyChangeListener("enabled", enabledListener);
    setEnabled(chooser.isEnabled());
    buildChooser();
    updateChooser();
  }
  
  public void uninstallChooserPanel(JColorChooser paramJColorChooser)
  {
    chooser.removePropertyChangeListener("enabled", enabledListener);
    chooser = null;
  }
  
  public ColorSelectionModel getColorSelectionModel()
  {
    return chooser != null ? chooser.getSelectionModel() : null;
  }
  
  protected Color getColorFromModel()
  {
    ColorSelectionModel localColorSelectionModel = getColorSelectionModel();
    return localColorSelectionModel != null ? localColorSelectionModel.getSelectedColor() : null;
  }
  
  void setSelectedColor(Color paramColor)
  {
    ColorSelectionModel localColorSelectionModel = getColorSelectionModel();
    if (localColorSelectionModel != null) {
      localColorSelectionModel.setSelectedColor(paramColor);
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
  }
  
  int getInt(Object paramObject, int paramInt)
  {
    Object localObject = UIManager.get(paramObject, getLocale());
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\AbstractColorChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */