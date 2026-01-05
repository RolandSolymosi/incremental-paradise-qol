package com.incrementalclient.common.utils.dependencyInjection;

import java.util.function.Function;

public record ServiceDescriptor(
        Class<?> serviceType,
        Class<?> implementationType,
        ServiceLifetime lifetime,
        Function<ServiceProvider, ?> factory
) {}