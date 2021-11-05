package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogisticDetailResponseForYanWen extends AbstractResponseYanWen<LogisticDetailResponseForYanWen> {

    private ExpressCollection ExpressCollection;
    private Integer TotalRecordCount;
    private Integer RecordPerPage;
    private Integer TotalPageCount;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpressCollection
    {
        private List<ExpressType> ExpressType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpressType
    {
        private String Epcode;
        private String Userid;
        private String Channel;
        private String UserOrderNumber;
        private Date SendDate;
        private Receiver Receiver;
        private Sender Sender;
        private Integer Quantity;
        private GoodsName GoodsName;
        private String YanwenNumber;
        private Boolean Insure;
        private String TrackingStatus;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Receiver
    {
        private String Userid;
        private String Name;
        private String Phone;
        private String Email;
        private String Company;
        private String Country;
        private String Postcode;
        private String State;
        private String City;
        private String Address1;
        private String Address2;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender
    {
        private String TaxNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoodsName
    {
        private String Userid;
        private String NameCh;
        private String NameEn;
        private String Weight;
        private BigDecimal DeclaredValue;
        private String DeclaredCurrency;
    }
}
