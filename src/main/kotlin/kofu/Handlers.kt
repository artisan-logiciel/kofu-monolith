package kofu

import kofu.Log.log
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.net.URI.create
import java.time.Duration

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
class UserHandler {
    fun createUser(request: ServerRequest): Mono<ServerResponse> {
        val userDto: UserDto? = null
        log.info(
            "UserHandler::createUser, /api/users/{userDto},converter: ${
                request.body(BodyExtractors.toMono(UserDto::class.java))
            }"
        )
        return ok()
            .contentType(APPLICATION_JSON)
            .bodyValue("UserHandler::createUser, /api/users/{userDto}")
            .doAfterTerminate { log.debug("UserHandler::createUser, /api/users/{userDto}") }
    }
}


//fun registerAccount(@Valid @RequestBody managedUserVM: ManagedUserVM): Mono<Void> {
//    if (!checkPasswordLength(managedUserVM.password)) {
//        throw InvalidPasswordException()
//    }
//    return userService.registerUser(managedUserVM, managedUserVM.password!!)
//        .doOnSuccess(mailService::sendActivationEmail)
//        .then()
//}

@Suppress("UNUSED_PARAMETER")
class AccountHandler
//    ( private val userService: UserService)
{
    fun registerAccount(request: ServerRequest): Mono<ServerResponse> {
//log.info(        request.body(BodyExtractors.toMono(ManagedUserVM::class.java)).block()?.password)
        return created(create("/api/register"))
            .contentType(APPLICATION_JSON)
            .body(request.bodyToMono(ManagedUserVM::class.java))
    }
}
//userService.registerUser(
//request.bodyToMono(ManagedUserVM::class.java).block()!!,
//request.bodyToMono(ManagedUserVM::class.java).block()!!.password
//)!!
@Suppress("UNUSED_PARAMETER")
class ConfHandler(private val properties: ApplicationProperties) {
    fun conf(request: ServerRequest) = ok()
        .bodyValue(properties.toJson())
        .doAfterTerminate {
            log.info("ConfHandler::conf")
        }
}

@Suppress("UNUSED_PARAMETER")
class UserDemoHandler(private val userDemoRepository: UserDemoRepository) {
    fun listApi(request: ServerRequest) =
        ok()
            .contentType(APPLICATION_JSON)
            .body(userDemoRepository.findAll())

    fun userApi(request: ServerRequest) =
        ok()
            .contentType(APPLICATION_JSON)
            .body(userDemoRepository.findOne(request.pathVariable("login")))
}


@Suppress("UNUSED_PARAMETER")
class UserJwtHandler {
    fun registerAccount(request: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(APPLICATION_JSON)
        .bodyValue("userJwtHandler")
}