import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.classic.methods.*
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

        // Create appropriate HTTP request based on method
        switch (method) {
            case "POST":
                httpReq = new HttpPost(uri)
                break
            case "PUT":
                httpReq = new HttpPut(uri)
                break
            case "DELETE":
                httpReq = new HttpDelete(uri)
                break
            case "PATCH":
                httpReq = new HttpPatch(uri)
                break
            case "HEAD":
                httpReq = new HttpHead(uri)
                break
            case "OPTIONS":
                httpReq = new HttpOptions(uri)
                break
            default: // GET as default
                httpReq = new HttpGet(uri)
        }

        // Set common headers
        httpReq.setHeader("Content-Type", "application/json")

        // Add request body for methods that support it
        if (method in ["POST", "PUT", "PATCH"]) {
            // Create a Map with format {"request": value}
            def requestMap = [request: request]

            // Convert the Map to JSON string
            def requestBody = JsonOutput.toJson(requestMap)

            httpReq.setEntity(new StringEntity(requestBody))
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
 *   "endpoint": "https://api.example.com/resource",
 *   "method": "POST", // Supports GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
 *   "proxyHost": null,
 *   "proxyPort": null,
 *   "proxyUser": null,
 *   "proxyPass": null
 * }
 */