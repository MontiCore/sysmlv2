/* (c) https://github.com/MontiCore/monticore */

flow of Fuel from port1 to port2;
flow port1 to port2;

bind a = b;

message of Fuel from port1 to port2;
message port1 to port2;

interface def interFace {
  end port1;
  end port2;
}
interface interFace connect a to b;
interface a to b;
