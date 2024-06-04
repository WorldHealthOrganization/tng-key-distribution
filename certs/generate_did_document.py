import json
import base64
import os
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.backends import default_backend

# Get the path to the public key file, did-id and did-controller from the environment variables
public_key_file = os.getenv('PUBLIC_KEY_FILE')
did_id = os.getenv('DID_ID')
did_controller = os.getenv('DID_CONTROLLER')

# Read the public key from the file
with open(public_key_file, 'rb') as f:
    public_key_pem = f.read()

# Load the public key
public_key = serialization.load_pem_public_key(public_key_pem, backend=default_backend())

# Check if the public key is an elliptic curve public key
if isinstance(public_key, ec.EllipticCurvePublicKey):
    # Get the x and y coordinates of the public key
    x = public_key.public_numbers().x
    y = public_key.public_numbers().y

# Convert the x and y coordinates to base64url format
x = base64.urlsafe_b64encode(x.to_bytes((x.bit_length() + 7) // 8, 'big')).decode().rstrip('=')
y = base64.urlsafe_b64encode(y.to_bytes((y.bit_length() + 7) // 8, 'big')).decode().rstrip('=')

# Convert the public key to PEM format and encode it in base64
public_key_pem = public_key.public_bytes(
    encoding=serialization.Encoding.PEM,
    format=serialization.PublicFormat.SubjectPublicKeyInfo
)
public_key_pem_b64 = base64.b64encode(public_key_pem).decode()

did_document = {
    "@context": [
        "https://www.w3.org/ns/did/v1",
        "https://w3id.org/security/suites/jws-2020/v1"
    ],
    "id": did_id,
    "controller": did_controller,
    "verificationMethod": [
        {
            "id": did_id + "#%2FrcyDdJNU%2FA%3D",
            "type": "JsonWebKey2020",
            "controller": did_controller,
            "publicKeyJwk": {
                "kty": "EC",
                "crv": "P-256",
                "x": x,
                "y": y
            }
        }
    ]
}

# Write the DID document to a file
with open('did.json', 'w') as f:
    json.dump(did_document, f, indent=4)
