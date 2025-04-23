package org.evan.ai.htmx;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
public class AskController {

  private final AiService aiService;

  public AskController(AiService aiService) {
    this.aiService = aiService;
  }

  @GetMapping("/")
  public Mono<String> chat(Model model) {
    return Mono.fromSupplier(() -> {
      model.addAttribute("documentTitle", aiService.getDocumentName());
      return "index";
    });
  }

  @PostMapping("/ask")
  public Mono<HtmxResponse> chat(MessageIn messageIn, Model model) {
    return Mono.fromSupplier(() -> {
      String completion = aiService.complete(messageIn.message());
      model.addAttribute("messageIn", messageIn);
      model.addAttribute("messageOut", completion);
      return HtmxResponse.builder()
              .view("chatMessage :: chatFragment")
              .build();
    });
  }

}