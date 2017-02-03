package li.vin.netv2;

import android.annotation.SuppressLint;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

public final class TestUtil {

  private TestUtil() {
  }

  /** ONLY for running tests on the JVM, where we need to do this bad thing to avoid SSL errs.. */
  public static OkHttpClient.Builder generateUnsafeBuilder() {
    try {
      // Create a trust manager that does not validate certificate chains
      final X509TrustManager x509TrustManager = new X509TrustManager() {
        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new java.security.cert.X509Certificate[] {};
        }
      };

      final TrustManager[] trustAllCerts = new TrustManager[] {
          x509TrustManager
      };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      // Create a host name verifier that verifies any host name
      final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(final String hostname, final SSLSession session) {
          return true;
        }
      };

      return new OkHttpClient.Builder() //
          .sslSocketFactory(sslSocketFactory, x509TrustManager) //
          .hostnameVerifier(hostnameVerifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
