package de.thi.inf.cnd.rest.controller;

public class SomeResponse {
  private String name;

  public SomeResponse() {

  }

  public SomeResponse(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
