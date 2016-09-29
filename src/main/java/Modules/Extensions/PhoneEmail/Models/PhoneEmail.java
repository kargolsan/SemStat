package Modules.Extensions.PhoneEmail.Models;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 29.09.2016
 * Time: 13:38
 */
public class PhoneEmail {

    private String domain;

    private String keyword;

    private String phones;

    private String emails;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }
}
