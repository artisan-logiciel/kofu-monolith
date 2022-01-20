package kofu

import kofu.Log.log
import org.apache.logging.log4j.LogManager.getLogger
import org.apache.logging.log4j.Logger
import org.springframework.core.env.Environment
import java.net.InetAddress.getLocalHost
import java.net.UnknownHostException

object Log {
    @JvmStatic
    val log: Logger by lazy { getLogger(Log.javaClass) }
}

fun logStartupEnvironment(env: Environment) {
    val protocol = if (env.getProperty("server.ssl.key-store") != null)
        "https" else "http"
    val serverPort = env.getProperty("server.port")
    val contextPath = env.getProperty("server.servlet.context-path") ?: "/"
    var hostAddress = "localhost"
    try {
        hostAddress = getLocalHost().hostAddress
    } catch (e: UnknownHostException) {
        log.warn(
            "The host name could not be determined, " +
                    "using `localhost` as fallback"
        )
    }
    log.info(
        ("\n\n\n" + """
        ----------------------------------------------------------
        Application '${env.getProperty("spring.application.name")}' is running! Access URLs:
        Local:      $protocol://localhost:$serverPort$contextPath
        External:   $protocol://$hostAddress:$serverPort$contextPath
        Profile(s): ${env.activeProfiles.joinToString(",")}
        ----------------------------------------------------------
        """ + "\n\n\n").trimIndent()
    )
}