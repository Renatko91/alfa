package example.demo.currencyrates;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="currency-service", url="${app.currencyurl}")
public interface CurrencyService {

    @RequestMapping(method = RequestMethod.GET, value = "/latest.json")
    String getLatestCurrencyJson(
            @RequestParam(value = "app_id", required = true) String id);

    @RequestMapping(method = RequestMethod.GET, value = "/historical/{date}.json")
    String getHistoryCurrencyJson(
            @PathVariable(value = "date") String date,
            @RequestParam(value = "app_id", required = true) String id);

}
