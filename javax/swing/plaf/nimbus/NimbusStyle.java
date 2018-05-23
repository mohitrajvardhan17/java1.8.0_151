package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public final class NimbusStyle
  extends SynthStyle
{
  public static final String LARGE_KEY = "large";
  public static final String SMALL_KEY = "small";
  public static final String MINI_KEY = "mini";
  public static final double LARGE_SCALE = 1.15D;
  public static final double SMALL_SCALE = 0.857D;
  public static final double MINI_SCALE = 0.714D;
  private static final Object NULL = Character.valueOf('\000');
  private static final Color DEFAULT_COLOR = new ColorUIResource(Color.BLACK);
  private static final Comparator<RuntimeState> STATE_COMPARATOR = new Comparator()
  {
    public int compare(NimbusStyle.RuntimeState paramAnonymousRuntimeState1, NimbusStyle.RuntimeState paramAnonymousRuntimeState2)
    {
      return state - state;
    }
  };
  private String prefix;
  private SynthPainter painter;
  private Values values;
  private CacheKey tmpKey = new CacheKey("", 0);
  private WeakReference<JComponent> component;
  
  NimbusStyle(String paramString, JComponent paramJComponent)
  {
    if (paramJComponent != null) {
      component = new WeakReference(paramJComponent);
    }
    prefix = paramString;
    painter = new SynthPainterImpl(this);
  }
  
  public void installDefaults(SynthContext paramSynthContext)
  {
    validate();
    super.installDefaults(paramSynthContext);
  }
  
  private void validate()
  {
    if (values != null) {
      return;
    }
    values = new Values(null);
    Object localObject1 = ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDefaultsForPrefix(prefix);
    Object localObject6;
    Object localObject7;
    if (component != null)
    {
      localObject2 = ((JComponent)component.get()).getClientProperty("Nimbus.Overrides");
      if ((localObject2 instanceof UIDefaults))
      {
        localObject3 = ((JComponent)component.get()).getClientProperty("Nimbus.Overrides.InheritDefaults");
        int i = (localObject3 instanceof Boolean) ? ((Boolean)localObject3).booleanValue() : 1;
        localObject4 = (UIDefaults)localObject2;
        localObject5 = new TreeMap();
        Iterator localIterator1 = ((UIDefaults)localObject4).keySet().iterator();
        while (localIterator1.hasNext())
        {
          localObject6 = localIterator1.next();
          if ((localObject6 instanceof String))
          {
            localObject7 = (String)localObject6;
            if (((String)localObject7).startsWith(prefix)) {
              ((TreeMap)localObject5).put(localObject7, ((UIDefaults)localObject4).get(localObject7));
            }
          }
        }
        if (i != 0) {
          ((Map)localObject1).putAll((Map)localObject5);
        } else {
          localObject1 = localObject5;
        }
      }
    }
    Object localObject2 = new ArrayList();
    Object localObject3 = new HashMap();
    ArrayList localArrayList = new ArrayList();
    Object localObject4 = (String)((Map)localObject1).get(prefix + ".States");
    if (localObject4 != null)
    {
      localObject5 = ((String)localObject4).split(",");
      for (int j = 0; j < localObject5.length; j++)
      {
        localObject5[j] = localObject5[j].trim();
        if (!State.isStandardStateName(localObject5[j]))
        {
          localObject6 = prefix + "." + localObject5[j];
          localObject7 = (State)((Map)localObject1).get(localObject6);
          if (localObject7 != null) {
            ((List)localObject2).add(localObject7);
          }
        }
        else
        {
          ((List)localObject2).add(State.getStandardState(localObject5[j]));
        }
      }
      if (((List)localObject2).size() > 0) {
        values.stateTypes = ((State[])((List)localObject2).toArray(new State[((List)localObject2).size()]));
      }
      j = 1;
      localObject6 = ((List)localObject2).iterator();
      while (((Iterator)localObject6).hasNext())
      {
        localObject7 = (State)((Iterator)localObject6).next();
        ((Map)localObject3).put(((State)localObject7).getName(), Integer.valueOf(j));
        j <<= 1;
      }
    }
    else
    {
      ((List)localObject2).add(State.Enabled);
      ((List)localObject2).add(State.MouseOver);
      ((List)localObject2).add(State.Pressed);
      ((List)localObject2).add(State.Disabled);
      ((List)localObject2).add(State.Focused);
      ((List)localObject2).add(State.Selected);
      ((List)localObject2).add(State.Default);
      ((Map)localObject3).put("Enabled", Integer.valueOf(1));
      ((Map)localObject3).put("MouseOver", Integer.valueOf(2));
      ((Map)localObject3).put("Pressed", Integer.valueOf(4));
      ((Map)localObject3).put("Disabled", Integer.valueOf(8));
      ((Map)localObject3).put("Focused", Integer.valueOf(256));
      ((Map)localObject3).put("Selected", Integer.valueOf(512));
      ((Map)localObject3).put("Default", Integer.valueOf(1024));
    }
    Object localObject5 = ((Map)localObject1).keySet().iterator();
    while (((Iterator)localObject5).hasNext())
    {
      String str = (String)((Iterator)localObject5).next();
      localObject6 = str.substring(prefix.length());
      if ((((String)localObject6).indexOf('"') == -1) && (((String)localObject6).indexOf(':') == -1))
      {
        localObject6 = ((String)localObject6).substring(1);
        localObject7 = null;
        Object localObject8 = null;
        int k = ((String)localObject6).indexOf(']');
        if (k < 0)
        {
          localObject8 = localObject6;
        }
        else
        {
          localObject7 = ((String)localObject6).substring(0, k);
          localObject8 = ((String)localObject6).substring(k + 2);
        }
        if (localObject7 == null)
        {
          if ("contentMargins".equals(localObject8)) {
            values.contentMargins = ((Insets)((Map)localObject1).get(str));
          } else if (!"States".equals(localObject8)) {
            values.defaults.put(localObject8, ((Map)localObject1).get(str));
          }
        }
        else
        {
          int m = 0;
          int n = 0;
          String[] arrayOfString = ((String)localObject7).split("\\+");
          for (Object localObject10 : arrayOfString) {
            if (((Map)localObject3).containsKey(localObject10))
            {
              n |= ((Integer)((Map)localObject3).get(localObject10)).intValue();
            }
            else
            {
              m = 1;
              break;
            }
          }
          if (m == 0)
          {
            ??? = null;
            Iterator localIterator2 = localArrayList.iterator();
            while (localIterator2.hasNext())
            {
              RuntimeState localRuntimeState = (RuntimeState)localIterator2.next();
              if (state == n)
              {
                ??? = localRuntimeState;
                break;
              }
            }
            if (??? == null)
            {
              ??? = new RuntimeState(n, (String)localObject7, null);
              localArrayList.add(???);
            }
            if ("backgroundPainter".equals(localObject8)) {
              backgroundPainter = getPainter((Map)localObject1, str);
            } else if ("foregroundPainter".equals(localObject8)) {
              foregroundPainter = getPainter((Map)localObject1, str);
            } else if ("borderPainter".equals(localObject8)) {
              borderPainter = getPainter((Map)localObject1, str);
            } else {
              defaults.put(localObject8, ((Map)localObject1).get(str));
            }
          }
        }
      }
    }
    Collections.sort(localArrayList, STATE_COMPARATOR);
    values.states = ((RuntimeState[])localArrayList.toArray(new RuntimeState[localArrayList.size()]));
  }
  
  private Painter getPainter(Map<String, Object> paramMap, String paramString)
  {
    Object localObject = paramMap.get(paramString);
    if ((localObject instanceof UIDefaults.LazyValue)) {
      localObject = ((UIDefaults.LazyValue)localObject).createValue(UIManager.getDefaults());
    }
    return (localObject instanceof Painter) ? (Painter)localObject : null;
  }
  
  public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets)
  {
    if (paramInsets == null) {
      paramInsets = new Insets(0, 0, 0, 0);
    }
    Values localValues = getValues(paramSynthContext);
    if (contentMargins == null)
    {
      bottom = (top = left = right = 0);
      return paramInsets;
    }
    bottom = contentMargins.bottom;
    top = contentMargins.top;
    left = contentMargins.left;
    right = contentMargins.right;
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str))
      {
        Insets tmp125_124 = paramInsets;
        125124bottom = ((int)(125124bottom * 1.15D));
        Insets tmp139_138 = paramInsets;
        139138top = ((int)(139138top * 1.15D));
        Insets tmp153_152 = paramInsets;
        153152left = ((int)(153152left * 1.15D));
        Insets tmp167_166 = paramInsets;
        167166right = ((int)(167166right * 1.15D));
      }
      else if ("small".equals(str))
      {
        Insets tmp194_193 = paramInsets;
        194193bottom = ((int)(194193bottom * 0.857D));
        Insets tmp208_207 = paramInsets;
        208207top = ((int)(208207top * 0.857D));
        Insets tmp222_221 = paramInsets;
        222221left = ((int)(222221left * 0.857D));
        Insets tmp236_235 = paramInsets;
        236235right = ((int)(236235right * 0.857D));
      }
      else if ("mini".equals(str))
      {
        Insets tmp263_262 = paramInsets;
        263262bottom = ((int)(263262bottom * 0.714D));
        Insets tmp277_276 = paramInsets;
        277276top = ((int)(277276top * 0.714D));
        Insets tmp291_290 = paramInsets;
        291290left = ((int)(291290left * 0.714D));
        Insets tmp305_304 = paramInsets;
        305304right = ((int)(305304right * 0.714D));
      }
    }
    return paramInsets;
  }
  
  protected Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType)
  {
    String str = null;
    if (paramColorType == ColorType.BACKGROUND) {
      str = "background";
    } else if (paramColorType == ColorType.FOREGROUND) {
      str = "textForeground";
    } else if (paramColorType == ColorType.TEXT_BACKGROUND) {
      str = "textBackground";
    } else if (paramColorType == ColorType.TEXT_FOREGROUND) {
      str = "textForeground";
    } else if (paramColorType == ColorType.FOCUS) {
      str = "focus";
    } else if (paramColorType != null) {
      str = paramColorType.toString();
    } else {
      return DEFAULT_COLOR;
    }
    Color localColor = (Color)get(paramSynthContext, str);
    if (localColor == null) {
      localColor = DEFAULT_COLOR;
    }
    return localColor;
  }
  
  protected Font getFontForState(SynthContext paramSynthContext)
  {
    Font localFont = (Font)get(paramSynthContext, "font");
    if (localFont == null) {
      localFont = UIManager.getFont("defaultFont");
    }
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str)) {
        localFont = localFont.deriveFont((float)Math.round(localFont.getSize2D() * 1.15D));
      } else if ("small".equals(str)) {
        localFont = localFont.deriveFont((float)Math.round(localFont.getSize2D() * 0.857D));
      } else if ("mini".equals(str)) {
        localFont = localFont.deriveFont((float)Math.round(localFont.getSize2D() * 0.714D));
      }
    }
    return localFont;
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext)
  {
    return painter;
  }
  
  public boolean isOpaque(SynthContext paramSynthContext)
  {
    if ("Table.cellRenderer".equals(paramSynthContext.getComponent().getName())) {
      return true;
    }
    Boolean localBoolean = (Boolean)get(paramSynthContext, "opaque");
    return localBoolean == null ? false : localBoolean.booleanValue();
  }
  
  public Object get(SynthContext paramSynthContext, Object paramObject)
  {
    Values localValues = getValues(paramSynthContext);
    String str1 = paramObject.toString();
    String str2 = str1.substring(str1.indexOf(".") + 1);
    Object localObject = null;
    int i = getExtendedState(paramSynthContext, localValues);
    tmpKey.init(str2, i);
    localObject = cache.get(tmpKey);
    int j = localObject != null ? 1 : 0;
    if (j == 0)
    {
      RuntimeState localRuntimeState = null;
      int[] arrayOfInt = { -1 };
      while ((localObject == null) && ((localRuntimeState = getNextState(states, arrayOfInt, i)) != null)) {
        localObject = defaults.get(str2);
      }
      if ((localObject == null) && (defaults != null)) {
        localObject = defaults.get(str2);
      }
      if (localObject == null) {
        localObject = UIManager.get(str1);
      }
      if ((localObject == null) && (str2.equals("focusInputMap"))) {
        localObject = super.get(paramSynthContext, str1);
      }
      cache.put(new CacheKey(str2, i), localObject == null ? NULL : localObject);
    }
    return localObject == NULL ? null : localObject;
  }
  
  public Painter getBackgroundPainter(SynthContext paramSynthContext)
  {
    Values localValues = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, localValues);
    Painter localPainter = null;
    tmpKey.init("backgroundPainter$$instance", i);
    localPainter = (Painter)cache.get(tmpKey);
    if (localPainter != null) {
      return localPainter;
    }
    RuntimeState localRuntimeState = null;
    int[] arrayOfInt = { -1 };
    while ((localRuntimeState = getNextState(states, arrayOfInt, i)) != null) {
      if (backgroundPainter != null) {
        localPainter = backgroundPainter;
      }
    }
    if (localPainter == null) {
      localPainter = (Painter)get(paramSynthContext, "backgroundPainter");
    }
    if (localPainter != null) {
      cache.put(new CacheKey("backgroundPainter$$instance", i), localPainter);
    }
    return localPainter;
  }
  
  public Painter getForegroundPainter(SynthContext paramSynthContext)
  {
    Values localValues = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, localValues);
    Painter localPainter = null;
    tmpKey.init("foregroundPainter$$instance", i);
    localPainter = (Painter)cache.get(tmpKey);
    if (localPainter != null) {
      return localPainter;
    }
    RuntimeState localRuntimeState = null;
    int[] arrayOfInt = { -1 };
    while ((localRuntimeState = getNextState(states, arrayOfInt, i)) != null) {
      if (foregroundPainter != null) {
        localPainter = foregroundPainter;
      }
    }
    if (localPainter == null) {
      localPainter = (Painter)get(paramSynthContext, "foregroundPainter");
    }
    if (localPainter != null) {
      cache.put(new CacheKey("foregroundPainter$$instance", i), localPainter);
    }
    return localPainter;
  }
  
  public Painter getBorderPainter(SynthContext paramSynthContext)
  {
    Values localValues = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, localValues);
    Painter localPainter = null;
    tmpKey.init("borderPainter$$instance", i);
    localPainter = (Painter)cache.get(tmpKey);
    if (localPainter != null) {
      return localPainter;
    }
    RuntimeState localRuntimeState = null;
    int[] arrayOfInt = { -1 };
    while ((localRuntimeState = getNextState(states, arrayOfInt, i)) != null) {
      if (borderPainter != null) {
        localPainter = borderPainter;
      }
    }
    if (localPainter == null) {
      localPainter = (Painter)get(paramSynthContext, "borderPainter");
    }
    if (localPainter != null) {
      cache.put(new CacheKey("borderPainter$$instance", i), localPainter);
    }
    return localPainter;
  }
  
  private Values getValues(SynthContext paramSynthContext)
  {
    validate();
    return values;
  }
  
  private boolean contains(String[] paramArrayOfString, String paramString)
  {
    assert (paramString != null);
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equals(paramArrayOfString[i])) {
        return true;
      }
    }
    return false;
  }
  
  private int getExtendedState(SynthContext paramSynthContext, Values paramValues)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    int i = 0;
    int j = 1;
    Object localObject1 = localJComponent.getClientProperty("Nimbus.State");
    Object localObject2;
    if (localObject1 != null)
    {
      String str1 = localObject1.toString();
      localObject2 = str1.split("\\+");
      String str2;
      if (stateTypes == null) {
        for (str2 : localObject2)
        {
          State.StandardState localStandardState = State.getStandardState(str2);
          if (localStandardState != null) {
            i |= localStandardState.getState();
          }
        }
      } else {
        for (str2 : stateTypes)
        {
          if (contains((String[])localObject2, str2.getName())) {
            i |= j;
          }
          j <<= 1;
        }
      }
    }
    else
    {
      if (stateTypes == null) {
        return paramSynthContext.getComponentState();
      }
      int k = paramSynthContext.getComponentState();
      for (Object localObject4 : stateTypes)
      {
        if (((State)localObject4).isInState(localJComponent, k)) {
          i |= j;
        }
        j <<= 1;
      }
    }
    return i;
  }
  
  private RuntimeState getNextState(RuntimeState[] paramArrayOfRuntimeState, int[] paramArrayOfInt, int paramInt)
  {
    if ((paramArrayOfRuntimeState != null) && (paramArrayOfRuntimeState.length > 0))
    {
      int i = 0;
      int j = -1;
      int k = -1;
      if (paramInt == 0)
      {
        for (m = paramArrayOfRuntimeState.length - 1; m >= 0; m--) {
          if (state == 0)
          {
            paramArrayOfInt[0] = m;
            return paramArrayOfRuntimeState[m];
          }
        }
        paramArrayOfInt[0] = -1;
        return null;
      }
      int m = (paramArrayOfInt == null) || (paramArrayOfInt[0] == -1) ? paramArrayOfRuntimeState.length : paramArrayOfInt[0];
      for (int n = m - 1; n >= 0; n--)
      {
        int i1 = state;
        if (i1 == 0)
        {
          if (k == -1) {
            k = n;
          }
        }
        else if ((paramInt & i1) == i1)
        {
          int i2 = i1;
          i2 -= ((0xAAAAAAAA & i2) >>> 1);
          i2 = (i2 & 0x33333333) + (i2 >>> 2 & 0x33333333);
          i2 = i2 + (i2 >>> 4) & 0xF0F0F0F;
          i2 += (i2 >>> 8);
          i2 += (i2 >>> 16);
          i2 &= 0xFF;
          if (i2 > i)
          {
            j = n;
            i = i2;
          }
        }
      }
      if (j != -1)
      {
        paramArrayOfInt[0] = j;
        return paramArrayOfRuntimeState[j];
      }
      if (k != -1)
      {
        paramArrayOfInt[0] = k;
        return paramArrayOfRuntimeState[k];
      }
    }
    paramArrayOfInt[0] = -1;
    return null;
  }
  
  private static final class CacheKey
  {
    private String key;
    private int xstate;
    
    CacheKey(Object paramObject, int paramInt)
    {
      init(paramObject, paramInt);
    }
    
    void init(Object paramObject, int paramInt)
    {
      key = paramObject.toString();
      xstate = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      CacheKey localCacheKey = (CacheKey)paramObject;
      if (paramObject == null) {
        return false;
      }
      if (xstate != xstate) {
        return false;
      }
      return key.equals(key);
    }
    
    public int hashCode()
    {
      int i = 3;
      i = 29 * i + key.hashCode();
      i = 29 * i + xstate;
      return i;
    }
  }
  
  private final class RuntimeState
    implements Cloneable
  {
    int state;
    Painter backgroundPainter;
    Painter foregroundPainter;
    Painter borderPainter;
    String stateName;
    UIDefaults defaults = new UIDefaults(10, 0.7F);
    
    private RuntimeState(int paramInt, String paramString)
    {
      state = paramInt;
      stateName = paramString;
    }
    
    public String toString()
    {
      return stateName;
    }
    
    public RuntimeState clone()
    {
      RuntimeState localRuntimeState = new RuntimeState(NimbusStyle.this, state, stateName);
      backgroundPainter = backgroundPainter;
      foregroundPainter = foregroundPainter;
      borderPainter = borderPainter;
      defaults.putAll(defaults);
      return localRuntimeState;
    }
  }
  
  private static final class Values
  {
    State[] stateTypes = null;
    NimbusStyle.RuntimeState[] states = null;
    Insets contentMargins;
    UIDefaults defaults = new UIDefaults(10, 0.7F);
    Map<NimbusStyle.CacheKey, Object> cache = new HashMap();
    
    private Values() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\NimbusStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */