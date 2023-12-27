package tng.trustnetwork.keydistribution.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import tng.trustnetwork.keydistribution.entity.ProofEntity;
import tng.trustnetwork.keydistribution.entity.PublicKeyJwkEntity;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;
import tng.trustnetwork.keydistribution.model.Proof;
import tng.trustnetwork.keydistribution.model.PublicKeyJwk;
import tng.trustnetwork.keydistribution.model.ResolvedKey;
import tng.trustnetwork.keydistribution.model.VerificationMethod;

@Service
public class DecentralizedIdentifierTestHelper {
	
	public DidDocumentUnmarshal creaDidDocumentUnmarshal() {
		DidDocumentUnmarshal didDocumentUnmarshal = new DidDocumentUnmarshal();
		didDocumentUnmarshal.setContext(Arrays.asList(new String[]{"https://www.w3.org/ns/did/v1", "https://w3id.org/security/suites/jws-2020/v1"}));
		didDocumentUnmarshal.setId("did:web:tng-cdn-dev.who.int:trustlist");
		didDocumentUnmarshal.setController("did:web:tng-cdn-dev.who.int:trustlist");
		
		Proof proof = new Proof();
		proof.setType("JsonWebSignature2020");
		proof.setCreated("2023-12-27T09:50:58Z");
		proof.setNonce("");
		proof.setProofPurpose("assertionMethod");
		proof.setVerificationMethod("did:web:tng-cdn-dev.who.int:trustlist:signerinfo");
		proof.setJws("eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJhbGciOiJFQyJ9..Wq6V0-g-MUvwcbjHU2AjjYJbvBsxwIIUifMiLgzkapR3NzIHgALdTsW9udVoSBSCTxKX4GOLu3QdcKULHRC6hQ");
		didDocumentUnmarshal.setProof(proof);
		
		List<ResolvedKey> resolvedKeys = new ArrayList<>();
		
		ResolvedKey resolvedKey = new ResolvedKey();
		resolvedKey.setId("did:web:tng-cdn-dev.who.int:trustlist:nld#%2B7gPaASOAJY%3D");
		resolvedKey.setType("JsonWebKey2020");
		resolvedKey.setController("did:web:tng-cdn-dev.who.int:trustlist:nld");
		
		
		PublicKeyJwk publicKeyJwk = new PublicKeyJwk();
		publicKeyJwk.setKty("EC");
		publicKeyJwk.setCrv("P-256");
		publicKeyJwk.setXvalue("Yx+cFKuYNN5tvKme19QrBameqJiby3NhGLsdGF3tTw0=");
		publicKeyJwk.setYvalue("ANRvGyYJBiWxYSpNXisjeBP3ve/FmAcI7MC5XXs3VQ5D");
		publicKeyJwk.setX5c(Arrays.asList(new String[]{"MIIDPzCCAuWgAwIBAgIUNKbtWVuem9+GO9NmCVGGczN+8ZQwCgYIKoZIzj0EAwIwgYYxFzAVBgNVBAMMDkNTQ0EgSGVhbHRoIE5MMQowCAYDVQQFEwExMS0wKwYDVQQLDCRNaW5pc3RyeSBvZiBIZWFsdGggV2VsZmFyZSBhbmQgU3BvcnQxIzAhBgNVBAoMGktpbmdkb20gb2YgdGhlIE5ldGhlcmxhbmRzMQswCQYDVQQGEwJOTDAeFw0yMjEyMjExNDMyMDBaFw0zMzEyMTgxNDMyMDBaMIGQMQswCQYDVQQGEwJOTDEjMCEGA1UECgwaS2luZ2RvbSBvZiB0aGUgTmV0aGVybGFuZHMxLTArBgNVBAsMJE1pbmlzdHJ5IG9mIEhlYWx0aCBXZWxmYXJlIGFuZCBTcG9ydDELMAkGA1UEBRMCMTAxIDAeBgNVBAMMF0hlYWx0aCBEU0MgZm9yIHJlY292ZXJ5MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYx+cFKuYNN5tvKme19QrBameqJiby3NhGLsdGF3tTw3UbxsmCQYlsWEqTV4rI3gT973vxZgHCOzAuV17N1UOQ6OCASMwggEfMBUGB2eBCAEBBgIECjAIAgEAMQMTAUgwHwYDVR0jBBgwFoAUIk34w40pc1ODd4aa91LBGuyhoR8wGwYDVR0SBBQwEqQQMA4xDDAKBgNVBAcMA05MRDAbBgNVHREEFDASpBAwDjEMMAoGA1UEBwwDTkxEMBcGA1UdJQQQMA4GDCsGAQQBAI43j2UBAzA2BgNVHR8ELzAtMCugKaAnhiVodHRwOi8vY3JsLm5wa2QubmwvQ1JMcy9OTC1IZWFsdGguY3JsMB0GA1UdDgQWBBTQc6dWgQT/MZH12eJqMWWc1Z5xyzArBgNVHRAEJDAigA8yMDIyMTIyMTE0MzIwMFqBDzIwMjMwNzE5MTQzMjAwWjAOBgNVHQ8BAf8EBAMCB4AwCgYIKoZIzj0EAwIDSAAwRQIgSjGqbVuDOFOqyAXJ3ujBR4egLgJRrTVsCPA4CXh0qFcCIQDIdOsi8A2d3QlomAsG3XQa4UtxB7ZQvsYv4KlR/EXPTw=="}));
		resolvedKey.setPublicKeyJwk(publicKeyJwk);
		
		resolvedKeys.add(resolvedKey);
		
		VerificationMethod verificationMethod = new VerificationMethod();
		verificationMethod.setResolvedKeys(resolvedKeys);
		didDocumentUnmarshal.setVerificationMethod(verificationMethod);
		
		return didDocumentUnmarshal;
		
	}
	
	public ProofEntity creaProofEntity() {
		ProofEntity proofEntity = new ProofEntity();
		proofEntity.setType("JsonWebSignature2020");
		proofEntity.setCreated("2023-12-27T09:50:58Z");
		proofEntity.setNonce("");
		proofEntity.setProofPurpose("assertionMethod");
		proofEntity.setVerificationMethod("did:web:tng-cdn-dev.who.int:trustlist:signerinfo");
		proofEntity.setJws("eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJhbGciOiJFQyJ9..Wq6V0-g-MUvwcbjHU2AjjYJbvBsxwIIUifMiLgzkapR3NzIHgALdTsW9udVoSBSCTxKX4GOLu3QdcKULHRC6hQ");
		return proofEntity;
	}
	
	public VerificationMethodEntity createVerificationMethodEntity() {
		VerificationMethodEntity verificationMethodEntity = new VerificationMethodEntity();
		verificationMethodEntity.setVmId("did:web:tng-cdn-dev.who.int:trustlist:nld#%2B7gPaASOAJY%3D");
		verificationMethodEntity.setType("JsonWebKey2020");
		verificationMethodEntity.setController("did:web:tng-cdn-dev.who.int:trustlist:nld");
		
		PublicKeyJwkEntity publicKeyJwkEntity = new PublicKeyJwkEntity();
		publicKeyJwkEntity.setKty("EC");
		publicKeyJwkEntity.setCrv("P-256");
		publicKeyJwkEntity.setXvalue("Yx+cFKuYNN5tvKme19QrBameqJiby3NhGLsdGF3tTw0=");
		publicKeyJwkEntity.setYvalue("ANRvGyYJBiWxYSpNXisjeBP3ve/FmAcI7MC5XXs3VQ5D");
		publicKeyJwkEntity.setX5c("MIIDPzCCAuWgAwIBAgIUNKbtWVuem9+GO9NmCVGGczN+8ZQwCgYIKoZIzj0EAwIwgYYxFzAVBgNVBAMMDkNTQ0EgSGVhbHRoIE5MMQowCAYDVQQFEwExMS0wKwYDVQQLDCRNaW5pc3RyeSBvZiBIZWFsdGggV2VsZmFyZSBhbmQgU3BvcnQxIzAhBgNVBAoMGktpbmdkb20gb2YgdGhlIE5ldGhlcmxhbmRzMQswCQYDVQQGEwJOTDAeFw0yMjEyMjExNDMyMDBaFw0zMzEyMTgxNDMyMDBaMIGQMQswCQYDVQQGEwJOTDEjMCEGA1UECgwaS2luZ2RvbSBvZiB0aGUgTmV0aGVybGFuZHMxLTArBgNVBAsMJE1pbmlzdHJ5IG9mIEhlYWx0aCBXZWxmYXJlIGFuZCBTcG9ydDELMAkGA1UEBRMCMTAxIDAeBgNVBAMMF0hlYWx0aCBEU0MgZm9yIHJlY292ZXJ5MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYx+cFKuYNN5tvKme19QrBameqJiby3NhGLsdGF3tTw3UbxsmCQYlsWEqTV4rI3gT973vxZgHCOzAuV17N1UOQ6OCASMwggEfMBUGB2eBCAEBBgIECjAIAgEAMQMTAUgwHwYDVR0jBBgwFoAUIk34w40pc1ODd4aa91LBGuyhoR8wGwYDVR0SBBQwEqQQMA4xDDAKBgNVBAcMA05MRDAbBgNVHREEFDASpBAwDjEMMAoGA1UEBwwDTkxEMBcGA1UdJQQQMA4GDCsGAQQBAI43j2UBAzA2BgNVHR8ELzAtMCugKaAnhiVodHRwOi8vY3JsLm5wa2QubmwvQ1JMcy9OTC1IZWFsdGguY3JsMB0GA1UdDgQWBBTQc6dWgQT/MZH12eJqMWWc1Z5xyzArBgNVHRAEJDAigA8yMDIyMTIyMTE0MzIwMFqBDzIwMjMwNzE5MTQzMjAwWjAOBgNVHQ8BAf8EBAMCB4AwCgYIKoZIzj0EAwIDSAAwRQIgSjGqbVuDOFOqyAXJ3ujBR4egLgJRrTVsCPA4CXh0qFcCIQDIdOsi8A2d3QlomAsG3XQa4UtxB7ZQvsYv4KlR/EXPTw==");
		
		
		return  verificationMethodEntity;
		
	}
	
	

}
