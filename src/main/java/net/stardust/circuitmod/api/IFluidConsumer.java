package net.stardust.circuitmod.api;

public interface IFluidConsumer {
    void addFluid(int fluidAmount, String fluidType);
    boolean canReceiveFluid(); // Define this method in your interface
}