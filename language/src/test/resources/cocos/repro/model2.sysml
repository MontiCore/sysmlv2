part def Repro0xF737F {
  attribute data : Packet;
  require constraint {
    forall v in data.values():
      v.size == 0
  }
}

attribute def Packet {
  attribute size : int;
}
