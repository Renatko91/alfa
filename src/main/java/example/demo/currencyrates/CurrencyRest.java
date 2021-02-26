package example.demo.currencyrates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class CurrencyRest {
    private static final JsonParser parser = JsonParserFactory.getJsonParser();

    @Value("${app.curid}")
    private String curid;
    @Value("${app.gifid}")
    private String gifid;
    @Value("${app.currency}")
    private String baseCurrency;
    @Value("${app.limit}")
    private int limit;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private GifsService gifsService;

    @RequestMapping(value = "/{currency}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> gifOnCurrencyRates(@PathVariable String currency) throws IOException {
        String latestAnswer = currencyService.getLatestCurrencyJson(curid);
        String yesterdayAnswer = currencyService.getHistoryCurrencyJson(yesterday(), curid);

        Map<String, Object> latestMap = parser.parseMap(latestAnswer);
        Map<String, Object> yesterdayMap = parser.parseMap(yesterdayAnswer);

        Map<String, Object> latestCurrencyMap = (Map<String, Object>) latestMap.get("rates");
        Map<String, Object> yesterdayCurrencyMap = (Map<String, Object>) yesterdayMap.get("rates");

        double oneResult = (Double) latestCurrencyMap.get(currency) / (Double) latestCurrencyMap.get(baseCurrency);
        double twoResult = (Double) yesterdayCurrencyMap.get(currency) / (Double) yesterdayCurrencyMap.get(baseCurrency);

        List<String> gifs;

        if (oneResult > twoResult) {
            gifs = gifsList("broke");
        } else {
            gifs = gifsList("rich");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_GIF);

        Random random = new Random();
        byte[] response = gifBytes(gifs.get(random.nextInt(gifs.size())));

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    public List<String> gifsList(String search) throws IOException {
        String gifUrl = null;

        ArrayList<Map<String, Object>> gifList;
        List<String> urls = new ArrayList<>();

        String gifs = gifsService.getGIFs(search, gifid, limit);

        Map<String, Object> gifRichsMap = parser.parseMap(gifs);

        gifList = (ArrayList) gifRichsMap.get("data");

        for (Map<String, Object> result : gifList) {
            Map<String, Object> images = (Map<String, Object>) result.get("images");
            Map<String, Object> original = (Map<String, Object>) images.get("original");
            gifUrl = getGifUrl(new URL((String) original.get("url")));
            if (gifUrl != null) {
                urls.add(gifUrl);
            }
        }

        return urls;
    }

    private byte[] gifBytes(String gifUrl) throws IOException {
        URL url = new URL(gifUrl);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }

    private String getGifUrl(URL url) throws IOException {
        List<String> istS = new ArrayList<>();
        InputStream is = url.openStream();
        BufferedReader buf = new BufferedReader(new InputStreamReader(new DataInputStream(is)));
        String str = buf.readLine();
        while (str != null) {
            istS.add(str);
            str = buf.readLine();
        }
        for (String s : istS) {
            if (s.contains("og:url")) {
                s = s.substring(s.indexOf("content=") + 9, s.indexOf(">") - 1);
                is.close();
                buf.close();
                return s;
            }
        }
        is.close();
        buf.close();
        return null;
    }

    private String yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }
}