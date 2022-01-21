package adapter;

import domain.model.DTUPayAccount;
import port.StorageInterface;
import storage.InMemory;
import java.util.Map;

/**
 * @author Maria Eleni
 */
public class StorageAdapter implements StorageInterface {
    private InMemory inMemory;

    /**
     * Constructor of StorageAdapter
     *
     * @param inMemory - InMemory
     */
    public StorageAdapter(InMemory inMemory) {
        this.inMemory = inMemory;
    }

    /**
     * Get an account by id
     *
     * @param id - String
     * @return dtuPayAccount object
     */
    public DTUPayAccount getAccount(String id) {
        return this.inMemory.getAccount(id);
    }

    /**
     * Get all accounts in the storage
     *
     * @return Map <String, DTUPayAccount>
     */
    public Map<String, DTUPayAccount> getAccounts() {
        return this.inMemory.getAccounts();
    }

    /**
     * Add account to the in memory storage
     *
     * @param account - DTUPayAccount
     */
    public void addAccount(DTUPayAccount account) {
       this.inMemory.addAccount(account);
    }

    /**
     * Remove account by id
     *
     * @param id - String
     */
    public void deleteAccount(String id) {
        this.inMemory.deleteAccount(id);
    }

    /**
     * Cleans up the registration list
     */
    public void cleanAccounts() {
        this.inMemory.cleanAccounts();
    }
}


