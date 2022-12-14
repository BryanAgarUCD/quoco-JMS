package service.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientApplicationMessage implements Serializable {
    public long id;
    public ClientInfo clientInfo;
    public List<Quotation> quotations = new ArrayList<Quotation>();
    public ClientApplicationMessage(long id, ClientInfo clientInfo, List<Quotation> quotations) {
        this.id = id;
        this.clientInfo = clientInfo;
        this.quotations = quotations;
    }
}
