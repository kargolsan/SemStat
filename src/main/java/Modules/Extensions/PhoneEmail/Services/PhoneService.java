package Modules.Extensions.PhoneEmail.Services;

import Application.Contracts.Data.IResultModel;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.BottomStripController;
import Application.Services.PropertyService;
import Modules.Bots.First.Services.ParseService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.lanwen.verbalregex.VerbalExpression;

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
public class PhoneService {

    /* @var unique phone for keyword */
    private Map<String, String> uniquePhone;

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
    public PhoneService(ParseService parseService, ResourceBundle bundle) {
        this.parseService = parseService;
        this.bundle = bundle;
        this.uniquePhone = new HashMap<>();
    }

    /**
     * Get phone of domain
     *
     * @param doc
     */
    public void analyzePhoneOfDomain(Document doc) {

        List<String> links = findContactLinks(doc);

        List<String> phones = findPhoneInWebsite(links);

        phones.addAll(extractPhonesFromText(doc.text()));

        addToUniquePhones(phones, doc);
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
     * Find phones in website
     *
     * @param links
     * @return phones in website
     */
    private List<String> findPhoneInWebsite(List<String> links) {

        List<String> phones = new ArrayList<>();

        links.forEach(link -> {
            try {

                Document doc = Jsoup.connect(link).userAgent(USER_AGENT).get();
                phones.addAll(extractPhonesFromText(doc.body().text()));
                this.parseService.setCountAnalyzed(this.parseService.getCountAnalyzed() + 1);
                BotController.setCountAnalyzed(this.parseService.getCountAnalyzed().toString());
                BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), doc.baseUri()));

            } catch (Exception e) {
            }
        });

        return phones;

    }

    /**
     * Extract phones from tekst
     *
     * @param text
     * @return list with phones
     */
    private List<String> extractPhonesFromText(String text) {
        List<String> phones = new ArrayList<>();
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        Iterator<PhoneNumberMatch> iterator = util.findNumbers(text, null).iterator();

        while (iterator.hasNext()) {
            String phone = iterator.next().rawString();
            phone = deleteCharPhone(phone);
            phones.add(phone);
        }

        text = deleteCharPhone(text);

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(text);
        while(m.find()){
            if (m.group().length() >= 9 && m.group().length() <=12 && !m.group().startsWith("000")){
                phones.add(m.group());
            }
        }

        return phones;
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
     * Add unique phones
     *
     * @param phones
     * @param
     */
    private void addToUniquePhones(List<String> phones, Document doc){
        for (String phone : phones){

            String domain = getDomainName(doc.baseUri());

            if (this.uniquePhone.get(domain) == null){

                this.uniquePhone.put(domain, phone + ",");

            }
            else {
                String value = this.uniquePhone.get(domain);
                this.uniquePhone.put(domain, value + phone + ",");
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

            if (this.uniquePhone.get(resultDomain) != null){
                String value = this.uniquePhone.get(resultDomain);
                value = uniqueJsonPhones(value);
                result.setPhones(value);
            }
        }

        return resultsToSave;
    }

    /**
     * Unique display phones
     *
     * @param value
     * @return phone
     */
    private String uniqueJsonPhones(String value){

        List<String> phones = new ArrayList<>(Arrays.asList(value.split(",")));
        Map<String, Integer> unique = new HashMap<>();

        for (String phone : phones){

            if (unique.get(phone)== null){
                unique.put(phone, 1);
            } else {
                Integer count = unique.get(phone);
                unique.put(phone, count + 1);
            }
        }

        String result = "";

        for(Map.Entry<String, Integer> entry : unique.entrySet()) {
            String k = entry.getKey();
            Integer v = entry.getValue();

            k = deleteCharPhone(k);

            result += String.format("{\\\"number\\\":\\\"%1$s\\\", \\\"quantity\\\":\\\"%2$s\\\"},", k, v);
        }

        if (result.length() > 1){
            result = result.substring(0,result.length()-1);
        }

        result += "";

        return result;
    }

    /**
     * Delete char phones
     * @param text
     * @return
     */
    private String deleteCharPhone(String text){
        text = text.replace(" ", "");
        text = text.replace("-", "");
        text = text.replace("(", "");
        text = text.replace(")","");
        text = text.replace("+","");
        text = text.replace(" ", "");
        return text;
    }

    /**
     * Clear
     */
    public void clear() {
        this.uniquePhone.clear();
    }

    /**
     * Finish
     */
    public void finish() {

    }
}
