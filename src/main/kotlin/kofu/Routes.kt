package kofu

import org.springframework.web.reactive.function.server.router

fun routes(
    confHandler: ConfHandler,
    userDemoHandler: UserDemoHandler,
    accountHandler: AccountHandler,
    userHandler: UserHandler
) = router {
    GET("/api/conf", confHandler::conf)
    GET("/api/user", userDemoHandler::listApi)
    GET("/api/user/{login}", userDemoHandler::userApi)
    POST("/api/register", accountHandler::registerAccount)
    POST("/api/users/{userDto}", userHandler::createUser)
}