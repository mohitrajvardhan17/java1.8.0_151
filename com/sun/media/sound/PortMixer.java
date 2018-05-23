package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.BooleanControl.Type;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.CompoundControl.Type;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;

final class PortMixer
  extends AbstractMixer
{
  private static final int SRC_UNKNOWN = 1;
  private static final int SRC_MICROPHONE = 2;
  private static final int SRC_LINE_IN = 3;
  private static final int SRC_COMPACT_DISC = 4;
  private static final int SRC_MASK = 255;
  private static final int DST_UNKNOWN = 256;
  private static final int DST_SPEAKER = 512;
  private static final int DST_HEADPHONE = 768;
  private static final int DST_LINE_OUT = 1024;
  private static final int DST_MASK = 65280;
  private Port.Info[] portInfos;
  private PortMixerPort[] ports;
  private long id = 0L;
  
  PortMixer(PortMixerProvider.PortMixerInfo paramPortMixerInfo)
  {
    super(paramPortMixerInfo, null, null, null);
    int i = 0;
    int j = 0;
    int k = 0;
    try
    {
      try
      {
        id = nOpen(getMixerIndex());
        if (id != 0L)
        {
          i = nGetPortCount(id);
          if (i < 0) {
            i = 0;
          }
        }
      }
      catch (Exception localException) {}
      portInfos = new Port.Info[i];
      for (m = 0; m < i; m++)
      {
        int n = nGetPortType(id, m);
        j += ((n & 0xFF) != 0 ? 1 : 0);
        k += ((n & 0xFF00) != 0 ? 1 : 0);
        portInfos[m] = getPortInfo(m, n);
      }
    }
    finally
    {
      if (id != 0L) {
        nClose(id);
      }
      id = 0L;
    }
    sourceLineInfo = new Port.Info[j];
    targetLineInfo = new Port.Info[k];
    j = 0;
    k = 0;
    for (int m = 0; m < i; m++) {
      if (portInfos[m].isSource()) {
        sourceLineInfo[(j++)] = portInfos[m];
      } else {
        targetLineInfo[(k++)] = portInfos[m];
      }
    }
  }
  
  public Line getLine(Line.Info paramInfo)
    throws LineUnavailableException
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if ((localInfo != null) && ((localInfo instanceof Port.Info))) {
      for (int i = 0; i < portInfos.length; i++) {
        if (localInfo.equals(portInfos[i])) {
          return getPort(i);
        }
      }
    }
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo)
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if (localInfo == null) {
      return 0;
    }
    if ((localInfo instanceof Port.Info)) {
      return 1;
    }
    return 0;
  }
  
  protected void implOpen()
    throws LineUnavailableException
  {
    id = nOpen(getMixerIndex());
  }
  
  protected void implClose()
  {
    long l = id;
    id = 0L;
    nClose(l);
    if (ports != null) {
      for (int i = 0; i < ports.length; i++) {
        if (ports[i] != null) {
          ports[i].disposeControls();
        }
      }
    }
  }
  
  protected void implStart() {}
  
  protected void implStop() {}
  
  private Port.Info getPortInfo(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    case 1: 
      return new PortInfo(nGetPortName(getID(), paramInt1), true, null);
    case 2: 
      return Port.Info.MICROPHONE;
    case 3: 
      return Port.Info.LINE_IN;
    case 4: 
      return Port.Info.COMPACT_DISC;
    case 256: 
      return new PortInfo(nGetPortName(getID(), paramInt1), false, null);
    case 512: 
      return Port.Info.SPEAKER;
    case 768: 
      return Port.Info.HEADPHONE;
    case 1024: 
      return Port.Info.LINE_OUT;
    }
    return null;
  }
  
  int getMixerIndex()
  {
    return ((PortMixerProvider.PortMixerInfo)getMixerInfo()).getIndex();
  }
  
  Port getPort(int paramInt)
  {
    if (ports == null) {
      ports = new PortMixerPort[portInfos.length];
    }
    if (ports[paramInt] == null)
    {
      ports[paramInt] = new PortMixerPort(portInfos[paramInt], this, paramInt, null);
      return ports[paramInt];
    }
    return ports[paramInt];
  }
  
  long getID()
  {
    return id;
  }
  
  private static native long nOpen(int paramInt)
    throws LineUnavailableException;
  
  private static native void nClose(long paramLong);
  
  private static native int nGetPortCount(long paramLong);
  
  private static native int nGetPortType(long paramLong, int paramInt);
  
  private static native String nGetPortName(long paramLong, int paramInt);
  
  private static native void nGetControls(long paramLong, int paramInt, Vector paramVector);
  
  private static native void nControlSetIntValue(long paramLong, int paramInt);
  
  private static native int nControlGetIntValue(long paramLong);
  
  private static native void nControlSetFloatValue(long paramLong, float paramFloat);
  
  private static native float nControlGetFloatValue(long paramLong);
  
  private static final class BoolCtrl
    extends BooleanControl
  {
    private final long controlID;
    private boolean closed = false;
    
    private static BooleanControl.Type createType(String paramString)
    {
      if (paramString.equals("Mute")) {
        return BooleanControl.Type.MUTE;
      }
      if (paramString.equals("Select")) {}
      return new BCT(paramString, null);
    }
    
    private BoolCtrl(long paramLong, String paramString)
    {
      this(paramLong, createType(paramString));
    }
    
    private BoolCtrl(long paramLong, BooleanControl.Type paramType)
    {
      super(false);
      controlID = paramLong;
    }
    
    public void setValue(boolean paramBoolean)
    {
      if (!closed) {
        PortMixer.nControlSetIntValue(controlID, paramBoolean ? 1 : 0);
      }
    }
    
    public boolean getValue()
    {
      if (!closed) {
        return PortMixer.nControlGetIntValue(controlID) != 0;
      }
      return false;
    }
    
    private static final class BCT
      extends BooleanControl.Type
    {
      private BCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class CompCtrl
    extends CompoundControl
  {
    private CompCtrl(String paramString, Control[] paramArrayOfControl)
    {
      super(paramArrayOfControl);
    }
    
    private static final class CCT
      extends CompoundControl.Type
    {
      private CCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class FloatCtrl
    extends FloatControl
  {
    private final long controlID;
    private boolean closed = false;
    private static final FloatControl.Type[] FLOAT_CONTROL_TYPES = { null, FloatControl.Type.BALANCE, FloatControl.Type.MASTER_GAIN, FloatControl.Type.PAN, FloatControl.Type.VOLUME };
    
    private FloatCtrl(long paramLong, String paramString1, float paramFloat1, float paramFloat2, float paramFloat3, String paramString2)
    {
      this(paramLong, new FCT(paramString1, null), paramFloat1, paramFloat2, paramFloat3, paramString2);
    }
    
    private FloatCtrl(long paramLong, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
    {
      this(paramLong, FLOAT_CONTROL_TYPES[paramInt], paramFloat1, paramFloat2, paramFloat3, paramString);
    }
    
    private FloatCtrl(long paramLong, FloatControl.Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
    {
      super(paramFloat1, paramFloat2, paramFloat3, 1000, paramFloat1, paramString);
      controlID = paramLong;
    }
    
    public void setValue(float paramFloat)
    {
      if (!closed) {
        PortMixer.nControlSetFloatValue(controlID, paramFloat);
      }
    }
    
    public float getValue()
    {
      if (!closed) {
        return PortMixer.nControlGetFloatValue(controlID);
      }
      return getMinimum();
    }
    
    private static final class FCT
      extends FloatControl.Type
    {
      private FCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class PortInfo
    extends Port.Info
  {
    private PortInfo(String paramString, boolean paramBoolean)
    {
      super(paramString, paramBoolean);
    }
  }
  
  private static final class PortMixerPort
    extends AbstractLine
    implements Port
  {
    private final int portIndex;
    private long id;
    
    private PortMixerPort(Port.Info paramInfo, PortMixer paramPortMixer, int paramInt)
    {
      super(paramPortMixer, null);
      portIndex = paramInt;
    }
    
    void implOpen()
      throws LineUnavailableException
    {
      long l = ((PortMixer)mixer).getID();
      if ((id == 0L) || (l != id) || (controls.length == 0))
      {
        id = l;
        Vector localVector = new Vector();
        synchronized (localVector)
        {
          PortMixer.nGetControls(id, portIndex, localVector);
          controls = new Control[localVector.size()];
          for (int i = 0; i < controls.length; i++) {
            controls[i] = ((Control)localVector.elementAt(i));
          }
        }
      }
      else
      {
        enableControls(controls, true);
      }
    }
    
    private void enableControls(Control[] paramArrayOfControl, boolean paramBoolean)
    {
      for (int i = 0; i < paramArrayOfControl.length; i++) {
        if ((paramArrayOfControl[i] instanceof PortMixer.BoolCtrl)) {
          closed = (!paramBoolean);
        } else if ((paramArrayOfControl[i] instanceof PortMixer.FloatCtrl)) {
          closed = (!paramBoolean);
        } else if ((paramArrayOfControl[i] instanceof CompoundControl)) {
          enableControls(((CompoundControl)paramArrayOfControl[i]).getMemberControls(), paramBoolean);
        }
      }
    }
    
    private void disposeControls()
    {
      enableControls(controls, false);
      controls = new Control[0];
    }
    
    void implClose()
    {
      enableControls(controls, false);
    }
    
    public void open()
      throws LineUnavailableException
    {
      synchronized (mixer)
      {
        if (!isOpen())
        {
          mixer.open(this);
          try
          {
            implOpen();
            setOpen(true);
          }
          catch (LineUnavailableException localLineUnavailableException)
          {
            mixer.close(this);
            throw localLineUnavailableException;
          }
        }
      }
    }
    
    public void close()
    {
      synchronized (mixer)
      {
        if (isOpen())
        {
          setOpen(false);
          implClose();
          mixer.close(this);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\PortMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */