package org.example;

import com.owlike.genson.Genson;
import org.example.Enums.CredentialTransferError;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "basic",
        info = @Info(
                title = "Credential Transfer Chaincode",
                description = "This is a hyperledger fabric credential store chaincode",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "peter.foris@student.manchester.ac.uk",
                        name = "Peter Foris",
                        url = "https://hyperledger.example.com")))
@Default
public final class CredentialTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateCredential(ctx, "credential1", "owner1", "credential-name-1", "credential-value-1");
        CreateCredential(ctx, "credential2", "owner2", "credential-name-2", "credential-value-2");
        CreateCredential(ctx, "credential3", "owner3", "credential-name-3", "credential-value-3");
        CreateCredential(ctx, "credential4", "owner4", "credential-name-4", "credential-value-4");
        CreateCredential(ctx, "credential5", "owner5", "credential-name-5", "credential-value-5");
        CreateCredential(ctx, "credential6", "owner6", "credential-name-6", "credential-value-6");
    }

    /**
     * Creates a new credential on the ledger.
     *
     * @param ctx             the transaction context
     * @param credentialID    the ID of the new credential
     * @param credentialOwner the credentialOwner of the new credential
     * @param credentialValue the credentialValue of the new credential
     * @return the created credential
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Credential CreateCredential(final Context ctx, final String credentialID, final String credentialOwner, final String credentialName, final String credentialValue) {
        ChaincodeStub stub = ctx.getStub();

        if (CredentialExists(ctx, credentialID, credentialOwner)) {
            String errorMessage = String.format("Credential %s already exists", credentialID);
            throw new ChaincodeException(errorMessage, CredentialTransferError.CREDENTIAL_ALREADY_EXISTS.toString());
        }
        Credential credential = new Credential(credentialID, credentialName, credentialOwner, credentialValue);
        String sortedJson = genson.serialize(credential);
        stub.putStringState(credentialID, sortedJson);
        return credential;
    }

    /**
     * Retrieves an credential with the specified ID from the ledger.
     *
     * @param ctx          the transaction context
     * @param credentialID the ID of the credential
     * @return the credential found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Credential ReadCredential(final Context ctx, final String credentialID, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();
        String credentialJSON = stub.getStringState(credentialID);

        if (credentialJSON == null || credentialJSON.isEmpty()) {
            String errorMessage = String.format("Credential %s does not exist", credentialID);
            throw new ChaincodeException(errorMessage, CredentialTransferError.CREDENTIAL_NOT_FOUND.toString());
        }

        return genson.deserialize(credentialJSON, Credential.class);
    }

    /**
     * Updates the properties of an credential on the ledger.
     *
     * @param ctx             the transaction context
     * @param credentialID    the ID of the credential being updated
     * @param credentialOwner the credentialOwner of the credential being updated
     * @param credentialValue the credentialValue of the credential being updated
     * @return the transferred credential
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Credential UpdateCredential(final Context ctx, final String credentialID, final String credentialOwner, final String credentialName, final String credentialValue) {
        ChaincodeStub stub = ctx.getStub();

        if (!CredentialExists(ctx, credentialID, credentialOwner)) {
            String errorMessage = String.format("Credential %s does not exist", credentialID);
            throw new ChaincodeException(errorMessage, CredentialTransferError.CREDENTIAL_NOT_FOUND.toString());
        }

        Credential newCredential = new Credential(credentialID, credentialName, credentialOwner, credentialValue);
        String sortedJson = genson.serialize(newCredential);
        stub.putStringState(credentialID, sortedJson);
        return newCredential;
    }

    /**
     * Deletes credential on the ledger.
     *
     * @param ctx          the transaction context
     * @param credentialID the ID of the credential being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteCredential(final Context ctx, final String credentialID, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();

        if (!CredentialExists(ctx, credentialID, credentialOwner)) {
            String errorMessage = String.format("Credential %s does not exist", credentialID);
            throw new ChaincodeException(errorMessage, CredentialTransferError.CREDENTIAL_NOT_FOUND.toString());
        }

        stub.delState(credentialID);
    }

    /**
     * Checks the existence of the credential on the ledger
     *
     * @param ctx          the transaction context
     * @param credentialID the ID of the credential
     * @return boolean indicating the existence of the credential
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean CredentialExists(final Context ctx, final String credentialID, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();
        String credentialJSON = stub.getStringState(credentialID);

        return (credentialJSON != null && !credentialJSON.isEmpty());
    }

    /**
     * Retrieves all credentials from the ledger.
     *
     * @param ctx the transaction context
     * @return array of credentials found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllCredentials(final Context ctx, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();

        List<Credential> queryResults = new ArrayList<Credential>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Credential credential = genson.deserialize(result.getStringValue(), Credential.class);
            if (credential.getCredentialOwner().equals(credentialOwner)) {
                queryResults.add(credential);
            }
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
