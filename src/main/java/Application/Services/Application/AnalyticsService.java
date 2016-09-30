package Application.Services.Application;

import Application.Services.LicenseService;
import Application.Services.PropertyService;
import com.brsanthu.googleanalytics.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 29.09.2016
 * Time: 21:57
 */
public class AnalyticsService {

    /* @var for chars for random string */
    private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /* @var random for strings */
    private SecureRandom rnd = new SecureRandom();

    /* @var anonymous id */
    private static String idUser;

    /* @var google analytics */
    private static GoogleAnalytics ga;

    /**
     * Constructor
     */
    public AnalyticsService(){
        build();
    }

    /**
     * Build analytics
     */
    private void build(){
        String version = PropertyService.get("version", "Application/Resources/properties.properties");
        String projectName = PropertyService.get("projectName", "Application/Resources/properties.properties");
        String analyticsId = PropertyService.get("analytics", "Application/Resources/properties.properties");

        if (AnalyticsService.idUser == null){
            this.idUser = randomString(10);

        }
        if (AnalyticsService.ga == null){
            AnalyticsService.ga = new GoogleAnalytics(analyticsId, projectName, version);
        }
    }

    /**
     * Generate random string
     *
     * @param len
     * @return random string
     */
    private String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    /**
     * Send signal to analytics
     *
     * @param track
     */
    public void send(String track){

        try {

            LicenseService licenseService = new LicenseService(null);

            final String version = PropertyService.get("version", "Application/Resources/properties.properties");
            final String projectName = PropertyService.get("projectName", "Application/Resources/properties.properties");

            String username = licenseService.getUsername("application");
            if (username.isEmpty()) {
                username = this.idUser;
            }

            final String userId = username;

            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("version", version);

            AnalyticsService.ga.postAsync(new RequestProvider() {
                public GoogleAnalyticsRequest getRequest() {
                    AppViewHit appViewHit = new AppViewHit(projectName, version, track);
                    appViewHit.userId(userId);
                    appViewHit.trackingId(track);
                    return new AppViewHit(projectName, version, track);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
