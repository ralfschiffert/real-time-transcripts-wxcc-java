package com.cisco.wccai.grpc.server;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;

public class ValidateToken {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateToken.class);
    private static JWKSet jwkSet; // Cache the JWK set to avoid repeated downloads

    public static boolean validateKey(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            LOGGER.warn("Token is null or does not start with 'Bearer '");
            LOGGER.info("Ignoring the missing \"Bearer :\" for now");
            //return false;
        }

        String jwsString = token.replace("Bearer ", "");
        // Remove "Bearer " prefix

        try {
            // 1. Parse the JWS to access header and payload without verification yet
            JWSObject jwsObject = JWSObject.parse(jwsString);
            String keyId = jwsObject.getHeader().getKeyID();
            LOGGER.info("Token key id {}", keyId);
            Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
            String issuer = (String) payload.get("iss");

            if (keyId == null || issuer == null) {
                LOGGER.error("Token is missing 'kid' or 'iss' claims.");
                return false;
            }

            // 2. Construct the JWK URL from the issuer claim
            // This is more robust than hardcoding the full URL
            String jwkUrl = issuer + "/oauth2/v2/keys/verificationjwk/";
            LOGGER.info("Fetching JWKs from: {}", jwkUrl);

            // 3. Download and cache the JWK Set if not already present
            if (jwkSet == null) {
                jwkSet = JWKSet.load(new URL(jwkUrl));
            }

            // 4. Find the specific key by its Key ID ('kid')
            JWK jwk = jwkSet.getKeyByKeyId(keyId);
            if (jwk == null) {
                LOGGER.error("Could not find a matching JWK for key ID: {}", keyId);
                return false;
            }

            // 5. Verify the signature using the correct public key
            if (!(jwk instanceof RSAKey)) {
                LOGGER.error("Unsupported key type. Expected RSA key but found {}.", jwk.getClass().getSimpleName());
                return false;
            }

            JWSVerifier verifier = new RSASSAVerifier(((RSAKey) jwk).toRSAPublicKey());
            boolean isValid = jwsObject.verify(verifier);

            LOGGER.info("JWS signature verification result: {}", isValid);
            return isValid;

        } catch (ParseException | IOException | JOSEException e) {
            LOGGER.error("Token validation failed with an exception: {}", e.getMessage(), e);
            return false;
        }
    }
}