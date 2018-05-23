package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

public final class TemplatesImpl
  implements Templates, Serializable
{
  static final long serialVersionUID = 673094361519270707L;
  public static final String DESERIALIZE_TRANSLET = "jdk.xml.enableTemplatesImplDeserialization";
  private static String ABSTRACT_TRANSLET = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
  private String _name = null;
  private byte[][] _bytecodes = (byte[][])null;
  private Class[] _class = null;
  private int _transletIndex = -1;
  private transient Map<String, Class<?>> _auxClasses = null;
  private Properties _outputProperties;
  private int _indentNumber;
  private transient URIResolver _uriResolver = null;
  private transient ThreadLocal _sdom = new ThreadLocal();
  private transient TransformerFactoryImpl _tfactory = null;
  private transient boolean _useServicesMechanism;
  private transient String _accessExternalStylesheet = "all";
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("_name", String.class), new ObjectStreamField("_bytecodes", byte[][].class), new ObjectStreamField("_class", Class[].class), new ObjectStreamField("_transletIndex", Integer.TYPE), new ObjectStreamField("_outputProperties", Properties.class), new ObjectStreamField("_indentNumber", Integer.TYPE) };
  
  protected TemplatesImpl(byte[][] paramArrayOfByte, String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    _bytecodes = paramArrayOfByte;
    init(paramString, paramProperties, paramInt, paramTransformerFactoryImpl);
  }
  
  protected TemplatesImpl(Class[] paramArrayOfClass, String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    _class = paramArrayOfClass;
    _transletIndex = 0;
    init(paramString, paramProperties, paramInt, paramTransformerFactoryImpl);
  }
  
  private void init(String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    _name = paramString;
    _outputProperties = paramProperties;
    _indentNumber = paramInt;
    _tfactory = paramTransformerFactoryImpl;
    _useServicesMechanism = paramTransformerFactoryImpl.useServicesMechnism();
    _accessExternalStylesheet = ((String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet"));
  }
  
  public TemplatesImpl() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject = SecuritySupport.getSystemProperty("jdk.xml.enableTemplatesImplDeserialization");
      if ((localObject == null) || ((((String)localObject).length() != 0) && (!((String)localObject).equalsIgnoreCase("true"))))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("DESERIALIZE_TEMPLATES_ERR");
        throw new UnsupportedOperationException(localErrorMsg.toString());
      }
    }
    Object localObject = paramObjectInputStream.readFields();
    _name = ((String)((ObjectInputStream.GetField)localObject).get("_name", null));
    _bytecodes = ((byte[][])((ObjectInputStream.GetField)localObject).get("_bytecodes", null));
    _class = ((Class[])((ObjectInputStream.GetField)localObject).get("_class", null));
    _transletIndex = ((ObjectInputStream.GetField)localObject).get("_transletIndex", -1);
    _outputProperties = ((Properties)((ObjectInputStream.GetField)localObject).get("_outputProperties", null));
    _indentNumber = ((ObjectInputStream.GetField)localObject).get("_indentNumber", 0);
    if (paramObjectInputStream.readBoolean()) {
      _uriResolver = ((URIResolver)paramObjectInputStream.readObject());
    }
    _tfactory = new TransformerFactoryImpl();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    if (_auxClasses != null) {
      throw new NotSerializableException("com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable");
    }
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("_name", _name);
    localPutField.put("_bytecodes", _bytecodes);
    localPutField.put("_class", _class);
    localPutField.put("_transletIndex", _transletIndex);
    localPutField.put("_outputProperties", _outputProperties);
    localPutField.put("_indentNumber", _indentNumber);
    paramObjectOutputStream.writeFields();
    if ((_uriResolver instanceof Serializable))
    {
      paramObjectOutputStream.writeBoolean(true);
      paramObjectOutputStream.writeObject((Serializable)_uriResolver);
    }
    else
    {
      paramObjectOutputStream.writeBoolean(false);
    }
  }
  
  public boolean useServicesMechnism()
  {
    return _useServicesMechanism;
  }
  
  public synchronized void setURIResolver(URIResolver paramURIResolver)
  {
    _uriResolver = paramURIResolver;
  }
  
  private synchronized void setTransletBytecodes(byte[][] paramArrayOfByte)
  {
    _bytecodes = paramArrayOfByte;
  }
  
  private synchronized byte[][] getTransletBytecodes()
  {
    return _bytecodes;
  }
  
  private synchronized Class[] getTransletClasses()
  {
    try
    {
      if (_class == null) {
        defineTransletClasses();
      }
    }
    catch (TransformerConfigurationException localTransformerConfigurationException) {}
    return _class;
  }
  
  public synchronized int getTransletIndex()
  {
    try
    {
      if (_class == null) {
        defineTransletClasses();
      }
    }
    catch (TransformerConfigurationException localTransformerConfigurationException) {}
    return _transletIndex;
  }
  
  protected synchronized void setTransletName(String paramString)
  {
    _name = paramString;
  }
  
  protected synchronized String getTransletName()
  {
    return _name;
  }
  
  private void defineTransletClasses()
    throws TransformerConfigurationException
  {
    if (_bytecodes == null)
    {
      localObject = new ErrorMsg("NO_TRANSLET_CLASS_ERR");
      throw new TransformerConfigurationException(((ErrorMsg)localObject).toString());
    }
    Object localObject = (TransletClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new TemplatesImpl.TransletClassLoader(ObjectFactory.findClassLoader(), _tfactory.getExternalExtensionsMap());
      }
    });
    try
    {
      int i = _bytecodes.length;
      _class = new Class[i];
      if (i > 1) {
        _auxClasses = new HashMap();
      }
      for (int j = 0; j < i; j++)
      {
        _class[j] = ((TransletClassLoader)localObject).defineClass(_bytecodes[j]);
        Class localClass = _class[j].getSuperclass();
        if (localClass.getName().equals(ABSTRACT_TRANSLET)) {
          _transletIndex = j;
        } else {
          _auxClasses.put(_class[j].getName(), _class[j]);
        }
      }
      if (_transletIndex < 0)
      {
        localErrorMsg = new ErrorMsg("NO_MAIN_TRANSLET_ERR", _name);
        throw new TransformerConfigurationException(localErrorMsg.toString());
      }
    }
    catch (ClassFormatError localClassFormatError)
    {
      localErrorMsg = new ErrorMsg("TRANSLET_CLASS_ERR", _name);
      throw new TransformerConfigurationException(localErrorMsg.toString());
    }
    catch (LinkageError localLinkageError)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", _name);
      throw new TransformerConfigurationException(localErrorMsg.toString());
    }
  }
  
  private Translet getTransletInstance()
    throws TransformerConfigurationException
  {
    try
    {
      if (_name == null) {
        return null;
      }
      if (_class == null) {
        defineTransletClasses();
      }
      AbstractTranslet localAbstractTranslet = (AbstractTranslet)_class[_transletIndex].newInstance();
      localAbstractTranslet.postInitialization();
      localAbstractTranslet.setTemplates(this);
      localAbstractTranslet.setServicesMechnism(_useServicesMechanism);
      localAbstractTranslet.setAllowedProtocols(_accessExternalStylesheet);
      if (_auxClasses != null) {
        localAbstractTranslet.setAuxiliaryClasses(_auxClasses);
      }
      return localAbstractTranslet;
    }
    catch (InstantiationException localInstantiationException)
    {
      localErrorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", _name);
      throw new TransformerConfigurationException(localErrorMsg.toString());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", _name);
      throw new TransformerConfigurationException(localErrorMsg.toString());
    }
  }
  
  public synchronized Transformer newTransformer()
    throws TransformerConfigurationException
  {
    TransformerImpl localTransformerImpl = new TransformerImpl(getTransletInstance(), _outputProperties, _indentNumber, _tfactory);
    if (_uriResolver != null) {
      localTransformerImpl.setURIResolver(_uriResolver);
    }
    if (_tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
      localTransformerImpl.setSecureProcessing(true);
    }
    return localTransformerImpl;
  }
  
  public synchronized Properties getOutputProperties()
  {
    try
    {
      return newTransformer().getOutputProperties();
    }
    catch (TransformerConfigurationException localTransformerConfigurationException) {}
    return null;
  }
  
  public DOM getStylesheetDOM()
  {
    return (DOM)_sdom.get();
  }
  
  public void setStylesheetDOM(DOM paramDOM)
  {
    _sdom.set(paramDOM);
  }
  
  static final class TransletClassLoader
    extends ClassLoader
  {
    private final Map<String, Class> _loadedExternalExtensionFunctions;
    
    TransletClassLoader(ClassLoader paramClassLoader)
    {
      super();
      _loadedExternalExtensionFunctions = null;
    }
    
    TransletClassLoader(ClassLoader paramClassLoader, Map<String, Class> paramMap)
    {
      super();
      _loadedExternalExtensionFunctions = paramMap;
    }
    
    public Class<?> loadClass(String paramString)
      throws ClassNotFoundException
    {
      Class localClass = null;
      if (_loadedExternalExtensionFunctions != null) {
        localClass = (Class)_loadedExternalExtensionFunctions.get(paramString);
      }
      if (localClass == null) {
        localClass = super.loadClass(paramString);
      }
      return localClass;
    }
    
    Class defineClass(byte[] paramArrayOfByte)
    {
      return defineClass(null, paramArrayOfByte, 0, paramArrayOfByte.length);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TemplatesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */