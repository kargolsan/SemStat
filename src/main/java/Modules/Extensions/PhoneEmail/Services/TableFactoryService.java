package Modules.Extensions.PhoneEmail.Services;

import Application.Contracts.Data.IResultModel;
import Application.Models.Application.Log;
import Modules.Data.File.Models.Data;
import Modules.Extensions.PhoneEmail.Models.Email;
import Modules.Extensions.PhoneEmail.Models.Phone;
import Modules.Extensions.PhoneEmail.Models.PhoneEmail;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 29.09.2016
 * Time: 13:41
 */
public class TableFactoryService {

    /**
     * Factory value from json phones
     *
     * @return value factory
     */
    public Callback<TableColumn.CellDataFeatures<PhoneEmail, String>, ObservableValue<String>> getPhonesFactory(){
        return new Callback<TableColumn.CellDataFeatures<PhoneEmail, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PhoneEmail, String> value) {

                String result = "";

                List<Phone> phones = new ArrayList<>();

                List<String> phonesJson = new ArrayList<>();

                Pattern p = Pattern.compile("\\{.*?\\}");
                Matcher m = p.matcher(value.getValue().getPhones());
                while(m.find()){
                    phonesJson.add(m.group());
                }

                ObjectMapper mapper = new ObjectMapper();

                for (String json : phonesJson){
                    try {
                        json = json.replace("\\", "");

                        Phone phone = mapper.readValue(json, Phone.class);
                        phones.add(phone);

                    } catch (IOException e) {}
                }

                for (Phone phone : phones){
                    result += String.format("[%2$s]....%1$s,   ", phone.getNumber(), phone.getQuantity());
                }

                if (result.length()>3){
                    result = result.substring(0,result.length()-3);
                }

                return new ReadOnlyObjectWrapper(result);
            }
        };
    }

    /**
     * Factory value from json emails
     *
     * @return value factory
     */
    public Callback<TableColumn.CellDataFeatures<PhoneEmail, String>, ObservableValue<String>> getEmailsFactory(){
        return new Callback<TableColumn.CellDataFeatures<PhoneEmail, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PhoneEmail, String> value) {
                String result = "";

                List<Email> emails = new ArrayList<>();

                List<String> emailsJson = new ArrayList<>();

                Pattern p = Pattern.compile("\\{.*?\\}");
                Matcher m = p.matcher(value.getValue().getEmails());
                while(m.find()){
                    emailsJson.add(m.group());
                }

                ObjectMapper mapper = new ObjectMapper();

                for (String json : emailsJson){
                    try {
                        json = json.replace("\\", "");

                        Email email = mapper.readValue(json, Email.class);
                        emails.add(email);

                    } catch (IOException e) {}
                }

                for (Email email : emails){
                    result += String.format("[%2$s]....%1$s,   ", email.getEmail(), email.getQuantity());
                }

                if (result.length()>3){
                    result = result.substring(0,result.length()-3);
                }

                return new ReadOnlyObjectWrapper(result);
            }
        };
    }
}
