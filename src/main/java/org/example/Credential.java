package org.example;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Credential {

    @Property()
    private final String credentialID;

    @Property()
    private final String credentialName;

    @Property()
    private final String credentialOwner;

    @Property()
    private final String credentialValue;

    public String getCredentialID() {
        return credentialID;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public String getCredentialOwner() {
        return credentialOwner;
    }

    public String getCredentialValue() {
        return credentialValue;
    }

    public Credential(@JsonProperty("credentialID") final String credentialID, @JsonProperty("credentialName") final String credentialName, @JsonProperty("credentialOwner") final String credentialOwner,
                      @JsonProperty("credentialValue") final String credentialValue) {
        this.credentialID = credentialID;
        this.credentialName = credentialName;
        this.credentialOwner = credentialOwner;
        this.credentialValue = credentialValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCredentialID(), getCredentialOwner(), getCredentialValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Credential other = (Credential) obj;

        return Objects.deepEquals(
                new String[] {getCredentialID(), getCredentialName(), getCredentialOwner(), getCredentialValue()},
                new String[] {other.getCredentialID(), other.getCredentialName(), other.getCredentialOwner(), getCredentialValue()});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [credentialID=" + credentialID + ", credentialName=" + credentialName + ", credentialOwner=" + credentialOwner + ", credentialValue=" + credentialValue + "]";
    }
}

