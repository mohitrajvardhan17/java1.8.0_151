package javax.swing.plaf.metal;

import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;

class MetalHighContrastTheme
  extends DefaultMetalTheme
{
  private static final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);
  private static final ColorUIResource primary2 = new ColorUIResource(204, 204, 204);
  private static final ColorUIResource primary3 = new ColorUIResource(255, 255, 255);
  private static final ColorUIResource primaryHighlight = new ColorUIResource(102, 102, 102);
  private static final ColorUIResource secondary2 = new ColorUIResource(204, 204, 204);
  private static final ColorUIResource secondary3 = new ColorUIResource(255, 255, 255);
  private static final ColorUIResource controlHighlight = new ColorUIResource(102, 102, 102);
  
  MetalHighContrastTheme() {}
  
  public String getName()
  {
    return "Contrast";
  }
  
  protected ColorUIResource getPrimary1()
  {
    return primary1;
  }
  
  protected ColorUIResource getPrimary2()
  {
    return primary2;
  }
  
  protected ColorUIResource getPrimary3()
  {
    return primary3;
  }
  
  public ColorUIResource getPrimaryControlHighlight()
  {
    return primaryHighlight;
  }
  
  protected ColorUIResource getSecondary2()
  {
    return secondary2;
  }
  
  protected ColorUIResource getSecondary3()
  {
    return secondary3;
  }
  
  public ColorUIResource getControlHighlight()
  {
    return secondary2;
  }
  
  public ColorUIResource getFocusColor()
  {
    return getBlack();
  }
  
  public ColorUIResource getTextHighlightColor()
  {
    return getBlack();
  }
  
  public ColorUIResource getHighlightedTextColor()
  {
    return getWhite();
  }
  
  public ColorUIResource getMenuSelectedBackground()
  {
    return getBlack();
  }
  
  public ColorUIResource getMenuSelectedForeground()
  {
    return getWhite();
  }
  
  public ColorUIResource getAcceleratorForeground()
  {
    return getBlack();
  }
  
  public ColorUIResource getAcceleratorSelectedForeground()
  {
    return getWhite();
  }
  
  public void addCustomEntriesToTable(UIDefaults paramUIDefaults)
  {
    BorderUIResource localBorderUIResource1 = new BorderUIResource(new LineBorder(getBlack()));
    BorderUIResource localBorderUIResource2 = new BorderUIResource(new LineBorder(getWhite()));
    BorderUIResource localBorderUIResource3 = new BorderUIResource(new CompoundBorder(localBorderUIResource1, new BasicBorders.MarginBorder()));
    Object[] arrayOfObject = { "ToolTip.border", localBorderUIResource1, "TitledBorder.border", localBorderUIResource1, "TextField.border", localBorderUIResource3, "PasswordField.border", localBorderUIResource3, "TextArea.border", localBorderUIResource3, "TextPane.border", localBorderUIResource3, "EditorPane.border", localBorderUIResource3, "ComboBox.background", getWindowBackground(), "ComboBox.foreground", getUserTextColor(), "ComboBox.selectionBackground", getTextHighlightColor(), "ComboBox.selectionForeground", getHighlightedTextColor(), "ProgressBar.foreground", getUserTextColor(), "ProgressBar.background", getWindowBackground(), "ProgressBar.selectionForeground", getWindowBackground(), "ProgressBar.selectionBackground", getUserTextColor(), "OptionPane.errorDialog.border.background", getPrimary1(), "OptionPane.errorDialog.titlePane.foreground", getPrimary3(), "OptionPane.errorDialog.titlePane.background", getPrimary1(), "OptionPane.errorDialog.titlePane.shadow", getPrimary2(), "OptionPane.questionDialog.border.background", getPrimary1(), "OptionPane.questionDialog.titlePane.foreground", getPrimary3(), "OptionPane.questionDialog.titlePane.background", getPrimary1(), "OptionPane.questionDialog.titlePane.shadow", getPrimary2(), "OptionPane.warningDialog.border.background", getPrimary1(), "OptionPane.warningDialog.titlePane.foreground", getPrimary3(), "OptionPane.warningDialog.titlePane.background", getPrimary1(), "OptionPane.warningDialog.titlePane.shadow", getPrimary2() };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
  
  boolean isSystemTheme()
  {
    return getClass() == MetalHighContrastTheme.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalHighContrastTheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */