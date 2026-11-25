package com.payana.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KnowledgeService {
  private final List<Entry> entries = List.of(
    new Entry("BMTC routes operate across Bengaluru with regular, Vajra and airport services.","bmtc bus route stop airport"),
    new Entry("Namma Metro connects major corridors through Purple and Green line stations.","metro train station purple green"),
    new Entry("Kempegowda International Airport is served by Vayu Vajra airport buses.","airport flight vayu vajra"),
    new Entry("Payana can combine walking, bus and train legs into one journey.","journey route walking transfer"));
  public Answer answer(String question) {
    String q=question.toLowerCase();
    Entry best=entries.stream().max((a,b)->Integer.compare(score(q,a.tags),score(q,b.tags))).orElse(entries.get(0));
    return new Answer(best.text(), Math.min(0.99,0.55 + score(q,best.tags)*0.1), "local-keyword-rag");
  }
  private int score(String q,String tags){int n=0;for(String word:tags.split(" "))if(q.contains(word))n++;return n;}
  private record Entry(String text,String tags){}
  public record Answer(String answer,double confidence,String source){}
}
