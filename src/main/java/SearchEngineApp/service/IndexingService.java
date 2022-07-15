package SearchEngineApp.service;

import SearchEngineApp.service.response.Response;

public interface IndexingService
{
    Response startAllIndexing();
    Response stopIndexing();
}
