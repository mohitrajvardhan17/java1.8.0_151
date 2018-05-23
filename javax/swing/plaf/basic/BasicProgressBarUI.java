package javax.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicProgressBarUI
  extends ProgressBarUI
{
  private int cachedPercent;
  private int cellLength;
  private int cellSpacing;
  private Color selectionForeground;
  private Color selectionBackground;
  private Animator animator;
  protected JProgressBar progressBar;
  protected ChangeListener changeListener;
  private Handler handler;
  private int animationIndex = 0;
  private int numFrames;
  private int repaintInterval;
  private int cycleTime;
  private static boolean ADJUSTTIMER = true;
  protected Rectangle boxRect;
  private Rectangle nextPaintRect;
  private Rectangle componentInnards;
  private Rectangle oldComponentInnards;
  private double delta = 0.0D;
  private int maxPosition = 0;
  
  public BasicProgressBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicProgressBarUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    progressBar = ((JProgressBar)paramJComponent);
    installDefaults();
    installListeners();
    if (progressBar.isIndeterminate()) {
      initIndeterminateValues();
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if (progressBar.isIndeterminate()) {
      cleanUpIndeterminateValues();
    }
    uninstallDefaults();
    uninstallListeners();
    progressBar = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installProperty(progressBar, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(progressBar, "ProgressBar.border");
    LookAndFeel.installColorsAndFont(progressBar, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
    cellLength = UIManager.getInt("ProgressBar.cellLength");
    if (cellLength == 0) {
      cellLength = 1;
    }
    cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
    selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
    selectionBackground = UIManager.getColor("ProgressBar.selectionBackground");
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(progressBar);
  }
  
  protected void installListeners()
  {
    changeListener = getHandler();
    progressBar.addChangeListener(changeListener);
    progressBar.addPropertyChangeListener(getHandler());
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected void startAnimationTimer()
  {
    if (animator == null) {
      animator = new Animator(null);
    }
    animator.start(getRepaintInterval());
  }
  
  protected void stopAnimationTimer()
  {
    if (animator != null) {
      animator.stop();
    }
  }
  
  protected void uninstallListeners()
  {
    progressBar.removeChangeListener(changeListener);
    progressBar.removePropertyChangeListener(getHandler());
    handler = null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if ((progressBar.isStringPainted()) && (progressBar.getOrientation() == 0))
    {
      FontMetrics localFontMetrics = progressBar.getFontMetrics(progressBar.getFont());
      Insets localInsets = progressBar.getInsets();
      int i = top;
      paramInt2 = paramInt2 - top - bottom;
      return i + (paramInt2 + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    if ((progressBar.isStringPainted()) && (progressBar.getOrientation() == 0)) {
      return Component.BaselineResizeBehavior.CENTER_OFFSET;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected Dimension getPreferredInnerHorizontal()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(progressBar, this, "ProgressBar.horizontalSize");
    if (localDimension == null) {
      localDimension = new Dimension(146, 12);
    }
    return localDimension;
  }
  
  protected Dimension getPreferredInnerVertical()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(progressBar, this, "ProgressBar.verticalSize");
    if (localDimension == null) {
      localDimension = new Dimension(12, 146);
    }
    return localDimension;
  }
  
  protected Color getSelectionForeground()
  {
    return selectionForeground;
  }
  
  protected Color getSelectionBackground()
  {
    return selectionBackground;
  }
  
  private int getCachedPercent()
  {
    return cachedPercent;
  }
  
  private void setCachedPercent(int paramInt)
  {
    cachedPercent = paramInt;
  }
  
  protected int getCellLength()
  {
    if (progressBar.isStringPainted()) {
      return 1;
    }
    return cellLength;
  }
  
  protected void setCellLength(int paramInt)
  {
    cellLength = paramInt;
  }
  
  protected int getCellSpacing()
  {
    if (progressBar.isStringPainted()) {
      return 0;
    }
    return cellSpacing;
  }
  
  protected void setCellSpacing(int paramInt)
  {
    cellSpacing = paramInt;
  }
  
  protected int getAmountFull(Insets paramInsets, int paramInt1, int paramInt2)
  {
    int i = 0;
    BoundedRangeModel localBoundedRangeModel = progressBar.getModel();
    if (localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getMinimum() != 0) {
      if (progressBar.getOrientation() == 0) {
        i = (int)Math.round(paramInt1 * progressBar.getPercentComplete());
      } else {
        i = (int)Math.round(paramInt2 * progressBar.getPercentComplete());
      }
    }
    return i;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (progressBar.isIndeterminate()) {
      paintIndeterminate(paramGraphics, paramJComponent);
    } else {
      paintDeterminate(paramGraphics, paramJComponent);
    }
  }
  
  protected Rectangle getBox(Rectangle paramRectangle)
  {
    int i = getAnimationIndex();
    int j = numFrames / 2;
    if ((sizeChanged()) || (delta == 0.0D) || (maxPosition == 0.0D)) {
      updateSizes();
    }
    paramRectangle = getGenericBox(paramRectangle);
    if (paramRectangle == null) {
      return null;
    }
    if (j <= 0) {
      return null;
    }
    if (progressBar.getOrientation() == 0)
    {
      if (i < j) {
        x = (componentInnards.x + (int)Math.round(delta * i));
      } else {
        x = (maxPosition - (int)Math.round(delta * (i - j)));
      }
    }
    else if (i < j) {
      y = (componentInnards.y + (int)Math.round(delta * i));
    } else {
      y = (maxPosition - (int)Math.round(delta * (i - j)));
    }
    return paramRectangle;
  }
  
  private void updateSizes()
  {
    int i = 0;
    if (progressBar.getOrientation() == 0)
    {
      i = getBoxLength(componentInnards.width, componentInnards.height);
      maxPosition = (componentInnards.x + componentInnards.width - i);
    }
    else
    {
      i = getBoxLength(componentInnards.height, componentInnards.width);
      maxPosition = (componentInnards.y + componentInnards.height - i);
    }
    delta = (2.0D * maxPosition / numFrames);
  }
  
  private Rectangle getGenericBox(Rectangle paramRectangle)
  {
    if (paramRectangle == null) {
      paramRectangle = new Rectangle();
    }
    if (progressBar.getOrientation() == 0)
    {
      width = getBoxLength(componentInnards.width, componentInnards.height);
      if (width < 0)
      {
        paramRectangle = null;
      }
      else
      {
        height = componentInnards.height;
        y = componentInnards.y;
      }
    }
    else
    {
      height = getBoxLength(componentInnards.height, componentInnards.width);
      if (height < 0)
      {
        paramRectangle = null;
      }
      else
      {
        width = componentInnards.width;
        x = componentInnards.x;
      }
    }
    return paramRectangle;
  }
  
  protected int getBoxLength(int paramInt1, int paramInt2)
  {
    return (int)Math.round(paramInt1 / 6.0D);
  }
  
  protected void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    Insets localInsets = progressBar.getInsets();
    int i = progressBar.getWidth() - (right + left);
    int j = progressBar.getHeight() - (top + bottom);
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    boxRect = getBox(boxRect);
    if (boxRect != null)
    {
      localGraphics2D.setColor(progressBar.getForeground());
      localGraphics2D.fillRect(boxRect.x, boxRect.y, boxRect.width, boxRect.height);
    }
    if (progressBar.isStringPainted()) {
      if (progressBar.getOrientation() == 0) {
        paintString(localGraphics2D, left, top, i, j, boxRect.x, boxRect.width, localInsets);
      } else {
        paintString(localGraphics2D, left, top, i, j, boxRect.y, boxRect.height, localInsets);
      }
    }
  }
  
  protected void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    Insets localInsets = progressBar.getInsets();
    int i = progressBar.getWidth() - (right + left);
    int j = progressBar.getHeight() - (top + bottom);
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    int k = getCellLength();
    int m = getCellSpacing();
    int n = getAmountFull(localInsets, i, j);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    localGraphics2D.setColor(progressBar.getForeground());
    if (progressBar.getOrientation() == 0)
    {
      if ((m == 0) && (n > 0)) {
        localGraphics2D.setStroke(new BasicStroke(j, 0, 2));
      } else {
        localGraphics2D.setStroke(new BasicStroke(j, 0, 2, 0.0F, new float[] { k, m }, 0.0F));
      }
      if (BasicGraphicsUtils.isLeftToRight(paramJComponent)) {
        localGraphics2D.drawLine(left, j / 2 + top, n + left, j / 2 + top);
      } else {
        localGraphics2D.drawLine(i + left, j / 2 + top, i + left - n, j / 2 + top);
      }
    }
    else
    {
      if ((m == 0) && (n > 0)) {
        localGraphics2D.setStroke(new BasicStroke(i, 0, 2));
      } else {
        localGraphics2D.setStroke(new BasicStroke(i, 0, 2, 0.0F, new float[] { k, m }, 0.0F));
      }
      localGraphics2D.drawLine(i / 2 + left, top + j, i / 2 + left, top + j - n);
    }
    if (progressBar.isStringPainted()) {
      paintString(paramGraphics, left, top, i, j, n, localInsets);
    }
  }
  
  protected void paintString(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Insets paramInsets)
  {
    if (progressBar.getOrientation() == 0)
    {
      if (BasicGraphicsUtils.isLeftToRight(progressBar))
      {
        if (progressBar.isIndeterminate())
        {
          boxRect = getBox(boxRect);
          paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, boxRect.x, boxRect.width, paramInsets);
        }
        else
        {
          paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt5, paramInsets);
        }
      }
      else {
        paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3 - paramInt5, paramInt5, paramInsets);
      }
    }
    else if (progressBar.isIndeterminate())
    {
      boxRect = getBox(boxRect);
      paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, boxRect.y, boxRect.height, paramInsets);
    }
    else
    {
      paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt2 + paramInt4 - paramInt5, paramInt5, paramInsets);
    }
  }
  
  private void paintString(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Insets paramInsets)
  {
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    String str = progressBar.getString();
    localGraphics2D.setFont(progressBar.getFont());
    Point localPoint = getStringPlacement(localGraphics2D, str, paramInt1, paramInt2, paramInt3, paramInt4);
    Rectangle localRectangle = localGraphics2D.getClipBounds();
    if (progressBar.getOrientation() == 0)
    {
      localGraphics2D.setColor(getSelectionBackground());
      SwingUtilities2.drawString(progressBar, localGraphics2D, str, x, y);
      localGraphics2D.setColor(getSelectionForeground());
      localGraphics2D.clipRect(paramInt5, paramInt2, paramInt6, paramInt4);
      SwingUtilities2.drawString(progressBar, localGraphics2D, str, x, y);
    }
    else
    {
      localGraphics2D.setColor(getSelectionBackground());
      AffineTransform localAffineTransform = AffineTransform.getRotateInstance(1.5707963267948966D);
      localGraphics2D.setFont(progressBar.getFont().deriveFont(localAffineTransform));
      localPoint = getStringPlacement(localGraphics2D, str, paramInt1, paramInt2, paramInt3, paramInt4);
      SwingUtilities2.drawString(progressBar, localGraphics2D, str, x, y);
      localGraphics2D.setColor(getSelectionForeground());
      localGraphics2D.clipRect(paramInt1, paramInt5, paramInt3, paramInt6);
      SwingUtilities2.drawString(progressBar, localGraphics2D, str, x, y);
    }
    localGraphics2D.setClip(localRectangle);
  }
  
  protected Point getStringPlacement(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(progressBar, paramGraphics, progressBar.getFont());
    int i = SwingUtilities2.stringWidth(progressBar, localFontMetrics, paramString);
    if (progressBar.getOrientation() == 0) {
      return new Point(paramInt1 + Math.round(paramInt3 / 2 - i / 2), paramInt2 + (paramInt4 + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2);
    }
    return new Point(paramInt1 + (paramInt3 - localFontMetrics.getAscent() + localFontMetrics.getLeading() + localFontMetrics.getDescent()) / 2, paramInt2 + Math.round(paramInt4 / 2 - i / 2));
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Insets localInsets = progressBar.getInsets();
    FontMetrics localFontMetrics = progressBar.getFontMetrics(progressBar.getFont());
    Dimension localDimension;
    String str;
    int i;
    int j;
    if (progressBar.getOrientation() == 0)
    {
      localDimension = new Dimension(getPreferredInnerHorizontal());
      if (progressBar.isStringPainted())
      {
        str = progressBar.getString();
        i = SwingUtilities2.stringWidth(progressBar, localFontMetrics, str);
        if (i > width) {
          width = i;
        }
        j = localFontMetrics.getHeight() + localFontMetrics.getDescent();
        if (j > height) {
          height = j;
        }
      }
    }
    else
    {
      localDimension = new Dimension(getPreferredInnerVertical());
      if (progressBar.isStringPainted())
      {
        str = progressBar.getString();
        i = localFontMetrics.getHeight() + localFontMetrics.getDescent();
        if (i > width) {
          width = i;
        }
        j = SwingUtilities2.stringWidth(progressBar, localFontMetrics, str);
        if (j > height) {
          height = j;
        }
      }
    }
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(progressBar);
    if (progressBar.getOrientation() == 0) {
      width = 10;
    } else {
      height = 10;
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(progressBar);
    if (progressBar.getOrientation() == 0) {
      width = 32767;
    } else {
      height = 32767;
    }
    return localDimension;
  }
  
  protected int getAnimationIndex()
  {
    return animationIndex;
  }
  
  protected final int getFrameCount()
  {
    return numFrames;
  }
  
  protected void setAnimationIndex(int paramInt)
  {
    if (animationIndex != paramInt)
    {
      if (sizeChanged())
      {
        animationIndex = paramInt;
        maxPosition = 0;
        delta = 0.0D;
        progressBar.repaint();
        return;
      }
      nextPaintRect = getBox(nextPaintRect);
      animationIndex = paramInt;
      if (nextPaintRect != null)
      {
        boxRect = getBox(boxRect);
        if (boxRect != null) {
          nextPaintRect.add(boxRect);
        }
      }
    }
    else
    {
      return;
    }
    if (nextPaintRect != null) {
      progressBar.repaint(nextPaintRect);
    } else {
      progressBar.repaint();
    }
  }
  
  private boolean sizeChanged()
  {
    if ((oldComponentInnards == null) || (componentInnards == null)) {
      return true;
    }
    oldComponentInnards.setRect(componentInnards);
    componentInnards = SwingUtilities.calculateInnerArea(progressBar, componentInnards);
    return !oldComponentInnards.equals(componentInnards);
  }
  
  protected void incrementAnimationIndex()
  {
    int i = getAnimationIndex() + 1;
    if (i < numFrames) {
      setAnimationIndex(i);
    } else {
      setAnimationIndex(0);
    }
  }
  
  private int getRepaintInterval()
  {
    return repaintInterval;
  }
  
  private int initRepaintInterval()
  {
    repaintInterval = DefaultLookup.getInt(progressBar, this, "ProgressBar.repaintInterval", 50);
    return repaintInterval;
  }
  
  private int getCycleTime()
  {
    return cycleTime;
  }
  
  private int initCycleTime()
  {
    cycleTime = DefaultLookup.getInt(progressBar, this, "ProgressBar.cycleTime", 3000);
    return cycleTime;
  }
  
  private void initIndeterminateDefaults()
  {
    initRepaintInterval();
    initCycleTime();
    if (repaintInterval <= 0) {
      repaintInterval = 100;
    }
    if (repaintInterval > cycleTime)
    {
      cycleTime = (repaintInterval * 20);
    }
    else
    {
      int i = (int)Math.ceil(cycleTime / (repaintInterval * 2.0D));
      cycleTime = (repaintInterval * i * 2);
    }
  }
  
  private void initIndeterminateValues()
  {
    initIndeterminateDefaults();
    numFrames = (cycleTime / repaintInterval);
    initAnimationIndex();
    boxRect = new Rectangle();
    nextPaintRect = new Rectangle();
    componentInnards = new Rectangle();
    oldComponentInnards = new Rectangle();
    progressBar.addHierarchyListener(getHandler());
    if (progressBar.isDisplayable()) {
      startAnimationTimer();
    }
  }
  
  private void cleanUpIndeterminateValues()
  {
    if (progressBar.isDisplayable()) {
      stopAnimationTimer();
    }
    cycleTime = (repaintInterval = 0);
    numFrames = (animationIndex = 0);
    maxPosition = 0;
    delta = 0.0D;
    boxRect = (nextPaintRect = null);
    componentInnards = (oldComponentInnards = null);
    progressBar.removeHierarchyListener(getHandler());
  }
  
  private void initAnimationIndex()
  {
    if ((progressBar.getOrientation() == 0) && (BasicGraphicsUtils.isLeftToRight(progressBar))) {
      setAnimationIndex(0);
    } else {
      setAnimationIndex(numFrames / 2);
    }
  }
  
  private class Animator
    implements ActionListener
  {
    private Timer timer;
    private long previousDelay;
    private int interval;
    private long lastCall;
    private int MINIMUM_DELAY = 5;
    
    private Animator() {}
    
    private void start(int paramInt)
    {
      previousDelay = paramInt;
      lastCall = 0L;
      if (timer == null) {
        timer = new Timer(paramInt, this);
      } else {
        timer.setDelay(paramInt);
      }
      if (BasicProgressBarUI.ADJUSTTIMER)
      {
        timer.setRepeats(false);
        timer.setCoalesce(false);
      }
      timer.start();
    }
    
    private void stop()
    {
      timer.stop();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicProgressBarUI.ADJUSTTIMER)
      {
        long l = System.currentTimeMillis();
        if (lastCall > 0L)
        {
          int i = (int)(previousDelay - l + lastCall + BasicProgressBarUI.this.getRepaintInterval());
          if (i < MINIMUM_DELAY) {
            i = MINIMUM_DELAY;
          }
          timer.setInitialDelay(i);
          previousDelay = i;
        }
        timer.start();
        lastCall = l;
      }
      incrementAnimationIndex();
    }
  }
  
  public class ChangeHandler
    implements ChangeListener
  {
    public ChangeHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicProgressBarUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  private class Handler
    implements ChangeListener, PropertyChangeListener, HierarchyListener
  {
    private Handler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BoundedRangeModel localBoundedRangeModel = progressBar.getModel();
      int i = localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getMinimum();
      int k = BasicProgressBarUI.this.getCachedPercent();
      int j;
      if (i > 0) {
        j = (int)(100L * localBoundedRangeModel.getValue() / i);
      } else {
        j = 0;
      }
      if (j != k)
      {
        BasicProgressBarUI.this.setCachedPercent(j);
        progressBar.repaint();
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ("indeterminate" == str)
      {
        if (progressBar.isIndeterminate()) {
          BasicProgressBarUI.this.initIndeterminateValues();
        } else {
          BasicProgressBarUI.this.cleanUpIndeterminateValues();
        }
        progressBar.repaint();
      }
    }
    
    public void hierarchyChanged(HierarchyEvent paramHierarchyEvent)
    {
      if (((paramHierarchyEvent.getChangeFlags() & 0x2) != 0L) && (progressBar.isIndeterminate())) {
        if (progressBar.isDisplayable()) {
          startAnimationTimer();
        } else {
          stopAnimationTimer();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */