package org.cru.crs.utils;


public enum Environment
{
    LOCAL("http", "localhost", 8080, "test", "test");
    
    public final String host;
    public final int port;
    public final String context = "/eventhub-api";

    private final String scheme;
    
    private Environment(String scheme, String host, int port, String siebelDatabase, String hcmDatabase)
    {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }
    
    public String getUrlAndContext()
    {
        return scheme + "://" + host + ":" + port + context;
    }
}
