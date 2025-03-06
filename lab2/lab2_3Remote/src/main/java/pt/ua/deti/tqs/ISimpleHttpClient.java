package pt.ua.deti.tqs;

import java.io.IOException;

public interface ISimpleHttpClient {
    public String doHttpGet(String url) throws IOException;
}
