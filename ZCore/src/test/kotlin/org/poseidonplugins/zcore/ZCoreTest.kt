package org.poseidonplugins.zcore

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.poseidonplugins.zcore.mocks.MockServer

internal interface ZCoreTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            ZCore().setupForTesting(MockServer)
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            ZCore.dataFolder.deleteRecursively()
        }
    }
}