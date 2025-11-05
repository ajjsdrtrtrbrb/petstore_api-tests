package api_project.model;



public class OrderBuilder {
    private long id=0;
    private int petId=0;
    private int quantity=0;
    private String shipDate="2025-10-08T05:41:28.759Z";
    private String status="test";
    private boolean complete=false;
    public OrderBuilder id (long id){
        this.id=id;
        return this;
    }
    public OrderBuilder petId(int petId){
        this.petId=petId;
        return this;
    }
    public OrderBuilder quantity(int quantity){
        this.quantity=quantity;
        return this;
    }
    public OrderBuilder shipDate(String shipDate){
        this.shipDate=shipDate;
        return this;
    }
    public OrderBuilder status(String status){
        this.status=status;
        return this;
    }
    public OrderBuilder complete(boolean complete){
        this.complete=complete;
        return this;
    }
    public Order build(){
        return new Order(id,petId,quantity,shipDate,status,complete);
    }

}
