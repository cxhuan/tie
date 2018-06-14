package com.facebook.presto.jdbc;


import static com.facebook.presto.jdbc.internal.client.OkHttpUtil.userAgent;
import static com.facebook.presto.jdbc.internal.guava.base.Strings.nullToEmpty;
import static java.lang.Integer.parseInt;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.presto.jdbc.internal.okhttp3.OkHttpClient;

public class CllPrestoDriver implements Driver, Closeable
{
    static final String DRIVER_NAME = "Presto JDBC Driver";
    static final String DRIVER_VERSION;
    static final int DRIVER_VERSION_MAJOR;
    static final int DRIVER_VERSION_MINOR;

    private static final String DRIVER_URL_START = "jdbc:presto2:";

    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .addInterceptor(userAgent(DRIVER_NAME + "/" + DRIVER_VERSION))
            .build();

    static {
        String version = nullToEmpty(CllPrestoDriver.class.getPackage().getImplementationVersion());
        Matcher matcher = Pattern.compile("^(\\d+)\\.(\\d+)($|[.-])").matcher(version);
        if (!matcher.find()) {
            DRIVER_VERSION = "unknown";
            DRIVER_VERSION_MAJOR = 0;
            DRIVER_VERSION_MINOR = 0;
        }
        else {
            DRIVER_VERSION = version;
            DRIVER_VERSION_MAJOR = parseInt(matcher.group(1));
            DRIVER_VERSION_MINOR = parseInt(matcher.group(2));
        }

        try {
            DriverManager.registerDriver(new CllPrestoDriver());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()
    {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }

    @Override
    public Connection connect(String url, Properties info)
            throws SQLException
    {
        if (!acceptsURL(url)) {
            return null;
        }

        PrestoDriverUri uri = new PrestoDriverUri(url, info);

        OkHttpClient.Builder builder = httpClient.newBuilder();
        uri.setupClient(builder);
        QueryExecutor executor = new QueryExecutor(builder.build());

        return new CllPrestoConnection(uri, executor);
    }

    @Override
    public boolean acceptsURL(String url)
            throws SQLException
    {
        return url.startsWith(DRIVER_URL_START);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
            throws SQLException
    {
        Properties properties = new PrestoDriverUri(url, info).getProperties();

        return ConnectionProperties.allProperties().stream()
                .map(property -> property.getDriverPropertyInfo(properties))
                .toArray(DriverPropertyInfo[]::new);
    }

    @Override
    public int getMajorVersion()
    {
        return DRIVER_VERSION_MAJOR;
    }

    @Override
    public int getMinorVersion()
    {
        return DRIVER_VERSION_MINOR;
    }

    @Override
    public boolean jdbcCompliant()
    {
        return false;
    }

    @Override
    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException
    {
        throw new SQLFeatureNotSupportedException();
    }
}
