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
import java.lang.reflect.Field;

import org.agorava.linkedin.model.Company;
import org.agorava.linkedin.model.LinkedInNetworkUpdate;
import org.agorava.linkedin.model.UpdateAction;
import org.agorava.linkedin.model.UpdateContent;
import org.agorava.linkedin.model.UpdateContentCompany;
import org.agorava.linkedin.model.UpdateContentConnection;
import org.agorava.linkedin.model.UpdateContentFollow;
import org.agorava.linkedin.model.UpdateContentGroup;
import org.agorava.linkedin.model.UpdateContentPersonActivity;
import org.agorava.linkedin.model.UpdateContentRecommendation;
import org.agorava.linkedin.model.UpdateContentShare;
import org.agorava.linkedin.model.UpdateContentStatus;
import org.agorava.linkedin.model.UpdateContentViral;
import org.agorava.linkedin.model.UpdateType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * 
 * @author Antoine Sabot-Durand
 * 
 */
class LinkedInNetworkUpdateListDeserializer extends JsonDeserializer<LinkedInNetworkUpdate> {

    @Override
    public LinkedInNetworkUpdate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDeserializationConfig(ctxt.getConfig());
        jp.setCodec(mapper);

        JsonNode dataNode = jp.readValueAsTree();
        if (dataNode != null) {
            LinkedInNetworkUpdate linkedInNetworkUpdate = (LinkedInNetworkUpdate) mapper.readValue(dataNode,
                    new TypeReference<LinkedInNetworkUpdate>() {
                    });

            UpdateContent updatedContent = null;
            UpdateType type = linkedInNetworkUpdate.getUpdateType();
            JsonNode updatedNode = dataNode.get("updateContent");
            JsonNode person = updatedNode.get("person");
            if (type == UpdateType.MSFC) {
                // Totally different. Looks like a bad API to be honest.
                person = updatedNode.get("companyPersonUpdate").get("person");
            }

            switch (type) {
                case CONN:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentConnection>() {
                    });
                    break;
                case STAT:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentStatus>() {
                    });
                    break;
                case JGRP:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentGroup>() {
                    });
                    break;
                case PREC:
                case SVPR:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentRecommendation>() {
                    });
                    break;
                case APPM:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentPersonActivity>() {
                    });
                    break;
                case MSFC:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentFollow>() {
                    });
                    break;
                case VIRL:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentViral>() {
                    });
                    break;
                case SHAR:
                    updatedContent = mapper.readValue(person, new TypeReference<UpdateContentShare>() {
                    });
                    break;
                case CMPY:
                    updatedContent = mapper.readValue(updatedNode, new TypeReference<UpdateContentCompany>() {
                    });
                    break;
                default:
                    try {
                        updatedContent = mapper.readValue(person, new TypeReference<UpdateContent>() {
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }

            // Need to use reflection to set private updateContent field
            try {
                Field f = LinkedInNetworkUpdate.class.getDeclaredField("updateContent");
                f.setAccessible(true);
                f.set(linkedInNetworkUpdate, updatedContent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (type == UpdateType.MSFC) {
                // Set the action via reflection as it's private
                String action = updatedNode.get("companyPersonUpdate").get("action").get("code").asText();
                try {
                    Field f = UpdateContentFollow.class.getDeclaredField("action");
                    f.setAccessible(true);
                    f.set(updatedContent, action);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // Set following via reflection as it's private
                Company company = mapper.readValue(updatedNode.get("company"), new TypeReference<Company>() {
                });
                try {
                    Field f = UpdateContentFollow.class.getDeclaredField("following");
                    f.setAccessible(true);
                    f.set(updatedContent, company);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (type == UpdateType.VIRL) {
                JsonNode originalUpdate = updatedNode.path("updateAction").path("originalUpdate");
                UpdateAction updateAction = mapper.readValue(originalUpdate, new TypeReference<UpdateAction>() {
                });
                String code = updatedNode.path("updateAction").path("action").path("code").getTextValue();

                // Set private immutable field action on updateAction
                try {
                    Field f = UpdateAction.class.getDeclaredField("action");
                    f.setAccessible(true);
                    f.set(updateAction, code);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // Set private immutable field updateAction on updatedContent
                try {
                    Field f = UpdateContentViral.class.getDeclaredField("updateAction");
                    f.setAccessible(true);
                    f.set(updatedContent, updateAction);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return linkedInNetworkUpdate;
        }

        return null;
    }

}
