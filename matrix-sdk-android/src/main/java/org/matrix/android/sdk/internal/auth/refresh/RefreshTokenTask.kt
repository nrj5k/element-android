/*
 * Copyright (c) 2022 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.auth.refresh

import org.matrix.android.sdk.internal.auth.data.RefreshResult
import org.matrix.android.sdk.internal.auth.AuthAPI
import org.matrix.android.sdk.internal.auth.RefreshTokenAPI
import org.matrix.android.sdk.internal.auth.data.RefreshParams
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface RefreshTokenTask : Task<RefreshTokenTask.Params, RefreshResult> {
    data class Params(
            val refreshToken: String
    )
}

internal class DefaultRefreshTokenTask @Inject constructor(
        private val refreshTokenAPI: RefreshTokenAPI
    ) : RefreshTokenTask {
    override suspend fun execute(params: RefreshTokenTask.Params): RefreshResult {
        return executeRequest(null) {
            refreshTokenAPI.refreshToken(RefreshParams(params.refreshToken))
        }
    }
}