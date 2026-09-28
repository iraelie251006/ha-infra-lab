package dev.iralie.inventory.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    public static final String PRIMARY = "primary";
    public static final String REPLICA = "replica";

    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void usePrimary() {
        context.set(PRIMARY);
    }

    public static void useReplica() {
        context.set(REPLICA);
    }

    public static void clear() {
        context.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return context.get() == null ? PRIMARY : context.get();
    }
}