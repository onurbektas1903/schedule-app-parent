package tr.com.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tr.com.common.enums.ActionTypeEnum;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainMessage implements Serializable {

    private static final long serialVersionUID = 881130607171682641L;
    private Object data;
    private String payloadType;
    private ActionTypeEnum actionType;

    public DomainMessage(ActionTypeEnum actionType, Object data) {
        this.data = data;
        this.payloadType = data.getClass().getSimpleName().toLowerCase();
        this.actionType = actionType;
    }
}
