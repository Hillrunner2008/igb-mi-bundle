/* 
 * Copyright 2015 Fondazione Istituto Italiano di Tecnologia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.iit.genomics.cru.structures.bridges.psicquic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Arnaud Ceol
 */
public class PsicquicClient {

    /**
     *
     */
    public static final String XML25 = "xml25";

    /**
     *
     */
    public static final String MITAB25 = "tab25";

    /**
     *
     */
    public static final String MITAB26 = "tab26";

    /**
     *
     */
    public static final String MITAB27 = "tab27";

    /**
     *
     */
    public static final String XGMML = "xgmml";

    /**
     *
     */
    public static final String BIOPAX = "biopax";

    /**
     *
     */
    public static final String BIOPAX_L2 = "biopax-L2";

    /**
     *
     */
    public static final String BIOPAX_L3 = "biopax-L3";

    /**
     *
     */
    public static final String RDF_XML = "rdf-xml";

    /**
     *
     */
    public static final String RDF_XML_ABBREV = "rdf-xml-abbrev";

    /**
     *
     */
    public static final String RDF_N3 = "rdf-n3";

    /**
     *
     */
    public static final String RDF_TURTLE = "rdf-turtle";

    /**
     *
     */
    public static final String COUNT = "count";

    private String serviceRestUrl;
    private final int connectionTimeout = 5000;
    private int readTimeout = 5000;
    private Proxy proxy;

    /**
     *
     * @param serviceRestUrl
     */
    public PsicquicClient(String serviceRestUrl) {
        this.serviceRestUrl = serviceRestUrl;
    }

    /**
     *
     * @param serviceRestUrl
     * @param proxy
     */
    public PsicquicClient(String serviceRestUrl, Proxy proxy) {
        this(serviceRestUrl);
        this.proxy = proxy;
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public InputStream getByQuery(String query) throws IOException {
        return getByQuery(query, MITAB25);
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public InputStream getByInteractor(String query) throws IOException {
        return getByQuery(query, MITAB25);
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public InputStream getByInteraction(String query) throws IOException {
        return getByQuery(query, MITAB25);
    }

    /**
     *
     * @param query
     * @param format
     * @return
     * @throws IOException
     */
    public InputStream getByQuery(String query, String format) throws IOException {
        return getByQuery(query, format, 0, Integer.MAX_VALUE);
    }

    /**
     *
     * @param query
     * @param format
     * @return
     * @throws IOException
     */
    public InputStream getByInteractor(String query, String format) throws IOException {
        return getByInteractor(query, format, 0, Integer.MAX_VALUE);
    }

    /**
     *
     * @param query
     * @param format
     * @return
     * @throws IOException
     */
    public InputStream getByInteraction(String query, String format) throws IOException {
        return getByInteraction(query, format, 0, Integer.MAX_VALUE);
    }

    /**
     *
     * @param query
     * @param format
     * @param firstResult
     * @param maxResults
     * @return
     * @throws IOException
     */
    public InputStream getByQuery(String query, String format, int firstResult, int maxResults) throws IOException {
        return getBy("query", query, format, firstResult, maxResults);
    }

    /**
     *
     * @param query
     * @param format
     * @param firstResult
     * @param maxResults
     * @return
     * @throws IOException
     */
    public InputStream getByInteractor(String query, String format, int firstResult, int maxResults) throws IOException {
        return getBy("interactor", query, format, firstResult, maxResults);
    }

    /**
     *
     * @param query
     * @param format
     * @param firstResult
     * @param maxResults
     * @return
     * @throws IOException
     */
    public InputStream getByInteraction(String query, String format, int firstResult, int maxResults) throws IOException {
        return getBy("interaction", query, format, firstResult, maxResults);
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public long countByQuery(String query) throws IOException {
        return countBy("query", query);
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public long countByInteractor(String query) throws IOException {
        return countBy("interactor", query);
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public long countByInteraction(String query) throws IOException {
        return countBy("interaction", query);
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public List<String> getFormats() throws IOException {
        String queryType = "/formats";

        String strUrl = serviceRestUrl + queryType;
        strUrl = strUrl.replaceAll("/" + queryType, queryType);

        HttpURLConnection connection;
        InputStream result;

        URL url;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Problem creating URL: " + strUrl, e);
        }

        if (this.proxy == null) {
            connection = (HttpURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection(this.proxy);
        }

        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);

        connection.connect();

        result = connection.getInputStream();

        String formats = streamToString(result);

        return Arrays.asList(formats.split("\n"));

    }

    private InputStream getBy(String queryType, String query, String format, int firstResult, int maxResults) throws IOException {
        HttpURLConnection connection;
        final String encodedQuery = encodeQuery(query);
        InputStream inputStream;

        URL url = createUrl(queryType, encodedQuery, format, firstResult, maxResults);
        if (this.proxy == null) {
            connection = (HttpURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection(this.proxy);
        }

        connection.setRequestProperty("accept", "text/*");
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);

        connection.connect();

        inputStream = connection.getInputStream();

        return inputStream;
    }

    private long countBy(String queryType, String query) throws IOException {
        InputStream result = getBy(queryType, query, COUNT, 0, 0);

        String strCount = streamToString(result);
        strCount = strCount.replaceAll("\n", "");

        return Long.parseLong(strCount);
    }

    private String encodeQuery(String query) {
        String encodedQuery;

        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
            encodedQuery = encodedQuery.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 should be supported");
        }
        return encodedQuery;
    }

    private URL createUrl(String queryType, String encodedQuery, String format, int firstResult, int maxResults) {
        String strUrl = serviceRestUrl + "/" + queryType + "/" + encodedQuery + "?format=" + format + "&firstResult=" + firstResult + "&maxResults=" + maxResults;
        strUrl = strUrl.replaceAll("//" + queryType, "/" + queryType);

        URL url;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Problem creating URL: " + strUrl, e);
        }
        return url;
    }

    private String streamToString(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                reader.close();
            }

        } finally {
            is.close();
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     *
     * @param readTimeout
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
