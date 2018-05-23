package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.transport.DefaultIORToSocketInfoImpl;
import com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserData;
import com.sun.corba.se.spi.orb.ParserDataFactory;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.spi.transport.ReadTimeoutsFactory;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public class ParserTable
{
  private static String MY_CLASS_NAME = ParserTable.class.getName();
  private static ParserTable myInstance = new ParserTable();
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");
  private ParserData[] parserData;
  
  public static ParserTable get()
  {
    return myInstance;
  }
  
  public ParserData[] getParserData()
  {
    ParserData[] arrayOfParserData = new ParserData[parserData.length];
    System.arraycopy(parserData, 0, arrayOfParserData, 0, parserData.length);
    return arrayOfParserData;
  }
  
  private ParserTable()
  {
    String str1 = "65537,65801,65568";
    String[] arrayOfString = { "subcontract", "poa", "transport" };
    USLPort[] arrayOfUSLPort = { new USLPort("FOO", 2701), new USLPort("BAR", 3333) };
    ReadTimeouts localReadTimeouts = TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20);
    ORBInitializer[] arrayOfORBInitializer = { null, new TestORBInitializer1(), new TestORBInitializer2() };
    StringPair[] arrayOfStringPair1 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer2", "dummy") };
    Acceptor[] arrayOfAcceptor = { new TestAcceptor2(), new TestAcceptor1(), null };
    StringPair[] arrayOfStringPair2 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor2", "dummy") };
    StringPair[] arrayOfStringPair3 = { new StringPair("Foo", "ior:930492049394"), new StringPair("Bar", "ior:3453465785633576") };
    URL localURL = null;
    String str2 = "corbaloc::camelot/NameService";
    try
    {
      localURL = new URL(str2);
    }
    catch (Exception localException) {}
    ParserData[] arrayOfParserData = { ParserDataFactory.make("com.sun.CORBA.ORBDebug", OperationFactory.listAction(",", OperationFactory.stringAction()), "debugFlags", new String[0], arrayOfString, "subcontract,poa,transport"), ParserDataFactory.make("org.omg.CORBA.ORBInitialHost", OperationFactory.stringAction(), "ORBInitialHost", "", "Foo", "Foo"), ParserDataFactory.make("org.omg.CORBA.ORBInitialPort", OperationFactory.integerAction(), "ORBInitialPort", new Integer(900), new Integer(27314), "27314"), ParserDataFactory.make("com.sun.CORBA.ORBServerHost", OperationFactory.stringAction(), "ORBServerHost", "", "camelot", "camelot"), ParserDataFactory.make("com.sun.CORBA.ORBServerPort", OperationFactory.integerAction(), "ORBServerPort", new Integer(0), new Integer(38143), "38143"), ParserDataFactory.make("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", OperationFactory.stringAction(), "listenOnAllInterfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBId", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.ORBid", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(-1), new Integer(1234), "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("com.sun.CORBA.connection.ORBHighWaterMark", OperationFactory.integerAction(), "highWaterMark", new Integer(240), new Integer(3745), "3745"), ParserDataFactory.make("com.sun.CORBA.connection.ORBLowWaterMark", OperationFactory.integerAction(), "lowWaterMark", new Integer(100), new Integer(12), "12"), ParserDataFactory.make("com.sun.CORBA.connection.ORBNumberToReclaim", OperationFactory.integerAction(), "numberToReclaim", new Integer(5), new Integer(231), "231"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOPVersion", makeGVOperation(), "giopVersion", GIOPVersion.DEFAULT_VERSION, new GIOPVersion(2, 3), "2.3"), ParserDataFactory.make("com.sun.CORBA.giop.ORBFragmentSize", makeFSOperation(), "giopFragmentSize", new Integer(1024), new Integer(65536), "65536"), ParserDataFactory.make("com.sun.CORBA.giop.ORBBufferSize", OperationFactory.integerAction(), "giopBufferSize", new Integer(1024), new Integer(234000), "234000"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP11BuffMgr", makeBMGROperation(), "giop11BuffMgr", new Integer(0), new Integer(1), "CLCT"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP12BuffMgr", makeBMGROperation(), "giop12BuffMgr", new Integer(2), new Integer(0), "GROW"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), OperationFactory.convertIntegerToShort()), "giopTargetAddressPreference", new Short(3), new Short(2), "2"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", makeADOperation(), "giopAddressDisposition", new Short(0), new Short(2), "2"), ParserDataFactory.make("com.sun.CORBA.codeset.AlwaysSendCodeSetCtx", OperationFactory.booleanAction(), "alwaysSendCodeSetCtx", Boolean.TRUE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkers", OperationFactory.booleanAction(), "useByteOrderMarkers", Boolean.valueOf(true), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps", OperationFactory.booleanAction(), "useByteOrderMarkersInEncaps", Boolean.valueOf(false), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.charsets", makeCSOperation(), "charData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.codeset.wcharsets", makeCSOperation(), "wcharData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.ORBAllowLocalOptimization", OperationFactory.booleanAction(), "allowLocalOptimization", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.legacy.connection.ORBSocketFactoryClass", makeLegacySocketFactoryOperation(), "legacySocketFactory", null, new TestLegacyORBSocketFactory(), MY_CLASS_NAME + "$TestLegacyORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBSocketFactoryClass", makeSocketFactoryOperation(), "socketFactory", new DefaultSocketFactoryImpl(), new TestORBSocketFactory(), MY_CLASS_NAME + "$TestORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBListenSocket", makeUSLOperation(), "userSpecifiedListenPorts", new USLPort[0], arrayOfUSLPort, "FOO:2701,BAR:3333"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIORToSocketInfoClass", makeIORToSocketInfoOperation(), "iorToSocketInfo", new DefaultIORToSocketInfoImpl(), new TestIORToSocketInfo(), MY_CLASS_NAME + "$TestIORToSocketInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass", makeIIOPPrimaryToContactInfoOperation(), "iiopPrimaryToContactInfo", null, new TestIIOPPrimaryToContactInfo(), MY_CLASS_NAME + "$TestIIOPPrimaryToContactInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBContactInfoList", makeContactInfoListFactoryOperation(), "corbaContactInfoListFactory", null, new TestContactInfoListFactory(), MY_CLASS_NAME + "$TestContactInfoListFactory"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.integerAction(), "persistentServerPort", new Integer(0), new Integer(2743), "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.setFlagAction(), "persistentPortInitialized", Boolean.FALSE, Boolean.TRUE, "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(0), new Integer(294), "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBActivated", OperationFactory.booleanAction(), "serverIsORBActivated", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.POA.ORBBadServerIdHandlerClass", OperationFactory.classAction(), "badServerIdHandlerClass", null, TestBadServerIdHandler.class, MY_CLASS_NAME + "$TestBadServerIdHandler"), ParserDataFactory.make("org.omg.PortableInterceptor.ORBInitializerClass.", makeROIOperation(), "orbInitializers", new ORBInitializer[0], arrayOfORBInitializer, arrayOfStringPair1, ORBInitializer.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptor", makeAcceptorInstantiationOperation(), "acceptors", new Acceptor[0], arrayOfAcceptor, arrayOfStringPair2, Acceptor.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketType", OperationFactory.stringAction(), "acceptorSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "acceptorSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "acceptorSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketType", OperationFactory.stringAction(), "connectionSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "connectionSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "connectionSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBDisableDirectByteBufferUse", OperationFactory.booleanAction(), "disableDirectByteBufferUse", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBTCPReadTimeouts", makeTTCPRTOperation(), "readTimeouts", TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20), localReadTimeouts, "100:3000:300:20"), ParserDataFactory.make("com.sun.CORBA.encoding.ORBEnableJavaSerialization", OperationFactory.booleanAction(), "enableJavaSerialization", Boolean.FALSE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.ORBUseRepId", OperationFactory.booleanAction(), "useRepId", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("org.omg.CORBA.ORBInitRef", OperationFactory.identityAction(), "orbInitialReferences", new StringPair[0], arrayOfStringPair3, arrayOfStringPair3, StringPair.class) };
    parserData = arrayOfParserData;
  }
  
  private Operation makeTTCPRTOperation()
  {
    Operation[] arrayOfOperation = { OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction() };
    Operation localOperation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
    Operation local1 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        Object[] arrayOfObject = (Object[])paramAnonymousObject;
        Integer localInteger1 = (Integer)arrayOfObject[0];
        Integer localInteger2 = (Integer)arrayOfObject[1];
        Integer localInteger3 = (Integer)arrayOfObject[2];
        Integer localInteger4 = (Integer)arrayOfObject[3];
        return TransportDefault.makeReadTimeoutsFactory().create(localInteger1.intValue(), localInteger2.intValue(), localInteger3.intValue(), localInteger4.intValue());
      }
    };
    Operation localOperation2 = OperationFactory.compose(localOperation1, local1);
    return localOperation2;
  }
  
  private Operation makeUSLOperation()
  {
    Operation[] arrayOfOperation = { OperationFactory.stringAction(), OperationFactory.integerAction() };
    Operation localOperation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
    Operation local2 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        Object[] arrayOfObject = (Object[])paramAnonymousObject;
        String str = (String)arrayOfObject[0];
        Integer localInteger = (Integer)arrayOfObject[1];
        return new USLPort(str, localInteger.intValue());
      }
    };
    Operation localOperation2 = OperationFactory.compose(localOperation1, local2);
    Operation localOperation3 = OperationFactory.listAction(",", localOperation2);
    return localOperation3;
  }
  
  private Operation makeMapOperation(final Map paramMap)
  {
    new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        return paramMap.get(paramAnonymousObject);
      }
    };
  }
  
  private Operation makeBMGROperation()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("GROW", new Integer(0));
    localHashMap.put("CLCT", new Integer(1));
    localHashMap.put("STRM", new Integer(2));
    return makeMapOperation(localHashMap);
  }
  
  private Operation makeLegacySocketFactoryOperation()
  {
    Operation local4 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          if (com.sun.corba.se.spi.legacy.connection.ORBSocketFactory.class.isAssignableFrom(localClass)) {
            return localClass.newInstance();
          }
          throw wrapper.illegalSocketFactoryType(localClass.toString());
        }
        catch (Exception localException)
        {
          throw wrapper.badCustomSocketFactory(localException, str);
        }
      }
    };
    return local4;
  }
  
  private Operation makeSocketFactoryOperation()
  {
    Operation local5 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          if (com.sun.corba.se.spi.transport.ORBSocketFactory.class.isAssignableFrom(localClass)) {
            return localClass.newInstance();
          }
          throw wrapper.illegalSocketFactoryType(localClass.toString());
        }
        catch (Exception localException)
        {
          throw wrapper.badCustomSocketFactory(localException, str);
        }
      }
    };
    return local5;
  }
  
  private Operation makeIORToSocketInfoOperation()
  {
    Operation local6 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          if (IORToSocketInfo.class.isAssignableFrom(localClass)) {
            return localClass.newInstance();
          }
          throw wrapper.illegalIorToSocketInfoType(localClass.toString());
        }
        catch (Exception localException)
        {
          throw wrapper.badCustomIorToSocketInfo(localException, str);
        }
      }
    };
    return local6;
  }
  
  private Operation makeIIOPPrimaryToContactInfoOperation()
  {
    Operation local7 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          if (IIOPPrimaryToContactInfo.class.isAssignableFrom(localClass)) {
            return localClass.newInstance();
          }
          throw wrapper.illegalIiopPrimaryToContactInfoType(localClass.toString());
        }
        catch (Exception localException)
        {
          throw wrapper.badCustomIiopPrimaryToContactInfo(localException, str);
        }
      }
    };
    return local7;
  }
  
  private Operation makeContactInfoListFactoryOperation()
  {
    Operation local8 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          if (CorbaContactInfoListFactory.class.isAssignableFrom(localClass)) {
            return localClass.newInstance();
          }
          throw wrapper.illegalContactInfoListFactoryType(localClass.toString());
        }
        catch (Exception localException)
        {
          throw wrapper.badContactInfoListFactory(localException, str);
        }
      }
    };
    return local8;
  }
  
  private Operation makeCSOperation()
  {
    Operation local9 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        return CodeSetComponentInfo.createFromString(str);
      }
    };
    return local9;
  }
  
  private Operation makeADOperation()
  {
    Operation local10 = new Operation()
    {
      private Integer[] map = { new Integer(0), new Integer(1), new Integer(2), new Integer(0) };
      
      public Object operate(Object paramAnonymousObject)
      {
        int i = ((Integer)paramAnonymousObject).intValue();
        return map[i];
      }
    };
    Operation localOperation1 = OperationFactory.integerRangeAction(0, 3);
    Operation localOperation2 = OperationFactory.compose(localOperation1, local10);
    Operation localOperation3 = OperationFactory.compose(localOperation2, OperationFactory.convertIntegerToShort());
    return localOperation3;
  }
  
  private Operation makeFSOperation()
  {
    Operation local11 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        int i = ((Integer)paramAnonymousObject).intValue();
        if (i < 32) {
          throw wrapper.fragmentSizeMinimum(new Integer(i), new Integer(32));
        }
        if (i % 8 != 0) {
          throw wrapper.fragmentSizeDiv(new Integer(i), new Integer(8));
        }
        return paramAnonymousObject;
      }
    };
    Operation localOperation = OperationFactory.compose(OperationFactory.integerAction(), local11);
    return localOperation;
  }
  
  private Operation makeGVOperation()
  {
    Operation localOperation1 = OperationFactory.listAction(".", OperationFactory.integerAction());
    Operation local12 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        Object[] arrayOfObject = (Object[])paramAnonymousObject;
        int i = ((Integer)arrayOfObject[0]).intValue();
        int j = ((Integer)arrayOfObject[1]).intValue();
        return new GIOPVersion(i, j);
      }
    };
    Operation localOperation2 = OperationFactory.compose(localOperation1, local12);
    return localOperation2;
  }
  
  private Operation makeROIOperation()
  {
    Operation localOperation1 = OperationFactory.classAction();
    Operation localOperation2 = OperationFactory.suffixAction();
    Operation localOperation3 = OperationFactory.compose(localOperation2, localOperation1);
    Operation localOperation4 = OperationFactory.maskErrorAction(localOperation3);
    Operation local13 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        final Class localClass = (Class)paramAnonymousObject;
        if (localClass == null) {
          return null;
        }
        if (ORBInitializer.class.isAssignableFrom(localClass))
        {
          ORBInitializer localORBInitializer = null;
          try
          {
            localORBInitializer = (ORBInitializer)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Object run()
                throws InstantiationException, IllegalAccessException
              {
                return localClass.newInstance();
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            throw wrapper.orbInitializerFailure(localPrivilegedActionException.getException(), localClass.getName());
          }
          catch (Exception localException)
          {
            throw wrapper.orbInitializerFailure(localException, localClass.getName());
          }
          return localORBInitializer;
        }
        throw wrapper.orbInitializerType(localClass.getName());
      }
    };
    Operation localOperation5 = OperationFactory.compose(localOperation4, local13);
    return localOperation5;
  }
  
  private Operation makeAcceptorInstantiationOperation()
  {
    Operation localOperation1 = OperationFactory.classAction();
    Operation localOperation2 = OperationFactory.suffixAction();
    Operation localOperation3 = OperationFactory.compose(localOperation2, localOperation1);
    Operation localOperation4 = OperationFactory.maskErrorAction(localOperation3);
    Operation local14 = new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        final Class localClass = (Class)paramAnonymousObject;
        if (localClass == null) {
          return null;
        }
        if (Acceptor.class.isAssignableFrom(localClass))
        {
          Acceptor localAcceptor = null;
          try
          {
            localAcceptor = (Acceptor)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Object run()
                throws InstantiationException, IllegalAccessException
              {
                return localClass.newInstance();
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            throw wrapper.acceptorInstantiationFailure(localPrivilegedActionException.getException(), localClass.getName());
          }
          catch (Exception localException)
          {
            throw wrapper.acceptorInstantiationFailure(localException, localClass.getName());
          }
          return localAcceptor;
        }
        throw wrapper.acceptorInstantiationTypeFailure(localClass.getName());
      }
    };
    Operation localOperation5 = OperationFactory.compose(localOperation4, local14);
    return localOperation5;
  }
  
  private Operation makeInitRefOperation()
  {
    new Operation()
    {
      public Object operate(Object paramAnonymousObject)
      {
        String[] arrayOfString = (String[])paramAnonymousObject;
        if (arrayOfString.length != 2) {
          throw wrapper.orbInitialreferenceSyntax();
        }
        return arrayOfString[0] + "=" + arrayOfString[1];
      }
    };
  }
  
  public static final class TestAcceptor1
    implements Acceptor
  {
    public TestAcceptor1() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestAcceptor1;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public boolean initialize()
    {
      return true;
    }
    
    public boolean initialized()
    {
      return true;
    }
    
    public String getConnectionCacheType()
    {
      return "FOO";
    }
    
    public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache) {}
    
    public InboundConnectionCache getConnectionCache()
    {
      return null;
    }
    
    public boolean shouldRegisterAcceptEvent()
    {
      return true;
    }
    
    public void setUseSelectThreadForConnections(boolean paramBoolean) {}
    
    public boolean shouldUseSelectThreadForConnections()
    {
      return true;
    }
    
    public void setUseWorkerThreadForConnections(boolean paramBoolean) {}
    
    public boolean shouldUseWorkerThreadForConnections()
    {
      return true;
    }
    
    public void accept() {}
    
    public void close() {}
    
    public EventHandler getEventHandler()
    {
      return null;
    }
    
    public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection)
    {
      return null;
    }
    
    public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator)
    {
      return null;
    }
    
    public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator)
    {
      return null;
    }
    
    public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator)
    {
      return null;
    }
  }
  
  public static final class TestAcceptor2
    implements Acceptor
  {
    public TestAcceptor2() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestAcceptor2;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public boolean initialize()
    {
      return true;
    }
    
    public boolean initialized()
    {
      return true;
    }
    
    public String getConnectionCacheType()
    {
      return "FOO";
    }
    
    public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache) {}
    
    public InboundConnectionCache getConnectionCache()
    {
      return null;
    }
    
    public boolean shouldRegisterAcceptEvent()
    {
      return true;
    }
    
    public void setUseSelectThreadForConnections(boolean paramBoolean) {}
    
    public boolean shouldUseSelectThreadForConnections()
    {
      return true;
    }
    
    public void setUseWorkerThreadForConnections(boolean paramBoolean) {}
    
    public boolean shouldUseWorkerThreadForConnections()
    {
      return true;
    }
    
    public void accept() {}
    
    public void close() {}
    
    public EventHandler getEventHandler()
    {
      return null;
    }
    
    public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection)
    {
      return null;
    }
    
    public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator)
    {
      return null;
    }
    
    public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator)
    {
      return null;
    }
    
    public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator)
    {
      return null;
    }
  }
  
  public final class TestBadServerIdHandler
    implements BadServerIdHandler
  {
    public TestBadServerIdHandler() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestBadServerIdHandler;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public void handle(ObjectKey paramObjectKey) {}
  }
  
  public static final class TestContactInfoListFactory
    implements CorbaContactInfoListFactory
  {
    public TestContactInfoListFactory() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestContactInfoListFactory;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public void setORB(com.sun.corba.se.spi.orb.ORB paramORB) {}
    
    public CorbaContactInfoList create(IOR paramIOR)
    {
      return null;
    }
  }
  
  public static final class TestIIOPPrimaryToContactInfo
    implements IIOPPrimaryToContactInfo
  {
    public TestIIOPPrimaryToContactInfo() {}
    
    public void reset(ContactInfo paramContactInfo) {}
    
    public boolean hasNext(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList)
    {
      return true;
    }
    
    public ContactInfo next(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList)
    {
      return null;
    }
  }
  
  public static final class TestIORToSocketInfo
    implements IORToSocketInfo
  {
    public TestIORToSocketInfo() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestIORToSocketInfo;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public List getSocketInfo(IOR paramIOR)
    {
      return null;
    }
  }
  
  public static final class TestLegacyORBSocketFactory
    implements com.sun.corba.se.spi.legacy.connection.ORBSocketFactory
  {
    public TestLegacyORBSocketFactory() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestLegacyORBSocketFactory;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public ServerSocket createServerSocket(String paramString, int paramInt)
    {
      return null;
    }
    
    public SocketInfo getEndPointInfo(org.omg.CORBA.ORB paramORB, IOR paramIOR, SocketInfo paramSocketInfo)
    {
      return null;
    }
    
    public Socket createSocket(SocketInfo paramSocketInfo)
    {
      return null;
    }
  }
  
  public static final class TestORBInitializer1
    extends LocalObject
    implements ORBInitializer
  {
    public TestORBInitializer1() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestORBInitializer1;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public void pre_init(ORBInitInfo paramORBInitInfo) {}
    
    public void post_init(ORBInitInfo paramORBInitInfo) {}
  }
  
  public static final class TestORBInitializer2
    extends LocalObject
    implements ORBInitializer
  {
    public TestORBInitializer2() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestORBInitializer2;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public void pre_init(ORBInitInfo paramORBInitInfo) {}
    
    public void post_init(ORBInitInfo paramORBInitInfo) {}
  }
  
  public static final class TestORBSocketFactory
    implements com.sun.corba.se.spi.transport.ORBSocketFactory
  {
    public TestORBSocketFactory() {}
    
    public boolean equals(Object paramObject)
    {
      return paramObject instanceof TestORBSocketFactory;
    }
    
    public int hashCode()
    {
      return 1;
    }
    
    public void setORB(com.sun.corba.se.spi.orb.ORB paramORB) {}
    
    public ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    {
      return null;
    }
    
    public Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    {
      return null;
    }
    
    public void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ParserTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */