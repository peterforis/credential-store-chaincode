package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
public final class CredentialTransferTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockCredentialResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> credentialList;

        MockCredentialResultsIterator() {
            super();

            credentialList = new ArrayList<KeyValue>();

            credentialList.add(new MockKeyValue("credential1",
                    "{ \"credentialID\": \"credential1\", \"credentialName\": \"credential-name-1\", \"credentialOwner\": \"owner1\", \"credentialValue\": \"credential-value-1\" }"));
            credentialList.add(new MockKeyValue("credential2",
                    "{ \"credentialID\": \"credential2\", \"credentialName\": \"credential-name-2\", \"credentialOwner\": \"owner2\", \"credentialValue\": \"credential-value-2\" }"));
            credentialList.add(new MockKeyValue("credential3",
                    "{ \"credentialID\": \"credential3\", \"credentialName\": \"credential-name-3\", \"credentialOwner\": \"owner3\", \"credentialValue\": \"credential-value-3\" }"));
            credentialList.add(new MockKeyValue("credential4",
                    "{ \"credentialID\": \"credential4\", \"credentialName\": \"credential-name-4\", \"credentialOwner\": \"owner4\", \"credentialValue\": \"credential-value-4\" }"));
            credentialList.add(new MockKeyValue("credential5",
                    "{ \"credentialID\": \"credential5\", \"credentialName\": \"credential-name-5\", \"credentialOwner\": \"owner5\", \"credentialValue\": \"credential-value-5\" }"));
            credentialList.add(new MockKeyValue("credential6",
                    "{ \"credentialID\": \"credential6\", \"credentialName\": \"credential-name-6\", \"credentialOwner\": \"owner6\", \"credentialValue\": \"credential-value-6\" }"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return credentialList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        CredentialTransfer contract = new CredentialTransfer();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeReadCredentialTransaction {

        @Test
        public void whenCredentialExists() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1"))
                    .thenReturn("{ \"credentialID\": \"credential1\", \"credentialName\": \"credential-name-1\", \"credentialOwner\": \"owner1\", \"credentialValue\": \"credential-value-1\" }");

            Credential credential = contract.ReadCredential(ctx, "credential1");

            assertThat(credential).isEqualTo(new Credential("credential1", "credential-name-1", "owner1", "credential-value-1"));
        }

        @Test
        public void whenCredentialDoesNotExist() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.ReadCredential(ctx, "credential1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Credential credential1 does not exist");

            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("CREDENTIAL_NOT_FOUND".getBytes());
        }
    }

    @Test
    void invokeInitLedgerTransaction() {
        CredentialTransfer contract = new CredentialTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        contract.InitLedger(ctx);

        InOrder inOrder = inOrder(stub);

        inOrder.verify(stub).putStringState("credential1", "{\"credentialID\":\"credential1\",\"credentialName\":\"credential-name-1\",\"credentialOwner\":\"owner1\",\"credentialValue\":\"credential-value-1\"}");
        inOrder.verify(stub).putStringState("credential2", "{\"credentialID\":\"credential2\",\"credentialName\":\"credential-name-2\",\"credentialOwner\":\"owner2\",\"credentialValue\":\"credential-value-2\"}");
        inOrder.verify(stub).putStringState("credential3", "{\"credentialID\":\"credential3\",\"credentialName\":\"credential-name-3\",\"credentialOwner\":\"owner3\",\"credentialValue\":\"credential-value-3\"}");
        inOrder.verify(stub).putStringState("credential4", "{\"credentialID\":\"credential4\",\"credentialName\":\"credential-name-4\",\"credentialOwner\":\"owner4\",\"credentialValue\":\"credential-value-4\"}");
        inOrder.verify(stub).putStringState("credential5", "{\"credentialID\":\"credential5\",\"credentialName\":\"credential-name-5\",\"credentialOwner\":\"owner5\",\"credentialValue\":\"credential-value-5\"}");
        inOrder.verify(stub).putStringState("credential6", "{\"credentialID\":\"credential6\",\"credentialName\":\"credential-name-6\",\"credentialOwner\":\"owner6\",\"credentialValue\":\"credential-value-6\"}");
    }

    @Nested
    class InvokeCreateCredentialTransaction {

        @Test
        public void whenCredentialExists() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1"))
                    .thenReturn("{\"credentialID\":\"credential1\",\"owner\":\"owner1\",\"credentialValue\":\"credential-value-1\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.CreateCredential(ctx, "credential1", "credential-name-1", "owner1", "credential-value-1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Credential credential1 already exists");

            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("CREDENTIAL_ALREADY_EXISTS".getBytes());
        }

        @Test
        public void whenCredentialDoesNotExist() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1")).thenReturn("");

            Credential credential = contract.CreateCredential(ctx, "credential1", "credential-name-1",  "owner1", "credential-value-1");

            assertThat(credential).isEqualTo(new Credential("credential1", "credential-name-1", "owner1", "credential-value-1"));
        }
    }

    @Test
    void invokeGetAllCredentialsTransaction() {
        CredentialTransfer contract = new CredentialTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStateByRange("", "")).thenReturn(new MockCredentialResultsIterator());

        String credentials = contract.GetAllCredentials(ctx);

        System.out.println(credentials);

        assertThat(credentials).isEqualTo(
                "[{\"credentialID\":\"credential1\",\"credentialName\":\"credential-name-1\",\"credentialOwner\":\"owner1\",\"credentialValue\":\"credential-value-1\"},"
                        + "{\"credentialID\":\"credential2\",\"credentialName\":\"credential-name-2\",\"credentialOwner\":\"owner2\",\"credentialValue\":\"credential-value-2\"},"
                        + "{\"credentialID\":\"credential3\",\"credentialName\":\"credential-name-3\",\"credentialOwner\":\"owner3\",\"credentialValue\":\"credential-value-3\"},"
                        + "{\"credentialID\":\"credential4\",\"credentialName\":\"credential-name-4\",\"credentialOwner\":\"owner4\",\"credentialValue\":\"credential-value-4\"},"
                        + "{\"credentialID\":\"credential5\",\"credentialName\":\"credential-name-5\",\"credentialOwner\":\"owner5\",\"credentialValue\":\"credential-value-5\"},"
                        + "{\"credentialID\":\"credential6\",\"credentialName\":\"credential-name-6\",\"credentialOwner\":\"owner6\",\"credentialValue\":\"credential-value-6\"}]"
        );
    }

    @Nested
    class UpdateCredentialTransaction {

        @Test
        public void whenCredentialExists() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1"))
                    .thenReturn("{ \"credentialID\": \"credential1\", \"owner\": \"owner1\", \"credentialValue\": \"credential-value-1\" }");

            Credential credential = contract.UpdateCredential(ctx, "credential1", "credential-name-1", "owner1", "credential-value-New");

            assertThat(credential).isEqualTo(new Credential("credential1", "credential-name-1", "owner1", "credential-value-New"));
        }
    }

    @Nested
    class DeleteCredentialTransaction {

        @Test
        public void whenCredentialDoesNotExist() {
            CredentialTransfer contract = new CredentialTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("credential1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.DeleteCredential(ctx, "credential1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Credential credential1 does not exist");

            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("CREDENTIAL_NOT_FOUND".getBytes());
        }
    }
}
