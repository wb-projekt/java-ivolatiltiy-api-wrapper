package wbprojekt.ivolatiltiy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EODOptionsBacktestingTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @Test
    void testEodEquityOptionsRawIVSsearchByParameters_HappyPath() throws IOException, URISyntaxException {
        String responseFromDocumentation = "{\n" +
                "  \"status\": {\n" +
                "    \"executionTime\": 1772,\n" +
                "    \"recordsFound\": 234,\n" +
                "    \"code\": \"PENDING\",\n" +
                "    \"urlForDetails\": \"https://restapi.ivolatility.com/data/info/B2FC976D-7AB4-41B4-ADDD-89460B67E5C8\"\n" +
                "  },\n" +
                "  \"query\": {\n" +
                "    \"requestUUID\": \"B2FC976D-7AB4-41B4-ADDD-89460B67E5C8\"\n" +
                "  },\n" +
                "  \"data\": []\n" +
                "}";
        stubFor(get(urlPathEqualTo("/equities/eod/stock-opts-by-param"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromDocumentation)));

        EODOptionsBacktesting.EODEquityOptionsRawIV result = new EODOptionsBacktesting("http://localhost:" + wireMockServer.port()).eodEquityOptionsRawIVSsearchByParameters(
                "AAPL", "2023-10-01", 30, 60, 0.5, 0.8, "C", "ASIA");

        assertEquals(1772, result.status.executionTime);
        assertEquals(234, result.status.recordsFound);
        assertEquals("PENDING", result.status.code);
        assertEquals("https://restapi.ivolatility.com/data/info/B2FC976D-7AB4-41B4-ADDD-89460B67E5C8", result.status.urlForDetails);
        assertEquals("B2FC976D-7AB4-41B4-ADDD-89460B67E5C8", result.query.requestUUID);
        assertEquals(Collections.emptyList(), result.data);
    }
}