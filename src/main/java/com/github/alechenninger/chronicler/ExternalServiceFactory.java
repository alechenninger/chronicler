package com.github.alechenninger.chronicler;

import java.util.Optional;

public interface ExternalServiceFactory {
  <T> Optional<T> getService(Class<T> service);
}
