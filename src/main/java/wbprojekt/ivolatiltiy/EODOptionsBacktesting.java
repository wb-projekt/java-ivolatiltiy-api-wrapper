package wbprojekt.ivolatiltiy;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.apache.hc.core5.http.HttpStatus.*;

public class EODOptionsBacktesting
{

    /**
     * TODO This will be externalised to a config file or environment variable.
     */
    private static final String API_KEY = "your_api_key";
    /**
     * TODO This will be externalised to a config file or environment variable/
     */
    public static final String BASE_URL = "https://restapi.ivolatility.com";
    private final String baseUrl;

    public EODOptionsBacktesting(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }
    public EODOptionsBacktesting()
    {
        //
        this.baseUrl = BASE_URL;
    }



    public EODEquityOptionsRawIV eodEquityOptionsRawIVSsearchByParameters(String symbol, String tradeDate, int dteFrom, int dteTo, double deltaFrom, double deltaTo, String cp, String region) throws IOException, URISyntaxException
    {
        URI uri = new URIBuilder(baseUrl + "/equities/eod/stock-opts-by-param")
                .addParameter("apiKey", API_KEY)
                .addParameter("symbol", symbol)
                .addParameter("tradeDate", tradeDate)
                .addParameter("dteFrom", String.valueOf(dteFrom))
                .addParameter("dteTo", String.valueOf(dteTo))
                .addParameter("deltaFrom", String.valueOf(deltaFrom))
                .addParameter("deltaTo", String.valueOf(deltaTo))
                .addParameter("cp", cp)
                .addParameter("region", region)
                .build();

        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            HttpGet httpget = new HttpGet(uri);
            return httpclient.execute(httpget, response -> {
                switch (response.getCode())
                {
                    case SC_OK:
                        String entityAsString = EntityUtils.toString(response.getEntity());
                        return new Gson().fromJson(entityAsString, EODEquityOptionsRawIV.class);
                    case SC_CLIENT_ERROR:
                        // TODO handle error
                        throw new UnsupportedOperationException("Client error");
                    case SC_UNAUTHORIZED:
                        // TODO handle error
                        throw new UnsupportedOperationException("Unauthorized");
                    case SC_FORBIDDEN:
                        // TODO handle error
                        throw new UnsupportedOperationException("Forbidden");
                    case SC_INTERNAL_SERVER_ERROR:
                        // TODO handle error
                        throw new UnsupportedOperationException("Internal server error");
                    default:
                        // TODO handle unexpected response code
                        throw new UnsupportedOperationException("Unexpected response code: " + response.getCode());
                }
            });
        }
    }

    public class EODEquityOptionsRawIV
    {
        public final Status status;
        public final Query query;
        public final List<Object> data;

        public EODEquityOptionsRawIV(Status status, Query query, List<Object> data)
        {
            this.status = status;
            this.query = query;
            this.data = data;
        }
    }

    public class Status {
        public final int executionTime;
        public final int recordsFound;
        public final String code;
        public final String urlForDetails;

        public Status(int executionTime, int recordsFound, String code, String urlForDetails) {
            this.executionTime = executionTime;
            this.recordsFound = recordsFound;
            this.code = code;
            this.urlForDetails = urlForDetails;
        }
    }

    public static class Query {
        public final String requestUUID;

        public Query(String requestUUID) {
            this.requestUUID = requestUUID;
        }
    }
}