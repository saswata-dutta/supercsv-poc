package org.sasdutta;

import org.sasdutta.csv.IdGenerator;

import java.util.UUID;

class RandomUuidGenerator implements IdGenerator {

  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
