// TestComponent for Dagger to provide dependencies in the test environment
package com.opticon.opticonnect.sdk

import android.content.Context
import com.opticon.opticonnect.sdk.internal.di.OptiConnectModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OptiConnectModule::class])
interface TestComponent {
    fun inject(test: DatabaseIntegrationTest)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): TestComponent
    }
}