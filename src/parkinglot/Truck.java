package parkinglot;

public class Truck extends Car{
    @Override
    public VehicleSize getSize() {
        return VehicleSize.Large;
    }
}
