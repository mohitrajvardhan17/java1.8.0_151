package javax.swing.colorchooser;

import javax.swing.JComponent;

public class ColorChooserComponentFactory
{
  private ColorChooserComponentFactory() {}
  
  public static AbstractColorChooserPanel[] getDefaultChooserPanels()
  {
    return new AbstractColorChooserPanel[] { new DefaultSwatchChooserPanel(), new ColorChooserPanel(new ColorModelHSV()), new ColorChooserPanel(new ColorModelHSL()), new ColorChooserPanel(new ColorModel()), new ColorChooserPanel(new ColorModelCMYK()) };
  }
  
  public static JComponent getPreviewPanel()
  {
    return new DefaultPreviewPanel();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\ColorChooserComponentFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */