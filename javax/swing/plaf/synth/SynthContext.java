package javax.swing.plaf.synth;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;

public class SynthContext
{
  private static final Queue<SynthContext> queue = new ConcurrentLinkedQueue();
  private JComponent component;
  private Region region;
  private SynthStyle style;
  private int state;
  
  static SynthContext getContext(JComponent paramJComponent, SynthStyle paramSynthStyle, int paramInt)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getRegion(paramJComponent), paramSynthStyle, paramInt);
  }
  
  static SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt)
  {
    SynthContext localSynthContext = (SynthContext)queue.poll();
    if (localSynthContext == null) {
      localSynthContext = new SynthContext();
    }
    localSynthContext.reset(paramJComponent, paramRegion, paramSynthStyle, paramInt);
    return localSynthContext;
  }
  
  static void releaseContext(SynthContext paramSynthContext)
  {
    queue.offer(paramSynthContext);
  }
  
  SynthContext() {}
  
  public SynthContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt)
  {
    if ((paramJComponent == null) || (paramRegion == null) || (paramSynthStyle == null)) {
      throw new NullPointerException("You must supply a non-null component, region and style");
    }
    reset(paramJComponent, paramRegion, paramSynthStyle, paramInt);
  }
  
  public JComponent getComponent()
  {
    return component;
  }
  
  public Region getRegion()
  {
    return region;
  }
  
  boolean isSubregion()
  {
    return getRegion().isSubregion();
  }
  
  void setStyle(SynthStyle paramSynthStyle)
  {
    style = paramSynthStyle;
  }
  
  public SynthStyle getStyle()
  {
    return style;
  }
  
  void setComponentState(int paramInt)
  {
    state = paramInt;
  }
  
  public int getComponentState()
  {
    return state;
  }
  
  void reset(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt)
  {
    component = paramJComponent;
    region = paramRegion;
    style = paramSynthStyle;
    state = paramInt;
  }
  
  void dispose()
  {
    component = null;
    style = null;
    releaseContext(this);
  }
  
  SynthPainter getPainter()
  {
    SynthPainter localSynthPainter = getStyle().getPainter(this);
    if (localSynthPainter != null) {
      return localSynthPainter;
    }
    return SynthPainter.NULL_PAINTER;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */