package kofu

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application

class ModelsTests {
    private val dataApp = application {
        enable(dataConfig)
    }
    companion object {
        private lateinit var context: ConfigurableApplicationContext
    }

    @BeforeAll
    fun beforeAll() {
        context = webApp.run(profiles = "test")
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun test_model() {

    }
}