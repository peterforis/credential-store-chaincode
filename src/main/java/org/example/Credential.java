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
    private final String owner;

    @Property()
    private final String credentialValue;

    public String getCredentialID() {
        return credentialID;
    }

    public String getOwner() {
        return owner;
    }

    public String getCredentialValue() {
        return credentialValue;
    }

    public Credential(@JsonProperty("credentialID") final String credentialID, @JsonProperty("owner") final String owner,
                      @JsonProperty("credentialValue") final String credentialValue) {
        this.credentialID = credentialID;
        this.owner = owner;
        this.credentialValue = credentialValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCredentialID(), getOwner(), getCredentialValue());
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
                new String[] {getCredentialID(), getOwner(), getCredentialValue()},
                new String[] {other.getCredentialID(), other.getOwner(), getCredentialValue()});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [credentialID=" + credentialID + ", owner=" + owner + ", credentialValue=" + credentialValue + "]";
    }
}

