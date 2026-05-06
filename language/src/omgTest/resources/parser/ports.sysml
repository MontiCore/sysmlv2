/* (c) https://github.com/MontiCore/monticore */
port def Connector {
  in attribute dataIn: int;
  attribute control: boolean;
  out attribute daatOut: int;
  inout attribute checksum: String;
}

port inTheWild : Connector;
port reverse : ~Connector;
