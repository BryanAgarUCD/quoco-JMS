package service.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientApplicationMessage implements Serializable {
    public ClientInfo clientInfo;
    public List<Quotation> quotations = new ArrayList<Quotation>();
    public ClientApplicationMessage(ClientInfo clientInfo, List<Quotation> quotations) {
        this.clientInfo = clientInfo;
        this.quotations = quotations;
    }
}
