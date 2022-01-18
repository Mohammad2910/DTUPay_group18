package facade.domain;

import java.util.ArrayList;
// todo: This class should be deleted as the payments will be saved in Payment microservice
public class Paymentlist {
    //This is a test class, so that the client had connection to the facade

    ArrayList<Payment> list = new ArrayList<>();

    //singleton
    private static Paymentlist paymentlist = new Paymentlist();
    private Paymentlist(){};
    public static Paymentlist getInstance(){return paymentlist;}
    //------------------------


    public void addPayment(Payment p){list.add(p);}
    public ArrayList<Payment> getPaymentList(){ return list;}


}
