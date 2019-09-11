# Server initialized WebSocketClient

There are occasions where you want to use a WebSocketClient that has been initialized
from a Server classloader scope, in order to get around ClassCastException and other
similar issues when you share common files between your webapp and the server.

This is particularly true of Jetty's HttpClient and QueuedTheadPool classes.

This project attempts to show a technique to initialize a WebSocketClient entirely
from the Server classloader scope, and then use that WebSocketClient from everywhere,
including any arbitrary WebApp classloader scope.

## To Build project

Use the command line

```
$ mvn clean install
```

This will build the `target/jetty-websocket-webapp-demo.war` and put a copy into
the `webapps/` directory.

## To Run War

Don't use `$ mvn jetty:run` as that will complicate matters with the maven classloader.

Instead, use an existing unpacked `jetty-home` artifact (found somewhere else on your system)
and just execute the existing Jetty instance configuration found in the root of this project.

```
$ java -jar /path/to/jetty-home-9.4.20.v20190813/start.jar
```

This will load the [`start.ini`](start.ini) found in this project, and deploy the webapp.

## What's Happens

The [`start.ini`](start.ini) has a reference to `--module=websocket-client` which is a module
found in the [`modules/`](modules/) directory of this project.

This in turn loads the XML found in [`etc/websocket-client.xml`](etc/websocket-client.xml).

That XML will create a `WebSocketClient`, based on an `HttpClient`, configured for selectors,
thread pool, idle timeout, etc.  This newly created, configured, and started `WebSocketClient`
will be placed in the `Server` attributes under the key named `"jetty-server-WebSocketClient"`.

Later, when the webapp itself loads, the webapp specific Context XML
[`webapps/jetty-websocket-webapp-demo.xml`](webapps/jetty-websocket-webapp-demo.xml)
will be used to load the Server attribute named `"jetty-server-WebSocketClient"` and
place its value into a `ServletContext.setAttribute("my-server-wsclient", serverWebSocketClient)`

This `ServletContext` attribute is used later during the `DoClientServlet.init()` method call.

Once the server is started, you can look at the server logs in `logs/####_##_##.jetty.log`.

The startup will look something like this ...

```
2019-09-11 13:02:46.478:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_256_GCM_SHA384 enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.479:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_128_GCM_SHA256 enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_256_CBC_SHA256 enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.480:WARN:oejusS.config:main: Weak cipher suite TLS_ECDH_RSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.481:WARN:oejusS.config:main: Weak cipher suite TLS_DHE_RSA_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.481:WARN:oejusS.config:main: Weak cipher suite TLS_DHE_DSS_WITH_AES_256_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.481:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_128_CBC_SHA256 enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.481:WARN:oejusS.config:main: Weak cipher suite TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_RSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_ECDH_RSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_DHE_RSA_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.482:WARN:oejusS.config:main: Weak cipher suite TLS_DHE_DSS_WITH_AES_128_CBC_SHA enabled for Client@7674b62c[provider=null,keyStore=null,trustStore=null]
2019-09-11 13:02:46.497:INFO:oejs.Server:main: jetty-9.4.20.v20190813; built: 2019-08-13T21:28:18.144Z; git: 84700530e645e812b336747464d6fbbf370c9a20; jvm 11.0.3+7
2019-09-11 13:02:46.506:INFO:oejdp.ScanningAppProvider:main: Deployment monitor [file:///home/joakim/code/jetty/stackoverflow/jetty-websocket-webapp-demo/webapps/] at interval 1
(DoClientServlet) classLoader = WebAppClassLoader{WebSocket Client Demo}@535779e4
(DoClientServlet) wsClient = WebSocketClient@6db124de[httpClient=HttpClient@7c37508a{STARTED},openSessions.size=0]
(DoClientServlet) wsClient.classLoader = startJarLoader@335eadca
(before) = QueuedThreadPool[WSClient-ThreadPool]@247310d0{STARTED,8<=8<=200,i=7,r=20,q=0}[ReservedThreadExecutor@5d018107{s=0/20,p=0}] - STARTED
+= ReservedThreadExecutor@5d018107{s=0/20,p=0} - STARTED
+> threads size=8
   +> 27 WSClient-ThreadPool-27 SELECTING RUNNABLE @ java.base@11.0.3/sun.nio.ch.EPoll.wait(Native Method)
   +> 29 WSClient-ThreadPool-29 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 34 WSClient-ThreadPool-34 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 32 WSClient-ThreadPool-32 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 30 WSClient-ThreadPool-30 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 33 WSClient-ThreadPool-33 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 31 WSClient-ThreadPool-31 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 28 WSClient-ThreadPool-28 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined
Attempting to call org.eclipse.jetty.util.thread.QueuedThreadPool.setMinThreads(1)
Attempting to call org.eclipse.jetty.util.thread.QueuedThreadPool.setMaxThreads(50)
(after) = QueuedThreadPool[WSClient-ThreadPool]@247310d0{STARTED,1<=8<=50,i=7,r=20,q=0}[ReservedThreadExecutor@5d018107{s=0/20,p=0}] - STARTED
+= ReservedThreadExecutor@5d018107{s=0/20,p=0} - STARTED
+> threads size=8
   +> 27 WSClient-ThreadPool-27 SELECTING RUNNABLE @ java.base@11.0.3/sun.nio.ch.EPoll.wait(Native Method)
   +> 29 WSClient-ThreadPool-29 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 34 WSClient-ThreadPool-34 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 32 WSClient-ThreadPool-32 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 30 WSClient-ThreadPool-30 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 33 WSClient-ThreadPool-33 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 31 WSClient-ThreadPool-31 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
   +> 28 WSClient-ThreadPool-28 IDLE TIMED_WAITING @ java.base@11.0.3/jdk.internal.misc.Unsafe.park(Native Method)
key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined
2019-09-11 13:02:46.832:INFO:oejsh.ContextHandler:main: Started o.e.j.w.WebAppContext@1608bcbd{WebSocket Client Demo,/,file:///tmp/jetty-0_0_0_0-8080-jetty-websocket-webapp-demo_war-_-any-10192661720150456606.dir/webapp/,AVAILABLE}{/home/joakim/code/jetty/stackoverflow/jetty-websocket-webapp-demo/webapps/jetty-websocket-webapp-demo.war}
2019-09-11 13:02:46.845:INFO:oejs.AbstractConnector:main: Started ServerConnector@5cfe1ba2{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
2019-09-11 13:02:46.845:INFO:oejs.Server:main: Started @1156ms
```

At this point you can see that `DoClientServlet.init()` has executed, it has ...

1. Fetched the `WebSocketClient` from the `ServletContext` attributes
2. Dumped the state of the `Executor` before making changes at runtime (Notice that the `QueuedThreadPool`
   is running with default min/max settings of `8<=8<=200` of 8 min and 200 max)
3. Changed the `Executor.setMinThreads` to 1
4. Changed the `Executor.setMaxThreads` to 50
5. Dumped the state of the `Executor` after making changes at runtime (Notice that the `QueuedThreadPool`
   is running with new min/max settings of `1<=8<=50` of 1 min and 50 max)
      
Now lets trigger an execution of the `WebSocketClient`

```
$ curl http://localhost:8080/doclient
```

This will produce no results on the curl request, but look at the server logs.

```
Connecting to : wss://echo.websocket.org
Got connect: WebSocketSession[websocket=JettyAnnotatedEventDriver[org.jettyproject.demo.SimpleEchoSocket@67c4bbfa],behavior=CLIENT,connection=WebSocketClientConnection@22064c9::DecryptedEndPoint@b15c1a6{echo.websocket.org/174.129.224.73:443<->/192.168.1.217:48532,OPEN,fill=-,flush=-,to=63/300000},remote=WebSocketRemoteEndpoint@339ea843[batching=true],incoming=JettyAnnotatedEventDriver[org.jettyproject.demo.SimpleEchoSocket@67c4bbfa],outgoing=ExtensionStack[queueSize=0,extensions=[],incoming=org.eclipse.jetty.websocket.common.WebSocketSession,outgoing=org.eclipse.jetty.websocket.client.io.WebSocketClientConnection]]
Got msg: Hello
Got msg: Thanks for the conversation.
Connection closed: 1006 - Disconnected
WebSocketClient classloader = startJarLoader@335eadca
```

