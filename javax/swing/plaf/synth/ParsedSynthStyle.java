package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import sun.swing.plaf.synth.DefaultSynthStyle;
import sun.swing.plaf.synth.DefaultSynthStyle.StateInfo;

class ParsedSynthStyle
  extends DefaultSynthStyle
{
  private static SynthPainter DELEGATING_PAINTER_INSTANCE = new DelegatingPainter(null);
  private PainterInfo[] _painters;
  
  private static PainterInfo[] mergePainterInfo(PainterInfo[] paramArrayOfPainterInfo1, PainterInfo[] paramArrayOfPainterInfo2)
  {
    if (paramArrayOfPainterInfo1 == null) {
      return paramArrayOfPainterInfo2;
    }
    if (paramArrayOfPainterInfo2 == null) {
      return paramArrayOfPainterInfo1;
    }
    int i = paramArrayOfPainterInfo1.length;
    int j = paramArrayOfPainterInfo2.length;
    int k = 0;
    PainterInfo[] arrayOfPainterInfo1 = new PainterInfo[i + j];
    System.arraycopy(paramArrayOfPainterInfo1, 0, arrayOfPainterInfo1, 0, i);
    for (int m = 0; m < j; m++)
    {
      int n = 0;
      for (int i1 = 0; i1 < i - k; i1++) {
        if (paramArrayOfPainterInfo2[m].equalsPainter(paramArrayOfPainterInfo1[i1]))
        {
          arrayOfPainterInfo1[i1] = paramArrayOfPainterInfo2[m];
          k++;
          n = 1;
          break;
        }
      }
      if (n == 0) {
        arrayOfPainterInfo1[(i + m - k)] = paramArrayOfPainterInfo2[m];
      }
    }
    if (k > 0)
    {
      PainterInfo[] arrayOfPainterInfo2 = arrayOfPainterInfo1;
      arrayOfPainterInfo1 = new PainterInfo[arrayOfPainterInfo1.length - k];
      System.arraycopy(arrayOfPainterInfo2, 0, arrayOfPainterInfo1, 0, arrayOfPainterInfo1.length);
    }
    return arrayOfPainterInfo1;
  }
  
  public ParsedSynthStyle() {}
  
  public ParsedSynthStyle(DefaultSynthStyle paramDefaultSynthStyle)
  {
    super(paramDefaultSynthStyle);
    if ((paramDefaultSynthStyle instanceof ParsedSynthStyle))
    {
      ParsedSynthStyle localParsedSynthStyle = (ParsedSynthStyle)paramDefaultSynthStyle;
      if (_painters != null) {
        _painters = _painters;
      }
    }
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext)
  {
    return DELEGATING_PAINTER_INSTANCE;
  }
  
  public void setPainters(PainterInfo[] paramArrayOfPainterInfo)
  {
    _painters = paramArrayOfPainterInfo;
  }
  
  public DefaultSynthStyle addTo(DefaultSynthStyle paramDefaultSynthStyle)
  {
    if (!(paramDefaultSynthStyle instanceof ParsedSynthStyle)) {
      paramDefaultSynthStyle = new ParsedSynthStyle(paramDefaultSynthStyle);
    }
    ParsedSynthStyle localParsedSynthStyle = (ParsedSynthStyle)super.addTo(paramDefaultSynthStyle);
    _painters = mergePainterInfo(_painters, _painters);
    return localParsedSynthStyle;
  }
  
  private SynthPainter getBestPainter(SynthContext paramSynthContext, String paramString, int paramInt)
  {
    StateInfo localStateInfo = (StateInfo)getStateInfo(paramSynthContext.getComponentState());
    SynthPainter localSynthPainter;
    if ((localStateInfo != null) && ((localSynthPainter = getBestPainter(localStateInfo.getPainters(), paramString, paramInt)) != null)) {
      return localSynthPainter;
    }
    if ((localSynthPainter = getBestPainter(_painters, paramString, paramInt)) != null) {
      return localSynthPainter;
    }
    return SynthPainter.NULL_PAINTER;
  }
  
  private SynthPainter getBestPainter(PainterInfo[] paramArrayOfPainterInfo, String paramString, int paramInt)
  {
    if (paramArrayOfPainterInfo != null)
    {
      SynthPainter localSynthPainter1 = null;
      SynthPainter localSynthPainter2 = null;
      for (int i = paramArrayOfPainterInfo.length - 1; i >= 0; i--)
      {
        PainterInfo localPainterInfo = paramArrayOfPainterInfo[i];
        if (localPainterInfo.getMethod() == paramString)
        {
          if (localPainterInfo.getDirection() == paramInt) {
            return localPainterInfo.getPainter();
          }
          if ((localSynthPainter2 == null) && (localPainterInfo.getDirection() == -1)) {
            localSynthPainter2 = localPainterInfo.getPainter();
          }
        }
        else if ((localSynthPainter1 == null) && (localPainterInfo.getMethod() == null))
        {
          localSynthPainter1 = localPainterInfo.getPainter();
        }
      }
      if (localSynthPainter2 != null) {
        return localSynthPainter2;
      }
      return localSynthPainter1;
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(super.toString());
    if (_painters != null)
    {
      localStringBuffer.append(",painters=[");
      for (int i = 0; i < _painters.length; i++) {
        localStringBuffer.append(_painters[i].toString());
      }
      localStringBuffer.append("]");
    }
    return localStringBuffer.toString();
  }
  
  private static class AggregatePainter
    extends SynthPainter
  {
    private List<SynthPainter> painters = new LinkedList();
    
    AggregatePainter(SynthPainter paramSynthPainter)
    {
      painters.add(paramSynthPainter);
    }
    
    void addPainter(SynthPainter paramSynthPainter)
    {
      if (paramSynthPainter != null) {
        painters.add(paramSynthPainter);
      }
    }
    
    public void paintArrowButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintArrowButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintArrowButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintArrowButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintArrowButtonForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintArrowButtonForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintCheckBoxMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintCheckBoxMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintCheckBoxMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintCheckBoxMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintCheckBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintCheckBoxBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintCheckBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintCheckBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintColorChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintColorChooserBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintColorChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintColorChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintComboBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintComboBoxBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintComboBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintComboBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintDesktopIconBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintDesktopIconBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintDesktopIconBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintDesktopIconBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintDesktopPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintDesktopPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintDesktopPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintDesktopPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintEditorPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintEditorPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintEditorPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintEditorPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintFileChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintFileChooserBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintFileChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintFileChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintFormattedTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintFormattedTextFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintFormattedTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintFormattedTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintInternalFrameTitlePaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintInternalFrameTitlePaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintInternalFrameTitlePaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintInternalFrameTitlePaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintInternalFrameBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintInternalFrameBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintInternalFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintInternalFrameBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintLabelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintLabelBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintLabelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintLabelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintListBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintListBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintListBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintListBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintOptionPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintOptionPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintOptionPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintOptionPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPanelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPanelBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPanelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPanelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPasswordFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPasswordFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPasswordFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPasswordFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPopupMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPopupMenuBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintPopupMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintPopupMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintProgressBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintProgressBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintProgressBarForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintProgressBarForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintRadioButtonMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRadioButtonMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintRadioButtonMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRadioButtonMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintRadioButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRadioButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintRadioButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRadioButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintRootPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRootPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintRootPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintRootPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollBarThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarThumbBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollBarThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarThumbBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintScrollPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintScrollPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintScrollPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSeparatorBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSeparatorBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSeparatorForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSeparatorForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderThumbBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderThumbBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSliderTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSpinnerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSpinnerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSpinnerBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSpinnerBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneDividerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneDividerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSplitPaneDividerForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneDividerForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSplitPaneDragDivider(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneDragDivider(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintSplitPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintSplitPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintSplitPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
    }
    
    public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneTabBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
    }
    
    public void paintTabbedPaneContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTabbedPaneContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTabbedPaneContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTableHeaderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTableHeaderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTableHeaderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTableHeaderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTableBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTableBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTableBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTableBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToggleButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToggleButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToggleButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToggleButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarDragWindowBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarDragWindowBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarDragWindowBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolBarDragWindowBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
    }
    
    public void paintToolTipBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolTipBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintToolTipBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintToolTipBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTreeBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTreeBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTreeBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTreeBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTreeCellBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTreeCellBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTreeCellBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTreeCellBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintTreeCellFocus(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintTreeCellFocus(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintViewportBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintViewportBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void paintViewportBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Iterator localIterator = painters.iterator();
      while (localIterator.hasNext())
      {
        SynthPainter localSynthPainter = (SynthPainter)localIterator.next();
        localSynthPainter.paintViewportBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
  }
  
  private static class DelegatingPainter
    extends SynthPainter
  {
    private DelegatingPainter() {}
    
    private static SynthPainter getPainter(SynthContext paramSynthContext, String paramString, int paramInt)
    {
      return ((ParsedSynthStyle)paramSynthContext.getStyle()).getBestPainter(paramSynthContext, paramString, paramInt);
    }
    
    public void paintArrowButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "arrowbuttonbackground", -1).paintArrowButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintArrowButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "arrowbuttonborder", -1).paintArrowButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintArrowButtonForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "arrowbuttonforeground", paramInt5).paintArrowButtonForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "buttonbackground", -1).paintButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "buttonborder", -1).paintButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintCheckBoxMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "checkboxmenuitembackground", -1).paintCheckBoxMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintCheckBoxMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "checkboxmenuitemborder", -1).paintCheckBoxMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintCheckBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "checkboxbackground", -1).paintCheckBoxBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintCheckBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "checkboxborder", -1).paintCheckBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintColorChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "colorchooserbackground", -1).paintColorChooserBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintColorChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "colorchooserborder", -1).paintColorChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintComboBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "comboboxbackground", -1).paintComboBoxBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintComboBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "comboboxborder", -1).paintComboBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintDesktopIconBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "desktopiconbackground", -1).paintDesktopIconBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintDesktopIconBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "desktopiconborder", -1).paintDesktopIconBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintDesktopPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "desktoppanebackground", -1).paintDesktopPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintDesktopPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "desktoppaneborder", -1).paintDesktopPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintEditorPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "editorpanebackground", -1).paintEditorPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintEditorPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "editorpaneborder", -1).paintEditorPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintFileChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "filechooserbackground", -1).paintFileChooserBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintFileChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "filechooserborder", -1).paintFileChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintFormattedTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "formattedtextfieldbackground", -1).paintFormattedTextFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintFormattedTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "formattedtextfieldborder", -1).paintFormattedTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintInternalFrameTitlePaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "internalframetitlepanebackground", -1).paintInternalFrameTitlePaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintInternalFrameTitlePaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "internalframetitlepaneborder", -1).paintInternalFrameTitlePaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintInternalFrameBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "internalframebackground", -1).paintInternalFrameBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintInternalFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "internalframeborder", -1).paintInternalFrameBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintLabelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "labelbackground", -1).paintLabelBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintLabelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "labelborder", -1).paintLabelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintListBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "listbackground", -1).paintListBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintListBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "listborder", -1).paintListBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menubarbackground", -1).paintMenuBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menubarborder", -1).paintMenuBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menuitembackground", -1).paintMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menuitemborder", -1).paintMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menubackground", -1).paintMenuBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "menuborder", -1).paintMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintOptionPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "optionpanebackground", -1).paintOptionPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintOptionPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "optionpaneborder", -1).paintOptionPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPanelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "panelbackground", -1).paintPanelBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPanelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "panelborder", -1).paintPanelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPasswordFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "passwordfieldbackground", -1).paintPasswordFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPasswordFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "passwordfieldborder", -1).paintPasswordFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPopupMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "popupmenubackground", -1).paintPopupMenuBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintPopupMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "popupmenuborder", -1).paintPopupMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "progressbarbackground", -1).paintProgressBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "progressbarbackground", paramInt5).paintProgressBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "progressbarborder", -1).paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "progressbarborder", paramInt5).paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintProgressBarForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "progressbarforeground", paramInt5).paintProgressBarForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintRadioButtonMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "radiobuttonmenuitembackground", -1).paintRadioButtonMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintRadioButtonMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "radiobuttonmenuitemborder", -1).paintRadioButtonMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintRadioButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "radiobuttonbackground", -1).paintRadioButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintRadioButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "radiobuttonborder", -1).paintRadioButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintRootPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "rootpanebackground", -1).paintRootPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintRootPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "rootpaneborder", -1).paintRootPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollbarbackground", -1).paintScrollBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbarbackground", paramInt5).paintScrollBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollbarborder", -1).paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbarborder", paramInt5).paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollBarThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbarthumbbackground", paramInt5).paintScrollBarThumbBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollBarThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbarthumbborder", paramInt5).paintScrollBarThumbBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollbartrackbackground", -1).paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbartrackbackground", paramInt5).paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollbartrackborder", -1).paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "scrollbartrackborder", paramInt5).paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintScrollPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollpanebackground", -1).paintScrollPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintScrollPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "scrollpaneborder", -1).paintScrollPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "separatorbackground", -1).paintSeparatorBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "separatorbackground", paramInt5).paintSeparatorBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "separatorborder", -1).paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "separatorborder", paramInt5).paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSeparatorForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "separatorforeground", paramInt5).paintSeparatorForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "sliderbackground", -1).paintSliderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "sliderbackground", paramInt5).paintSliderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "sliderborder", -1).paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "sliderborder", paramInt5).paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "sliderthumbbackground", paramInt5).paintSliderThumbBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "sliderthumbborder", paramInt5).paintSliderThumbBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "slidertrackbackground", -1).paintSliderTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "slidertrackbackground", paramInt5).paintSliderTrackBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "slidertrackborder", -1).paintSliderTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "slidertrackborder", paramInt5).paintSliderTrackBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSpinnerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "spinnerbackground", -1).paintSpinnerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSpinnerBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "spinnerborder", -1).paintSpinnerBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "splitpanedividerbackground", -1).paintSplitPaneDividerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "splitpanedividerbackground", paramInt5).paintSplitPaneDividerBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSplitPaneDividerForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "splitpanedividerforeground", paramInt5).paintSplitPaneDividerForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSplitPaneDragDivider(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "splitpanedragdivider", paramInt5).paintSplitPaneDragDivider(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintSplitPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "splitpanebackground", -1).paintSplitPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintSplitPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "splitpaneborder", -1).paintSplitPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpanebackground", -1).paintTabbedPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpaneborder", -1).paintTabbedPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpanetabareabackground", -1).paintTabbedPaneTabAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "tabbedpanetabareabackground", paramInt5).paintTabbedPaneTabAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpanetabareaborder", -1).paintTabbedPaneTabAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "tabbedpanetabareaborder", paramInt5).paintTabbedPaneTabAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "tabbedpanetabbackground", -1).paintTabbedPaneTabBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      getPainter(paramSynthContext, "tabbedpanetabbackground", paramInt6).paintTabbedPaneTabBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    
    public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "tabbedpanetabborder", -1).paintTabbedPaneTabBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      getPainter(paramSynthContext, "tabbedpanetabborder", paramInt6).paintTabbedPaneTabBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    
    public void paintTabbedPaneContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpanecontentbackground", -1).paintTabbedPaneContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTabbedPaneContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tabbedpanecontentborder", -1).paintTabbedPaneContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTableHeaderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tableheaderbackground", -1).paintTableHeaderBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTableHeaderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tableheaderborder", -1).paintTableHeaderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTableBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tablebackground", -1).paintTableBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTableBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tableborder", -1).paintTableBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textareabackground", -1).paintTextAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textareaborder", -1).paintTextAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textpanebackground", -1).paintTextPaneBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textpaneborder", -1).paintTextPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textfieldbackground", -1).paintTextFieldBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "textfieldborder", -1).paintTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToggleButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "togglebuttonbackground", -1).paintToggleButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToggleButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "togglebuttonborder", -1).paintToggleButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbarbackground", -1).paintToolBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbarbackground", paramInt5).paintToolBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbarborder", -1).paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbarborder", paramInt5).paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbarcontentbackground", -1).paintToolBarContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbarcontentbackground", paramInt5).paintToolBarContentBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbarcontentborder", -1).paintToolBarContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbarcontentborder", paramInt5).paintToolBarContentBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbardragwindowbackground", -1).paintToolBarDragWindowBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbardragwindowbackground", paramInt5).paintToolBarDragWindowBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "toolbardragwindowborder", -1).paintToolBarDragWindowBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      getPainter(paramSynthContext, "toolbardragwindowborder", paramInt5).paintToolBarDragWindowBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void paintToolTipBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tooltipbackground", -1).paintToolTipBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintToolTipBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "tooltipborder", -1).paintToolTipBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTreeBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "treebackground", -1).paintTreeBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTreeBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "treeborder", -1).paintTreeBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTreeCellBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "treecellbackground", -1).paintTreeCellBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTreeCellBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "treecellborder", -1).paintTreeCellBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintTreeCellFocus(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "treecellfocus", -1).paintTreeCellFocus(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintViewportBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "viewportbackground", -1).paintViewportBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void paintViewportBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      getPainter(paramSynthContext, "viewportborder", -1).paintViewportBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  static class PainterInfo
  {
    private String _method;
    private SynthPainter _painter;
    private int _direction;
    
    PainterInfo(String paramString, SynthPainter paramSynthPainter, int paramInt)
    {
      if (paramString != null) {
        _method = paramString.intern();
      }
      _painter = paramSynthPainter;
      _direction = paramInt;
    }
    
    void addPainter(SynthPainter paramSynthPainter)
    {
      if (!(_painter instanceof ParsedSynthStyle.AggregatePainter)) {
        _painter = new ParsedSynthStyle.AggregatePainter(_painter);
      }
      ((ParsedSynthStyle.AggregatePainter)_painter).addPainter(paramSynthPainter);
    }
    
    String getMethod()
    {
      return _method;
    }
    
    SynthPainter getPainter()
    {
      return _painter;
    }
    
    int getDirection()
    {
      return _direction;
    }
    
    boolean equalsPainter(PainterInfo paramPainterInfo)
    {
      return (_method == _method) && (_direction == _direction);
    }
    
    public String toString()
    {
      return "PainterInfo {method=" + _method + ",direction=" + _direction + ",painter=" + _painter + "}";
    }
  }
  
  static class StateInfo
    extends DefaultSynthStyle.StateInfo
  {
    private ParsedSynthStyle.PainterInfo[] _painterInfo;
    
    public StateInfo() {}
    
    public StateInfo(DefaultSynthStyle.StateInfo paramStateInfo)
    {
      super();
      if ((paramStateInfo instanceof StateInfo)) {
        _painterInfo = _painterInfo;
      }
    }
    
    public void setPainters(ParsedSynthStyle.PainterInfo[] paramArrayOfPainterInfo)
    {
      _painterInfo = paramArrayOfPainterInfo;
    }
    
    public ParsedSynthStyle.PainterInfo[] getPainters()
    {
      return _painterInfo;
    }
    
    public Object clone()
    {
      return new StateInfo(this);
    }
    
    public DefaultSynthStyle.StateInfo addTo(DefaultSynthStyle.StateInfo paramStateInfo)
    {
      if (!(paramStateInfo instanceof StateInfo))
      {
        paramStateInfo = new StateInfo(paramStateInfo);
      }
      else
      {
        paramStateInfo = super.addTo(paramStateInfo);
        StateInfo localStateInfo = (StateInfo)paramStateInfo;
        _painterInfo = ParsedSynthStyle.mergePainterInfo(_painterInfo, _painterInfo);
      }
      return paramStateInfo;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer(super.toString());
      localStringBuffer.append(",painters=[");
      if (_painterInfo != null) {
        for (int i = 0; i < _painterInfo.length; i++) {
          localStringBuffer.append("    ").append(_painterInfo[i].toString());
        }
      }
      localStringBuffer.append("]");
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\ParsedSynthStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */