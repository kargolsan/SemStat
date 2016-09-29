package Modules.Extensions.PhoneEmail.Services;

import Application.Contracts.Data.IResultModel;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.BottomStripController;
import Application.Services.PropertyService;
import Modules.Bots.First.Services.ParseService;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 28.09.2016
 * Time: 21:34
 */
public class EmailService {

    /* @var unique email for keyword */
    private Map<String, String> uniqueEmail;

    /* @var parse service */
    private ParseService parseService;

    /* @var resource bundle */
    private ResourceBundle bundle;

    /* @var user agent for jsoup */
    private static final String USER_AGENT = PropertyService.get("user_agent", "Application/Resources/properties.properties");

    /**
     * Constructor
     *
     * @param parseService
     * @param bundle
     */
    public EmailService(ParseService parseService, ResourceBundle bundle){
        this.parseService = parseService;
        this.bundle = bundle;
        this.uniqueEmail = new HashMap<>();
    }

    /**
     * Get email of domain
     *
     * @param doc
     * @return
     */
    public void analyzeEmailOfDomain(Document doc){
        List<String> links = findContactLinks(doc);

        List<String> emails = findEmailInWebsite(links);

        emails.addAll(extractEmailsFromText(doc.text()));

        addToUniqueEmails(emails, doc);
    }

    /**
     * Find links of contact
     *
     * @param doc
     * @return
     */
    private List<String> findContactLinks(Document doc) {

        List<String> words = new ArrayList<String>(
                Arrays.asList("kontakt", "contact", "adres", "phone", "telefon", "nas", "firmie",
                        "mapa", "dojazd", "dojechać", "about"));

        List<String> links = new ArrayList<>();

        Elements as = doc.body().getElementsByTag("a");

        for (Element e : as){

            String href = e.attr("href");
            String aText = e.text();

            for (String w : words){
                if (href.contains(w) || aText.contains(w)){

                    String link = null;
                    if (!e.attr("href").contains("http://")){
                        link = "http://" + getDomainName(doc.baseUri()) + "/" + href;
                        if (!linkIsThisSomeDomain(link, doc)) continue;
                        links.add(link);
                    } else {
                        if (!linkIsThisSomeDomain(href, doc)) continue;
                        links.add(href);
                    }
                }
            }
        }

        return links;
    }

    /**
     * Find emails in website
     *
     * @param links
     * @return emails in website
     */
    private List<String> findEmailInWebsite(List<String> links) {

        List<String> emails = new ArrayList<>();

        links.forEach(link -> {
            try {

                Document doc = Jsoup.connect(link).userAgent(USER_AGENT).get();
                emails.addAll(extractEmailsFromText(doc.text()));
                this.parseService.setCountAnalyzed(this.parseService.getCountAnalyzed() + 1);
                BotController.setCountAnalyzed(this.parseService.getCountAnalyzed().toString());
                BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), doc.baseUri()));

            } catch (Exception e) {
            }
        });

        return emails;

    }

    /**
     * Extract emails from tekst
     *
     * @param text
     * @return list with emails
     */
    private List<String> extractEmailsFromText(String text) {
        List<String> emails = new ArrayList<>();

        Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = p.matcher(text);

        while(matcher.find()) {
            emails.add(matcher.group());
        }

        return emails;
    }

    /**
     * Get domain from url
     *
     * @param url
     * @return
     */
    public String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();

            if (uri.getHost() == null) {
                URI uri2 = new URI (url.replace("_", ""));
                domain = uri2.getHost();
            }

            if (domain == null) return null;
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {}
        return null;
    }

    /**
     * Add unique emails
     *
     * @param emails
     * @param
     */
    private void addToUniqueEmails(List<String> emails, Document doc){
        for (String email : emails){

            String domain = getDomainName(doc.baseUri());

            if (this.uniqueEmail.get(domain) == null){

                this.uniqueEmail.put(domain, email + ",");

            }
            else {
                String value = this.uniqueEmail.get(domain);
                this.uniqueEmail.put(domain, value + email + ",");
            }
        }
    }

    /**
     * Check link is this contains domain of website
     *
     * @param link
     * @param doc
     * @return true if this some, false if isn't this some
     */
    private Boolean linkIsThisSomeDomain(String link, Document doc){
        String domainLink = getDomainName(link);
        String domainWebsite = getDomainName(doc.baseUri());

        return domainLink.equals(domainWebsite);
    }

    /**
     * Run before save for modification results
     *
     * @param resultsToSave
     * @return resultsToSave
     */
    public List<IResultModel> beforeSaved(List<IResultModel> resultsToSave) {
        for (IResultModel result : resultsToSave){
            String resultDomain = result.getDomain();

            if (this.uniqueEmail.get(resultDomain) != null){
                String value = this.uniqueEmail.get(resultDomain);
                value = uniqueJsonEmails(value);
                result.setEmails(value);
            }
        }
        return resultsToSave;
    }

    /**
     * Unique display emails
     *
     * @param value
     * @return emails
     */
    private String uniqueJsonEmails(String value){

        List<String> emails = new ArrayList<>(Arrays.asList(value.split(",")));
        Map<String, Integer> unique = new HashMap<>();

        for (String email : emails){

            if (unique.get(email)== null){
                unique.put(email, 1);
            } else {
                Integer count = unique.get(email);
                unique.put(email, count + 1);
            }
        }

        String result = "";

        for(Map.Entry<String, Integer> entry : unique.entrySet()) {
            String k = entry.getKey();
            Integer v = entry.getValue();

            result += String.format("{\\\"email\\\":\\\"%1$s\\\", \\\"quantity\\\":\\\"%2$s\\\"},", k, v);
        }

        if (result.length() > 1){
            result = result.substring(0,result.length()-1);
        }

        result += "";

        return result;
    }


    /**
     * Clear
     */
    public void clear() {
        this.uniqueEmail.clear();
    }

    /**
     * Finish
     */
    public void finish() {

    }
}
