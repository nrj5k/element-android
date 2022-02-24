/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.debug.features

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import im.vector.app.features.HomeserverCapabilitiesOverride
import im.vector.app.features.VectorOverrides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.matrix.android.sdk.api.extensions.orFalse

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vector_overrides")
private val forceDialPadDisplay = booleanPreferencesKey("force_dial_pad_display")
private val forceCanChangeDisplayName = booleanPreferencesKey("force_can_change_display_name")
private val forceCanChangeAvatar = booleanPreferencesKey("force_can_change_avatar")

class DebugVectorOverrides(private val context: Context) : VectorOverrides {

    override fun forceDialPad() = forceDialPadDisplayFlow

    override fun forceHomeserverCapabilities() = forceHomeserverCapabilities

    suspend fun setForceDialPadDisplay(force: Boolean) {
        context.dataStore.edit { settings ->
            settings[forceDialPadDisplay] = force
        }
    }

    private val forceDialPadDisplayFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[forceDialPadDisplay].orFalse()
    }

    suspend fun updateHomeserverCapabilities(block: HomeserverCapabilitiesOverride.() -> HomeserverCapabilitiesOverride) {
        val capabilitiesOverride = block(forceHomeserverCapabilities.firstOrNull() ?: HomeserverCapabilitiesOverride(null, null))
        context.dataStore.edit { settings ->
            when (capabilitiesOverride.canChangeDisplayName) {
                null -> settings.remove(forceCanChangeDisplayName)
                else -> settings[forceCanChangeDisplayName] = capabilitiesOverride.canChangeDisplayName
            }
            when (capabilitiesOverride.canChangeAvatar) {
                null -> settings.remove(forceCanChangeAvatar)
                else -> settings[forceCanChangeAvatar] = capabilitiesOverride.canChangeAvatar
            }
        }
    }

    private val forceHomeserverCapabilities: Flow<HomeserverCapabilitiesOverride> = context.dataStore.data.map { preferences ->
        HomeserverCapabilitiesOverride(
                canChangeDisplayName = preferences[forceCanChangeDisplayName],
                canChangeAvatar = preferences[forceCanChangeAvatar]
        )
    }
}
