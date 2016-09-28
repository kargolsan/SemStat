package Modules.Bots.First.Services;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 28.09.2016
 * Time: 12:00
 */
public class FiltrExtensionsDomainService {

    /* @var filtr extensions domain */
    private List<String> filtrExtensionsDomain;

    /**
     * Constructor
     */
    public FiltrExtensionsDomainService() {
        this.filtrExtensionsDomain = new ArrayList<>();
    }

    /**
     * Set filtr Extensions domain
     *
     * @param filtrExtensionsDomain
     */
    public void setFiltrExtensionsDomain(String filtrExtensionsDomain){
        this.filtrExtensionsDomain.clear();
        this.filtrExtensionsDomain.addAll(Arrays.asList(filtrExtensionsDomain.split(",")));
    }

    /**
     * Check is domain can be analyze
     *
     * @param domain
     * @return
     */
    public Boolean can(String domain){
        if (domain == null) return false;

        for (String f : filtrExtensionsDomain){
            f = f.trim();
            if (domain.endsWith(f)) return true;
        }
        return false;
    }
}
