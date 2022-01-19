import controller.ServiceProvider;



public class TokenStartUp {
    public static void main(String[] args) {
        new TokenStartUp().startUp();
    }
    public void startUp(){
        System.out.println("Token startup has been initiated!");
        ServiceProvider serviceProvider = new ServiceProvider();
    }
}
