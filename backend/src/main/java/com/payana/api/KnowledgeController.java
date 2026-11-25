package com.payana.api;

import com.payana.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
  private final KnowledgeService knowledge;
  public KnowledgeController(KnowledgeService knowledge){this.knowledge=knowledge;}
  @GetMapping public KnowledgeService.Answer answer(@RequestParam String question){return knowledge.answer(question);}
}
