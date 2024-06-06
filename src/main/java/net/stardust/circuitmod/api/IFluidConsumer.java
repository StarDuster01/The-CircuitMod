package net.stardust.circuitmod.api;

public interface IFluidConsumer {
    void addFluid(int fluidAmount, String fluidType);
    boolean canReceiveFluid(String fluidType); // Update the method to accept a fluidType parameter
}
