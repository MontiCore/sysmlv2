port def Connector {
  in attribute dataIn: Integer;
  attribute control: Boolean;
  out attribute daatOut: Integer;
  inout attribute checksum: String;
}

port inTheWild : Connector;
port reverse : ~Connector;
