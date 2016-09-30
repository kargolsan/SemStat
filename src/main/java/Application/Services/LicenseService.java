package Application.Services;

import Application.Services.Application.SettingsService;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 29.09.2016
 * Time: 16:12
 */
public class LicenseService {

    /* @var bundle resource */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bundle
     */
    public LicenseService(ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Add license
     *
     * @param key
     * @param extensionCode
     */
    public void addLicense(String key, String extensionCode){
        key = key.trim();
        try {
            String keyValues = decrypt(key);
            String[] values = keyValues.split("\\|\\|\\|");

            String version = values[0];
            String extension = values[1];
            String dataEnd = values[2];
            String username = values[3];
            String other = values[4];

            if (version == null || extension == null || dataEnd == null || username == null || other == null ){
                AlertService.error(this.bundle.getString("dialog_alert.error"), null, this.bundle.getString("dialog_alert.key_license.error"));
            }

            String[] vApp = PropertyService.get("version", "Application/Resources/properties.properties").split("\\.");

            String versionApp = vApp[0] + "." +vApp[1];

            if (!version.equals(versionApp)){
                AlertService.error(this.bundle.getString("dialog_alert.error"), null, String.format(this.bundle.getString("dialog_alert.key_license.error_version"), version));
            }

            if (!extension.equals(extensionCode)){
                AlertService.error(this.bundle.getString("dialog_alert.error"), null, String.format(this.bundle.getString("dialog_alert.key_license.error.code_extension")));
            }

            if (extension.equals(extensionCode)){
                SettingsService.add(extensionCode + versionApp, key);
                AlertService.info(this.bundle.getString("dialog_alert.information"), null, String.format(this.bundle.getString("dialog_alert.key_license.added." + extensionCode), dataEnd));
            }

        } catch (Exception e) {
            AlertService.error(this.bundle.getString("dialog_alert.error"), null, this.bundle.getString("dialog_alert.key_license.error"));
        }
    }

    /**
     * Check license
     *
     * @param extensionCode
     * @return true if has actuality license or id has not license
     */
    public Boolean hasLicense(String extensionCode){

        String[] vApp = PropertyService.get("version", "Application/Resources/properties.properties").split("\\.");

        String versionApp = vApp[0] + "." +vApp[1];

        String key = SettingsService.get(extensionCode + versionApp);

        try {

            String keyValues = decrypt(key);
            String[] values = keyValues.split("\\|\\|\\|");

            String version = values[0];
            String extension = values[1];
            String dataEnd = values[2];
            String username = values[3];
            String other = values[4];

            if (version == null || extension == null || dataEnd == null || username == null || other == null ){
                return false;
            }

            if (!version.equals(versionApp)){
                return false;
            }

            if (extension.equals(extensionCode)){
                if (!canDateLicense(dataEnd)) return false;
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Get date end
     *
     * @param extensionCode
     * @return date end
     */
    public String getDateEnd(String extensionCode){

        String[] vApp = PropertyService.get("version", "Application/Resources/properties.properties").split("\\.");

        String versionApp = vApp[0] + "." +vApp[1];

        String key = SettingsService.get(extensionCode + versionApp);

        try {

            String keyValues = decrypt(key);
            String[] values = keyValues.split("\\|\\|\\|");

            return values[2];

        } catch (Exception e) {}
        return "";
    }

    /**
     * Get username
     *
     * @param extensionCode
     * @return username
     */
    public String getUsername(String extensionCode){
        String[] vApp = PropertyService.get("version", "Application/Resources/properties.properties").split("\\.");

        String versionApp = vApp[0] + "." +vApp[1];

        String key = SettingsService.get(extensionCode + versionApp);

        try {

            String keyValues = decrypt(key);
            String[] values = keyValues.split("\\|\\|\\|");

            return values[3];

        } catch (Exception e) {}
        return "";
    }

    /**
     * Delete license
     *
     * @param extensionCode
     */
    public void delete(String extensionCode){
        String[] vApp = PropertyService.get("version", "Application/Resources/properties.properties").split("\\.");

        String versionApp = vApp[0] + "." +vApp[1];

        SettingsService.remove(extensionCode + versionApp);
    }

    /**
     * Check date license
     *
     * @param dateEnd
     * @return true if license is in date, false if license end date
     */
    private Boolean canDateLicense(String dateEnd){
        try {

            Calendar today = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            end.setTime(sdf.parse(dateEnd));

            if (end.compareTo(today) == -1) {
                return false;
            }
            return true;

        } catch (Exception e){}

        return false;
    }

    /**
     * Decrypt license key
     *
     * @param encryptedValue
     * @return
     * @throws Exception
     */
    public String decrypt(String encryptedValue) throws Exception {
        Key key = new SecretKeySpec(getSaltCipher(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     * Get application salt for cipher
     *
     * @return salt in bytes
     */
    public byte[] getSaltCipher(){
        String salt = PropertyService.get("license_salt", "Application/Resources/properties.properties");
        return salt.getBytes(StandardCharsets.UTF_8);
    }
}
