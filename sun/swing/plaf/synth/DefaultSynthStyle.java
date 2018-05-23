package sun.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class DefaultSynthStyle
  extends SynthStyle
  implements Cloneable
{
  private static final Object PENDING = new Object();
  private boolean opaque;
  private Insets insets;
  private StateInfo[] states;
  private Map data;
  private Font font;
  private SynthGraphicsUtils synthGraphics;
  private SynthPainter painter;
  
  public DefaultSynthStyle() {}
  
  public DefaultSynthStyle(DefaultSynthStyle paramDefaultSynthStyle)
  {
    opaque = opaque;
    if (insets != null) {
      insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }
    if (states != null)
    {
      states = new StateInfo[states.length];
      for (int i = states.length - 1; i >= 0; i--) {
        states[i] = ((StateInfo)states[i].clone());
      }
    }
    if (data != null)
    {
      data = new HashMap();
      data.putAll(data);
    }
    font = font;
    synthGraphics = synthGraphics;
    painter = painter;
  }
  
  public DefaultSynthStyle(Insets paramInsets, boolean paramBoolean, StateInfo[] paramArrayOfStateInfo, Map paramMap)
  {
    insets = paramInsets;
    opaque = paramBoolean;
    states = paramArrayOfStateInfo;
    data = paramMap;
  }
  
  public Color getColor(SynthContext paramSynthContext, ColorType paramColorType)
  {
    return getColor(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState(), paramColorType);
  }
  
  public Color getColor(JComponent paramJComponent, Region paramRegion, int paramInt, ColorType paramColorType)
  {
    if ((!paramRegion.isSubregion()) && (paramInt == 1))
    {
      if (paramColorType == ColorType.BACKGROUND) {
        return paramJComponent.getBackground();
      }
      if (paramColorType == ColorType.FOREGROUND) {
        return paramJComponent.getForeground();
      }
      if (paramColorType == ColorType.TEXT_FOREGROUND)
      {
        localColor = paramJComponent.getForeground();
        if (!(localColor instanceof UIResource)) {
          return localColor;
        }
      }
    }
    Color localColor = getColorForState(paramJComponent, paramRegion, paramInt, paramColorType);
    if (localColor == null)
    {
      if ((paramColorType == ColorType.BACKGROUND) || (paramColorType == ColorType.TEXT_BACKGROUND)) {
        return paramJComponent.getBackground();
      }
      if ((paramColorType == ColorType.FOREGROUND) || (paramColorType == ColorType.TEXT_FOREGROUND)) {
        return paramJComponent.getForeground();
      }
    }
    return localColor;
  }
  
  protected Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType)
  {
    return getColorForState(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState(), paramColorType);
  }
  
  protected Color getColorForState(JComponent paramJComponent, Region paramRegion, int paramInt, ColorType paramColorType)
  {
    StateInfo localStateInfo = getStateInfo(paramInt);
    Color localColor;
    if ((localStateInfo != null) && ((localColor = localStateInfo.getColor(paramColorType)) != null)) {
      return localColor;
    }
    if ((localStateInfo == null) || (localStateInfo.getComponentState() != 0))
    {
      localStateInfo = getStateInfo(0);
      if (localStateInfo != null) {
        return localStateInfo.getColor(paramColorType);
      }
    }
    return null;
  }
  
  public void setFont(Font paramFont)
  {
    font = paramFont;
  }
  
  public Font getFont(SynthContext paramSynthContext)
  {
    return getFont(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState());
  }
  
  public Font getFont(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    if ((!paramRegion.isSubregion()) && (paramInt == 1)) {
      return paramJComponent.getFont();
    }
    Font localFont = paramJComponent.getFont();
    if ((localFont != null) && (!(localFont instanceof UIResource))) {
      return localFont;
    }
    return getFontForState(paramJComponent, paramRegion, paramInt);
  }
  
  protected Font getFontForState(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    if (paramJComponent == null) {
      return font;
    }
    StateInfo localStateInfo = getStateInfo(paramInt);
    Font localFont;
    if ((localStateInfo != null) && ((localFont = localStateInfo.getFont()) != null)) {
      return localFont;
    }
    if ((localStateInfo == null) || (localStateInfo.getComponentState() != 0))
    {
      localStateInfo = getStateInfo(0);
      if ((localStateInfo != null) && ((localFont = localStateInfo.getFont()) != null)) {
        return localFont;
      }
    }
    return font;
  }
  
  protected Font getFontForState(SynthContext paramSynthContext)
  {
    return getFontForState(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState());
  }
  
  public void setGraphicsUtils(SynthGraphicsUtils paramSynthGraphicsUtils)
  {
    synthGraphics = paramSynthGraphicsUtils;
  }
  
  public SynthGraphicsUtils getGraphicsUtils(SynthContext paramSynthContext)
  {
    if (synthGraphics == null) {
      return super.getGraphicsUtils(paramSynthContext);
    }
    return synthGraphics;
  }
  
  public void setInsets(Insets paramInsets)
  {
    insets = paramInsets;
  }
  
  public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets)
  {
    if (paramInsets == null) {
      paramInsets = new Insets(0, 0, 0, 0);
    }
    if (insets != null)
    {
      left = insets.left;
      right = insets.right;
      top = insets.top;
      bottom = insets.bottom;
    }
    else
    {
      left = (right = top = bottom = 0);
    }
    return paramInsets;
  }
  
  public void setPainter(SynthPainter paramSynthPainter)
  {
    painter = paramSynthPainter;
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext)
  {
    return painter;
  }
  
  public void setOpaque(boolean paramBoolean)
  {
    opaque = paramBoolean;
  }
  
  public boolean isOpaque(SynthContext paramSynthContext)
  {
    return opaque;
  }
  
  public void setData(Map paramMap)
  {
    data = paramMap;
  }
  
  public Map getData()
  {
    return data;
  }
  
  public Object get(SynthContext paramSynthContext, Object paramObject)
  {
    StateInfo localStateInfo = getStateInfo(paramSynthContext.getComponentState());
    if ((localStateInfo != null) && (localStateInfo.getData() != null) && (getKeyFromData(localStateInfo.getData(), paramObject) != null)) {
      return getKeyFromData(localStateInfo.getData(), paramObject);
    }
    localStateInfo = getStateInfo(0);
    if ((localStateInfo != null) && (localStateInfo.getData() != null) && (getKeyFromData(localStateInfo.getData(), paramObject) != null)) {
      return getKeyFromData(localStateInfo.getData(), paramObject);
    }
    if (getKeyFromData(data, paramObject) != null) {
      return getKeyFromData(data, paramObject);
    }
    return getDefaultValue(paramSynthContext, paramObject);
  }
  
  private Object getKeyFromData(Map paramMap, Object paramObject)
  {
    Object localObject1 = null;
    if (paramMap != null)
    {
      synchronized (paramMap)
      {
        localObject1 = paramMap.get(paramObject);
      }
      while (localObject1 == PENDING) {
        synchronized (paramMap)
        {
          try
          {
            paramMap.wait();
          }
          catch (InterruptedException localInterruptedException) {}
          localObject1 = paramMap.get(paramObject);
        }
      }
      if ((localObject1 instanceof UIDefaults.LazyValue))
      {
        synchronized (paramMap)
        {
          paramMap.put(paramObject, PENDING);
        }
        localObject1 = ((UIDefaults.LazyValue)localObject1).createValue(null);
        synchronized (paramMap)
        {
          paramMap.put(paramObject, localObject1);
          paramMap.notifyAll();
        }
      }
    }
    return localObject1;
  }
  
  public Object getDefaultValue(SynthContext paramSynthContext, Object paramObject)
  {
    return super.get(paramSynthContext, paramObject);
  }
  
  public Object clone()
  {
    DefaultSynthStyle localDefaultSynthStyle;
    try
    {
      localDefaultSynthStyle = (DefaultSynthStyle)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      return null;
    }
    if (states != null)
    {
      states = new StateInfo[states.length];
      for (int i = states.length - 1; i >= 0; i--) {
        states[i] = ((StateInfo)states[i].clone());
      }
    }
    if (data != null)
    {
      data = new HashMap();
      data.putAll(data);
    }
    return localDefaultSynthStyle;
  }
  
  public DefaultSynthStyle addTo(DefaultSynthStyle paramDefaultSynthStyle)
  {
    if (insets != null) {
      insets = insets;
    }
    if (font != null) {
      font = font;
    }
    if (painter != null) {
      painter = painter;
    }
    if (synthGraphics != null) {
      synthGraphics = synthGraphics;
    }
    opaque = opaque;
    if (states != null)
    {
      int i;
      if (states == null)
      {
        states = new StateInfo[states.length];
        for (i = states.length - 1; i >= 0; i--) {
          if (states[i] != null) {
            states[i] = ((StateInfo)states[i].clone());
          }
        }
      }
      else
      {
        i = 0;
        int j = 0;
        int k = states.length;
        int n;
        int i1;
        int i2;
        for (int m = states.length - 1; m >= 0; m--)
        {
          n = states[m].getComponentState();
          i1 = 0;
          for (i2 = k - 1 - j; i2 >= 0; i2--) {
            if (n == states[i2].getComponentState())
            {
              states[i2] = states[m].addTo(states[i2]);
              StateInfo localStateInfo = states[(k - 1 - j)];
              states[(k - 1 - j)] = states[i2];
              states[i2] = localStateInfo;
              j++;
              i1 = 1;
              break;
            }
          }
          if (i1 == 0) {
            i++;
          }
        }
        if (i != 0)
        {
          StateInfo[] arrayOfStateInfo = new StateInfo[i + k];
          n = k;
          System.arraycopy(states, 0, arrayOfStateInfo, 0, k);
          for (i1 = states.length - 1; i1 >= 0; i1--)
          {
            i2 = states[i1].getComponentState();
            int i3 = 0;
            for (int i4 = k - 1; i4 >= 0; i4--) {
              if (i2 == states[i4].getComponentState())
              {
                i3 = 1;
                break;
              }
            }
            if (i3 == 0) {
              arrayOfStateInfo[(n++)] = ((StateInfo)states[i1].clone());
            }
          }
          states = arrayOfStateInfo;
        }
      }
    }
    if (data != null)
    {
      if (data == null) {
        data = new HashMap();
      }
      data.putAll(data);
    }
    return paramDefaultSynthStyle;
  }
  
  public void setStateInfo(StateInfo[] paramArrayOfStateInfo)
  {
    states = paramArrayOfStateInfo;
  }
  
  public StateInfo[] getStateInfo()
  {
    return states;
  }
  
  public StateInfo getStateInfo(int paramInt)
  {
    if (states != null)
    {
      int i = 0;
      int j = -1;
      int k = -1;
      if (paramInt == 0)
      {
        for (m = states.length - 1; m >= 0; m--) {
          if (states[m].getComponentState() == 0) {
            return states[m];
          }
        }
        return null;
      }
      for (int m = states.length - 1; m >= 0; m--)
      {
        int n = states[m].getComponentState();
        if (n == 0)
        {
          if (k == -1) {
            k = m;
          }
        }
        else if ((paramInt & n) == n)
        {
          int i1 = n;
          i1 -= ((0xAAAAAAAA & i1) >>> 1);
          i1 = (i1 & 0x33333333) + (i1 >>> 2 & 0x33333333);
          i1 = i1 + (i1 >>> 4) & 0xF0F0F0F;
          i1 += (i1 >>> 8);
          i1 += (i1 >>> 16);
          i1 &= 0xFF;
          if (i1 > i)
          {
            j = m;
            i = i1;
          }
        }
      }
      if (j != -1) {
        return states[j];
      }
      if (k != -1) {
        return states[k];
      }
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(super.toString()).append(',');
    localStringBuffer.append("data=").append(data).append(',');
    localStringBuffer.append("font=").append(font).append(',');
    localStringBuffer.append("insets=").append(insets).append(',');
    localStringBuffer.append("synthGraphics=").append(synthGraphics).append(',');
    localStringBuffer.append("painter=").append(painter).append(',');
    StateInfo[] arrayOfStateInfo1 = getStateInfo();
    if (arrayOfStateInfo1 != null)
    {
      localStringBuffer.append("states[");
      for (StateInfo localStateInfo : arrayOfStateInfo1) {
        localStringBuffer.append(localStateInfo.toString()).append(',');
      }
      localStringBuffer.append(']').append(',');
    }
    localStringBuffer.deleteCharAt(localStringBuffer.length() - 1);
    return localStringBuffer.toString();
  }
  
  public static class StateInfo
  {
    private Map data;
    private Font font;
    private Color[] colors;
    private int state;
    
    public StateInfo() {}
    
    public StateInfo(int paramInt, Font paramFont, Color[] paramArrayOfColor)
    {
      state = paramInt;
      font = paramFont;
      colors = paramArrayOfColor;
    }
    
    public StateInfo(StateInfo paramStateInfo)
    {
      state = state;
      font = font;
      if (data != null)
      {
        if (data == null) {
          data = new HashMap();
        }
        data.putAll(data);
      }
      if (colors != null)
      {
        colors = new Color[colors.length];
        System.arraycopy(colors, 0, colors, 0, colors.length);
      }
    }
    
    public Map getData()
    {
      return data;
    }
    
    public void setData(Map paramMap)
    {
      data = paramMap;
    }
    
    public void setFont(Font paramFont)
    {
      font = paramFont;
    }
    
    public Font getFont()
    {
      return font;
    }
    
    public void setColors(Color[] paramArrayOfColor)
    {
      colors = paramArrayOfColor;
    }
    
    public Color[] getColors()
    {
      return colors;
    }
    
    public Color getColor(ColorType paramColorType)
    {
      if (colors != null)
      {
        int i = paramColorType.getID();
        if (i < colors.length) {
          return colors[i];
        }
      }
      return null;
    }
    
    public StateInfo addTo(StateInfo paramStateInfo)
    {
      if (font != null) {
        font = font;
      }
      if (data != null)
      {
        if (data == null) {
          data = new HashMap();
        }
        data.putAll(data);
      }
      if (colors != null) {
        if (colors == null)
        {
          colors = new Color[colors.length];
          System.arraycopy(colors, 0, colors, 0, colors.length);
        }
        else
        {
          if (colors.length < colors.length)
          {
            Color[] arrayOfColor = colors;
            colors = new Color[colors.length];
            System.arraycopy(arrayOfColor, 0, colors, 0, arrayOfColor.length);
          }
          for (int i = colors.length - 1; i >= 0; i--) {
            if (colors[i] != null) {
              colors[i] = colors[i];
            }
          }
        }
      }
      return paramStateInfo;
    }
    
    public void setComponentState(int paramInt)
    {
      state = paramInt;
    }
    
    public int getComponentState()
    {
      return state;
    }
    
    private int getMatchCount(int paramInt)
    {
      paramInt &= state;
      paramInt -= ((0xAAAAAAAA & paramInt) >>> 1);
      paramInt = (paramInt & 0x33333333) + (paramInt >>> 2 & 0x33333333);
      paramInt = paramInt + (paramInt >>> 4) & 0xF0F0F0F;
      paramInt += (paramInt >>> 8);
      paramInt += (paramInt >>> 16);
      return paramInt & 0xFF;
    }
    
    public Object clone()
    {
      return new StateInfo(this);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(super.toString()).append(',');
      localStringBuffer.append("state=").append(Integer.toString(state)).append(',');
      localStringBuffer.append("font=").append(font).append(',');
      if (colors != null) {
        localStringBuffer.append("colors=").append(Arrays.asList(colors)).append(',');
      }
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\plaf\synth\DefaultSynthStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */