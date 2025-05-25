import groovy.json.JsonSlurper
import java.net.*
import java.net.http.*
import java.util.Base64

return { request, config ->
    def cfg = new JsonSlurper().parseText(config)
    def uri = URI.create(cfg.endpoint)
    def builder = HttpRequest.newBuilder(uri)
            .method(cfg.method ?: "GET", HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/json")

    // Proxy support
    def clientBuilder = HttpClient.newBuilder()
    if (cfg.proxyHost && cfg.proxyPort) {
        clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(cfg.proxyHost, cfg.proxyPort as int)))
    }

    // Proxy auth
    if (cfg.proxyUser && cfg.proxyPass) {
        def auth = Base64.encoder.encodeToString("${cfg.proxyUser}:${cfg.proxyPass}".bytes)
        builder.header("Proxy-Authorization", "Basic ${auth}")
    }

    def client = clientBuilder.build()
    def response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}