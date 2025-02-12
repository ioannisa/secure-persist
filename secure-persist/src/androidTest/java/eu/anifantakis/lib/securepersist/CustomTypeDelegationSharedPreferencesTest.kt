package eu.anifantakis.lib.securepersist

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomTypeDelegationSharedPreferencesTest {

    private lateinit var persistManager: PersistManager
    private lateinit var testClass: TestClass

    data class AuthInfo(
        val accessToken: String = "",
        val refreshToken: String = "",
        val expiresIn: Long = 0L
    )

    class TestClass(persistManager: PersistManager) {
        var authInfo by persistManager.sharedPrefs.preference(AuthInfo())
        var authInfo2 by persistManager.sharedPrefs.preference(AuthInfo(), key = "authInfo")
    }

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        persistManager = PersistManager(context)
        testClass = TestClass(persistManager)

        // Clear existing preference before each test
        persistManager.sharedPrefs.delete("authInfo")
        persistManager.sharedPrefs.put(
            key = "authInfo",
            value = AuthInfo(
                accessToken = "token123",
                refreshToken = "refresh123",
                expiresIn = 3600L
            )
        )
    }

    @Test
    fun testCustomTypePreference() {
        val storedAuthInfo = testClass.authInfo

        val authInfo2 = testClass.authInfo2

        assertEquals("token123", authInfo2.accessToken)

        assertEquals("token123", storedAuthInfo.accessToken)
        assertEquals("refresh123", storedAuthInfo.refreshToken)
        assertEquals(3600L, storedAuthInfo.expiresIn)
    }

    @Test
    fun testCustomTypePreferenceDelegation() {
        // if no key provided, SharedPreferences uses the variable name as key
        val authInfo by persistManager.sharedPrefs.preference(AuthInfo())

        assertEquals("token123", authInfo.accessToken)
        assertEquals("refresh123", authInfo.refreshToken)
        assertEquals(3600L, authInfo.expiresIn)
    }

    @Test
    fun testCustomTypePreferenceDelegationSetKey() {
        // if a key is provided, it will be used by SharedPreference as a key
        val storedAuthInfo by persistManager.sharedPrefs.preference(AuthInfo(), "authInfo")

        assertEquals("token123", storedAuthInfo.accessToken)
        assertEquals("refresh123", storedAuthInfo.refreshToken)
        assertEquals(3600L, storedAuthInfo.expiresIn)
    }
}
