package Modules.Extensions.PhoneEmail.Services;

import Application.Contracts.Data.IResultModel;
import Application.Contracts.Extensions.IExtension;
import Application.Services.Application.SettingsService;
import Modules.Bots.First.Services.ParseService;
import org.jsoup.nodes.Document;

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
     * Check extension can be used
     *
     * @return true if can or false if can not
     */
    public Boolean access(){

        // sprawdzenie czy moduł ma licencję
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
        this.emailService.clear();
        this.phoneService.clear();
    }

    /**
     * Call if bot finished
     */
    @Override
    public void finish() {
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
        resultsToSave = this.phoneService.beforeSaved(resultsToSave);
        resultsToSave = this.emailService.beforeSaved(resultsToSave);
        return resultsToSave;
    }
}
