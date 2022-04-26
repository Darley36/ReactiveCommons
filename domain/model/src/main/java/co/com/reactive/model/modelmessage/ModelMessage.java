package co.com.reactive.model.modelmessage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ModelMessage {
    private String message;
}
