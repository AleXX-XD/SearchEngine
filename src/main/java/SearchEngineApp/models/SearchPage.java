package SearchEngineApp.models;

import lombok.Data;

@Data
public class SearchPage
{
    private WebPage webPage;
    private String title;
    private String snippet;
    private float absoluteRelevance;
    private double relevance;

    public SearchPage() {}
}

