package Modules.Data.MySQL.Models;

import Application.Contracts.Data.IResultModel;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 19:18
 */
public class Data implements IResultModel {

    private String domain;

    private String url;

    private Integer quantity;

    private Calendar date;

    private String keyword;

    private String phones;

    private String emails;

    @Override
    public String getPhones() {
        return phones;
    }

    @Override
    public void setPhones(String phones) {
        this.phones = phones;
    }

    @Override
    public String getEmails() {
        return emails;
    }

    @Override
    public void setEmails(String emails) {
        this.emails = emails;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Calendar getDate() {
        return date;
    }

    @Override
    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
