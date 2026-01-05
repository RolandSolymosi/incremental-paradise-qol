package com.incrementalclient.common.utils.dependencyInjection;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Configurable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceCollection {
    private final List<ServiceDescriptor> descriptors = new ArrayList<>();

    // Reflection-based registrations
    public <T> ServiceCollection addSingleton(Class<T> serviceType, Class<? extends T> impl) {
        descriptors.add(new ServiceDescriptor(serviceType, impl, ServiceLifetime.SINGLETON, null));
        return this;
    }

    //public <T> ServiceCollection addTransient(Class<T> serviceType, Class<? extends T> impl) {
    //    descriptors.add(new ServiceDescriptor(serviceType, impl, ServiceLifetime.TRANSIENT, null));
    //    return this;
    //}

    // Factory-based registrations
    public <T> ServiceCollection addSingleton(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        descriptors.add(new ServiceDescriptor(serviceType, null, ServiceLifetime.SINGLETON, factory));
        return this;
    }

    //public <T> ServiceCollection addTransient(Class<T> serviceType, Function<ServiceProvider, T> factory) {
    //    descriptors.add(new ServiceDescriptor(serviceType, null, ServiceLifetime.TRANSIENT, factory));
    //    return this;
    //}

    public <T extends ListenableBase<?>> ServiceCollection addListenable(Class<T> concreteType) {
        return this.addSingleton(concreteType, concreteType);
    }

    public <T extends ObservableBase<?, ?>> ServiceCollection addObservable(Class<T> concreteType) {
        return this.addSingleton(concreteType, concreteType);
    }

    /**
     * Maps an interface to an already registered implementation type.
     */
    public <TInterface, TImpl extends TInterface> ServiceCollection forwardSingleton(
            Class<TInterface> interfaceType,
            Class<TImpl> implementationType) {

        return this.addSingleton(interfaceType, sp -> sp.getService(implementationType));
    }

    /**
     * Maps an interface to an already registered implementation type.
     */
    //public <TInterface, TImpl extends TInterface> ServiceCollection forwardTransient(
    //        Class<TInterface> interfaceType,
    //        Class<TImpl> implementationType) {
//
    //    return this.addTransient(interfaceType, sp -> sp.getService(implementationType));
    //}

    public ServiceProvider buildServiceProvider() {
        return new DefaultServiceProvider(descriptors);
    }

    static class DefaultServiceProvider implements ServiceProvider {
        private final Map<Class<?>, List<ServiceDescriptor>> descriptorGroups;
        private final Map<ServiceDescriptor, Object> singletonInstances = new ConcurrentHashMap<>();
        private final Set<ServiceDescriptor> validatedTypes = Collections.newSetFromMap(new ConcurrentHashMap<>());

        private final ThreadLocal<Deque<Class<?>>> resolutionStack = ThreadLocal.withInitial(ArrayDeque::new);
        private final ThreadLocal<ServiceLifetime> currentConstructionLifetime = new ThreadLocal<>();

        public DefaultServiceProvider(List<ServiceDescriptor> descriptors) {
            this.descriptorGroups = descriptors.stream()
                    .collect(Collectors.groupingBy(ServiceDescriptor::serviceType));
        }

        public void initialize() {
            for (Class<?> serviceType : descriptorGroups.keySet()) {
                getService(serviceType);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getService(Class<T> serviceType) {
            if (serviceType.isArray()) return (T) getServices(serviceType.getComponentType());
            List<ServiceDescriptor> matches = descriptorGroups.get(serviceType);
            if (matches == null || matches.isEmpty()) return null;
            return (T) resolveDescriptor(matches.getLast());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] getServices(Class<T> serviceType) {
            List<ServiceDescriptor> matches = descriptorGroups.getOrDefault(serviceType, List.of());
            T[] array = (T[]) Array.newInstance(serviceType, matches.size());
            for (int i = 0; i < matches.size(); i++) {
                array[i] = (T) resolveDescriptor(matches.get(i));
            }
            return array;
        }

        private Object resolveDescriptor(ServiceDescriptor descriptor) {
            if (descriptor.lifetime() == ServiceLifetime.SINGLETON) {
                Object existing = singletonInstances.get(descriptor);
                if (existing != null) return existing;
            }
            else if(descriptor.lifetime() == ServiceLifetime.TRANSIENT && validatedTypes.contains(descriptor)){
                return createInstance(descriptor);
            }

            // Lock on singleton and first time transient creation to protect against Minecraft's multithreaded execution
            synchronized (singletonInstances) {
                // 0. Check again inside synchronized block
                Object existing = singletonInstances.get(descriptor);
                if (existing != null) return existing;

                // 1. Captive Dependency Check
                if (currentConstructionLifetime.get() == ServiceLifetime.SINGLETON && descriptor.lifetime() == ServiceLifetime.TRANSIENT) {
                    throw new RuntimeException("Captive Dependency: Singleton " + Objects.requireNonNull(resolutionStack.get().peek()).getSimpleName() + " cannot depend on Transient " + descriptor.serviceType().getSimpleName());
                }

                // 2. Circular Check
                Class<?> type = descriptor.serviceType();
                if (resolutionStack.get().contains(type)) {
                    throw new RuntimeException("Circular Dependency: " + resolutionStack.get().stream().map(Class::getSimpleName).collect(Collectors.joining(" -> ")) + " -> " + type.getSimpleName());
                }

                // 3. Setup Context
                ServiceLifetime previous = currentConstructionLifetime.get();
                currentConstructionLifetime.set(descriptor.lifetime());
                resolutionStack.get().push(type);

                try {
                    if (descriptor.lifetime() == ServiceLifetime.TRANSIENT) return createInstance(descriptor);
                    var instance = createInstance(descriptor);
                    singletonInstances.put(descriptor, instance);
                    return instance;
                } finally {
                    resolutionStack.get().pop();
                    currentConstructionLifetime.set(previous);
                }
            }
        }

        private Object createInstance(ServiceDescriptor descriptor) {
            if (descriptor.factory() != null) {
                return descriptor.factory().apply(this);
            }

            try {
                Constructor<?> constructor = descriptor.implementationType().getConstructors()[0];

                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> paramType = paramTypes[i];

                    if (paramType.isArray()) {
                        // Array parameter: inject all registered services
                        Class<?> elementType = paramType.getComponentType();
                        Object[] allServices = getServices(elementType);

                        // Create array of correct type
                        Object arr = java.lang.reflect.Array.newInstance(elementType, allServices.length);
                        for (int j = 0; j < allServices.length; j++) {
                            java.lang.reflect.Array.set(arr, j, allServices[j]);
                        }
                        args[i] = arr;
                    } else {
                        // Single service: inject normally
                        Object dep = getService(paramType);
                        if (dep == null) {
                            throw new RuntimeException("Missing dependency: " + paramType.getSimpleName());
                        }
                        args[i] = dep;
                    }
                }

                // Instantiate
                return constructor.newInstance(args);

            } catch (RuntimeException e) {
                throw e; // propagate
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + descriptor.serviceType().getSimpleName(), e);
            }
        }
    }
}