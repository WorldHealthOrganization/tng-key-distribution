# TNG Additional Context Schemas

This document describes the JSON schemas and context files added to support additional metadata validation in DID documents for the TNG Key Distribution Service.

## Overview

The TNG Key Distribution Service now includes JSON schemas to validate additional metadata fields in DID verification methods:

- `participant`: TNG participant code (2-3 letter country/organization codes)
- `keyusage`: Key usage code (SCA, UPLOAD, AUTHENTICATION, DSC)
- `domain`: Trust domain code (DCC, DIVOC, ICAO, SHC)
- `kid`: Key identifier in publicKeyJwk

## Schema Files

### Generic Schemas
Located in `src/main/resources/did_contexts/`:

- `valueset-keyusage.schema.json`: Key usage validation schema
- `valueset-participants.schema.json`: Participant codes validation schema
- `valueset-domain.schema.json`: Domain codes validation schema
- `tng-additional-context_v1.json`: JSON-LD context for additional metadata

### Environment-Specific Schemas

#### DEV Environment
- `valueset-keyusage-dev.schema.json`
- `valueset-participants-dev.schema.json`
- `valueset-domain-dev.schema.json`
- `tng-additional-context-dev_v1.json`

#### UAT Environment
- `valueset-keyusage-uat.schema.json`
- `valueset-participants-uat.schema.json`
- `valueset-domain-uat.schema.json`
- `tng-additional-context-uat_v1.json`

#### PROD Environment
- `valueset-keyusage-prod.schema.json`
- `valueset-participants-prod.schema.json`
- `valueset-domain-prod.schema.json`
- `tng-additional-context-prod_v1.json`

## Configuration

### Context Mapping

The context loader is configured to map the following URL to the appropriate environment-specific context file:

- Generic: `https://worldhealthorganization.github.io/smart-trust/tng-additional-context/v1`

Environment-specific configurations are in:
- `application-dev.yml`: Maps to `tng-additional-context-dev_v1.json`
- `application-uat.yml`: Maps to `tng-additional-context-uat_v1.json`
- `application-prod.yml`: Maps to `tng-additional-context-prod_v1.json`

### Schema URLs

#### Generic URLs (for all environments initially)
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-KeyUsage.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Participants.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Domain.schema.json`

#### Environment-Specific URLs

**DEV Environment:**
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-KeyUsage-DEV.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Participants-DEV.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Domain-DEV.schema.json`

**UAT Environment:**
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-KeyUsage-UAT.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Participants-UAT.schema.json`
- `https://worldhealthorganization.github.io/smart-trust/ValueSet-Domain-UAT.schema.json`

**PROD Environment:**
- `https://smart.who.int/trust/ValueSet-KeyUsage.schema.json`
- `https://smart.who.int/trust/ValueSet-Participants.schema.json`
- `https://smart.who.int/trust/ValueSet-Domain.schema.json`

## Usage

The schemas are automatically loaded when the KDS starts and are used to validate DID documents that include the additional metadata fields.

### Example DID Document Structure

```json
{
  "@context": [
    "https://www.w3.org/ns/did/v1",
    "https://w3id.org/security/suites/jws-2020/v1",
    "https://worldhealthorganization.github.io/smart-trust/tng-additional-context/v1"
  ],
  "id": "did:web:example.com",
  "verificationMethod": [{
    "id": "did:web:example.com#key1",
    "type": "JsonWebKey2020",
    "controller": "did:web:example.com",
    "publicKeyJwk": {
      "kty": "EC",
      "kid": "key1",
      "crv": "P-256",
      "x": "...",
      "y": "..."
    },
    "participant": "DE",
    "keyusage": "SCA",
    "domain": "DCC"
  }]
}
```

## Validation

The schemas validate:

1. **Participant**: Must be 2-3 letter uppercase codes
2. **Keyusage**: Must be one of: SCA, UPLOAD, AUTHENTICATION, DSC
3. **Domain**: Must be one of: DCC, DIVOC, ICAO, SHC
4. **Kid**: Must be a string value

## Environment Activation

To use environment-specific schemas, activate the appropriate Spring profile:

```bash
# For DEV
java -jar app.jar --spring.profiles.active=dev

# For UAT  
java -jar app.jar --spring.profiles.active=uat

# For PROD
java -jar app.jar --spring.profiles.active=prod
```

## Testing

Run the tests to validate schema functionality:

```bash
mvn test -Dtest=TngAdditionalContextTest
```

This test verifies:
- JSON syntax of all schema files
- Proper context structure
- Environment-specific URL references
- Schema validation functionality