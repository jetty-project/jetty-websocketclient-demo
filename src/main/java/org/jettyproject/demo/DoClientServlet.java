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

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class DoClientServlet extends HttpServlet
{
    private WebSocketClient wsClient;

    @Override
    public void init() throws ServletException
    {
        super.init();
        wsClient = (WebSocketClient)getServletContext().getAttribute("my-server-wsclient");

        System.out.println("(DoClientServlet) classLoader = " + this.getClass().getClassLoader());
        System.out.println("(DoClientServlet) wsClient = " + wsClient);
        System.out.println("(DoClientServlet) wsClient.classLoader = " + wsClient.getClass().getClassLoader());
        System.out.println("(before) = " + getDump(wsClient.getExecutor()));

        setConfigInt(wsClient.getExecutor(), "setMinThreads", 1);
        setConfigInt(wsClient.getExecutor(), "setMaxThreads", 50);

        System.out.println("(after) = " + getDump(wsClient.getExecutor()));
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

    private String getDump(Object obj)
    {
        try
        {
            Method method = obj.getClass().getMethod("dump");
            return (String)method.invoke(obj);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore)
        {
            ignore.printStackTrace();
            return "<error>";
        }
    }

    private void setConfigInt(Executor executor, String methodName, int newValue)
    {
        try
        {
            System.out.printf("Attempting to call %s.%s(%d)%n", executor.getClass().getName(), methodName, newValue);
            Method method = executor.getClass().getMethod(methodName, Integer.TYPE);
            method.invoke(executor, newValue);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore)
        {
            ignore.printStackTrace();
        }
    }
}
