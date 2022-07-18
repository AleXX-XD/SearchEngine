package SearchEngineApp.service;

import SearchEngineApp.service.response.Response;

import java.util.concurrent.ExecutionException;

public interface IndexingService
{
    Response startAllIndexing() throws Exception;
    Response stopIndexing() throws InterruptedException;
}
