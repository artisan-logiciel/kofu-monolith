package kofu

import kofu.ProfileConstants.Companion.SPRING_PROFILE_DEVELOPMENT
import org.springframework.fu.kofu.reactiveWebApplication


val webApp = reactiveWebApplication {
    configurationProperties<ApplicationProperties>(prefix = PROP_PREFIX)
    enable(logConfig)
    enable(dataConfig)
    enable(webConfig)
}

fun main() = logStartupEnvironment(
    webApp.run(
        emptyArray(),
        SPRING_PROFILE_DEVELOPMENT
    ).environment
)
