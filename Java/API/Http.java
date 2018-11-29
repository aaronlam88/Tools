import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Http {
    private static final Logger logger = LogManager.getLogger(Http.class);

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
        try {
            // create a HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // create a post request
            HttpPost httpPost = new HttpPost(url);

            // if credentials is included, set up basic authentication
            if (credentials != null) {
                httpPost.addHeader(new BasicScheme().authenticate(credentials, httpPost, null));
            }

            // if headers is included
            if (headers != null) {
                for (Header header : headers) {
                    httpPost.addHeader(header);
                }
            }

            // set up post body
            if (requestBodyString != null && !requestBodyString.isEmpty()) {
                StringEntity params = new StringEntity(requestBodyString);
                httpPost.setEntity(params);
            }

            // send post request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Error check for empty httpResponse
            if (httpResponse == null) {
                logger.error("Get empty HttpsResponse from url " + url + " with request: " + requestBodyString);
                throw new Exception("Empty HttpsResponse");
            }

            // process data from post request
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity == null) {
                logger.error("Get empty HttpEntity from url " + url + " with request: " + requestBodyString);
                throw new Exception("Empty HttpEntity");
            }

            // converting the response httpEntity to string
            return EntityUtils.toString(httpEntity, "UTF-8");

        } catch (Exception e) {
            logger.error("Error accessing url: " + url + " with username: " + credentials.getUserName() + " & password: "
                    + credentials.getPassword() + " - Error Message: " + e.getLocalizedMessage());
            throw e;
        }
    }
}