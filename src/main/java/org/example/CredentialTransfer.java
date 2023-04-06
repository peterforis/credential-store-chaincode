package org.example;

import com.owlike.genson.Genson;
import org.example.Enums.CredentialTransferError;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(name = "credential-store-chaincode")
@Default
public final class CredentialTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        CreateCredential(ctx, "credential1", "owner1", "credential-name-1", "credential-value-1");
        CreateCredential(ctx, "credential2", "owner2", "credential-name-2", "credential-value-2");
        CreateCredential(ctx, "credential3", "owner3", "credential-name-3", "credential-value-3");
        CreateCredential(ctx, "credential4", "owner4", "credential-name-4", "credential-value-4");
        CreateCredential(ctx, "credential5", "owner5", "credential-name-5", "credential-value-5");
        CreateCredential(ctx, "credential6", "owner6", "credential-name-6", "credential-value-6");
    }

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

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteCredential(final Context ctx, final String credentialID, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();

        if (!CredentialExists(ctx, credentialID, credentialOwner)) {
            String errorMessage = String.format("Credential %s does not exist", credentialID);
            throw new ChaincodeException(errorMessage, CredentialTransferError.CREDENTIAL_NOT_FOUND.toString());
        }

        stub.delState(credentialID);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean CredentialExists(final Context ctx, final String credentialID, final String credentialOwner) {
        ChaincodeStub stub = ctx.getStub();
        String credentialJSON = stub.getStringState(credentialID);

        return (credentialJSON != null && !credentialJSON.isEmpty());
    }

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

        return genson.serialize(queryResults);
    }
}
