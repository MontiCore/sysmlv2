interface def WaterDelivery {
  end suppliedBy : SpigotBank[1] {
    port hot : Spigot;
    port cold : Spigot;
  }
  end deliveredTo : Faucet[1..*] {
    port hot : FaucetInlet;
    port cold : FaucetInlet;
  }

  connect suppliedBy::hot to deliveredTo::hot;
  connect suppliedBy::cold to deliveredTo::cold;
}
