package javax.swing.plaf.metal;

import com.sun.java.swing.plaf.windows.DesktopProperty;
import java.awt.Font;

class MetalFontDesktopProperty
  extends DesktopProperty
{
  private static final String[] propertyMapping = { "win.ansiVar.font.height", "win.tooltip.font.height", "win.ansiVar.font.height", "win.menu.font.height", "win.frame.captionFont.height", "win.menu.font.height" };
  private int type;
  
  MetalFontDesktopProperty(int paramInt)
  {
    this(propertyMapping[paramInt], paramInt);
  }
  
  MetalFontDesktopProperty(String paramString, int paramInt)
  {
    super(paramString, null);
    type = paramInt;
  }
  
  protected Object configureValue(Object paramObject)
  {
    if ((paramObject instanceof Integer)) {
      paramObject = new Font(DefaultMetalTheme.getDefaultFontName(type), DefaultMetalTheme.getDefaultFontStyle(type), ((Integer)paramObject).intValue());
    }
    return super.configureValue(paramObject);
  }
  
  protected Object getDefaultValue()
  {
    return new Font(DefaultMetalTheme.getDefaultFontName(type), DefaultMetalTheme.getDefaultFontStyle(type), DefaultMetalTheme.getDefaultFontSize(type));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalFontDesktopProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */