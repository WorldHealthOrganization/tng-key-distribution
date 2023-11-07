package tng.trustnetwork.keydistribution.client;

/*-
 * ---license-start
 * WHO Digital Documentation Covid Certificate Gateway Service / ddcc-gateway-lib
 * ---
 * Copyright (C) 2022 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */
import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@ConditionalOnProperty("kds.gateway.connector.enabled")
@FeignClient(
    name = "kds-gateway-connector",
    url = "${kds.gateway.connector.endpoint}",
    configuration = KdsGatewayConnectorRestClientConfig.class
)
public interface KdsGatewayConnectorRestClient {

    /**
     * Gets the trusted certificates from digital green certificate gateway.
     *
     * @param type The type to filter for (e.g. CSCA, UPLOAD, AUTHENTICATION, DSC)
     * @return List of trustListItems
     */
    @GetMapping(path = "/trustList/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TrustListItemDto> getTrustedCertificates(@PathVariable("type") CertificateTypeDto type);
    
    @GetMapping(path = "/trustList", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TrustListItemDto> getTrustedCertificates();



}
