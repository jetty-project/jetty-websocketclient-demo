//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.jettyproject.demo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.http.HttpClientTransportOverHTTP;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class DoClientServlet extends HttpServlet
{
    private HttpClient httpClient;
    private WebSocketClient wsClient;

    @Override
    public void init() throws ServletException
    {
        super.init();
        HttpClientTransportOverHTTP httpTransport = new HttpClientTransportOverHTTP(1);
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);
        sslContextFactory.setExcludeCipherSuites();
        httpClient = new HttpClient(httpTransport, sslContextFactory);
        wsClient = new WebSocketClient(httpClient);

        LifeCycle.start(httpClient);
        LifeCycle.start(wsClient);

        System.out.println("(DoClientServlet) classLoader = " + this.getClass().getClassLoader());
        System.out.println("(DoClientServlet) wsClient = " + wsClient);
        System.out.println("(DoClientServlet) wsClient.classLoader = " + wsClient.getClass().getClassLoader());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            String destUri = "wss://echo.websocket.org";

            SimpleEchoSocket socket = new SimpleEchoSocket();

            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setHeader("Origin", "https://websocket.org/");
            wsClient.connect(socket, echoUri, request);
            System.out.printf("Connecting to : %s%n", echoUri);

            // wait for closed socket connection.
            socket.awaitClose(5, TimeUnit.SECONDS);
        }
        catch (Throwable cause)
        {
            cause.printStackTrace();
        }
        finally
        {
            System.out.printf("WebSocketClient classloader = %s%n", wsClient.getClass().getClassLoader());
        }
    }
}
