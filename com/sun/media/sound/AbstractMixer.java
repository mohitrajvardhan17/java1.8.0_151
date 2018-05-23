package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

abstract class AbstractMixer
  extends AbstractLine
  implements Mixer
{
  protected static final int PCM = 0;
  protected static final int ULAW = 1;
  protected static final int ALAW = 2;
  private final Mixer.Info mixerInfo;
  protected Line.Info[] sourceLineInfo;
  protected Line.Info[] targetLineInfo;
  private boolean started = false;
  private boolean manuallyOpened = false;
  private final Vector sourceLines = new Vector();
  private final Vector targetLines = new Vector();
  
  protected AbstractMixer(Mixer.Info paramInfo, Control[] paramArrayOfControl, Line.Info[] paramArrayOfInfo1, Line.Info[] paramArrayOfInfo2)
  {
    super(new Line.Info(Mixer.class), null, paramArrayOfControl);
    mixer = this;
    if (paramArrayOfControl == null) {
      paramArrayOfControl = new Control[0];
    }
    mixerInfo = paramInfo;
    sourceLineInfo = paramArrayOfInfo1;
    targetLineInfo = paramArrayOfInfo2;
  }
  
  public final Mixer.Info getMixerInfo()
  {
    return mixerInfo;
  }
  
  public final Line.Info[] getSourceLineInfo()
  {
    Line.Info[] arrayOfInfo = new Line.Info[sourceLineInfo.length];
    System.arraycopy(sourceLineInfo, 0, arrayOfInfo, 0, sourceLineInfo.length);
    return arrayOfInfo;
  }
  
  public final Line.Info[] getTargetLineInfo()
  {
    Line.Info[] arrayOfInfo = new Line.Info[targetLineInfo.length];
    System.arraycopy(targetLineInfo, 0, arrayOfInfo, 0, targetLineInfo.length);
    return arrayOfInfo;
  }
  
  public final Line.Info[] getSourceLineInfo(Line.Info paramInfo)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < sourceLineInfo.length; i++) {
      if (paramInfo.matches(sourceLineInfo[i])) {
        localVector.addElement(sourceLineInfo[i]);
      }
    }
    Line.Info[] arrayOfInfo = new Line.Info[localVector.size()];
    for (i = 0; i < arrayOfInfo.length; i++) {
      arrayOfInfo[i] = ((Line.Info)localVector.elementAt(i));
    }
    return arrayOfInfo;
  }
  
  public final Line.Info[] getTargetLineInfo(Line.Info paramInfo)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < targetLineInfo.length; i++) {
      if (paramInfo.matches(targetLineInfo[i])) {
        localVector.addElement(targetLineInfo[i]);
      }
    }
    Line.Info[] arrayOfInfo = new Line.Info[localVector.size()];
    for (i = 0; i < arrayOfInfo.length; i++) {
      arrayOfInfo[i] = ((Line.Info)localVector.elementAt(i));
    }
    return arrayOfInfo;
  }
  
  public final boolean isLineSupported(Line.Info paramInfo)
  {
    for (int i = 0; i < sourceLineInfo.length; i++) {
      if (paramInfo.matches(sourceLineInfo[i])) {
        return true;
      }
    }
    for (i = 0; i < targetLineInfo.length; i++) {
      if (paramInfo.matches(targetLineInfo[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract Line getLine(Line.Info paramInfo)
    throws LineUnavailableException;
  
  public abstract int getMaxLines(Line.Info paramInfo);
  
  protected abstract void implOpen()
    throws LineUnavailableException;
  
  protected abstract void implStart();
  
  protected abstract void implStop();
  
  protected abstract void implClose();
  
  public final Line[] getSourceLines()
  {
    Line[] arrayOfLine;
    synchronized (sourceLines)
    {
      arrayOfLine = new Line[sourceLines.size()];
      for (int i = 0; i < arrayOfLine.length; i++) {
        arrayOfLine[i] = ((Line)sourceLines.elementAt(i));
      }
    }
    return arrayOfLine;
  }
  
  public final Line[] getTargetLines()
  {
    Line[] arrayOfLine;
    synchronized (targetLines)
    {
      arrayOfLine = new Line[targetLines.size()];
      for (int i = 0; i < arrayOfLine.length; i++) {
        arrayOfLine[i] = ((Line)targetLines.elementAt(i));
      }
    }
    return arrayOfLine;
  }
  
  public final void synchronize(Line[] paramArrayOfLine, boolean paramBoolean)
  {
    throw new IllegalArgumentException("Synchronization not supported by this mixer.");
  }
  
  public final void unsynchronize(Line[] paramArrayOfLine)
  {
    throw new IllegalArgumentException("Synchronization not supported by this mixer.");
  }
  
  public final boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean)
  {
    return false;
  }
  
  public final synchronized void open()
    throws LineUnavailableException
  {
    open(true);
  }
  
  final synchronized void open(boolean paramBoolean)
    throws LineUnavailableException
  {
    if (!isOpen())
    {
      implOpen();
      setOpen(true);
      if (paramBoolean) {
        manuallyOpened = true;
      }
    }
  }
  
  final synchronized void open(Line paramLine)
    throws LineUnavailableException
  {
    if (equals(paramLine)) {
      return;
    }
    if (isSourceLine(paramLine.getLineInfo()))
    {
      if (!sourceLines.contains(paramLine))
      {
        open(false);
        sourceLines.addElement(paramLine);
      }
    }
    else if ((isTargetLine(paramLine.getLineInfo())) && (!targetLines.contains(paramLine)))
    {
      open(false);
      targetLines.addElement(paramLine);
    }
  }
  
  final synchronized void close(Line paramLine)
  {
    if (equals(paramLine)) {
      return;
    }
    sourceLines.removeElement(paramLine);
    targetLines.removeElement(paramLine);
    if ((sourceLines.isEmpty()) && (targetLines.isEmpty()) && (!manuallyOpened)) {
      close();
    }
  }
  
  public final synchronized void close()
  {
    if (isOpen())
    {
      Line[] arrayOfLine = getSourceLines();
      for (int i = 0; i < arrayOfLine.length; i++) {
        arrayOfLine[i].close();
      }
      arrayOfLine = getTargetLines();
      for (i = 0; i < arrayOfLine.length; i++) {
        arrayOfLine[i].close();
      }
      implClose();
      setOpen(false);
    }
    manuallyOpened = false;
  }
  
  final synchronized void start(Line paramLine)
  {
    if (equals(paramLine)) {
      return;
    }
    if (!started)
    {
      implStart();
      started = true;
    }
  }
  
  final synchronized void stop(Line paramLine)
  {
    if (equals(paramLine)) {
      return;
    }
    Vector localVector1 = (Vector)sourceLines.clone();
    for (int i = 0; i < localVector1.size(); i++) {
      if ((localVector1.elementAt(i) instanceof AbstractDataLine))
      {
        AbstractDataLine localAbstractDataLine1 = (AbstractDataLine)localVector1.elementAt(i);
        if ((localAbstractDataLine1.isStartedRunning()) && (!localAbstractDataLine1.equals(paramLine))) {
          return;
        }
      }
    }
    Vector localVector2 = (Vector)targetLines.clone();
    for (int j = 0; j < localVector2.size(); j++) {
      if ((localVector2.elementAt(j) instanceof AbstractDataLine))
      {
        AbstractDataLine localAbstractDataLine2 = (AbstractDataLine)localVector2.elementAt(j);
        if ((localAbstractDataLine2.isStartedRunning()) && (!localAbstractDataLine2.equals(paramLine))) {
          return;
        }
      }
    }
    started = false;
    implStop();
  }
  
  final boolean isSourceLine(Line.Info paramInfo)
  {
    for (int i = 0; i < sourceLineInfo.length; i++) {
      if (paramInfo.matches(sourceLineInfo[i])) {
        return true;
      }
    }
    return false;
  }
  
  final boolean isTargetLine(Line.Info paramInfo)
  {
    for (int i = 0; i < targetLineInfo.length; i++) {
      if (paramInfo.matches(targetLineInfo[i])) {
        return true;
      }
    }
    return false;
  }
  
  final Line.Info getLineInfo(Line.Info paramInfo)
  {
    if (paramInfo == null) {
      return null;
    }
    for (int i = 0; i < sourceLineInfo.length; i++) {
      if (paramInfo.matches(sourceLineInfo[i])) {
        return sourceLineInfo[i];
      }
    }
    for (i = 0; i < targetLineInfo.length; i++) {
      if (paramInfo.matches(targetLineInfo[i])) {
        return targetLineInfo[i];
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AbstractMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */