package Modules.Extensions.PhoneEmail.Services;

import Application.Contracts.Data.IResultModel;
import Application.Contracts.Extensions.IExtension;
import Application.Controllers.Application.Extensions.PhoneEmailController;
import Application.Services.Application.SettingsService;
import Application.Services.LicenseService;
import Application.Services.PropertyService;
import Modules.Bots.First.Services.ParseService;
import Modules.Extensions.PhoneEmail.Models.PhoneEmail;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 28.09.2016
 * Time: 20:01
 */
public class PhoneEmailService implements IExtension {

    /* @var phone service */
    private PhoneService phoneService;

    /* @var email service */
    private EmailService emailService;

    /**
     * Constructor
     *
     * @param parseService
     * @param bundle
     */
    public PhoneEmailService(ParseService parseService, ResourceBundle bundle){
        this.phoneService = new PhoneService(parseService, bundle);
        this.emailService = new EmailService(parseService, bundle);
    }

    /**
     * Check extension has active license
     * @return
     */
    public Boolean hasLicense(){
        LicenseService licenseService = new LicenseService(null);
        return licenseService.hasLicense("ext_phone_email");
    }

    /**
     * Check extension can be used
     *
     * @return true if can or false if can not
     */
    public Boolean access(){

        if (!hasLicense()) return false;

        String getEPA = SettingsService.get("extension.phone_email.active");

        if (getEPA.equals("true")) return true;

        return false;
    }

    /**
     * Analyze in extension
     *
     * @param doc
     * @return result
     */
    @Override
    public void analyze(Document doc) {
        phoneService.analyzePhoneOfDomain(doc);
        emailService.analyzeEmailOfDomain(doc);
    }

    /**
     * Clear data in extension
     */
    @Override
    public void clear() {
        if (!access()) return;
        this.emailService.clear();
        this.phoneService.clear();
    }

    /**
     * Call if bot finished
     */
    @Override
    public void finish() {
        if (!access()) return;
        this.emailService.finish();
        this.phoneService.finish();
    }

    /**
     * Run before save for modification results
     *
     * @param resultsToSave
     * @return resultsToSave
     */
    @Override
    public List<IResultModel> beforeSaved(List<IResultModel> resultsToSave) {
        if (!access()) return resultsToSave;

        resultsToSave = this.phoneService.beforeSaved(resultsToSave);
        resultsToSave = this.emailService.beforeSaved(resultsToSave);

        List<IResultModel> displayResult = removeDuplicateData(resultsToSave);

        for (IResultModel r : displayResult){

            if (r.getPhones() == null && r.getEmails()==null) continue;

            r.setPhones((r.getPhones() == null) ? "" : r.getPhones());
            r.setEmails((r.getEmails() == null) ? "" : r.getEmails());

            PhoneEmail phoneEmail = new PhoneEmail();
            phoneEmail.setDomain(r.getDomain());
            phoneEmail.setKeyword(r.getKeyword());
            phoneEmail.setPhones(r.getPhones());
            phoneEmail.setEmails(r.getEmails());
            PhoneEmailController.addResult(phoneEmail);
        }

        return resultsToSave;
    }

    /**
     * Remove duplicate items
     *
     * @param data
     * @return items
     */
    private List<IResultModel> removeDuplicateData(List<IResultModel>  data){

        List<IResultModel> uniqueList = new ArrayList<IResultModel>();

        if (data.size() > 0){
            uniqueList.add(data.get(0));
        }

        for (IResultModel dt : data){

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean has = false;

            for (IResultModel du : uniqueList){

                String domainUnique = du.getDomain();
                String keywordUnique = du.getKeyword();

                if (domainData.equals(domainUnique) && keywordData.equals(keywordUnique)){
                    has = true;
                }
            }

            if (!has){
                uniqueList.add(dt);
            }
        }

        return uniqueList;
    }
}
