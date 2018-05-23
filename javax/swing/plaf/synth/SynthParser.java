package javax.swing.plaf.synth;

import com.sun.beans.decoder.DocumentHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults.LazyInputMap;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;
import sun.swing.plaf.synth.DefaultSynthStyle.StateInfo;

class SynthParser
  extends DefaultHandler
{
  private static final String ELEMENT_SYNTH = "synth";
  private static final String ELEMENT_STYLE = "style";
  private static final String ELEMENT_STATE = "state";
  private static final String ELEMENT_FONT = "font";
  private static final String ELEMENT_COLOR = "color";
  private static final String ELEMENT_IMAGE_PAINTER = "imagePainter";
  private static final String ELEMENT_PAINTER = "painter";
  private static final String ELEMENT_PROPERTY = "property";
  private static final String ELEMENT_SYNTH_GRAPHICS = "graphicsUtils";
  private static final String ELEMENT_IMAGE_ICON = "imageIcon";
  private static final String ELEMENT_BIND = "bind";
  private static final String ELEMENT_BIND_KEY = "bindKey";
  private static final String ELEMENT_INSETS = "insets";
  private static final String ELEMENT_OPAQUE = "opaque";
  private static final String ELEMENT_DEFAULTS_PROPERTY = "defaultsProperty";
  private static final String ELEMENT_INPUT_MAP = "inputMap";
  private static final String ATTRIBUTE_ACTION = "action";
  private static final String ATTRIBUTE_ID = "id";
  private static final String ATTRIBUTE_IDREF = "idref";
  private static final String ATTRIBUTE_CLONE = "clone";
  private static final String ATTRIBUTE_VALUE = "value";
  private static final String ATTRIBUTE_NAME = "name";
  private static final String ATTRIBUTE_STYLE = "style";
  private static final String ATTRIBUTE_SIZE = "size";
  private static final String ATTRIBUTE_TYPE = "type";
  private static final String ATTRIBUTE_TOP = "top";
  private static final String ATTRIBUTE_LEFT = "left";
  private static final String ATTRIBUTE_BOTTOM = "bottom";
  private static final String ATTRIBUTE_RIGHT = "right";
  private static final String ATTRIBUTE_KEY = "key";
  private static final String ATTRIBUTE_SOURCE_INSETS = "sourceInsets";
  private static final String ATTRIBUTE_DEST_INSETS = "destinationInsets";
  private static final String ATTRIBUTE_PATH = "path";
  private static final String ATTRIBUTE_STRETCH = "stretch";
  private static final String ATTRIBUTE_PAINT_CENTER = "paintCenter";
  private static final String ATTRIBUTE_METHOD = "method";
  private static final String ATTRIBUTE_DIRECTION = "direction";
  private static final String ATTRIBUTE_CENTER = "center";
  private DocumentHandler _handler;
  private int _depth;
  private DefaultSynthStyleFactory _factory;
  private List<ParsedSynthStyle.StateInfo> _stateInfos = new ArrayList();
  private ParsedSynthStyle _style;
  private ParsedSynthStyle.StateInfo _stateInfo;
  private List<String> _inputMapBindings = new ArrayList();
  private String _inputMapID;
  private Map<String, Object> _mapping = new HashMap();
  private URL _urlResourceBase;
  private Class<?> _classResourceBase;
  private List<ColorType> _colorTypes = new ArrayList();
  private Map<String, Object> _defaultsMap;
  private List<ParsedSynthStyle.PainterInfo> _stylePainters = new ArrayList();
  private List<ParsedSynthStyle.PainterInfo> _statePainters = new ArrayList();
  
  SynthParser() {}
  
  public void parse(InputStream paramInputStream, DefaultSynthStyleFactory paramDefaultSynthStyleFactory, URL paramURL, Class<?> paramClass, Map<String, Object> paramMap)
    throws ParseException, IllegalArgumentException
  {
    if ((paramInputStream == null) || (paramDefaultSynthStyleFactory == null) || ((paramURL == null) && (paramClass == null))) {
      throw new IllegalArgumentException("You must supply an InputStream, StyleFactory and Class or URL");
    }
    assert ((paramURL == null) || (paramClass == null));
    _factory = paramDefaultSynthStyleFactory;
    _classResourceBase = paramClass;
    _urlResourceBase = paramURL;
    _defaultsMap = paramMap;
    try
    {
      SAXParser localSAXParser;
      return;
    }
    catch (SAXException localSAXException) {}finally
    {
      reset();
    }
  }
  
  private URL getResource(String paramString)
  {
    if (_classResourceBase != null) {
      return _classResourceBase.getResource(paramString);
    }
    try
    {
      return new URL(_urlResourceBase, paramString);
    }
    catch (MalformedURLException localMalformedURLException) {}
    return null;
  }
  
  private void reset()
  {
    _handler = null;
    _depth = 0;
    _mapping.clear();
    _stateInfos.clear();
    _colorTypes.clear();
    _statePainters.clear();
    _stylePainters.clear();
  }
  
  private boolean isForwarding()
  {
    return _depth > 0;
  }
  
  private DocumentHandler getHandler()
  {
    if (_handler == null)
    {
      _handler = new DocumentHandler();
      Object localObject2;
      if (_urlResourceBase != null)
      {
        localObject1 = new URL[] { getResource(".") };
        localObject2 = Thread.currentThread().getContextClassLoader();
        URLClassLoader localURLClassLoader = new URLClassLoader((URL[])localObject1, (ClassLoader)localObject2);
        _handler.setClassLoader(localURLClassLoader);
      }
      else
      {
        _handler.setClassLoader(_classResourceBase.getClassLoader());
      }
      Object localObject1 = _mapping.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        _handler.setVariable((String)localObject2, _mapping.get(localObject2));
      }
    }
    return _handler;
  }
  
  private Object checkCast(Object paramObject, Class paramClass)
    throws SAXException
  {
    if (!paramClass.isInstance(paramObject)) {
      throw new SAXException("Expected type " + paramClass + " got " + paramObject.getClass());
    }
    return paramObject;
  }
  
  private Object lookup(String paramString, Class paramClass)
    throws SAXException
  {
    if ((_handler != null) && (_handler.hasVariable(paramString))) {
      return checkCast(_handler.getVariable(paramString), paramClass);
    }
    Object localObject = _mapping.get(paramString);
    if (localObject == null) {
      throw new SAXException("ID " + paramString + " has not been defined");
    }
    return checkCast(localObject, paramClass);
  }
  
  private void register(String paramString, Object paramObject)
    throws SAXException
  {
    if (paramString != null)
    {
      if ((_mapping.get(paramString) != null) || ((_handler != null) && (_handler.hasVariable(paramString)))) {
        throw new SAXException("ID " + paramString + " is already defined");
      }
      if (_handler != null) {
        _handler.setVariable(paramString, paramObject);
      } else {
        _mapping.put(paramString, paramObject);
      }
    }
  }
  
  private int nextInt(StringTokenizer paramStringTokenizer, String paramString)
    throws SAXException
  {
    if (!paramStringTokenizer.hasMoreTokens()) {
      throw new SAXException(paramString);
    }
    try
    {
      return Integer.parseInt(paramStringTokenizer.nextToken());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SAXException(paramString);
    }
  }
  
  private Insets parseInsets(String paramString1, String paramString2)
    throws SAXException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1);
    return new Insets(nextInt(localStringTokenizer, paramString2), nextInt(localStringTokenizer, paramString2), nextInt(localStringTokenizer, paramString2), nextInt(localStringTokenizer, paramString2));
  }
  
  private void startStyle(Attributes paramAttributes)
    throws SAXException
  {
    String str1 = null;
    _style = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
    {
      String str2 = paramAttributes.getQName(i);
      if (str2.equals("clone")) {
        _style = ((ParsedSynthStyle)((ParsedSynthStyle)lookup(paramAttributes.getValue(i), ParsedSynthStyle.class)).clone());
      } else if (str2.equals("id")) {
        str1 = paramAttributes.getValue(i);
      }
    }
    if (_style == null) {
      _style = new ParsedSynthStyle();
    }
    register(str1, _style);
  }
  
  private void endStyle()
  {
    int i = _stylePainters.size();
    if (i > 0)
    {
      _style.setPainters((ParsedSynthStyle.PainterInfo[])_stylePainters.toArray(new ParsedSynthStyle.PainterInfo[i]));
      _stylePainters.clear();
    }
    i = _stateInfos.size();
    if (i > 0)
    {
      _style.setStateInfo((DefaultSynthStyle.StateInfo[])_stateInfos.toArray(new ParsedSynthStyle.StateInfo[i]));
      _stateInfos.clear();
    }
    _style = null;
  }
  
  private void startState(Attributes paramAttributes)
    throws SAXException
  {
    Object localObject = null;
    int i = 0;
    String str1 = null;
    _stateInfo = null;
    for (int j = paramAttributes.getLength() - 1; j >= 0; j--)
    {
      String str2 = paramAttributes.getQName(j);
      if (str2.equals("id"))
      {
        str1 = paramAttributes.getValue(j);
      }
      else if (str2.equals("idref"))
      {
        _stateInfo = ((ParsedSynthStyle.StateInfo)lookup(paramAttributes.getValue(j), ParsedSynthStyle.StateInfo.class));
      }
      else if (str2.equals("clone"))
      {
        _stateInfo = ((ParsedSynthStyle.StateInfo)((ParsedSynthStyle.StateInfo)lookup(paramAttributes.getValue(j), ParsedSynthStyle.StateInfo.class)).clone());
      }
      else if (str2.equals("value"))
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(paramAttributes.getValue(j));
        while (localStringTokenizer.hasMoreTokens())
        {
          String str3 = localStringTokenizer.nextToken().toUpperCase().intern();
          if (str3 == "ENABLED") {
            i |= 0x1;
          } else if (str3 == "MOUSE_OVER") {
            i |= 0x2;
          } else if (str3 == "PRESSED") {
            i |= 0x4;
          } else if (str3 == "DISABLED") {
            i |= 0x8;
          } else if (str3 == "FOCUSED") {
            i |= 0x100;
          } else if (str3 == "SELECTED") {
            i |= 0x200;
          } else if (str3 == "DEFAULT") {
            i |= 0x400;
          } else if (str3 != "AND") {
            throw new SAXException("Unknown state: " + i);
          }
        }
      }
    }
    if (_stateInfo == null) {
      _stateInfo = new ParsedSynthStyle.StateInfo();
    }
    _stateInfo.setComponentState(i);
    register(str1, _stateInfo);
    _stateInfos.add(_stateInfo);
  }
  
  private void endState()
  {
    int i = _statePainters.size();
    if (i > 0)
    {
      _stateInfo.setPainters((ParsedSynthStyle.PainterInfo[])_statePainters.toArray(new ParsedSynthStyle.PainterInfo[i]));
      _statePainters.clear();
    }
    _stateInfo = null;
  }
  
  private void startFont(Attributes paramAttributes)
    throws SAXException
  {
    Object localObject = null;
    int i = 0;
    int j = 0;
    String str1 = null;
    String str2 = null;
    for (int k = paramAttributes.getLength() - 1; k >= 0; k--)
    {
      String str3 = paramAttributes.getQName(k);
      if (str3.equals("id"))
      {
        str1 = paramAttributes.getValue(k);
      }
      else if (str3.equals("idref"))
      {
        localObject = (Font)lookup(paramAttributes.getValue(k), Font.class);
      }
      else if (str3.equals("name"))
      {
        str2 = paramAttributes.getValue(k);
      }
      else if (str3.equals("size"))
      {
        try
        {
          j = Integer.parseInt(paramAttributes.getValue(k));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new SAXException("Invalid font size: " + paramAttributes.getValue(k));
        }
      }
      else if (str3.equals("style"))
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(paramAttributes.getValue(k));
        while (localStringTokenizer.hasMoreTokens())
        {
          String str4 = localStringTokenizer.nextToken().intern();
          if (str4 == "BOLD") {
            i = (i | 0x0) ^ 0x0 | 0x1;
          } else if (str4 == "ITALIC") {
            i |= 0x2;
          }
        }
      }
    }
    if (localObject == null)
    {
      if (str2 == null) {
        throw new SAXException("You must define a name for the font");
      }
      if (j == 0) {
        throw new SAXException("You must define a size for the font");
      }
      localObject = new FontUIResource(str2, i, j);
    }
    else if ((str2 != null) || (j != 0) || (i != 0))
    {
      throw new SAXException("Name, size and style are not for use with idref");
    }
    register(str1, localObject);
    if (_stateInfo != null) {
      _stateInfo.setFont((Font)localObject);
    } else if (_style != null) {
      _style.setFont((Font)localObject);
    }
  }
  
  private void startColor(Attributes paramAttributes)
    throws SAXException
  {
    Object localObject1 = null;
    String str1 = null;
    _colorTypes.clear();
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
    {
      String str2 = paramAttributes.getQName(i);
      if (str2.equals("id"))
      {
        str1 = paramAttributes.getValue(i);
      }
      else if (str2.equals("idref"))
      {
        localObject1 = (Color)lookup(paramAttributes.getValue(i), Color.class);
      }
      else if (!str2.equals("name"))
      {
        Object localObject3;
        if (str2.equals("value"))
        {
          localObject3 = paramAttributes.getValue(i);
          if (((String)localObject3).startsWith("#")) {
            try
            {
              int i2 = ((String)localObject3).length();
              int n;
              boolean bool;
              if (i2 < 8)
              {
                n = Integer.decode((String)localObject3).intValue();
                bool = false;
              }
              else if (i2 == 8)
              {
                n = Integer.decode((String)localObject3).intValue();
                bool = true;
              }
              else if (i2 == 9)
              {
                int i3 = Integer.decode('#' + ((String)localObject3).substring(3, 9)).intValue();
                int i4 = Integer.decode(((String)localObject3).substring(0, 3)).intValue();
                n = i4 << 24 | i3;
                bool = true;
              }
              else
              {
                throw new SAXException("Invalid Color value: " + (String)localObject3);
              }
              localObject1 = new ColorUIResource(new Color(n, bool));
            }
            catch (NumberFormatException localNumberFormatException)
            {
              throw new SAXException("Invalid Color value: " + (String)localObject3);
            }
          } else {
            try
            {
              localObject1 = new ColorUIResource((Color)Color.class.getField(((String)localObject3).toUpperCase()).get(Color.class));
            }
            catch (NoSuchFieldException localNoSuchFieldException1)
            {
              throw new SAXException("Invalid color name: " + (String)localObject3);
            }
            catch (IllegalAccessException localIllegalAccessException1)
            {
              throw new SAXException("Invalid color name: " + (String)localObject3);
            }
          }
        }
        else if (str2.equals("type"))
        {
          localObject3 = new StringTokenizer(paramAttributes.getValue(i));
          while (((StringTokenizer)localObject3).hasMoreTokens())
          {
            String str3 = ((StringTokenizer)localObject3).nextToken();
            int i1 = str3.lastIndexOf('.');
            Class localClass;
            if (i1 == -1)
            {
              localClass = ColorType.class;
              i1 = 0;
            }
            else
            {
              try
              {
                localClass = ReflectUtil.forName(str3.substring(0, i1));
              }
              catch (ClassNotFoundException localClassNotFoundException)
              {
                throw new SAXException("Unknown class: " + str3.substring(0, i1));
              }
              i1++;
            }
            try
            {
              _colorTypes.add((ColorType)checkCast(localClass.getField(str3.substring(i1)).get(localClass), ColorType.class));
            }
            catch (NoSuchFieldException localNoSuchFieldException2)
            {
              throw new SAXException("Unable to find color type: " + str3);
            }
            catch (IllegalAccessException localIllegalAccessException2)
            {
              throw new SAXException("Unable to find color type: " + str3);
            }
          }
        }
      }
    }
    if (localObject1 == null) {
      throw new SAXException("color: you must specificy a value");
    }
    register(str1, localObject1);
    if ((_stateInfo != null) && (_colorTypes.size() > 0))
    {
      Object localObject2 = _stateInfo.getColors();
      int j = 0;
      for (int k = _colorTypes.size() - 1; k >= 0; k--) {
        j = Math.max(j, ((ColorType)_colorTypes.get(k)).getID());
      }
      if ((localObject2 == null) || (localObject2.length <= j))
      {
        Color[] arrayOfColor = new Color[j + 1];
        if (localObject2 != null) {
          System.arraycopy(localObject2, 0, arrayOfColor, 0, localObject2.length);
        }
        localObject2 = arrayOfColor;
      }
      for (int m = _colorTypes.size() - 1; m >= 0; m--) {
        localObject2[((ColorType)_colorTypes.get(m)).getID()] = localObject1;
      }
      _stateInfo.setColors((Color[])localObject2);
    }
  }
  
  private void startProperty(Attributes paramAttributes, Object paramObject)
    throws SAXException
  {
    Object localObject = null;
    String str1 = null;
    int i = 0;
    String str2 = null;
    for (int j = paramAttributes.getLength() - 1; j >= 0; j--)
    {
      String str3 = paramAttributes.getQName(j);
      if (str3.equals("type"))
      {
        String str4 = paramAttributes.getValue(j).toUpperCase();
        if (str4.equals("IDREF")) {
          i = 0;
        } else if (str4.equals("BOOLEAN")) {
          i = 1;
        } else if (str4.equals("DIMENSION")) {
          i = 2;
        } else if (str4.equals("INSETS")) {
          i = 3;
        } else if (str4.equals("INTEGER")) {
          i = 4;
        } else if (str4.equals("STRING")) {
          i = 5;
        } else {
          throw new SAXException(paramObject + " unknown type, useidref, boolean, dimension, insets or integer");
        }
      }
      else if (str3.equals("value"))
      {
        str2 = paramAttributes.getValue(j);
      }
      else if (str3.equals("key"))
      {
        str1 = paramAttributes.getValue(j);
      }
    }
    if (str2 != null) {
      switch (i)
      {
      case 0: 
        localObject = lookup(str2, Object.class);
        break;
      case 1: 
        if (str2.toUpperCase().equals("TRUE")) {
          localObject = Boolean.TRUE;
        } else {
          localObject = Boolean.FALSE;
        }
        break;
      case 2: 
        StringTokenizer localStringTokenizer = new StringTokenizer(str2);
        localObject = new DimensionUIResource(nextInt(localStringTokenizer, "Invalid dimension"), nextInt(localStringTokenizer, "Invalid dimension"));
        break;
      case 3: 
        localObject = parseInsets(str2, paramObject + " invalid insets");
        break;
      case 4: 
        try
        {
          localObject = new Integer(Integer.parseInt(str2));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new SAXException(paramObject + " invalid value");
        }
      case 5: 
        localObject = str2;
      }
    }
    if ((localObject == null) || (str1 == null)) {
      throw new SAXException(paramObject + ": you must supply a key and value");
    }
    if (paramObject == "defaultsProperty")
    {
      _defaultsMap.put(str1, localObject);
    }
    else if (_stateInfo != null)
    {
      if (_stateInfo.getData() == null) {
        _stateInfo.setData(new HashMap());
      }
      _stateInfo.getData().put(str1, localObject);
    }
    else if (_style != null)
    {
      if (_style.getData() == null) {
        _style.setData(new HashMap());
      }
      _style.getData().put(str1, localObject);
    }
  }
  
  private void startGraphics(Attributes paramAttributes)
    throws SAXException
  {
    SynthGraphicsUtils localSynthGraphicsUtils = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
    {
      String str = paramAttributes.getQName(i);
      if (str.equals("idref")) {
        localSynthGraphicsUtils = (SynthGraphicsUtils)lookup(paramAttributes.getValue(i), SynthGraphicsUtils.class);
      }
    }
    if (localSynthGraphicsUtils == null) {
      throw new SAXException("graphicsUtils: you must supply an idref");
    }
    if (_style != null) {
      _style.setGraphicsUtils(localSynthGraphicsUtils);
    }
  }
  
  private void startInsets(Attributes paramAttributes)
    throws SAXException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    Object localObject = null;
    String str1 = null;
    for (int n = paramAttributes.getLength() - 1; n >= 0; n--)
    {
      String str2 = paramAttributes.getQName(n);
      try
      {
        if (str2.equals("idref")) {
          localObject = (Insets)lookup(paramAttributes.getValue(n), Insets.class);
        } else if (str2.equals("id")) {
          str1 = paramAttributes.getValue(n);
        } else if (str2.equals("top")) {
          i = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str2.equals("left")) {
          k = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str2.equals("bottom")) {
          j = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str2.equals("right")) {
          m = Integer.parseInt(paramAttributes.getValue(n));
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new SAXException("insets: bad integer value for " + paramAttributes.getValue(n));
      }
    }
    if (localObject == null) {
      localObject = new InsetsUIResource(i, k, j, m);
    }
    register(str1, localObject);
    if (_style != null) {
      _style.setInsets((Insets)localObject);
    }
  }
  
  private void startBind(Attributes paramAttributes)
    throws SAXException
  {
    ParsedSynthStyle localParsedSynthStyle = null;
    String str1 = null;
    int i = -1;
    for (int j = paramAttributes.getLength() - 1; j >= 0; j--)
    {
      String str2 = paramAttributes.getQName(j);
      if (str2.equals("style"))
      {
        localParsedSynthStyle = (ParsedSynthStyle)lookup(paramAttributes.getValue(j), ParsedSynthStyle.class);
      }
      else if (str2.equals("type"))
      {
        String str3 = paramAttributes.getValue(j).toUpperCase();
        if (str3.equals("NAME")) {
          i = 0;
        } else if (str3.equals("REGION")) {
          i = 1;
        } else {
          throw new SAXException("bind: unknown type " + str3);
        }
      }
      else if (str2.equals("key"))
      {
        str1 = paramAttributes.getValue(j);
      }
    }
    if ((localParsedSynthStyle == null) || (str1 == null) || (i == -1)) {
      throw new SAXException("bind: you must specify a style, type and key");
    }
    try
    {
      _factory.addStyle(localParsedSynthStyle, str1, i);
    }
    catch (PatternSyntaxException localPatternSyntaxException)
    {
      throw new SAXException("bind: " + str1 + " is not a valid regular expression");
    }
  }
  
  private void startPainter(Attributes paramAttributes, String paramString)
    throws SAXException
  {
    Insets localInsets1 = null;
    Insets localInsets2 = null;
    Object localObject1 = null;
    boolean bool1 = true;
    boolean bool2 = true;
    Object localObject2 = null;
    String str1 = null;
    Object localObject3 = null;
    int i = -1;
    boolean bool3 = false;
    int j = 0;
    int k = 0;
    for (int m = paramAttributes.getLength() - 1; m >= 0; m--)
    {
      String str2 = paramAttributes.getQName(m);
      String str3 = paramAttributes.getValue(m);
      if (str2.equals("id"))
      {
        localObject3 = str3;
      }
      else if (str2.equals("method"))
      {
        str1 = str3.toLowerCase(Locale.ENGLISH);
      }
      else if (str2.equals("idref"))
      {
        localObject2 = (SynthPainter)lookup(str3, SynthPainter.class);
      }
      else if (str2.equals("path"))
      {
        localObject1 = str3;
      }
      else if (str2.equals("sourceInsets"))
      {
        localInsets1 = parseInsets(str3, paramString + ": sourceInsets must be top left bottom right");
      }
      else if (str2.equals("destinationInsets"))
      {
        localInsets2 = parseInsets(str3, paramString + ": destinationInsets must be top left bottom right");
      }
      else if (str2.equals("paintCenter"))
      {
        bool1 = str3.toLowerCase().equals("true");
        k = 1;
      }
      else if (str2.equals("stretch"))
      {
        bool2 = str3.toLowerCase().equals("true");
        j = 1;
      }
      else if (str2.equals("direction"))
      {
        str3 = str3.toUpperCase().intern();
        if (str3 == "EAST") {
          i = 3;
        } else if (str3 == "NORTH") {
          i = 1;
        } else if (str3 == "SOUTH") {
          i = 5;
        } else if (str3 == "WEST") {
          i = 7;
        } else if (str3 == "TOP") {
          i = 1;
        } else if (str3 == "LEFT") {
          i = 2;
        } else if (str3 == "BOTTOM") {
          i = 3;
        } else if (str3 == "RIGHT") {
          i = 4;
        } else if (str3 == "HORIZONTAL") {
          i = 0;
        } else if (str3 == "VERTICAL") {
          i = 1;
        } else if (str3 == "HORIZONTAL_SPLIT") {
          i = 1;
        } else if (str3 == "VERTICAL_SPLIT") {
          i = 0;
        } else {
          throw new SAXException(paramString + ": unknown direction");
        }
      }
      else if (str2.equals("center"))
      {
        bool3 = str3.toLowerCase().equals("true");
      }
    }
    if (localObject2 == null)
    {
      if (paramString == "painter") {
        throw new SAXException(paramString + ": you must specify an idref");
      }
      if ((localInsets1 == null) && (!bool3)) {
        throw new SAXException("property: you must specify sourceInsets");
      }
      if (localObject1 == null) {
        throw new SAXException("property: you must specify a path");
      }
      if ((bool3) && ((localInsets1 != null) || (localInsets2 != null) || (k != 0) || (j != 0))) {
        throw new SAXException("The attributes: sourceInsets, destinationInsets, paintCenter and stretch  are not legal when center is true");
      }
      localObject2 = new ImagePainter(!bool2, bool1, localInsets1, localInsets2, getResource((String)localObject1), bool3);
    }
    register((String)localObject3, localObject2);
    if (_stateInfo != null) {
      addPainterOrMerge(_statePainters, str1, (SynthPainter)localObject2, i);
    } else if (_style != null) {
      addPainterOrMerge(_stylePainters, str1, (SynthPainter)localObject2, i);
    }
  }
  
  private void addPainterOrMerge(List<ParsedSynthStyle.PainterInfo> paramList, String paramString, SynthPainter paramSynthPainter, int paramInt)
  {
    ParsedSynthStyle.PainterInfo localPainterInfo1 = new ParsedSynthStyle.PainterInfo(paramString, paramSynthPainter, paramInt);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      ParsedSynthStyle.PainterInfo localPainterInfo2 = (ParsedSynthStyle.PainterInfo)localObject;
      if (localPainterInfo1.equalsPainter(localPainterInfo2))
      {
        localPainterInfo2.addPainter(paramSynthPainter);
        return;
      }
    }
    paramList.add(localPainterInfo1);
  }
  
  private void startImageIcon(Attributes paramAttributes)
    throws SAXException
  {
    String str1 = null;
    String str2 = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
    {
      String str3 = paramAttributes.getQName(i);
      if (str3.equals("id")) {
        str2 = paramAttributes.getValue(i);
      } else if (str3.equals("path")) {
        str1 = paramAttributes.getValue(i);
      }
    }
    if (str1 == null) {
      throw new SAXException("imageIcon: you must specify a path");
    }
    register(str2, new LazyImageIcon(getResource(str1)));
  }
  
  private void startOpaque(Attributes paramAttributes)
  {
    if (_style != null)
    {
      _style.setOpaque(true);
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
      {
        String str = paramAttributes.getQName(i);
        if (str.equals("value")) {
          _style.setOpaque("true".equals(paramAttributes.getValue(i).toLowerCase()));
        }
      }
    }
  }
  
  private void startInputMap(Attributes paramAttributes)
    throws SAXException
  {
    _inputMapBindings.clear();
    _inputMapID = null;
    if (_style != null) {
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
      {
        String str = paramAttributes.getQName(i);
        if (str.equals("id")) {
          _inputMapID = paramAttributes.getValue(i);
        }
      }
    }
  }
  
  private void endInputMap()
    throws SAXException
  {
    if (_inputMapID != null) {
      register(_inputMapID, new UIDefaults.LazyInputMap(_inputMapBindings.toArray(new Object[_inputMapBindings.size()])));
    }
    _inputMapBindings.clear();
    _inputMapID = null;
  }
  
  private void startBindKey(Attributes paramAttributes)
    throws SAXException
  {
    if (_inputMapID == null) {
      return;
    }
    if (_style != null)
    {
      String str1 = null;
      String str2 = null;
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--)
      {
        String str3 = paramAttributes.getQName(i);
        if (str3.equals("key")) {
          str1 = paramAttributes.getValue(i);
        } else if (str3.equals("action")) {
          str2 = paramAttributes.getValue(i);
        }
      }
      if ((str1 == null) || (str2 == null)) {
        throw new SAXException("bindKey: you must supply a key and action");
      }
      _inputMapBindings.add(str1);
      _inputMapBindings.add(str2);
    }
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws IOException, SAXException
  {
    if (isForwarding()) {
      return getHandler().resolveEntity(paramString1, paramString2);
    }
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().notationDecl(paramString1, paramString2, paramString3);
    }
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (isForwarding()) {
      getHandler().setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().startDocument();
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().endDocument();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    paramString3 = paramString3.intern();
    if (paramString3 == "style")
    {
      startStyle(paramAttributes);
    }
    else if (paramString3 == "state")
    {
      startState(paramAttributes);
    }
    else if (paramString3 == "font")
    {
      startFont(paramAttributes);
    }
    else if (paramString3 == "color")
    {
      startColor(paramAttributes);
    }
    else if (paramString3 == "painter")
    {
      startPainter(paramAttributes, paramString3);
    }
    else if (paramString3 == "imagePainter")
    {
      startPainter(paramAttributes, paramString3);
    }
    else if (paramString3 == "property")
    {
      startProperty(paramAttributes, "property");
    }
    else if (paramString3 == "defaultsProperty")
    {
      startProperty(paramAttributes, "defaultsProperty");
    }
    else if (paramString3 == "graphicsUtils")
    {
      startGraphics(paramAttributes);
    }
    else if (paramString3 == "insets")
    {
      startInsets(paramAttributes);
    }
    else if (paramString3 == "bind")
    {
      startBind(paramAttributes);
    }
    else if (paramString3 == "bindKey")
    {
      startBindKey(paramAttributes);
    }
    else if (paramString3 == "imageIcon")
    {
      startImageIcon(paramAttributes);
    }
    else if (paramString3 == "opaque")
    {
      startOpaque(paramAttributes);
    }
    else if (paramString3 == "inputMap")
    {
      startInputMap(paramAttributes);
    }
    else if (paramString3 != "synth")
    {
      if (_depth++ == 0) {
        getHandler().startDocument();
      }
      getHandler().startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (isForwarding())
    {
      getHandler().endElement(paramString1, paramString2, paramString3);
      _depth -= 1;
      if (!isForwarding()) {
        getHandler().startDocument();
      }
    }
    else
    {
      paramString3 = paramString3.intern();
      if (paramString3 == "style") {
        endStyle();
      } else if (paramString3 == "state") {
        endState();
      } else if (paramString3 == "inputMap") {
        endInputMap();
      }
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().processingInstruction(paramString1, paramString2);
    }
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().warning(paramSAXParseException);
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().error(paramSAXParseException);
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (isForwarding()) {
      getHandler().fatalError(paramSAXParseException);
    }
    throw paramSAXParseException;
  }
  
  private static class LazyImageIcon
    extends ImageIcon
    implements UIResource
  {
    private URL location;
    
    public LazyImageIcon(URL paramURL)
    {
      location = paramURL;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (getImage() != null) {
        super.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
      }
    }
    
    public int getIconWidth()
    {
      if (getImage() != null) {
        return super.getIconWidth();
      }
      return 0;
    }
    
    public int getIconHeight()
    {
      if (getImage() != null) {
        return super.getIconHeight();
      }
      return 0;
    }
    
    public Image getImage()
    {
      if (location != null)
      {
        setImage(Toolkit.getDefaultToolkit().getImage(location));
        location = null;
      }
      return super.getImage();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */