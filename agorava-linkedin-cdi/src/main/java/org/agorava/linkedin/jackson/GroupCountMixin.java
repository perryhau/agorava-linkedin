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

import java.io.IOException;

import org.agorava.linkedin.model.Post.PostCategory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * 
 * @author Antoine Sabot-Durand
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class GroupCountMixin {

    @JsonCreator
    GroupCountMixin(@JsonProperty("category") @JsonDeserialize(using = PostCategoryDeserializer.class) PostCategory category,
            @JsonProperty("count") Integer count) {
    }

    private static final class PostCategoryDeserializer extends JsonDeserializer<PostCategory> {
        @Override
        public PostCategory deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.readValueAsTree();
            return PostCategory.valueOf(node.get("code").getTextValue().replace('-', '_').toUpperCase());
        }
    }

}
