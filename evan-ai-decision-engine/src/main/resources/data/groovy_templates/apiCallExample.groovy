import groovy.json.JsonSlurper

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider

def closure= { request, config ->
    def cfg = new JsonSlurper().parseText(config)
    def method = (cfg.method ?: "GET").toUpperCase()
    def uri = cfg.endpoint

    // Proxy setup
    def proxy = null
    if (cfg.proxyHost && cfg.proxyPort) {
        proxy = new HttpHost(cfg.proxyHost, cfg.proxyPort as int)
    }

    // Credentials provider for proxy auth
    def credsProvider = null
    if (cfg.proxyUser && cfg.proxyPass) {
        credsProvider = new BasicCredentialsProvider()
        credsProvider.setCredentials(
                new AuthScope(cfg.proxyHost, cfg.proxyPort as int),
                new UsernamePasswordCredentials(cfg.proxyUser, cfg.proxyPass.toCharArray())
        )
    }

    CloseableHttpClient client = HttpClients.custom()
            .with { b ->
                if (proxy) b.setProxy(proxy)
                if (credsProvider) b.setDefaultCredentialsProvider(credsProvider)
                b.build()
            }

    def responseText = null
    try {
        def httpReq
        if (method == "POST") {
            httpReq = new HttpPost(uri)
            httpReq.setHeader("Content-Type", "application/json")
            httpReq.setEntity(new StringEntity(request?.toString() ?: ""))
        } else {
            httpReq = new HttpGet(uri)
            httpReq.setHeader("Content-Type", "application/json")
        }
        def response = client.execute(httpReq)
        responseText = response.entity?.content?.getText("UTF-8")
        response.close()
    } finally {
        client.close()
    }
    return responseText
}

/**
 * Example config:
 * {
 *   "endpoint": "https://dog.ceo/api/breeds/image/random",
 *   "method": "GET",
 *   "proxyHost": null,
 *   "proxyPort": null,
 *   "proxyUser": null,
 *   "proxyPass": null
 * }
 */