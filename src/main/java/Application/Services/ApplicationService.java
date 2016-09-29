package Application.Services;

import Application.Models.Version;
import Modules.Extensions.PhoneEmail.Models.Email;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 29.09.2016
 * Time: 21:09
 */
public class ApplicationService {
    /* @var bundle resource */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bundle
     */
    public ApplicationService(ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Check new version
     */
    public void checkNewVersion(){
        String currentVersion = PropertyService.get("version", "Application/Resources/properties.properties");
        String urlAppVersions = PropertyService.get("url_app_versions", "Application/Resources/properties.properties");
        String projectName = PropertyService.get("projectName", "Application/Resources/properties.properties");
        String urlProject = PropertyService.get("urlProject", "Application/Resources/properties.properties");

        try {

            Document doc = Jsoup.connect(urlAppVersions).get();
            String versionNew = getNewVersion(projectName, doc.body().text());

            if (versionNew == null) return;

            DefaultArtifactVersion cVersion = new DefaultArtifactVersion(currentVersion);
            DefaultArtifactVersion nVersion = new DefaultArtifactVersion(versionNew);

            if (nVersion.compareTo(cVersion) > 0) {
                AlertService.info(
                        this.bundle.getString("dialog_alert.information"),
                        null,
                        String.format(
                                this.bundle.getString("dialog_alert.version.new"),
                                versionNew, urlProject)
                );
            }

        } catch (IOException e) {}
    }

    /**
     * Unique display phones
     *
     * @param projectName
     * @param json
     * @return new version
     */
    private String getNewVersion(String projectName, String json){

        List<String> jsons = new ArrayList<>();
        List<Version> versions = new ArrayList<>();

        Pattern p = Pattern.compile("\\{.*?\\}");
        Matcher m = p.matcher(json);
        while(m.find()){
            jsons.add(m.group());
        }

        ObjectMapper mapper = new ObjectMapper();

        for (String j : jsons){
            try {
                Version version = mapper.readValue(j, Version.class);
                if (projectName.equals(version.getApp())){
                    return version.getVersion();
                }

            } catch (IOException e) {}
        }

        return null;
    }
}
