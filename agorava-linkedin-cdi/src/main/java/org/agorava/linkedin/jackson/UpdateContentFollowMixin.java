/*******************************************************************************
 * Copyright 2012 Agorava
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.agorava.linkedin.jackson;

import org.agorava.linkedin.model.UrlResource;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author Antoine Sabot-Durand
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class UpdateContentFollowMixin {

    @JsonCreator
    UpdateContentFollowMixin(@JsonProperty("id") String id, @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName, @JsonProperty("headline") String headline,
            @JsonProperty("industry") String industry, @JsonProperty("publicProfileUrl") String publicProfileUrl,
            @JsonProperty("siteStandardProfileRequest") UrlResource siteStandardProfileRequest,
            @JsonProperty("pictureUrl") String profilePictureUrl) {
    }

    @JsonProperty("summary")
    String summary;

}
