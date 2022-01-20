package kofu


import io.r2dbc.spi.ConnectionFactory
import kofu.Log.log
import kofu.TestDataSet.admin
import kofu.TestDataSet.user
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.springframework.beans.factory.getBean
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.fu.kofu.application
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionCallback
import org.springframework.transaction.reactive.TransactionalOperator.create
import org.springframework.transaction.reactive.executeAndAwait
import reactor.core.publisher.Mono


class RepositoriesTests {
    private val dataApp = application {
        enable(dataConfig)
    }

    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private val authRepo by lazy { context.getBean<AuthorityRepository>() }
        private val userRepo by lazy { context.getBean<UserRepository>() }
        private val database by lazy { context.getBean<R2dbcEntityOperations>().databaseClient }
        private val transaction by lazy { context.getBean<TransactionalOperator>() }
        private val countUser
            get() = (database.sql("SELECT COUNT(*) FROM user")
                .fetch().one().block()?.values?.first() as Long).toInt()
    }

    @BeforeAll
    fun beforeAll() {
        context = webApp.run(profiles = "test")
        userRepo.apply {
            save(admin).block()
            save(user).block()
        }
    }

    @AfterAll
    fun afterAll() {
        log.info("user count afterAll(): $countUser")
        context.close()
    }

    @Test
    fun test_AuthorityRepository_findRole() {
        authRepo.apply {
            assert(findByRole(ROLE_ADMIN).block()?.getId().equals(ROLE_ADMIN))
            assert(findByRole(ROLE_USER).block()?.getId().equals(ROLE_USER))
            assert(findByRole(ROLE_ANONYMOUS).block()?.getId().equals(ROLE_ANONYMOUS))
        }
    }

    @Test
    fun test_AuthorityRepository_findAll() {
        authRepo.findAll()
            .collectList().block()?.run {
                assert(
                    map(Authority::getId).containsAll(
                        listOf(ROLE_ADMIN, ROLE_USER, ROLE_ANONYMOUS)
                    )
                )
            }
    }

    @Test
    fun test_UserDemoRepository_countUserDemo() =
        context.getBean<UserDemoRepository>()
            .run {
                assert(count().block()?.toInt() == 3)
            }


    @Test
    fun test_UserRepository_save_with_valid_user() = userRepo.run {
        log.info(context.beanDefinitionNames.map { "$it\n" })
        val countBeforeSave = countUser
//        GlobalScope.async {
//            transaction.execute ({
                save(User().apply {
                    login = "jdoe"
                    email = "jdoe@acme.com"
                    password = login
                    authorities = user.authorities
                }).block()
                assert(countUser - countBeforeSave == 1)
//                it.setRollbackOnly()
//            }).
//            assert(countUser == countBeforeSave)
//        }
    }


    @Test
    fun copy_test_UserRepository_save_with_valid_user() =
        userRepo.run {
            val countBeforeSave = countUser
            save(User().apply {
                login = "jdoe"
                email = "jdoe@acme.com"
                password = "jdoe"
                authorities = mutableSetOf(Authority(ROLE_USER))
            }).block()
            assert(countUser - countBeforeSave == 1)
            //placer le rollback ici !!!!!!assert(countBeforeSave == countUser) { "rollback not executed" }
        }


    //(id: UUID)
    @Test
    fun test_findById() {
        assert(user.id != null)
        log.info("countUser(): $countUser")
        log.info("user.id: ${user.id}")
//        log.info("user: ${context.getBean<UserRepository>().findOneWithAuthoritiesByLogin(user.login!!).block()}")
//        assert(context.getBean<UserRepository>().findById(user.id!!).block()?.login.equals(user.login))
    }

    //(email: String)
    @Test
    fun test_findOneByEmailIgnoreCase() {
    }

    //(login: String)
    @Test
    fun test_findOneByLogin() {
    }

    //(userId: UUID, authority: String)
    @Test
    fun test_saveUserAuthority() {
    }

    //(activationKey: String)
    @Test
    fun test_findOneByActivationKey() {
    }

    //(resetKey: String)
    @Test
    fun test_findOneByResetKey() {
    }

    //(dateTime: LocalDateTime)
    @Test
    fun test_findAllByNotActivatedInTime() {
    }

    //(anonymousUser: String)
    @Test
    fun test_countAllByLoginNot() {
    }

    @Test
    fun test_deleteAllUserAuthorities() {
    }

    //(login: String): Mono<User>
    @Test
    fun test_findOneWithAuthoritiesByLogin() {
    }

    //(email: String): Mono<User>
    @Test
    fun test_findOneWithAuthoritiesByEmailIgnoreCase() {
    }

    //(pageable: Pageable, login: String)
    @Test
    fun test_findAllByLoginNot() {
    }

    //(user: User)
    @Test
    fun test_delete() {
    }
}