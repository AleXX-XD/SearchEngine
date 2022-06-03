package SearchEngineApp.utils;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CreateLemmasUtil
{
    public static HashMap<String,Integer> getLemmas (String string) throws IOException {
        HashMap<String,Integer> rightWords = new HashMap<>();
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        String[] stringArray = string.split("[^а-яА-ЯёЁ]");

        for (int i = 0; i < stringArray.length; i++) {
            if (!stringArray[i].equals("")) {
                List<String> wordList = luceneMorph.getNormalForms(stringArray[i].toLowerCase());
                List<String> wordInfo = luceneMorph.getMorphInfo(stringArray[i].toLowerCase());
                String[] infoArray = wordInfo.get(0).split("[|\\s]+");
                if(!(infoArray[1].equals("l") || infoArray[1].equals("n") || infoArray[1].equals("o")  || infoArray[1].equals("p"))) {
                    for (String word  : wordList) {
                        if(rightWords.containsKey(word)){
                            int value = rightWords.get(word);
                            rightWords.put(word,value+1);
                        }
                        else {
                            rightWords.put(word,1);
                        }
                    }
                }
            }
        }
        return rightWords;
    }
}
