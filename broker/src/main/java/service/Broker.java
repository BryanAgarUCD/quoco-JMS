package service;

import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.*;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;

public class Broker {

    static String host;
    static Map<Long, ClientInfo> cache = new HashMap<>();
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
            CbsThread cbsThread = new CbsThread();
            SbcThread sbcThread = new SbcThread();
            new Thread(cbsThread).start();
            new Thread(sbcThread).start();

        } catch (JMSException e){
            System.out.println(e);
        }
    }

    //The thread Client -> Broker -> Services
    public static class CbsThread implements Runnable{

        @Override
        public void run() {
            try {
                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue queue = session.createQueue("REQUEST");
                Topic topic = session.createTopic("APPLICATIONS");
                MessageProducer producer = session.createProducer(topic);
                MessageConsumer consumer = session.createConsumer(queue);
                connection.start();
                while (true){
                    Message message = consumer.receive();
                    if (message instanceof ObjectMessage) {
                        Object content = ((ObjectMessage) message).getObject();
                        if (content instanceof QuotationRequestMessage) {
                            QuotationRequestMessage request = (QuotationRequestMessage) content;
                            producer.send(message);
                            cache.put(request.id,request.info);
                        }
                    } else {
                        System.out.println("Unknown message type: " +
                                message.getClass().getCanonicalName());
                    }
                }
            } catch (JMSException e){
                System.out.println(e);
            }
        }
    }

    //The thread Services -> Broker -> Client
    public static class SbcThread implements Runnable{

        @Override
        public void run() {
            try {
                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue queue = session.createQueue("QUOTATIONS");
                Topic topic = session.createTopic("RESPONSE");
                MessageProducer producer = session.createProducer(topic);
                MessageConsumer consumer = session.createConsumer(queue);
                MessageProducer rebacker = session.createProducer(queue);
                connection.start();
                while (true) {
                    long t1 = 0;
                    ArrayList<Quotation> quotations = new ArrayList<>();
                    long id = -1;
                    while (t1!=3) {
                            Message message = consumer.receive();
                            if (message instanceof ObjectMessage) {
                                Object content = ((ObjectMessage) message).getObject();
                                if (content instanceof QuotationResponseMessage) {
                                    QuotationResponseMessage request = (QuotationResponseMessage) content;
                                    System.out.println(request.id);
                                    if ((id == -1) || (id == request.id)) {
                                        message.acknowledge();
                                        quotations.add(request.quotation);
                                        id = request.id;
                                        t1++;
                                    } else {
                                        rebacker.send(message);
                                    }
                                }
                            } else {
                                System.out.println("Unknown message type: " +
                                        message.getClass().getCanonicalName());
                            }
                    }
                    System.out.println(quotations);
                    Message application = session.createObjectMessage(new ClientApplicationMessage(id, cache.get(id), quotations));
                    producer.send(application);
                }

            } catch (JMSException e){
                System.out.println(e);
            }
        }
    }

}
