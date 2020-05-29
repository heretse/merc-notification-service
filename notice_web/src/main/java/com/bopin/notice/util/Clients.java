/**
 * Project Name:bopin-message
 * File Name:Http.java
 * Package Name:com.bopin.msg.http
 * Date:2017年3月7日上午10:16:59
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 *
*/

package com.bopin.notice.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class Clients extends HttpClientBuilder
{
    /**
     * doCancelSSL:(取消https的证书验证). <br/>
     *
     * @author YanHenghui
     * @return CloseableHttpClient
     * @throws Exception
     * @since JDK 1.6
     */
    public static CloseableHttpClient getClientsByNotSSL() throws Exception
    {
        SSLContext ctx = SSLContext.getInstance( "TLS" );
        //SSLContext ctx = SSLContexts.custom().loadTrustMaterial(new File("my.keystore"), "nopassword".toCharArray(),new TrustSelfSignedStrategy()).build();
        X509TrustManager tm = new X509TrustManager()
        {
            public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException
            {
            }

            public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException
            {
            }

            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
        };
        // Allow TLSv1 protocol only
        ctx.init( null, new TrustManager[] {tm}, null );
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( ctx, new String[] {"TLSv1"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier() );
        return HttpClients.custom().setSSLSocketFactory( sslsf ).build();
        /*
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory( ctx, NoopHostnameVerifier.INSTANCE );
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register( "http", PlainConnectionSocketFactory.INSTANCE ).register( "https", socketFactory ).build();
        // 创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager( socketFactoryRegistry );
        // defaultRequestConfig
        RequestConfig reqcfg = RequestConfig.custom().setCookieSpec( CookieSpecs.STANDARD_STRICT ).setExpectContinueEnabled( true )
                .setTargetPreferredAuthSchemes( Arrays.asList( AuthSchemes.NTLM, AuthSchemes.DIGEST ) )
                .setProxyPreferredAuthSchemes( Arrays.asList( AuthSchemes.BASIC ) ).build();
        return HttpClients.custom().setConnectionManager( connectionManager ).setDefaultRequestConfig( reqcfg ).build();*/
    }
}