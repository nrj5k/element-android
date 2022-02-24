/*
 * Copyright 2020 The Matrix.org Foundation C.I.C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageLocationContent(
        /**
         * Required. Must be 'm.location'.
         */
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String = MessageType.MSGTYPE_LOCATION,

        /**
         * Required. A description of the location e.g. 'Big Ben, London, UK', or some kind
         * of content description for accessibility e.g. 'location attachment'.
         */
        @Json(name = "body") override val body: String,

        /**
         * Required. RFC5870 formatted geo uri 'geo:latitude,longitude;uncertainty' like 'geo:40.05,29.24;30' representing this location.
         */
        @Json(name = "geo_uri") val geoUri: String,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null
) : MessageContent {

    /**
     * See https://github.com/matrix-org/matrix-doc/blob/matthew/location/proposals/3488-location.md
     */
    @Json(name = "org.matrix.msc3488.location") var unstableLocationInfo: LocationInfo? = null
    @Json(name = "m.location") var stableLocationInfo: LocationInfo? = null

    @Transient
    var locationInfo: LocationInfo? = null
    get() = stableLocationInfo ?: unstableLocationInfo
    set(value) {
        field = value
        unstableLocationInfo = value
    }

    /**
     * Exact time that the data in the event refers to (milliseconds since the UNIX epoch)
     */
    @Json(name = "org.matrix.msc3488.ts") var unstableTs: Long? = null
    @Json(name = "m.ts") var stableTs: Long? = null

    @Transient
    var ts: Long? = null
    get() = stableTs ?: unstableTs
    set(value) {
        field = value
        unstableTs = value
    }

    @Json(name = "org.matrix.msc1767.text") var unstableText: String? = null
    @Json(name = "m.text") var stableText: String? = null

    @Transient
    var text: String? = null
    get() = stableText ?: unstableText
    set(value) {
        field = value
        unstableText = value
    }

    /**
     * m.asset defines a generic asset that can be used for location tracking but also in other places like
     * inventories, geofencing, checkins/checkouts etc.
     * It should contain a mandatory namespaced type key defining what particular asset is being referred to.
     * For the purposes of user location tracking m.self should be used in order to avoid duplicating the mxid.
     */
    @Json(name = "org.matrix.msc3488.asset") var unstableLocationAsset: LocationAsset? = null
    @Json(name = "m.asset") var stableLocationAsset: LocationAsset? = null

    @Transient
    var locationAsset: LocationAsset? = null
    get() = stableLocationAsset ?: unstableLocationAsset
    set(value) {
        field = value
        unstableLocationAsset = value
    }

    fun getBestGeoUri() = locationInfo?.geoUri ?: geoUri

    /**
     * @return true if the location asset is a user location, not a generic one.
     */
    fun isSelfLocation(): Boolean {
        // Should behave like m.self if locationAsset is null
        return locationAsset?.type == null || locationAsset?.type == LocationAssetType.SELF
    }
}
