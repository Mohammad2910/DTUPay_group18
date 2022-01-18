package controller;

import adapters.StorageAdapter;
import domain.TokenManager;
import storage.TokenStorage;

/**
 * @author Renjue, Christian and David
 *                                                  !!This is the Service provider class!!
 *                                          This class should be instantiated in the main method
 *                      The Responsibility of this class is to provide the right dependency injection for the required classes.
 *                                              To add a dependency:
 *                                                      - make an object of the wanted class
 *                                                      - inject the instantiation of the object in the parameter of the dependent class.
 */
public class ServiceProvider {

    private TokenManager tokenManager;
    private StorageAdapter storageAdapter;

    public ServiceProvider() {
        storageAdapter = new StorageAdapter(new TokenStorage());
        tokenManager = new TokenManager(storageAdapter);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public StorageAdapter getStorageAdapter() {
        return storageAdapter;
    }
}
