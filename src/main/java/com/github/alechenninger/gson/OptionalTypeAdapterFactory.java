package com.github.alechenninger.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * For Java 8's {@link java.util.Optional} type.
 */
public class OptionalTypeAdapterFactory implements TypeAdapterFactory{
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    Type type = typeToken.getType();

    if (typeToken.getRawType() != Optional.class || !(type instanceof ParameterizedType)) {
      return null;
    }

    Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
    TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));

    return (TypeAdapter<T>) newOptionalAdapter(elementAdapter);
  }

  private <E> TypeAdapter<Optional<E>> newOptionalAdapter(final TypeAdapter<E> elementAdapter) {
    return new TypeAdapter<Optional<E>>() {
      public void write(JsonWriter out, Optional<E> value) throws IOException {
        if (value == null || !value.isPresent()) {
          out.nullValue();
          return;
        }

        elementAdapter.write(out, value.get());
      }

      public Optional<E> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return Optional.empty();
        }

        return Optional.of(elementAdapter.read(in));
      }
    };
  }
}
