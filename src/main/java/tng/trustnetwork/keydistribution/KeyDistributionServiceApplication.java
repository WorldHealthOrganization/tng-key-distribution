/*-
 * ---license-start
 * WorldHealthOrganization / tng-key-distribution
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
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

package tng.trustnetwork.keydistribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

/**
 * The Application class.
 */
@SpringBootApplication
@EnableConfigurationProperties(KdsConfigProperties.class)
public class KeyDistributionServiceApplication extends SpringBootServletInitializer {

    /**
     * The main Method.
     *
     * @param args the args for the main method
     */
    public static void main(String[] args) {
        SpringApplication.run(KeyDistributionServiceApplication.class, args);
    }
}