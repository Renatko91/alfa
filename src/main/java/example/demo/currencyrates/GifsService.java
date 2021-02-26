package example.demo.currencyrates;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="gif-service", url="${app.gifurl}")
public interface GifsService {
    @RequestMapping(method = RequestMethod.GET)
    String getGIFs(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "api_key", required = true) String id,
            @RequestParam(value = "limit", required = true) int limit
    );
}
