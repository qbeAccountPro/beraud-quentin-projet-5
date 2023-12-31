package com.safetynet.alerts.web.serialization.serializer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.safetynet.alerts.web.model.Person;

/**
 * Some javadoc.
 * 
 * Custom serializer for Person objects when serializing community emails.
 * This serializer converts a Person object to JSON format with only the email
 * field.
 */
public class CommunityEmailSerializer extends StdSerializer<Person> {

  public CommunityEmailSerializer(Class<Person> t) {
    super(t);
  }

  /**
   * Some javadoc.
   * 
   * Serialize a single Person object to JSON format with only the email field.
   *
   * @param person   The Person object to be serialized.
   * @param gen      The JsonGenerator to write JSON content.
   * @param provider The SerializerProvider for the serialization process.
   */
  @Override
  public void serialize(Person person, JsonGenerator gen, SerializerProvider provider) {
    try {
      gen.writeStartObject();
      gen.writeStringField("email", person.getEmail());
      gen.writeEndObject();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Some javadoc.
   * 
   * Serialize a list of Person objects to JSON format with only the email field.
   *
   * @param persons  The list of Person objects to be serialized.
   * @param gen      The JsonGenerator to write JSON content.
   * @param provider The SerializerProvider for the serialization process.
   */
  public void serializeList(List<Person> persons, JsonGenerator gen, SerializerProvider provider) {
    try {
      gen.writeStartArray();
      for (Person person : persons) {
        serialize(person, gen, provider);
      }
      gen.writeEndArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}