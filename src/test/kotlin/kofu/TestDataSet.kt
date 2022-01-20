package kofu

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.getBean
import org.springframework.context.ConfigurableApplicationContext

object TestDataSet {
    val admin = User().apply {
        login = "admin"
        email = "admin@acme.com"
        authorities = mutableSetOf(
            Authority(ROLE_ADMIN),
            Authority(ROLE_USER)
        )
    }
    val user = User().apply {
        login = "user"
        email = "user@acme.com"
        authorities = mutableSetOf(Authority(ROLE_USER))
    }

    val userDto = UserDto().apply {
        email = user.email
        login = user.login
    }

    val managedUserVM = ManagedUserVM(password = userDto.login as String)
        .apply {
            email = userDto.email
            login = userDto.login
        }

}
