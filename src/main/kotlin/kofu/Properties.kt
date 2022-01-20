package kofu

const val PROP_PREFIX = "kofu"

class ApplicationProperties(
    val message: String,
    val item: String,
    val mail: Mail = Mail(),
    val http: Http = Http(),
    val cache: Cache = Cache(),
    val security: Security = Security()
) {
    class Mail(
        var isEnabled: Boolean = false,
        var from: String = "",
        var baseUrl: String = ""
    )

    class Http(val cache: Cache = Cache()) {
        class Cache(var timeToLiveInDays: Int = 1461)
    }

    class Cache(val ehcache: Ehcache = Ehcache()) {
        class Ehcache(
            var timeToLiveSeconds: Int = 3600,
            var maxEntries: Long = 100
        )
    }

    class Security(
        val rememberMe: RememberMe = RememberMe(),
        val authentication: Authentication = Authentication(),
        val clientAuthorization: ClientAuthorization = ClientAuthorization()
    ) {
        class RememberMe(var key: String? = null)

        class Authentication(val jwt: Jwt = Jwt()) {
            class Jwt(
                var tokenValidityInSecondsForRememberMe: Long = 2592000,
                var tokenValidityInSeconds: Long = 1800,
                var base64Secret: String? = null,
                var secret: String? = null
            )
        }

        class ClientAuthorization(
            var accessTokenUri: String? = null,
            var tokenServiceId: String? = null,
            var clientId: String? = null,
            var clientSecret: String? = null
        )
    }

    fun toJson() = """
        {
        "$PROP_PREFIX":
            "message": ${this.message},
            "item": ${this.item},            
            "mail":
            {
                "base-url": ${this.mail.baseUrl},
                "from": ${this.mail.from}
            },
            "security":
            {
                    "authentication":
                        "jwt":
                        {
                            "secret": ${this.security.authentication.jwt.secret},
                            "base64-secret": ${this.security.authentication.jwt.base64Secret},
                            "token-validity-in-seconds": ${this.security.authentication.jwt.tokenValidityInSeconds},
                            "token-validity-in-seconds-for-remember-me": ${this.security.authentication.jwt.tokenValidityInSecondsForRememberMe},
                        }
            }
            
        "server": {"port": "8080"}
        "spring":
            {
                "application": {"name": "kofuApp"}
            }            
        }
    """.trimIndent()

}