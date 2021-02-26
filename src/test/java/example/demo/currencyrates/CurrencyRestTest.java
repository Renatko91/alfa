package example.demo.currencyrates;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest()
@RunWith(SpringRunner.class)
class CurrencyRestTest {
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private GifsService gifsService;

    @Test
    void gifOnCurrencyRates() {
        String curid = "8e420a061b784407a490dc44538a11bc";

        String latestAnswer = currencyService.getLatestCurrencyJson(curid);
        String yesterdayAnswer = currencyService.getHistoryCurrencyJson(yesterday(), curid);
        assertNotNull(latestAnswer);
        assertNotNull(yesterdayAnswer);
    }

    @Test
    void gifsList() {
        String search = "rich";
        String gifid = "5Ft2MTnNdvszZ7ETNiOEuzK88ImqMlF1";
        int limit = 5;

        String gifs = gifsService.getGIFs(search, gifid, limit);
        assertNotNull(gifs);
    }

    private String yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }
}