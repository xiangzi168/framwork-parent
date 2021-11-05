package com.amg.fulfillment.cloud.logistics.model.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class DeleteReq {

    @NotNull(message = "ids不能为空")
    @Size(min=1,message = "ids至少有一项内容")
    private List<Long> ids;
}
