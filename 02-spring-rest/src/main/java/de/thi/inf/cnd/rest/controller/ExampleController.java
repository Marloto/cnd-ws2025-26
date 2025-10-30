package de.thi.inf.cnd.rest.controller;

import de.thi.inf.cnd.rest.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Übernimmt die Anfrageverarbeitung und alles was damit verbunden ist
 * (vgl. 01-java-bad, hier war das in ein Servlet gemischt)
 *
 * Spring regelt alles über Komponenten, welche automatisch erzeugt werden
 */
@RestController
public class ExampleController {

  // "/hello-world/Ich

  @GetMapping("/hello-world/{name}")
  public String helloWorld(@PathVariable String name) {
    return "Hello " + name;
  }

  @PostMapping("/hello-world")
  /** mit POST ist ein Payload im Anschluss zum Request verbunden, wir erhalten Daten **/
  public SomeResponse helloWorldPost(@RequestBody SomeExample data) {
    return new SomeResponse("Hello " + data.getName() + " !");
  }
}
