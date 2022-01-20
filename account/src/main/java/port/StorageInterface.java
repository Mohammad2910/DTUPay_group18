package port;

import domain.model.DTUPayAccount;
import java.util.Map;

/**
 * Interface that describes what methods that should be implemented by an adapter
 */
public interface StorageInterface {
    /**
     * Get an account by id
     *
     * @param id
     * @return dtuPayAccount object
     */
    DTUPayAccount getAccount(String id);

    /**
     * Get all accounts in the storage
     *
     * @return Map of id: dtuPayAccount
     */
    Map<String, DTUPayAccount> getAccounts();

    /**
     * Add account to the in memory storage
     *
     * @param account
     */
    void addAccount(DTUPayAccount account);

    /**
     * Remove account by id
     *
     * @param id
     */
    void deleteAccount(String id);

    /**
     * Cleans up the registration list
     */
    void cleanAccounts();
}


