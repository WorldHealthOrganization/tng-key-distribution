/*-
 * ---license-start
 * WorldHealthOrganization / tng-key-distribution
 * ---
 * Copyright (C) 2021 - 2024 T-Systems International GmbH and all other contributors
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

package tng.trustnetwork.keydistribution.service.did;

import com.danubetech.keyformats.crypto.ByteSigner;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

@Service
@ConditionalOnProperty(name = "dgc.did.didSigningProvider", havingValue = "local-keystore")
public class LocalKeystoreByteSigner extends ByteSigner {

    private final PrivateKey signingKey;

    /**
     * Initialize LocalKeyStoreByteSigner. Configured Key will be loaded.
     */
    public LocalKeystoreByteSigner(KdsConfigProperties kdsConfigProperties)
        throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
               UnrecoverableKeyException {

        super("EC");

        KeyStore keyStore = KeyStore.getInstance("JKS");

        try (FileInputStream fileInputStream = new FileInputStream(
            ResourceUtils.getFile(kdsConfigProperties.getDid().getLocalKeyStore().getPath()));
        ) {
            keyStore.load(fileInputStream, kdsConfigProperties.getDid().getLocalKeyStore().getPassword());
        }

        signingKey = (PrivateKey) keyStore.getKey(
            kdsConfigProperties.getDid().getLocalKeyStore().getAlias(),
            kdsConfigProperties.getDid().getLocalKeyStore().getPassword());

    }

    @Override
    protected byte[] sign(byte[] content) throws GeneralSecurityException {

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(signingKey);
        signature.update(content);
        return signature.sign();
    }
}
