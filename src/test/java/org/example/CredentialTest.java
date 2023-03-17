package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
public final class CredentialTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Credential credential = new Credential("credential1", "owner1", "credential-value-test");

            assertThat(credential).isEqualTo(credential);
        }

        @Test
        public void isSymmetric() {
            Credential credentialA = new Credential("credential1", "owner1", "credential-value-test");
            Credential credentialB = new Credential("credential1", "owner1", "credential-value-test");

            assertThat(credentialA).isEqualTo(credentialB);
            assertThat(credentialB).isEqualTo(credentialA);
        }

        @Test
        public void isTransitive() {
            Credential credentialA = new Credential("credential1", "owner1", "credential-value-test");
            Credential credentialB = new Credential("credential1", "owner1", "credential-value-test");
            Credential credentialC = new Credential("credential1", "owner1", "credential-value-test");

            assertThat(credentialA).isEqualTo(credentialB);
            assertThat(credentialB).isEqualTo(credentialC);
            assertThat(credentialA).isEqualTo(credentialC);
        }

        @Test
        public void handlesInequality() {
            Credential credentialA = new Credential("credential1", "owner1", "credential-value-test");
            Credential credentialB = new Credential("credential2", "owner1", "credential-value-test");

            assertThat(credentialA).isNotEqualTo(credentialB);
        }

        @Test
        public void handlesOtherObjects() {
            Credential credentialA = new Credential("credential1", "owner1", "credential-value-test");
            String credentialB = "this is not a credential";

            assertThat(credentialA).isNotEqualTo(credentialB);
        }

        @Test
        public void handlesNull() {
            Credential credential = new Credential("credential1", "owner1", "credential-value-test");

            assertThat(credential).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesCredential() {
        Credential credential = new Credential("credential1", "owner1", "credential-value-test");

        assertThat(credential.toString()).isEqualTo("Credential@b7f5b31f [credentialID=credential1, owner=owner1, credentialValue=credential-value-test]");
    }
}
