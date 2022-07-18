package SearchEngineApp.controllers;

import SearchEngineApp.service.IndexingService;
import SearchEngineApp.service.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexingController
{
    private final IndexingService indexingService;

    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @GetMapping(value = "/startIndexing")
    public ResponseEntity<Object> startIndexing() throws Exception {
        Response response = indexingService.startAllIndexing();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/stopIndexing")
    public ResponseEntity<Object> stopIndexing() throws InterruptedException {
        Response response = indexingService.stopIndexing();
        return ResponseEntity.ok(response);
    }
}
