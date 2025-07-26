public class StopOrder implements OrderStrategy{
    @Override
    public void esegui() {
        System.out.println("StopOrder execution");
    }
}
