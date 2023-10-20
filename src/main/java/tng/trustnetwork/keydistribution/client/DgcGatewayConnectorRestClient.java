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
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tng.trustnetwork.keydistribution.dto.CertificateTypeDto;
import tng.trustnetwork.keydistribution.dto.TrustListItemDto;

@ConditionalOnProperty("dgc.gateway.connector.enabled")
@FeignClient(
    name = "dgc-gateway-connector",
    url = "${dgc.gateway.connector.endpoint}",
    configuration = DgcGatewayConnectorRestClientConfig.class
)
public interface DgcGatewayConnectorRestClient {

    /**
     * Gets the trusted certificates from digital green certificate gateway.
     *
     * @param type The type to filter for (e.g. CSCA, UPLOAD, AUTHENTICATION, DSC)
     * @return List of trustListItems
     */
    @GetMapping(value = "/trustList/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TrustListItemDto>> dataLoader(@PathVariable("type") CertificateTypeDto type);



}
