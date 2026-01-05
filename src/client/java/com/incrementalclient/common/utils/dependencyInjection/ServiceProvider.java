package com.incrementalclient.common.utils.dependencyInjection;

public interface ServiceProvider {
    /**
     * Resolves a service. If multiple are registered, returns the last one.
     */
    <T> T getService(Class<T> serviceType);

    /**
     * Resolves all registered instances of a service.
     */
    <T> T[] getServices(Class<T> serviceType);

    /**
     * Validate the Service Provider for circular or missing dependencies to catch unresolvable services
     */
    void initialize();
}
