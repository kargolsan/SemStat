package Modules.Bots.First.Services;

import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 12:54
 */
public class ProcessorService {

    /** @var file with properties of application */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Get limit threads of processor
     *
     * @return quantity threads
     */
    public Integer getLimitThreads() {
        Integer limit = 0;

        String getPLT = SettingsService.get("processor.limit_threads");
        String defaultPLT = PropertyService.get("default_processor_limit_threads", PROPERTIES_FILE);

        getPLT = (getPLT == "") ? defaultPLT : getPLT;

        limit = Integer.parseInt(getPLT);

        limit = (limit < 4) ? 2 : limit - 2;

        return limit;
    }
}
