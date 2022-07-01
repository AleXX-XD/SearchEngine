package SearchEngineApp.controllers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileReader;
import java.io.IOException;

@Controller
public class StatisticsController
{
    @GetMapping(value = "/statistics")
    @ResponseBody
    public JSONObject statistic() throws IOException, ParseException {
        return getJson();
    }

    //TODO: метод для проверки работоспособности /statistic. УБРАТЬ после формирования своей статистики
    private JSONObject getJson () throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("src/main/resources/file.json"));
        return (JSONObject) obj;
    }
}
