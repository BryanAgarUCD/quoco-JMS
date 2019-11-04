package service;

import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.*;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;

public class Broker implements BrokerService {

    static String host;
    static long SEED_ID = 0;
    static Map<Long, ClientInfo> cache = new HashMap<>();
    static LinkedList<Message> REQUESTS = new LinkedList<>();
    private static Session session;
    private static MessageProducer producer;
    private static MessageConsumer consumer;
    private static Connection connection;

    public static void main(String[] args) {
        host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        try{
            System.out.println("Starting Broker on: " +host);
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
            connection = factory.createConnection();
            connection.setClientID("broker");
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Queue queue = session.createQueue("QUOTATIONS");
            Topic topic = session.createTopic("APPLICATIONS");
            producer = session.createProducer(topic);
            consumer = session.createConsumer(queue);

        } catch (JMSException e){
            System.out.println(e);
        }
    }

    @Override
    public List<Quotation> getQuotations(ClientInfo info) {
        List<Quotation> quotations = new ArrayList<>();
        try {
            connection.start();
            long t1 = System.currentTimeMillis();
            while (true) {
                long t2 = System.currentTimeMillis();
                // 2s to timeout
                if(t2-t1 > 2*1000){
                    break;
                } else {
                    Message message = consumer.receive();
                    if (message instanceof ObjectMessage) {
                        Object content = ((ObjectMessage) message).getObject();
                        if (content instanceof QuotationResponseMessage) {
                            QuotationResponseMessage response = (QuotationResponseMessage) content;
                            quotations.add(response.quotation);
                        }
                        message.acknowledge();
                    } else {
                        System.out.println("Unknown message type: " +
                                message.getClass().getCanonicalName());
                    }
                }
            }
            connection.close();
            REQUESTS.pop();
        } catch (JMSException e){
            System.out.println(e);
        }
        return quotations;
    }

    public ClientApplicationMessage getClientApplicationMessage(QuotationRequestMessage quotationRequest){
        try {
            Message request = session.createObjectMessage(quotationRequest);
            cache.put(quotationRequest.id, quotationRequest.info);
            REQUESTS.add(request);
            while(true){
                if (REQUESTS.getFirst()==request){
                    producer.send(request);
                    break;
                } else {
                    continue;
                }
            }

        } catch (JMSException e){
            System.out.println(e);
        }
        List<Quotation> quotations = getQuotations(quotationRequest.info);
        ClientApplicationMessage clientApplicationMessage = new ClientApplicationMessage(quotationRequest.info,quotations);
        return clientApplicationMessage;
    }
}
