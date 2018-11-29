import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class is for testing https api call only, should not using this in
 * production
 */
public class Https{
    private static final Logger log = LogManager.getLogger(Https.class);

    private static final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }
    } };

    private static final SSLContext trustAllSslContext;
    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

    public static OkHttpClient trustAllSslClient(OkHttpClient client) {
        log.warn("Using the trustAllSslClient is highly discouraged and should not be used in Production!");
        Builder builder = client.newBuilder();
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return builder.build();
    }

    /**
     * Send post request
     * 
     * @param url               the url of the post request
     * @param credentials       UsernamePasswordCredentials if needed (can be null)
     * @param requestBodyString a json string for the body of the post request
     * 
     * @return HttpResponse or null on Error
     * @throws Exception
     */
    public static String postRequest(String url, UsernamePasswordCredentials credentials, Header[] headers,
            String requestBodyString) throws Exception {
        OkHttpClient client = new OkHttpClient();
        client = trustAllSslClient(client);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBodyString);

        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Authorization", Credentials.basic(credentials.getUserName(), credentials.getPassword()))
                .addHeader("Content-Type", "application/json").build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}