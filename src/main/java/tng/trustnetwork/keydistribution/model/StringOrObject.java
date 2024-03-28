package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonDeserialize(using = StringOrObjectDeserializer.class)
public class StringOrObject<T> {

    private T objectValue;

    private String stringValue;
}
