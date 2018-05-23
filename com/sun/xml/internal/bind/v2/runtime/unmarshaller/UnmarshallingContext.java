package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXParseException2;
import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class UnmarshallingContext
  extends Coordinator
  implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, XmlVisitor.TextPredictor
{
  private static final Logger logger;
  private final State root;
  private State current;
  private static final LocatorEx DUMMY_INSTANCE;
  @NotNull
  private LocatorEx locator = DUMMY_INSTANCE;
  private Object result;
  private JaxBeanInfo expectedType;
  private IDResolver idResolver;
  private boolean isUnmarshalInProgress = true;
  private boolean aborted = false;
  public final UnmarshallerImpl parent;
  private final AssociationMap assoc;
  private boolean isInplaceMode;
  private InfosetScanner scanner;
  private Object currentElement;
  private NamespaceContext environmentNamespaceContext;
  @Nullable
  public ClassResolver classResolver;
  @Nullable
  public ClassLoader classLoader;
  private static volatile int errorsCounter = 10;
  private final Map<Class, Factory> factories = new HashMap();
  private Patcher[] patchers = null;
  private int patchersLen = 0;
  private String[] nsBind = new String[16];
  private int nsLen = 0;
  private Scope[] scopes = new Scope[16];
  private int scopeTop = 0;
  private static final Loader DEFAULT_ROOT_LOADER = new DefaultRootLoader(null);
  private static final Loader EXPECTED_TYPE_ROOT_LOADER = new ExpectedTypeRootLoader(null);
  
  public UnmarshallingContext(UnmarshallerImpl paramUnmarshallerImpl, AssociationMap paramAssociationMap)
  {
    for (int i = 0; i < scopes.length; i++) {
      scopes[i] = new Scope(this);
    }
    parent = paramUnmarshallerImpl;
    assoc = paramAssociationMap;
    root = (current = new State(null, null));
  }
  
  public void reset(InfosetScanner paramInfosetScanner, boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo, IDResolver paramIDResolver)
  {
    scanner = paramInfosetScanner;
    isInplaceMode = paramBoolean;
    expectedType = paramJaxBeanInfo;
    idResolver = paramIDResolver;
  }
  
  public JAXBContextImpl getJAXBContext()
  {
    return parent.context;
  }
  
  public State getCurrentState()
  {
    return current;
  }
  
  public Loader selectRootLoader(State paramState, TagName paramTagName)
    throws SAXException
  {
    try
    {
      Loader localLoader = getJAXBContext().selectRootLoader(paramState, paramTagName);
      if (localLoader != null) {
        return localLoader;
      }
      if (classResolver != null)
      {
        Class localClass = classResolver.resolveElementName(uri, local);
        if (localClass != null)
        {
          JAXBContextImpl localJAXBContextImpl = getJAXBContext().createAugmented(localClass);
          JaxBeanInfo localJaxBeanInfo = localJAXBContextImpl.getBeanInfo(localClass);
          return localJaxBeanInfo.getLoader(localJAXBContextImpl, true);
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      handleError(localException);
    }
    return null;
  }
  
  public void clearStates()
  {
    for (State localState = current; next != null; localState = next) {}
    while (prev != null)
    {
      loader = null;
      nil = false;
      receiver = null;
      intercepter = null;
      elementDefaultValue = null;
      target = null;
      localState = prev;
      next.prev = null;
      next = null;
    }
    current = localState;
  }
  
  public void setFactories(Object paramObject)
  {
    factories.clear();
    if (paramObject == null) {
      return;
    }
    if ((paramObject instanceof Object[])) {
      for (Object localObject : (Object[])paramObject) {
        addFactory(localObject);
      }
    } else {
      addFactory(paramObject);
    }
  }
  
  private void addFactory(Object paramObject)
  {
    for (Method localMethod : paramObject.getClass().getMethods()) {
      if ((localMethod.getName().startsWith("create")) && (localMethod.getParameterTypes().length <= 0))
      {
        Class localClass = localMethod.getReturnType();
        factories.put(localClass, new Factory(paramObject, localMethod));
      }
    }
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext)
    throws SAXException
  {
    if (paramLocatorEx != null) {
      locator = paramLocatorEx;
    }
    environmentNamespaceContext = paramNamespaceContext;
    result = null;
    current = root;
    patchersLen = 0;
    aborted = false;
    isUnmarshalInProgress = true;
    nsLen = 0;
    if (expectedType != null) {
      root.loader = EXPECTED_TYPE_ROOT_LOADER;
    } else {
      root.loader = DEFAULT_ROOT_LOADER;
    }
    idResolver.startDocument(this);
  }
  
  /* Error */
  public void startElement(TagName paramTagName)
    throws SAXException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 662	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:pushCoordinator	()V
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 666	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:_startElement	(Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/TagName;)V
    //   9: aload_0
    //   10: invokevirtual 661	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:popCoordinator	()V
    //   13: goto +10 -> 23
    //   16: astore_2
    //   17: aload_0
    //   18: invokevirtual 661	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:popCoordinator	()V
    //   21: aload_2
    //   22: athrow
    //   23: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	UnmarshallingContext
    //   0	24	1	paramTagName	TagName
    //   16	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	9	16	finally
  }
  
  private void _startElement(TagName paramTagName)
    throws SAXException
  {
    if (assoc != null) {
      currentElement = scanner.getCurrentElement();
    }
    Loader localLoader = current.loader;
    current.push();
    localLoader.childElement(current, paramTagName);
    assert (current.loader != null);
    current.loader.startElement(current, paramTagName);
  }
  
  /* Error */
  public void text(CharSequence paramCharSequence)
    throws SAXException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 662	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:pushCoordinator	()V
    //   4: aload_0
    //   5: getfield 621	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:current	Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;
    //   8: invokestatic 691	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State:access$1000	(Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;)Ljava/lang/String;
    //   11: ifnull +20 -> 31
    //   14: aload_1
    //   15: invokeinterface 738 1 0
    //   20: ifne +11 -> 31
    //   23: aload_0
    //   24: getfield 621	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:current	Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;
    //   27: invokestatic 691	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State:access$1000	(Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;)Ljava/lang/String;
    //   30: astore_1
    //   31: aload_0
    //   32: getfield 621	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:current	Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;
    //   35: invokestatic 686	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State:access$600	(Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;)Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/Loader;
    //   38: aload_0
    //   39: getfield 621	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:current	Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;
    //   42: aload_1
    //   43: invokevirtual 654	com/sun/xml/internal/bind/v2/runtime/unmarshaller/Loader:text	(Lcom/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext$State;Ljava/lang/CharSequence;)V
    //   46: aload_0
    //   47: invokevirtual 661	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:popCoordinator	()V
    //   50: goto +10 -> 60
    //   53: astore_2
    //   54: aload_0
    //   55: invokevirtual 661	com/sun/xml/internal/bind/v2/runtime/unmarshaller/UnmarshallingContext:popCoordinator	()V
    //   58: aload_2
    //   59: athrow
    //   60: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	UnmarshallingContext
    //   0	61	1	paramCharSequence	CharSequence
    //   53	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	46	53	finally
  }
  
  public final void endElement(TagName paramTagName)
    throws SAXException
  {
    pushCoordinator();
    try
    {
      State localState = current;
      loader.leaveElement(localState, paramTagName);
      Object localObject1 = target;
      Receiver localReceiver = receiver;
      Intercepter localIntercepter = intercepter;
      localState.pop();
      if (localIntercepter != null) {
        localObject1 = localIntercepter.intercept(current, localObject1);
      }
      if (localReceiver != null) {
        localReceiver.receive(current, localObject1);
      }
    }
    finally
    {
      popCoordinator();
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    runPatchers();
    idResolver.endDocument();
    isUnmarshalInProgress = false;
    currentElement = null;
    locator = DUMMY_INSTANCE;
    environmentNamespaceContext = null;
    assert (root == current);
  }
  
  @Deprecated
  public boolean expectText()
  {
    return current.loader.expectText;
  }
  
  @Deprecated
  public XmlVisitor.TextPredictor getPredictor()
  {
    return this;
  }
  
  public UnmarshallingContext getContext()
  {
    return this;
  }
  
  public Object getResult()
    throws UnmarshalException
  {
    if (isUnmarshalInProgress) {
      throw new IllegalStateException();
    }
    if (!aborted) {
      return result;
    }
    throw new UnmarshalException((String)null);
  }
  
  void clearResult()
  {
    if (isUnmarshalInProgress) {
      throw new IllegalStateException();
    }
    result = null;
  }
  
  public Object createInstance(Class<?> paramClass)
    throws SAXException
  {
    if (!factories.isEmpty())
    {
      Factory localFactory = (Factory)factories.get(paramClass);
      if (localFactory != null) {
        return localFactory.createInstance();
      }
    }
    return ClassFactory.create(paramClass);
  }
  
  public Object createInstance(JaxBeanInfo paramJaxBeanInfo)
    throws SAXException
  {
    if (!factories.isEmpty())
    {
      Factory localFactory = (Factory)factories.get(jaxbType);
      if (localFactory != null) {
        return localFactory.createInstance();
      }
    }
    try
    {
      return paramJaxBeanInfo.createInstance(this);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Loader.reportError("Unable to create an instance of " + jaxbType.getName(), localIllegalAccessException, false);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Loader.reportError("Unable to create an instance of " + jaxbType.getName(), localInvocationTargetException, false);
    }
    catch (InstantiationException localInstantiationException)
    {
      Loader.reportError("Unable to create an instance of " + jaxbType.getName(), localInstantiationException, false);
    }
    return null;
  }
  
  public void handleEvent(ValidationEvent paramValidationEvent, boolean paramBoolean)
    throws SAXException
  {
    ValidationEventHandler localValidationEventHandler = parent.getEventHandler();
    boolean bool = localValidationEventHandler.handleEvent(paramValidationEvent);
    if (!bool) {
      aborted = true;
    }
    if ((!paramBoolean) || (!bool)) {
      throw new SAXParseException2(paramValidationEvent.getMessage(), locator, new UnmarshalException(paramValidationEvent.getMessage(), paramValidationEvent.getLinkedException()));
    }
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    try
    {
      boolean bool = parent.getEventHandler().handleEvent(paramValidationEvent);
      if (!bool) {
        aborted = true;
      }
      return bool;
    }
    catch (RuntimeException localRuntimeException) {}
    return false;
  }
  
  public void handleError(Exception paramException)
    throws SAXException
  {
    handleError(paramException, true);
  }
  
  public void handleError(Exception paramException, boolean paramBoolean)
    throws SAXException
  {
    handleEvent(new ValidationEventImpl(1, paramException.getMessage(), locator.getLocation(), paramException), paramBoolean);
  }
  
  public void handleError(String paramString)
  {
    handleEvent(new ValidationEventImpl(1, paramString, locator.getLocation()));
  }
  
  protected ValidationEventLocator getLocation()
  {
    return locator.getLocation();
  }
  
  public LocatorEx getLocator()
  {
    return locator;
  }
  
  public void errorUnresolvedIDREF(Object paramObject, String paramString, LocatorEx paramLocatorEx)
    throws SAXException
  {
    handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(new Object[] { paramString }), paramLocatorEx.getLocation()), true);
  }
  
  public void addPatcher(Patcher paramPatcher)
  {
    if (patchers == null) {
      patchers = new Patcher[32];
    }
    if (patchers.length == patchersLen)
    {
      Patcher[] arrayOfPatcher = new Patcher[patchersLen * 2];
      System.arraycopy(patchers, 0, arrayOfPatcher, 0, patchersLen);
      patchers = arrayOfPatcher;
    }
    patchers[(patchersLen++)] = paramPatcher;
  }
  
  private void runPatchers()
    throws SAXException
  {
    if (patchers != null) {
      for (int i = 0; i < patchersLen; i++)
      {
        patchers[i].run();
        patchers[i] = null;
      }
    }
  }
  
  public String addToIdTable(String paramString)
    throws SAXException
  {
    Object localObject = current.target;
    if (localObject == null) {
      localObject = access$500current).target;
    }
    idResolver.bind(paramString, localObject);
    return paramString;
  }
  
  public Callable getObjectFromId(String paramString, Class paramClass)
    throws SAXException
  {
    return idResolver.resolve(paramString, paramClass);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    if (nsBind.length == nsLen)
    {
      String[] arrayOfString = new String[nsLen * 2];
      System.arraycopy(nsBind, 0, arrayOfString, 0, nsLen);
      nsBind = arrayOfString;
    }
    nsBind[(nsLen++)] = paramString1;
    nsBind[(nsLen++)] = paramString2;
  }
  
  public void endPrefixMapping(String paramString)
  {
    nsLen -= 2;
  }
  
  private String resolveNamespacePrefix(String paramString)
  {
    if (paramString.equals("xml")) {
      return "http://www.w3.org/XML/1998/namespace";
    }
    for (int i = nsLen - 2; i >= 0; i -= 2) {
      if (paramString.equals(nsBind[i])) {
        return nsBind[(i + 1)];
      }
    }
    if (environmentNamespaceContext != null) {
      return environmentNamespaceContext.getNamespaceURI(paramString.intern());
    }
    if (paramString.equals("")) {
      return "";
    }
    return null;
  }
  
  public String[] getNewlyDeclaredPrefixes()
  {
    return getPrefixList(access$500current).numNsDecl);
  }
  
  public String[] getAllDeclaredPrefixes()
  {
    return getPrefixList(0);
  }
  
  private String[] getPrefixList(int paramInt)
  {
    int i = (current.numNsDecl - paramInt) / 2;
    String[] arrayOfString = new String[i];
    for (int j = 0; j < arrayOfString.length; j++) {
      arrayOfString[j] = nsBind[(paramInt + j * 2)];
    }
    return arrayOfString;
  }
  
  public Iterator<String> getPrefixes(String paramString)
  {
    return Collections.unmodifiableList(getAllPrefixesInList(paramString)).iterator();
  }
  
  private List<String> getAllPrefixesInList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if (paramString.equals("http://www.w3.org/XML/1998/namespace"))
    {
      localArrayList.add("xml");
      return localArrayList;
    }
    if (paramString.equals("http://www.w3.org/2000/xmlns/"))
    {
      localArrayList.add("xmlns");
      return localArrayList;
    }
    for (int i = nsLen - 2; i >= 0; i -= 2) {
      if ((paramString.equals(nsBind[(i + 1)])) && (getNamespaceURI(nsBind[i]).equals(nsBind[(i + 1)]))) {
        localArrayList.add(nsBind[i]);
      }
    }
    return localArrayList;
  }
  
  public String getPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if (paramString.equals("http://www.w3.org/XML/1998/namespace")) {
      return "xml";
    }
    if (paramString.equals("http://www.w3.org/2000/xmlns/")) {
      return "xmlns";
    }
    for (int i = nsLen - 2; i >= 0; i -= 2) {
      if ((paramString.equals(nsBind[(i + 1)])) && (getNamespaceURI(nsBind[i]).equals(nsBind[(i + 1)]))) {
        return nsBind[i];
      }
    }
    if (environmentNamespaceContext != null) {
      return environmentNamespaceContext.getPrefix(paramString);
    }
    return null;
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if (paramString.equals("xmlns")) {
      return "http://www.w3.org/2000/xmlns/";
    }
    return resolveNamespacePrefix(paramString);
  }
  
  public void startScope(int paramInt)
  {
    scopeTop += paramInt;
    if (scopeTop >= scopes.length)
    {
      Scope[] arrayOfScope = new Scope[Math.max(scopeTop + 1, scopes.length * 2)];
      System.arraycopy(scopes, 0, arrayOfScope, 0, scopes.length);
      for (int i = scopes.length; i < arrayOfScope.length; i++) {
        arrayOfScope[i] = new Scope(this);
      }
      scopes = arrayOfScope;
    }
  }
  
  public void endScope(int paramInt)
    throws SAXException
  {
    try
    {
      while (paramInt > 0)
      {
        scopes[scopeTop].finish();
        paramInt--;
        scopeTop -= 1;
      }
    }
    catch (AccessorException localAccessorException)
    {
      handleError(localAccessorException);
      while (paramInt > 0)
      {
        scopes[(scopeTop--)] = new Scope(this);
        paramInt--;
      }
    }
  }
  
  public Scope getScope(int paramInt)
  {
    return scopes[(scopeTop - paramInt)];
  }
  
  public void recordInnerPeer(Object paramObject)
  {
    if (assoc != null) {
      assoc.addInner(currentElement, paramObject);
    }
  }
  
  public Object getInnerPeer()
  {
    if ((assoc != null) && (isInplaceMode)) {
      return assoc.getInnerPeer(currentElement);
    }
    return null;
  }
  
  public void recordOuterPeer(Object paramObject)
  {
    if (assoc != null) {
      assoc.addOuter(currentElement, paramObject);
    }
  }
  
  public Object getOuterPeer()
  {
    if ((assoc != null) && (isInplaceMode)) {
      return assoc.getOuterPeer(currentElement);
    }
    return null;
  }
  
  public String getXMIMEContentType()
  {
    Object localObject = current.target;
    if (localObject == null) {
      return null;
    }
    return getJAXBContext().getXMIMEContentType(localObject);
  }
  
  public static UnmarshallingContext getInstance()
  {
    return (UnmarshallingContext)Coordinator._getInstance();
  }
  
  public Collection<QName> getCurrentExpectedElements()
  {
    pushCoordinator();
    try
    {
      State localState = getCurrentState();
      Loader localLoader = loader;
      Collection<QName> localCollection = localLoader != null ? localLoader.getExpectedChildElements() : null;
      return localCollection;
    }
    finally
    {
      popCoordinator();
    }
  }
  
  public Collection<QName> getCurrentExpectedAttributes()
  {
    pushCoordinator();
    try
    {
      State localState = getCurrentState();
      Loader localLoader = loader;
      Collection<QName> localCollection = localLoader != null ? localLoader.getExpectedAttributes() : null;
      return localCollection;
    }
    finally
    {
      popCoordinator();
    }
  }
  
  public StructureLoader getStructureLoader()
  {
    if ((current.loader instanceof StructureLoader)) {
      return (StructureLoader)current.loader;
    }
    return null;
  }
  
  public boolean shouldErrorBeReported()
    throws SAXException
  {
    if (logger.isLoggable(Level.FINEST)) {
      return true;
    }
    if (errorsCounter >= 0)
    {
      errorsCounter -= 1;
      if (errorsCounter == 0) {
        handleEvent(new ValidationEventImpl(0, Messages.ERRORS_LIMIT_EXCEEDED.format(new Object[0]), getLocator().getLocation(), null), true);
      }
    }
    return errorsCounter >= 0;
  }
  
  static
  {
    logger = Logger.getLogger(UnmarshallingContext.class.getName());
    LocatorImpl localLocatorImpl = new LocatorImpl();
    localLocatorImpl.setPublicId(null);
    localLocatorImpl.setSystemId(null);
    localLocatorImpl.setLineNumber(-1);
    localLocatorImpl.setColumnNumber(-1);
    DUMMY_INSTANCE = new LocatorExWrapper(localLocatorImpl);
  }
  
  private static final class DefaultRootLoader
    extends Loader
    implements Receiver
  {
    private DefaultRootLoader() {}
    
    public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      Loader localLoader = paramState.getContext().selectRootLoader(paramState, paramTagName);
      if (localLoader != null)
      {
        UnmarshallingContext.State.access$602(paramState, localLoader);
        UnmarshallingContext.State.access$802(paramState, this);
        return;
      }
      JaxBeanInfo localJaxBeanInfo = XsiTypeLoader.parseXsiType(paramState, paramTagName, null);
      if (localJaxBeanInfo == null)
      {
        reportUnexpectedChildElement(paramTagName, false);
        return;
      }
      UnmarshallingContext.State.access$602(paramState, localJaxBeanInfo.getLoader(null, false));
      UnmarshallingContext.State.access$1702(UnmarshallingContext.State.access$500(paramState), new JAXBElement(paramTagName.createQName(), Object.class, null));
      UnmarshallingContext.State.access$802(paramState, this);
    }
    
    public Collection<QName> getExpectedChildElements()
    {
      return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames();
    }
    
    public void receive(UnmarshallingContext.State paramState, Object paramObject)
    {
      if (UnmarshallingContext.State.access$1700(paramState) != null)
      {
        ((JAXBElement)UnmarshallingContext.State.access$1700(paramState)).setValue(paramObject);
        paramObject = UnmarshallingContext.State.access$1700(paramState);
      }
      if (UnmarshallingContext.State.access$700(paramState)) {
        ((JAXBElement)paramObject).setNil(true);
      }
      getContextresult = paramObject;
    }
  }
  
  private static final class ExpectedTypeRootLoader
    extends Loader
    implements Receiver
  {
    private ExpectedTypeRootLoader() {}
    
    public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    {
      UnmarshallingContext localUnmarshallingContext = paramState.getContext();
      QName localQName = new QName(uri, local);
      UnmarshallingContext.State.access$1102(UnmarshallingContext.State.access$500(paramState), new JAXBElement(localQName, expectedType.jaxbType, null, null));
      UnmarshallingContext.State.access$802(paramState, this);
      UnmarshallingContext.State.access$602(paramState, new XsiNilLoader(expectedType.getLoader(null, true)));
    }
    
    public void receive(UnmarshallingContext.State paramState, Object paramObject)
    {
      JAXBElement localJAXBElement = (JAXBElement)UnmarshallingContext.State.access$1100(paramState);
      localJAXBElement.setValue(paramObject);
      paramState.getContext().recordOuterPeer(localJAXBElement);
      getContextresult = localJAXBElement;
    }
  }
  
  private static class Factory
  {
    private final Object factorInstance;
    private final Method method;
    
    public Factory(Object paramObject, Method paramMethod)
    {
      factorInstance = paramObject;
      method = paramMethod;
    }
    
    public Object createInstance()
      throws SAXException
    {
      try
      {
        return method.invoke(factorInstance, new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        UnmarshallingContext.getInstance().handleError(localIllegalAccessException, false);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        UnmarshallingContext.getInstance().handleError(localInvocationTargetException, false);
      }
      return null;
    }
  }
  
  public final class State
  {
    private Loader loader;
    private Receiver receiver;
    private Intercepter intercepter;
    private Object target;
    private Object backup;
    private int numNsDecl;
    private String elementDefaultValue;
    private State prev;
    private State next;
    private boolean nil = false;
    private boolean mixed = false;
    
    public UnmarshallingContext getContext()
    {
      return UnmarshallingContext.this;
    }
    
    private State(State paramState)
    {
      prev = paramState;
      if (paramState != null)
      {
        next = this;
        if (mixed) {
          mixed = true;
        }
      }
    }
    
    private void push()
    {
      if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
        UnmarshallingContext.logger.log(Level.FINEST, "State.push");
      }
      if (next == null)
      {
        assert (current == this);
        next = new State(UnmarshallingContext.this, this);
      }
      nil = false;
      State localState = next;
      numNsDecl = nsLen;
      current = localState;
    }
    
    private void pop()
    {
      if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
        UnmarshallingContext.logger.log(Level.FINEST, "State.pop");
      }
      assert (prev != null);
      loader = null;
      nil = false;
      mixed = false;
      receiver = null;
      intercepter = null;
      elementDefaultValue = null;
      target = null;
      current = prev;
      next = null;
    }
    
    public boolean isMixed()
    {
      return mixed;
    }
    
    public Object getTarget()
    {
      return target;
    }
    
    public void setLoader(Loader paramLoader)
    {
      if ((paramLoader instanceof StructureLoader)) {
        mixed = (!((StructureLoader)paramLoader).getBeanInfo().hasElementOnlyContentModel());
      }
      loader = paramLoader;
    }
    
    public void setReceiver(Receiver paramReceiver)
    {
      receiver = paramReceiver;
    }
    
    public State getPrev()
    {
      return prev;
    }
    
    public void setIntercepter(Intercepter paramIntercepter)
    {
      intercepter = paramIntercepter;
    }
    
    public void setBackup(Object paramObject)
    {
      backup = paramObject;
    }
    
    public void setTarget(Object paramObject)
    {
      target = paramObject;
    }
    
    public Object getBackup()
    {
      return backup;
    }
    
    public boolean isNil()
    {
      return nil;
    }
    
    public void setNil(boolean paramBoolean)
    {
      nil = paramBoolean;
    }
    
    public Loader getLoader()
    {
      return loader;
    }
    
    public String getElementDefaultValue()
    {
      return elementDefaultValue;
    }
    
    public void setElementDefaultValue(String paramString)
    {
      elementDefaultValue = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\UnmarshallingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */